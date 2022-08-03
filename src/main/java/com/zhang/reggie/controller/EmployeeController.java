package com.zhang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhang.reggie.common.R;
import com.zhang.reggie.entity.Employee;
import com.zhang.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //1、 将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、 根据页面提交的用户名username查询数据库（username是唯一的）
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<Employee>();
        lqw.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(lqw);

        //3、 如果没有查询到，则返回登录失败的结果
        if (emp == null){
            return R.error("用户名不存在，登录失败！");
        }

        //4、 密码比对，如果不一致则返回登录失败的结果
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误，登录失败！");
        }

        //5、 查看员工状态，如果status为 0 ，则说明账号为禁用状态，则返回员工已禁用的结果
        if (emp.getStatus() == 0){
            return R.error("账号已禁用！");
        }

        //6、 登录成功，将员工id存入Session并返回登录成功的结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出登录
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("员工退出登录成功！");
    }

    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工，员工信息{}",employee.toString());

        //设置初始密码为123456，并使用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置新员工的创建时间、更新时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获取新员工创建者的id
        //Long empid = (Long) request.getSession().getAttribute("employee");

        //设置新员工的创建者id
        //employee.setCreateUser(empid);
        //employee.setUpdateUser(empid);

        //添加新员工
        employeeService.save(employee);
        return R.success("新员工添加成功！");
    }


    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name ={}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        //添加过滤条件
        lqw.like(name != null,Employee::getName,name);

        //添加排序条件
        lqw.orderByDesc(Employee::getUpdateTime);

        //执行查询
        Page endPage = employeeService.page(pageInfo, lqw);

        return R.success(endPage);
    }

    /**
     * 员工信息修改
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){

        Long id = Thread.currentThread().getId();
        log.info("当前线程id为 {}",id);

        //获取修改人id
        //Long  empId = (Long) request.getSession().getAttribute("employee");

        //设置修改时间和修改人id
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);

        //执行修改
        employeeService.updateById(employee);
        return R.success("员工信息修改成功！");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息！");
    }
}
