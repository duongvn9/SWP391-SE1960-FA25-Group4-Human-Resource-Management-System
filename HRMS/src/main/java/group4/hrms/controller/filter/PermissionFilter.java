package group4.hrms.controller.filter;

import group4.hrms.util.PermissionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Filter to set permission flags in request attributes for JSP
 */
@WebFilter(filterName = "PermissionFilter", urlPatterns = { "/*" })
public class PermissionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PermissionFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            logger.debug("PermissionFilter processing: {}", httpRequest.getRequestURI());

            // Set permission flags as request attributes
            request.setAttribute("canViewUserList", PermissionUtil.canViewUserList(httpRequest));
            request.setAttribute("canViewAccountList", PermissionUtil.canViewAccountList(httpRequest));
            request.setAttribute("canCreateUser", PermissionUtil.canCreateUser(httpRequest));
            request.setAttribute("canCreateAccount", PermissionUtil.canCreateAccount(httpRequest));
            request.setAttribute("canResetPassword", PermissionUtil.canResetPassword(httpRequest));
            request.setAttribute("canViewAllUsers", PermissionUtil.canViewAllUsers(httpRequest));

            // Set position code
            String positionCode = PermissionUtil.getCurrentUserPositionCode(httpRequest);
            request.setAttribute("userPositionCode", positionCode);
            request.setAttribute("isAdminPosition", "ADMIN".equals(positionCode));
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("PermissionFilter initialized");
    }

    @Override
    public void destroy() {
        logger.info("PermissionFilter destroyed");
    }
}
