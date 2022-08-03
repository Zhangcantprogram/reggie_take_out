package com.zhang.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.zhang.reggie.common.BaseContext;
import com.zhang.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 利用过滤器，检查用户是否登录
 */
//过滤器名称为loginCheckFilter，可拦截的路径为所有路径
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、 获取本次请求的url
        String requestURI = request.getRequestURI();
        log.info("过滤器拦截请求：{}",requestURI);

        //2、 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",    //移动端发送短信
                "/user/login"   //移动端登录
        };

        //3、 判断本次请求是否需要处理
        boolean check = Check(urls, requestURI);

        //4、 如果不需要处理，则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //5-1、 判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){

            Long empId = (Long) request.getSession().getAttribute("employee");

            log.info("用户已登录，且用户id为{}",empId);

            //将当前登录的用户id设置到ThreadLocal中
            BaseContext.setCurrentId(empId);

            //查看当前线程的id
            Long id = Thread.currentThread().getId();
            log.info("当前线程id为 {}",id);

            filterChain.doFilter(request,response);
            return;
        }

        //5-2、 判断移动端登录状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("user") != null){
//
//            Long userId = (Long) request.getSession().getAttribute("user");
//
//            log.info("用户已登录，且用户id为{}",userId);
//
//            //将当前登录的用户id设置到ThreadLocal中
//            BaseContext.setCurrentId(userId);
//
//            //查看当前线程的id
//            Long id = Thread.currentThread().getId();
//            log.info("当前线程id为 {}",id);
//
//            filterChain.doFilter(request,response);
//            return;
//        }

        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("user"));

            Long userId= (Long) request.getSession().getAttribute("user");

            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        //6、 如果未登录，则返回未登录的结果,通过输出流方式来向页面发送JSON数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 判断是否为可放行的路径
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean Check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
