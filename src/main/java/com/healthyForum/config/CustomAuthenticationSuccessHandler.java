package com.healthyForum.config;

import com.healthyForum.model.User;
import com.healthyForum.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String username = authentication.getName();

        // Tìm user trong database
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra role và trạng thái
        String roleName = user.getRole().getRoleName();

        if ("ADMIN".equals(roleName)) {
            // Nếu là admin, chuyển đến trang admin
            response.sendRedirect("/admin");
        } else if ("USER".equals(roleName)) {
            // Nếu là user, kiểm tra trạng thái suspended
            if (user.isSuspended()) {
                // User bị cấm - logout và redirect với message
                request.getSession().invalidate();
                response.sendRedirect("/login?suspended=true");
                return;
            } else {
                // User hoạt động bình thường - chuyển đến home
                response.sendRedirect("/home");
            }
        } else {
            // Role không hợp lệ
            response.sendRedirect("/login?error=invalid_role");
        }
    }
}