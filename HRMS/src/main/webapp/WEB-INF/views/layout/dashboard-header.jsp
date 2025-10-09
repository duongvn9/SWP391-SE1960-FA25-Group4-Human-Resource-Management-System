<%@ page contentType="text/html; charset=UTF-8" %>
<<<<<<< HEAD

    <style>
        /* Vars cục bộ cho header */
        :root {
            --header-h: 64px;
            /* chiều cao header */
        }

        /* đẩy nội dung xuống bằng đúng chiều cao header */
        .main-content {
            padding-top: var(--header-h);
        }

        /* Header fixed, luôn dính trên cùng */
        .top-navbar {
            position: fixed;
            top: 0;
            left: var(--sidebar-width, 260px);
            /* canh theo sidebar */
            right: 0;
            height: var(--header-h);
            z-index: 1060;

            background: #fff;
            padding: 1rem 1.25rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, .08);
            display: flex;
            align-items: center;
            gap: .75rem;
        }

        /* Khi sidebar thu gọn (desktop) */
        body.sidebar-collapsed .top-navbar {
            left: 70px;
        }

        /* Mobile: header sát mép trái, sidebar off-canvas */
        @media (max-width:768px) {
            .top-navbar {
                left: 0;
            }

            .main-content {
                padding-top: var(--header-h);
            }
        }

        /* Trang trí */
        .page-title {
            margin: 0;
            font-weight: 600;
            font-size: 1.1rem;
        }

        .toggle-sidebar {
            background: none;
            border: 0;
            font-size: 1.2rem;
            cursor: pointer;
            color: var(--primary-color, #4f46e5);
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }

        .nav-right {
            margin-left: auto;
            display: flex;
            align-items: center;
            gap: .5rem;
        }

        .user-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: var(--primary-color, #4f46e5);
            color: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .top-navbar .dropdown-menu {
            min-width: 220px;
        }

        .dropdown-toggle {
            padding: .25rem .5rem;
            border-radius: .5rem;
        }

        .dropdown-toggle:hover {
            background: rgba(79, 70, 229, .08);
        }

        .dropdown-menu .dropdown-item {
            display: flex;
            align-items: center;
            gap: .5rem;
        }
    </style>

    <header class="top-navbar" role="banner">
        <button class="toggle-sidebar" type="button" aria-label="Toggle sidebar">
            <i class="fas fa-bars"></i>
        </button>

        <h1 class="page-title">
            <i class="fa-solid fa-table-columns me-2"></i> Dashboard
        </h1>

        <div class="nav-right">
            <div class="dropdown">
                <a href="#" class="nav-link dropdown-toggle d-inline-flex align-items-center gap-2" role="button"
                    data-bs-toggle="dropdown" data-bs-auto-close="outside" aria-expanded="false">
                    <div class="user-avatar"><i class="fas fa-user"></i></div>
                    <span class="d-none d-md-inline fw-semibold">
                        ${sessionScope.userFullName != null ? sessionScope.userFullName : 'User'}
                    </span>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow">
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/user-profile">
                            <i class="fas fa-user"></i> Profile</a></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/contracts">
                            <i class="fas fa-file-alt"></i> Employment Contract</a></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/settings">
                            <i class="fas fa-cog"></i> Settings</a></li>
                    <li>
                        <hr class="dropdown-divider">
                    </li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/">
                            <i class="fas fa-home"></i> Back to Home</a></li>
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout">
                            <i class="fas fa-sign-out-alt"></i> Logout</a></li>
                </ul>
            </div>
        </div>
    </header>   
=======
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
>>>>>>> d34c3fa115d309b2aba93cde0c1728777b327d74
