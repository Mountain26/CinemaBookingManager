package finalproject_cinemabooking.config;

import finalproject_cinemabooking.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Set<String> USER_PAGES = Set.of("/home", "/showtimes", "/seats", "/checkout", "/profile");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);
        Long userId = session == null ? null : (Long) session.getAttribute("USER_ID");
        User.Role role = session == null ? null : (User.Role) session.getAttribute("ROLE");

        if (uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/webjars/")
                || uri.endsWith(".css")
                || uri.endsWith(".js")
                || uri.endsWith(".png")
                || uri.endsWith(".jpg")
                || uri.endsWith(".jpeg")
                || uri.endsWith(".svg")
                || uri.endsWith(".ico")
                || uri.equals("/favicon.ico")) {
            return true;
        }

        if (uri.equals("/") || uri.startsWith("/login") || uri.startsWith("/register") || uri.startsWith("/api/auth")) {
            return true;
        }

        if (userId == null) {
            response.sendRedirect("/login");
            return false;
        }

        boolean isAdminPage = uri.startsWith("/admin") || uri.startsWith("/api/admin");
        boolean isStaffPage = uri.startsWith("/staff");
        boolean isUserPage = USER_PAGES.stream().anyMatch(uri::startsWith);

        if (isAdminPage && role != User.Role.ADMIN) {
            String redirectTarget = User.Role.STAFF.equals(role) ? "/staff" : "/home";
            response.sendRedirect(redirectTarget + "?error=access_denied");
            return false;
        }

        if (isStaffPage && role != User.Role.STAFF) {
            response.sendRedirect("/home?error=access_denied");
            return false;
        }

        if (role == User.Role.STAFF && isUserPage) {
            response.sendRedirect("/staff?error=access_denied");
            return false;
        }

        if (role == User.Role.CUSTOMER && (isAdminPage || isStaffPage)) {
            response.sendRedirect("/home?error=access_denied");
            return false;
        }

        return true;
    }
}
