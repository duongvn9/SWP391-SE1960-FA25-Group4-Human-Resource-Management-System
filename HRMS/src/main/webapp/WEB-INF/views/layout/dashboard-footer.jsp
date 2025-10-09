<%@ page contentType="text/html; charset=UTF-8" %>
    <%@ taglib uri="jakarta.tags.core" prefix="c" %>

        <!-- Dashboard Footer -->
        <div class="dashboard-footer">
            <p><strong>HRMS - Human Resource Management System</strong></p>
            <p class="text-muted">Copyright © 2024 Group 4. All rights reserved.</p>
            <p class="text-muted">Last updated: <span id="lastUpdate"></span></p>
        </div>

        <!-- Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <!-- Chart.js -->
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

        <script>
            // Dashboard specific JavaScript
            document.addEventListener('DOMContentLoaded', function () {
                // Sidebar toggle functionality
                const toggleBtn = document.getElementById('toggle-sidebar');
                const sidebar = document.getElementById('sidebar');
                const mainContent = document.querySelector('.main-content');

                if (toggleBtn && sidebar && mainContent) {
                    toggleBtn.addEventListener('click', function () {
                        sidebar.classList.toggle('collapsed');
                        mainContent.classList.toggle('expanded');
                    });
                }

                // Sidebar dropdown toggle with improved animation
                document.querySelectorAll('.sidebar-dropdown-toggle').forEach(function (toggle) {
                    toggle.addEventListener('click', function (e) {
                        e.preventDefault();
                        e.stopPropagation();

                        const targetId = this.getAttribute('data-target');
                        const submenu = document.getElementById(targetId);
                        const arrow = this.querySelector('.dropdown-arrow');

                        if (submenu) {
                            const isCurrentlyOpen = submenu.classList.contains('show');

                            // Close all other submenus first
                            document.querySelectorAll('.sidebar-submenu').forEach(function (menu) {
                                if (menu !== submenu) {
                                    menu.classList.remove('show');
                                }
                            });
                            document.querySelectorAll('.sidebar-dropdown-toggle').forEach(function (otherToggle) {
                                if (otherToggle !== toggle) {
                                    otherToggle.setAttribute('aria-expanded', 'false');
                                    const otherArrow = otherToggle.querySelector('.dropdown-arrow');
                                    if (otherArrow) {
                                        otherArrow.style.transform = 'rotate(0deg)';
                                    }
                                }
                            });

                            // Toggle current submenu
                            if (isCurrentlyOpen) {
                                submenu.classList.remove('show');
                                this.setAttribute('aria-expanded', 'false');
                                if (arrow) arrow.style.transform = 'rotate(0deg)';
                            } else {
                                submenu.classList.add('show');
                                this.setAttribute('aria-expanded', 'true');
                                if (arrow) arrow.style.transform = 'rotate(90deg)';
                            }
                        }
                    });
                });

                // Mobile sidebar toggle
                if (window.innerWidth <= 768) {
                    if (toggleBtn && sidebar) {
                        toggleBtn.addEventListener('click', function () {
                            sidebar.classList.toggle('show');
                        });
                    }
                }

                // Counter animation
                const counters = document.querySelectorAll('.counter');
                counters.forEach(counter => {
                    const target = parseInt(counter.textContent);
                    const increment = target / 50;
                    let current = 0;

                    const timer = setInterval(() => {
                        current += increment;
                        if (current >= target) {
                            current = target;
                            clearInterval(timer);
                        }
                        counter.textContent = Math.floor(current);
                    }, 30);
                });

                // Update last update time
                function updateLastUpdateTime() {
                    const now = new Date();
                    const day = String(now.getDate()).padStart(2, '0');
                    const month = String(now.getMonth() + 1).padStart(2, '0');
                    const year = now.getFullYear();
                    const hours = String(now.getHours()).padStart(2, '0');
                    const minutes = String(now.getMinutes()).padStart(2, '0');

                    const formattedTime = `${day}/${month}/${year} - ${hours}:${minutes}`;
                    const lastUpdateElement = document.getElementById('lastUpdate');
                    if (lastUpdateElement) {
                        lastUpdateElement.textContent = formattedTime;
                    }
                }

                // Update time every minute
                updateLastUpdateTime();
                setInterval(updateLastUpdateTime, 60000);
            });

            // Attendance Chart - sẽ được khởi tạo sau khi DOM loaded
            function initAttendanceChart() {
                const ctx = document.getElementById('attendanceChart');
                if (ctx) {
                    new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
                            datasets: [{
                                label: 'Có mặt',
                                data: [142, 148, 156, 151, 147, 98, 45],
                                borderColor: '#2c5aa0',
                                backgroundColor: 'rgba(44, 90, 160, 0.1)',
                                tension: 0.4,
                                fill: true
                            }, {
                                label: 'Vắng mặt',
                                data: [14, 8, 0, 5, 9, 12, 8],
                                borderColor: '#dc3545',
                                backgroundColor: 'rgba(220, 53, 69, 0.1)',
                                tension: 0.4,
                                fill: true
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            scales: {
                                y: {
                                    beginAtZero: true
                                }
                            },
                            plugins: {
                                legend: {
                                    position: 'top'
                                }
                            }
                        }
                    });
                }
            }

            // Initialize chart after page load
            window.addEventListener('load', function () {
                initAttendanceChart();
            });
        </script>