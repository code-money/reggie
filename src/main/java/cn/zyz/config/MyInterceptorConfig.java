package cn.zyz.config;

import cn.zyz.common.R;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: reggie
 * @author: zyz
 * @create: 2022-07-20 08:49
 **/

//@Slf4j
//@Configuration
public class MyInterceptorConfig /*implements WebMvcConfigurer*/ {
//
//    @Autowired
//    private MyInterceptor myInterceptor;
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(myInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns(
//                        "/employee/login",
//                        "/employee/logout",
//                        "/backend/**");
//    }
//
//    @Component
//    public class MyInterceptor implements HandlerInterceptor{
//        @Override
//        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//            String requestURI = request.getRequestURI();
//            Object session = request.getSession().getAttribute("employee");
//            log.info("Going to MyInterceptor Session is:{},RequestUri:{}",session,requestURI);
//            if (session==null){
//                response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//                return false;
//            }
//            return true;
//        }
//    }
}



