<%@ page contentType="text/html; charset=UTF-8" %>

    <style>
        :root {
            --header-h: 64px;
            --header-gap: 12px;
        }

        .main-content {
            padding-top: calc(var(--header-h) + var(--header-gap));
        }

        .top-navbar {
            position: fixed;
            top: 0;
            left: var(--sidebar-width, 260px);
            right: 0;
            height: var(--header-h);
            z-index: 1060;
            background: #fff;
            padding: 1rem 1.25rem;
            box-shadow: 0 2px 10px rgba(0, 0, 0, .08);
            display: flex;
            align-items: center;
        }

        body.sidebar-collapsed .top-navbar {
            left: 70px;
        }

        @media (max-width:768px) {
            .top-navbar {
                left: 0;
            }
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

        /* đẩy dropdown sang phải vì đã bỏ tiêu đề */
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

        <!-- (đÃ BỎ tiêu đề Dashboard ở giữa) -->

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
                    <li><a class="dropdown-item" href="${pageContext.request.contextPath}/my-account">
                            <i class="fas fa-user-circle"></i> My Account</a></li>
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

    <script>
        // đo chiều cao header thực tế để đệm nội dung
        (function () {
            function setHeaderHeight() {
                var header = document.querySelector('.top-navbar');
                if (!header) return;
                document.documentElement.style.setProperty('--header-h', header.offsetHeight + 'px');
            }
            document.addEventListener('DOMContentLoaded', setHeaderHeight);
            var t; window.addEventListener('resize', function () {
                clearTimeout(t); t = setTimeout(setHeaderHeight, 100);
            });
        })();
    </script>