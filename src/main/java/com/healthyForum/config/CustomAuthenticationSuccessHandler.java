package com.healthyForum.config;

import com.healthyForum.model.User;
import com.healthyForum.model.UserAccount;
import com.healthyForum.repository.UserRepository;
import com.healthyForum.repository.UserAccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        String username = authentication.getName();
        
        // Try to find by username first
        UserAccount account = userAccountRepository.findByUsername(username).orElse(null);
        if (account == null) {
            // Try to find by email
            User user = userRepository.findByEmail(username).orElse(null);
            if (user != null) {
                account = userAccountRepository.findByUserId(user.getId()).orElse(null);
            }
        }
        
        if (account != null) {
            // Update last login
            account.updateLastLogin();
            userAccountRepository.save(account);
        }
        
        // Kiểm tra role và trạng thái
        String roleName = account.getUser().getRole().getRoleName();
        if ("ADMIN".equals(roleName)) {
            // Nếu là admin, chuyển đến trang admin
            response.sendRedirect("/admin");
        } else if ("USER".equals(roleName)) {
            // Nếu là user, kiểm tra trạng thái suspended
            if (account.isSuspended()) {
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