package com.learning.filters.TaskManagement.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class CustomHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            RequiresRole requiresRoles = handlerMethod.getMethod().getAnnotation(RequiresRole.class);
            if (requiresRoles != null) {
                String userRole = request.getHeader("X-USER-ROLE");
                if (!Arrays.asList(requiresRoles.value()).contains(userRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("FORBIDDEN");
                    return false;
                }
            }
        }
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        Object correlationIdAttr = request.getAttribute("correlationId");
        String correlationId = correlationIdAttr !=null ? correlationIdAttr.toString():"UNKNOWN";
        long startTime = (long) request.getAttribute("startTime");
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedTime = currentTimeMillis - startTime;
        System.out.println("Elapsed time: " + elapsedTime + " ms"+" "+correlationId+response.getStatus()+" "+request.getRequestURI()+" "+request.getMethod());
    }
}
