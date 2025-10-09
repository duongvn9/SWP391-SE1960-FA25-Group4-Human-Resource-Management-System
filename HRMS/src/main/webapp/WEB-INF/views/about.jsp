<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>About Us - HRMS</title>

    <!-- Common Styles -->
    <jsp:include page="/WEB-INF/views/layout/head.jsp" />

    <style>
        :root {
            --primary-color: #2c5aa0;
            --secondary-color: #f8f9fa;
            --accent-color: #667eea;
            --success-color: #28a745;
            --text-dark: #333;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: var(--text-dark);
        }

        /* Header */
        .navbar {
            background: white;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 1rem 0;
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
        }

        .navbar-nav .nav-link:hover,
        .navbar-nav .nav-link.active {
            color: var(--primary-color) !important;
        }

        /* Hero Section */
        .hero-section {
            background: linear-gradient(135deg, var(--primary-color) 0%, var(--accent-color) 100%);
            color: white;
            padding: 100px 0 80px;
            text-align: center;
        }

        .hero-section h1 {
            font-size: 3rem;
            font-weight: bold;
            margin-bottom: 1rem;
        }

        .hero-section p {
            font-size: 1.2rem;
            opacity: 0.9;
        }

        /* Breadcrumb */
        .breadcrumb-section {
            background: var(--secondary-color);
            padding: 1rem 0;
        }

        .breadcrumb {
            background: none;
            margin: 0;
        }

        .breadcrumb-item a {
            color: var(--primary-color);
            text-decoration: none;
        }

        /* Features Section */
        .features-section {
            padding: 80px 0;
        }

        .feature-box {
            text-align: center;
            padding: 2rem;
            border-radius: 10px;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
            height: 100%;
        }

        .feature-box:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
        }

        .feature-icon {
            width: 80px;
            height: 80px;
            background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0 auto 1.5rem;
            color: white;
            font-size: 2rem;
        }

        .feature-box h4 {
            color: var(--primary-color);
            margin-bottom: 1rem;
            font-weight: 600;
        }

        /* Story Section */
        .story-section {
            background: var(--secondary-color);
            padding: 80px 0;
        }

        .story-content h2 {
            color: var(--primary-color);
            font-weight: bold;
            margin-bottom: 1rem;
        }

        .story-content h5 {
            color: var(--accent-color);
            margin-bottom: 1.5rem;
        }

        .story-content p {
            margin-bottom: 1.5rem;
            text-align: justify;
        }

        .story-image {
            border-radius: 15px;
            overflow: hidden;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
            position: relative;
            max-width: 800px;
            margin: 0 auto;
        }

        .story-image img {
            width: 100%;
            height: auto;
            object-fit: cover;
            background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
            background-image: url('https://images.unsplash.com/photo-1522071820081-009f0129c71c?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&h=500&q=80');
            background-size: cover;
            background-position: center;
            max-height: none;
            min-height: 400px;
            display: block;
        }

        .story-content p {
            margin-bottom: 1.5rem;
            font-size: 1.1rem;
            line-height: 1.8;
        }

        .story-content h2 {
            margin-bottom: 1rem;
            font-weight: bold;
        }

        .story-content h5 {
            margin-bottom: 2rem;
        }

        /* Values Section */
        .values-section {
            padding: 80px 0;
        }

        .section-title {
            text-align: center;
            margin-bottom: 3rem;
        }

        .section-title h2 {
            color: var(--primary-color);
            font-weight: bold;
            margin-bottom: 1rem;
        }

        .section-title p {
            font-size: 1.1rem;
            color: #666;
        }

        .value-item {
            background: white;
            border-radius: 10px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
            transition: transform 0.3s ease;
        }

        .value-item:hover {
            transform: translateY(-5px);
        }

        .value-item .fas {
            color: var(--accent-color);
            font-size: 2.5rem;
            margin-bottom: 1rem;
        }

        .value-item h4 {
            color: var(--primary-color);
            margin-bottom: 1rem;
        }

        /* Footer */
        .footer {
            background: var(--text-dark);
            color: white;
            padding: 3rem 0 1rem;
            margin-top: 5rem;
        }

        .footer h5 {
            color: var(--accent-color);
            margin-bottom: 1rem;
        }

        .footer a {
            color: #ccc;
            text-decoration: none;
            transition: color 0.3s ease;
        }

        .footer a:hover {
            color: var(--accent-color);
        }

        .footer-bottom {
            border-top: 1px solid #444;
            padding-top: 1rem;
            margin-top: 2rem;
            text-align: center;
            color: #999;
        }
    </style>
</head>

<body>
    <!-- Navigation -->
    <jsp:include page="/WEB-INF/views/layout/header.jsp" />

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="container">
            <h1>About Us</h1>
            <p>Explore HRMS's story and vision</p>
        </div>
    </section>

    <!-- Breadcrumb -->
    <section class="breadcrumb-section">
        <div class="container">
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/">Home</a>
                    </li>
                    <li class="breadcrumb-item active">About Us</li>
                </ol>
            </nav>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
        <div class="container">
            <div class="row">
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="feature-box">
                        <div class="feature-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <h4>Human Resource Management</h4>
                        <p>A comprehensive employee information management system packed with modern, convenient features.</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="feature-box">
                        <div class="feature-icon">
                            <i class="fas fa-clock"></i>
                        </div>
                        <h4>Smart Timekeeping</h4>
                        <p>Accurately track working hours with advanced timekeeping technology and detailed reports.</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="feature-box">
                        <div class="feature-icon">
                            <i class="fas fa-money-bill-wave"></i>
                        </div>
                        <h4>Payroll Management</h4>
                        <p>Automatically calculate salaries, bonuses, and allowances with flexible, transparent formulas.</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="feature-box">
                        <div class="feature-icon">
                            <i class="fas fa-chart-bar"></i>
                        </div>
                        <h4>Analytics & Reports</h4>
                        <p>An intuitive dashboard with in-depth analytical reports to support management decisions.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Story Section -->
    <section class="story-section">
        <div class="container-fluid">
            <div class="row">
                <div class="col-12">
                    <div class="text-center mb-5">
                        <h2 style="color: var(--primary-color); font-weight: bold;">Our Story</h2>
                        <h5 style="color: var(--accent-color);">It began with a simple idea</h5>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-12">
                    <div class="story-image mb-4">
                        <img src="${pageContext.request.contextPath}/assets/images/Company.jpg" alt="HRMS">
                    </div>
                </div>
            </div>
            <div class="row justify-content-center">
                <div class="col-lg-8">
                    <div class="story-content text-center">
                        <p>
                            HRMS was born from the real needs of businesses to manage their workforce efficiently.
                            We noticed that many companies still use manual methods or outdated systems,
                            making it difficult to track and manage employee information.
                        </p>
                        <p>
                            With a young and passionate development team, we researched and built
                            a modern, user-friendly HR management system tailored for Vietnamese businesses.
                        </p>
                        <p>
                            Our journey started in the academic environment at FPT University,
                            where we learned and applied cutting-edge technologies to create a product with real impact.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Values Section -->
    <section class="values-section">
        <div class="container">
            <div class="section-title">
                <h2>Core Values</h2>
                <p>The principles that guide everything we do</p>
            </div>

            <div class="row">
                <div class="col-lg-4 col-md-6">
                    <div class="value-item">
                        <i class="fas fa-lightbulb"></i>
                        <h4>Innovation</h4>
                        <p>We constantly seek new technologies to improve user experience and increase management efficiency.</p>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6">
                    <div class="value-item">
                        <i class="fas fa-shield-alt"></i>
                        <h4>Security & Privacy</h4>
                        <p>HR data is invaluable. We are committed to protecting it with the highest security standards.</p>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6">
                    <div class="value-item">
                        <i class="fas fa-handshake"></i>
                        <h4>Dedicated Support</h4>
                        <p>Our professional support team is ready to help 24/7 with enthusiasm and care.</p>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6">
                    <div class="value-item">
                        <i class="fas fa-cogs"></i>
                        <h4>Flexible Customization</h4>
                        <p>Every business is unique. We design the system to be customized for specific requirements.</p>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6">
                    <div class="value-item">
                        <i class="fas fa-rocket"></i>
                        <h4>High Performance</h4>
                        <p>Modern technologies ensure the system is fast, stable, and reliable.</p>
                    </div>
                </div>
                <div class="col-lg-4 col-md-6">
                    <div class="value-item">
                        <i class="fas fa-graduation-cap"></i>
                        <h4>Continuous Learning</h4>
                        <p>We keep learning and updating our knowledge to deliver the best solutions to customers.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>

</html>
