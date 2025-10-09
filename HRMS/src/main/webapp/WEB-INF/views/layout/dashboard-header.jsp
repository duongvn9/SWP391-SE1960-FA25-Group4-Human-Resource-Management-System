<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!-- Top Navigation for Dashboard -->
<div class="top-navbar">
    <button class="toggle-sidebar" id="toggle-sidebar">
        <i class="fas fa-bars"></i>
    </button>

    <div class="user-info">
        <div class="user-avatar">
            <i class="fas fa-user"></i>
        </div>
        <div class="dropdown">
            <button class="btn btn-link dropdown-toggle text-decoration-none" type="button"
                    data-bs-toggle="dropdown">
                ${sessionScope.userFullName != null ? sessionScope.userFullName : 'User'}
            </button>
            <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user-profile">
                        <i class="fas fa-user me-2"></i>Profile
                    </a></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/contracts">
                        <i class="bi bi-file-earmark-text"></i> 📝Employment Contract
                    </a></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/settings">
                        <i class="fas fa-cog me-2"></i>Settings
                    </a></li>
                <li>
                    <hr class="dropdown-divider">
                </li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/">
                        <i class="fas fa-home me-2"></i>Back to Home
                    </a></li>
                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                        <i class="fas fa-sign-out-alt me-2"></i>Logout
                    </a></li>
            </ul>
        </div>
    </div>
</div>