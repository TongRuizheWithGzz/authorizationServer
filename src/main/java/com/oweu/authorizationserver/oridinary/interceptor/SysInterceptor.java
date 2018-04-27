package com.oweu.authorizationserver.oridinary.interceptor;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 拦截器 用户权限校验
 */
public class SysInterceptor implements HandlerInterceptor {  
    
	private static final Logger logger = LoggerFactory.getLogger(SysInterceptor.class);
	
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)  
            throws Exception {  
            return true;
    }  
    public void print(HttpServletResponse response,Object message){
    	try {
			response.setStatus(HttpStatus.OK.value());
			response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
			response.setHeader("Cache-Control", "no-cache, must-revalidate");
			PrintWriter writer = response.getWriter();
			writer.write(message.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,  
                           ModelAndView modelAndView) throws Exception {  
        if(response.getStatus()==500){  
            modelAndView.setViewName("/error/500");  
        }else if(response.getStatus()==404){  
            modelAndView.setViewName("/error/404");  
        }  
    }  
  

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)  
            throws Exception {  
    }  
}  