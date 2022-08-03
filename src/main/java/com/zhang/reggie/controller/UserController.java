package com.zhang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zhang.reggie.common.R;
import com.zhang.reggie.entity.User;
import com.zhang.reggie.service.UserService;
import com.zhang.reggie.utils.SMSUtils;
import com.zhang.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机验证码
     * @param user
     * @param session
     * @return
     */
//    @PostMapping("/sendMsg")
//    public R<String> sendMsg(@RequestBody User user, HttpSession session){
//        //获取手机号
//        String userPhone = user.getPhone();
//
//        if (userPhone != null){
//            //随机生成6位的验证码
//            String code = ValidateCodeUtils.generateValidateCode(6).toString();
//
//            //调用阿里云的短信服务API来发送短信
//            //SMSUtils.sendMessage("","",userPhone,code);
//            log.info("code = {}",code);
//
//            //需要将生成的验证码保存在session中
//            session.setAttribute(userPhone,code);
//
//            return R.success("短信验证码发送成功！");
//        }
//
//        return R.error("短信验证码发送失败！");
//    }
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone=user.getPhone();
        if(phone != null) {
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //需要将生成的验证码保存到Session
            //session.setAttribute(phone,code);

            //将生成的验证码缓存到redis中，并且设置有效时长为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("手机短信发送失败");
    }


    /**
     * 移动端登录
     * @param map
     * @param session
     * @return
     */
//    @PostMapping("/login")
//    public R<User> login(@RequestBody Map map,HttpSession session){
//        log.info("map:{}", map.toString());
//
//        //获取手机号
//        String userPhone = map.get("userPhone").toString();
//
//        //获取验证码
//        String code = map.get("code").toString();
//
//        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(userPhone);
//
//        //进行验证码的比对（页面提交的验证码和session中保存的验证码比对）
//        if (codeInSession != null && codeInSession.equals(code)){
//            //如果比对成功，说明登录成功
//            //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册（将数据保存到数据库中）
//            LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
//            lqw.eq(User::getPhone,userPhone);
//            User user = userService.getOne(lqw);
//            if (user == null){
//                user = new User();
//                user.setPhone(userPhone);
//                user.setStatus(1);
//
//                userService.save(user);
//            }
//
//            session.setAttribute("user",user.getId());
//
//            return R.success(user);
//        }
//
//        return R.error("用户登录失败！");
//    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("map:{}", map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);

        //从redis中获取缓存的验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        //进行验证码比对（页面提交的验证码和Session中保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)) {
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判断当前手机号是否为新用户，如果是新用户则自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());

            //如果用户登录成功，则删除redis缓存中的验证码
            redisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登陆失败");
    }


    //用户登出
    @PostMapping("/loginout")
    public R<String> loginout(HttpServletRequest request){
        //清理Session中保存的当前用户登录的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

}
