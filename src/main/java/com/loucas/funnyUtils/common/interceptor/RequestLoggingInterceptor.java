package com.loucas.funnyUtils.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 获取客户端真实 IP，并做兼容处理
        String remoteAddr = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(remoteAddr)) {
            remoteAddr = "127.0.0.1";
        }

        // 打印基础请求信息
        logger.info("请求地址: {} {}", method, uri);
        logger.info("远程IP: {}", remoteAddr);
        logger.info("User-Agent: {}", request.getHeader("User-Agent"));
        logger.info("Accept: {}", request.getHeader("Accept"));
        logger.info("Referer: {}", request.getHeader("Referer"));

        return true; // 继续执行后续拦截器或控制器
    }
}
