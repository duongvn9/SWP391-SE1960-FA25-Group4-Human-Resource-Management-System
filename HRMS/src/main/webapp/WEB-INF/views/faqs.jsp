<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">

    <head>
        <jsp:include page="layout/head.jsp">
            <jsp:param name="pageTitle" value="FAQS - HRMS" />
            <jsp:param name="pageCss" value="faqs.css" />
        </jsp:include>
    </head>

    <body>
        <!-- Header -->
        <jsp:include page="layout/header.jsp">
            <jsp:param name="currentPage" value="faqs" />
        </jsp:include>

        <!-- Hero Section -->
        <section class="hero-section">
            <div class="container">
                <h1>Frequently Asked Questions</h1>
                <p>Find answers to popular questions about HRMS</p>
            </div>
        </section>

        <!-- Search Section -->
        <section class="search-section">
            <div class="container">
                <div class="search-box">
                    <input type="text" id="faqSearch" placeholder="Search questions...">
                    <button class="search-btn">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </div>
        </section>

        <!-- FAQ Categories -->
        <section class="faq-section">
            <div class="container">
                <div class="faq-categories text-center">
                    <a href="#" class="category-btn active" data-category="all">All</a>
                    <a href="#" class="category-btn" data-category="general">General</a>
                    <a href="#" class="category-btn" data-category="employee">Employees</a>
                    <a href="#" class="category-btn" data-category="attendance">Attendance</a>
                    <a href="#" class="category-btn" data-category="payroll">Payroll</a>
                    <a href="#" class="category-btn" data-category="leave">Leave</a>
                    <a href="#" class="category-btn" data-category="technical">Technical</a>
                </div>

                <!-- FAQ Accordion -->
                <div class="faq-accordion">
                    <div class="accordion" id="faqAccordion">

                        <!-- General FAQs -->
                        <div class="accordion-item faq-item" data-category="general">
                            <div class="accordion-header" id="faq1">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse1">
                                    What is HRMS?
                                </button>
                            </div>
                            <div id="collapse1" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    HRMS (Human Resource Management System) is an integrated system that digitizes
                                    and automates HR processes such as employee information, attendance,
                                    payroll, leave management, and recruitment.
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item faq-item" data-category="general">
                            <div class="accordion-header" id="faq2">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse2">
                                    Who can use HRMS?
                                </button>
                            </div>
                            <div id="collapse2" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    HRMS suits businesses of all sizes, including HR Managers, line managers,
                                    and employees. Each role has appropriate permissions and features
                                    aligned with their work.
                                </div>
                            </div>
                        </div>

                        <!-- Employee Management FAQs -->
                        <div class="accordion-item faq-item" data-category="employee">
                            <div class="accordion-header" id="faq3">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse3">
                                    How do I add a new employee to the system?
                                </button>
                            </div>
                            <div id="collapse3" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    HR Managers can add new employees via "Employee Management" → "Add New Employee".
                                    Fill in personal details, job information, and create a login account.
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item faq-item" data-category="employee">
                            <div class="accordion-header" id="faq4">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse4">
                                    Can employees update their own personal information?
                                </button>
                            </div>
                            <div id="collapse4" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Yes. Employees can update some personal information such as address, phone number,
                                    and emergency contact on the "Profile" page. Other sensitive data requires HR approval.
                                </div>
                            </div>
                        </div>

                        <!-- Attendance FAQs -->
                        <div class="accordion-item faq-item" data-category="attendance">
                            <div class="accordion-header" id="faq5">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse5">
                                    How does timekeeping work in the system?
                                </button>
                            </div>
                            <div id="collapse5" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    The system supports multiple methods: web-based check-in/out, integration with
                                    time attendance devices, and a mobile app. Data can be recorded directly
                                    or synchronized from devices.
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item faq-item" data-category="attendance">
                            <div class="accordion-header" id="faq6">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse6">
                                    What if an employee forgets to check in/out?
                                </button>
                            </div>
                            <div id="collapse6" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Employees can submit a time adjustment request. Line managers or HR
                                    will review and approve according to company policy.
                                </div>
                            </div>
                        </div>

                        <!-- Payroll FAQs -->
                        <div class="accordion-item faq-item" data-category="payroll">
                            <div class="accordion-header" id="faq7">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse7">
                                    How is payroll calculated?
                                </button>
                            </div>
                            <div id="collapse7" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Payroll is automatically calculated from attendance data, base salary,
                                    allowances, bonuses, and deductions. Formulas are configurable
                                    per company policy.
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item faq-item" data-category="payroll">
                            <div class="accordion-header" id="faq8">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse8">
                                    Can employees view their payslips?
                                </button>
                            </div>
                            <div id="collapse8" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Yes. Employees can view detailed payroll, history, and download payslips
                                    in the "Compensation & Benefits" section of their account.
                                </div>
                            </div>
                        </div>

                        <!-- Leave Management FAQs -->
                        <div class="accordion-item faq-item" data-category="leave">
                            <div class="accordion-header" id="faq9">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse9">
                                    How do I request leave in the system?
                                </button>
                            </div>
                            <div id="collapse9" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Go to "Leave" → "Create New Request", choose a leave type,
                                    select the period and reason. The request will be sent to your line manager for approval.
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item faq-item" data-category="leave">
                            <div class="accordion-header" id="faq10">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse10">
                                    How can I check my remaining leave balance?
                                </button>
                            </div>
                            <div id="collapse10" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Leave balance is shown on your personal dashboard and the "Leave" page.
                                    The system updates it automatically after each approved leave.
                                </div>
                            </div>
                        </div>

                        <!-- Technical FAQs -->
                        <div class="accordion-item faq-item" data-category="technical">
                            <div class="accordion-header" id="faq11">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse11">
                                    What should I do if I forget my password?
                                </button>
                            </div>
                            <div id="collapse11" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Use the "Forgot Password" feature on the login page or contact IT/HR
                                    to get support with a reset.
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item faq-item" data-category="technical">
                            <div class="accordion-header" id="faq12">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse12">
                                    Is the system supported on mobile devices?
                                </button>
                            </div>
                            <div id="collapse12" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    Yes. The system is responsive across devices. We are also developing
                                    a dedicated mobile app for an even better experience.
                                </div>
                            </div>
                        </div>

                        <div class="accordion-item faq-item" data-category="technical">
                            <div class="accordion-header" id="faq13">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
                                        data-bs-target="#collapse13">
                                    Is data in the system secure?
                                </button>
                            </div>
                            <div id="collapse13" class="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                                <div class="accordion-body">
                                    We apply advanced security measures, including data encryption,
                                    multi-factor authentication, granular authorization, and regular backups
                                    to ensure information safety.
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- No Results Message -->
                    <div class="no-results">
                        <i class="fas fa-search fa-3x mb-3" style="color: #ccc;"></i>
                        <h5>No matching questions found</h5>
                        <p>Try another keyword or <a href="${pageContext.request.contextPath}/contact">contact us</a> for support.</p>
                    </div>
                </div>
            </div>
        </section>

  

        <!-- Footer -->
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />

        <!-- FAQ JavaScript -->
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const searchInput = document.getElementById('faqSearch');
                const categoryBtns = document.querySelectorAll('.category-btn');
                const faqItems = document.querySelectorAll('.faq-item');
                const noResults = document.querySelector('.no-results');

                // Search functionality
                searchInput.addEventListener('input', function () {
                    const searchTerm = this.value.toLowerCase();
                    let hasResults = false;

                    faqItems.forEach(item => {
                        const question = item.querySelector('.accordion-button').textContent.toLowerCase();
                        const answer = item.querySelector('.accordion-body').textContent.toLowerCase();

                        if (question.includes(searchTerm) || answer.includes(searchTerm)) {
                            item.style.display = 'block';
                            item.classList.remove('hidden');
                            hasResults = true;
                        } else {
                            item.style.display = 'none';
                            item.classList.add('hidden');
                        }
                    });

                    noResults.style.display = hasResults ? 'none' : 'block';
                });

                // Category filtering
                categoryBtns.forEach(btn => {
                    btn.addEventListener('click', function (e) {
                        e.preventDefault();

                        // Update active button
                        categoryBtns.forEach(b => b.classList.remove('active'));
                        this.classList.add('active');

                        const category = this.dataset.category;
                        let hasResults = false;

                        faqItems.forEach(item => {
                            if (category === 'all' || item.dataset.category === category) {
                                item.style.display = 'block';
                                item.classList.remove('hidden');
                                hasResults = true;
                            } else {
                                item.style.display = 'none';
                                item.classList.add('hidden');
                            }
                        });

                        // Clear search when filtering by category
                        searchInput.value = '';
                        noResults.style.display = hasResults ? 'none' : 'block';
                    });
                });
            });
        </script>
    </body>

</html>
