<%@ page contentType="text/html;charset=UTF-8" language="java" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <jsp:include page="layout/head.jsp">
                <jsp:param name="pageTitle" value="About us - HRMS" />
                <jsp:param name="pageCss" value="about.css" />
            </jsp:include>
        </head>
        
        
        
        <style>     
            /* Stats Section */
        .stats-section { background: var(--secondary-color); padding: 60px 0; }
        .stat-item { text-align: center; padding: 2rem; }
        .stat-icon {
            width: 80px; height: 80px;
            background: linear-gradient(135deg, var(--primary-color), var(--accent-color));
            border-radius: 50%; display: flex; align-items: center; justify-content: center;
            color: white; font-size: 2rem; margin: 0 auto 1rem;
        }
        .stat-number { font-size: 2.5rem; font-weight: bold; color: var(--primary-color); margin-bottom: .5rem; }
        .stat-label { color: #666; font-size: 1.1rem; }
            </style>
            
            
            
            
        <body>
            <!-- Header -->
            <jsp:include page="layout/header.jsp">
                <jsp:param name="currentPage" value="about" />
            </jsp:include>

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
