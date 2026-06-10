package com.property.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.property.dto.Result;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        Integer role = (Integer) request.getAttribute("role");
        String uri = request.getRequestURI();

        // 业主路由：/api/owner/** （含 /api/owner/parking, /api/owner/building, /api/owner/repairs, /api/owner/complaints）
        if (uri.startsWith("/api/owner/")) {
            if (role == null || role != 1) {
                writeError(response, 403, "无权访问业主功能");
                return false;
            }
        } else if (uri.startsWith("/api/buildings") || uri.startsWith("/api/employees")
                || uri.startsWith("/api/owners") || uri.startsWith("/api/parkings")
                || uri.startsWith("/api/repairs") || uri.startsWith("/api/complaints")) {
            if (role == null || role != 0) {
                writeError(response, 403, "无权访问管理员功能");
                return false;
            }
        }

        return true;
    }

    private void writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code);
        Result<?> result = Result.error(code, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
