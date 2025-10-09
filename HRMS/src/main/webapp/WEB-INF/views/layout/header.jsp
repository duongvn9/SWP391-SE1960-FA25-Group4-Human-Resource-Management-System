<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!-- Navigation -->
        <nav class="navbar navbar-expand-lg navbar-light fixed-top">
            <div class="container">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/">
                    <i class="fas fa-users-cog me-2"></i>HRMS
                </a>

                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>

                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav ms-auto">
                        <li class="nav-item">
                            <a class="nav-link ${param.currentPage == 'home' ? 'active' : ''}"
                                href="${pageContext.request.contextPath}/">Home</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="#features">Features</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${param.currentPage == 'about' ? 'active' : ''}"
                                href="${pageContext.request.contextPath}/about">About</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${param.currentPage == 'contact' ? 'active' : ''}"
                                href="${pageContext.request.contextPath}/contact">Contact</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link ${param.currentPage == 'faqs' ? 'active' : ''}"
                                href="${pageContext.request.contextPath}/faqs">FAQs</a>
                        </li>

                        <!-- Display menu based on login status -->
                        <c:choose>
                            <c:when test="${not empty sessionScope.userId}">
                                <!-- User logged in -->
                                <li class="nav-item dropdown">
                                    <a class="nav-link dropdown-toggle" href="#" role="button"
                                        data-bs-toggle="dropdown">
                                        <i class="fas fa-user me-1"></i>${sessionScope.userFullName}
                                    </a>
                                    <ul class="dropdown-menu">
                                        <li><a class="dropdown-item"
                                                href="${pageContext.request.contextPath}/dashboard">
                                                <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                                            </a></li>
                                        <li>
                                            <hr class="dropdown-divider">
                                        </li>
                                        <li><a class="dropdown-item"
                                                href="${pageContext.request.contextPath}/auth/logout">
                                                <i class="fas fa-sign-out-alt me-2"></i>Logout
                                            </a></li>
                                    </ul>
                                </li>
                            </c:when>
                            <c:otherwise>
                                <!-- User not logged in -->
                                <li class="nav-item">
                                    <a class="nav-link ${currentPage == 'login' ? 'active' : ''}"
                                        href="${pageContext.request.contextPath}/login">
                                        <i class="fas fa-sign-in-alt me-1"></i>Login
                                    </a>
                                </li>
                            </c:otherwise>
                        </c:choose>
                    </ul>
                </div>
            </div>
        </nav>