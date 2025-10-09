<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="HRMS - Modern Human Resource Management System">
        <meta name="keywords" content="HRMS, human resource management, HR, employee">
        <meta name="author" content="Group4 - SWP391">
        <title>
            <c:choose>
                <c:when test="${not empty param.pageTitle}">${param.pageTitle}</c:when>
                <c:otherwise>HRMS - Human Resource Management System</c:otherwise>
            </c:choose>
        </title>

        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <!-- AOS Animation -->
        <link href="https://unpkg.com/aos@2.3.1/dist/aos.css" rel="stylesheet">

        <style>
            :root {
                --primary-color: #2c5aa0;
                --secondary-color: #f8f9fa;
                --accent-color: #667eea;
                --success-color: #28a745;
                --text-dark: #333;
                --text-light: #666;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                color: var(--text-dark);
            }

            /* Alert Messages */
            .alert {
                border-radius: 10px;
                border: none;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
                margin-bottom: 2rem;
            }

            .alert-success {
                background: linear-gradient(135deg, #d4edda 0%, #c3e6cb 100%);
                color: #155724;
            }

            /* Navigation */
            .navbar {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(10px);
                box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
                padding: 1rem 0;
                transition: all 0.3s ease;
            }

            .navbar.scrolled {
                background: white;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);
            }

            .navbar-brand {
                font-size: 1.8rem;
                font-weight: bold;
                color: var(--primary-color) !important;
            }

            .navbar-nav .nav-link {
                color: var(--text-dark) !important;
                font-weight: 500;
                margin: 0 1rem;
                transition: color 0.3s ease;
                position: relative;
            }

            .navbar-nav .nav-link:hover,
            .navbar-nav .nav-link.active {
                color: var(--primary-color) !important;
            }

            .navbar-nav .nav-link::after {
                content: '';
                position: absolute;
                width: 0;
                height: 2px;
                bottom: -5px;
                left: 50%;
                background: var(--accent-color);
                transition: all 0.3s ease;
            }

            .navbar-nav .nav-link:hover::after,
            .navbar-nav .nav-link.active::after {
                width: 100%;
                left: 0;
            }

            /* Buttons */
            .btn-hero {
                background: white;
                color: var(--primary-color);
                border: 2px solid white;
                border-radius: 50px;
                padding: 15px 40px;
                font-weight: 600;
                font-size: 1.1rem;
                transition: all 0.3s ease;
                margin: 0 10px 10px 0;
            }

            .btn-hero:hover {
                background: transparent;
                color: white;
                transform: translateY(-2px);
                box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
            }

            .btn-hero-outline {
                background: transparent;
                color: white;
                border: 2px solid white;
                border-radius: 50px;
                padding: 15px 40px;
                font-weight: 600;
                font-size: 1.1rem;
                transition: all 0.3s ease;
                margin: 0 10px 10px 0;
            }

            .btn-hero-outline:hover {
                background: white;
                color: var(--primary-color);
                transform: translateY(-2px);
                box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
            }

            /* Sections */
            .section-title {
                text-align: center;
                margin-bottom: 4rem;
            }

            .section-title h2 {
                font-size: 2.5rem;
                font-weight: bold;
                color: var(--primary-color);
                margin-bottom: 1rem;
            }

            .section-title p {
                font-size: 1.2rem;
                color: var(--text-light);
                max-width: 600px;
                margin: 0 auto;
            }

            /* Footer */
            .footer {
                background: var(--text-dark);
                color: white;
                padding: 4rem 0 2rem;
            }

            .footer h5 {
                color: var(--accent-color);
                margin-bottom: 1.5rem;
                font-weight: 600;
            }

            .footer a {
                color: #ccc;
                text-decoration: none;
                transition: color 0.3s ease;
                display: block;
                padding: 0.25rem 0;
            }

            .footer a:hover {
                color: var(--accent-color);
            }

            .footer-bottom {
                border-top: 1px solid #444;
                padding-top: 2rem;
                margin-top: 3rem;
                text-align: center;
                color: #999;
            }

            .social-links a {
                display: inline-block;
                width: 40px;
                height: 40px;
                background: #444;
                border-radius: 50%;
                text-align: center;
                line-height: 40px;
                margin: 0 5px;
                transition: all 0.3s ease;
            }

            .social-links a:hover {
                background: var(--accent-color);
                transform: translateY(-2px);
            }
        </style>

        <!-- Page specific CSS -->
        <c:if test="${not empty param.pageCss}">
            <link href="${pageContext.request.contextPath}/assets/css/${param.pageCss}" rel="stylesheet">
        </c:if>

        <!-- Debug CSS path -->
        <!-- CSS Path: ${pageContext.request.contextPath}/assets/css/${param.pageCss} -->