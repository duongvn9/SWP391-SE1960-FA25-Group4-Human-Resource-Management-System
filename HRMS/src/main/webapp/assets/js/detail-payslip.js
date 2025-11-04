function exportToPDF() {
    // Hide no-print elements
    const noPrintElements = document.querySelectorAll('.no-print');
    noPrintElements.forEach(el => el.style.display = 'none');

    // Print the page
    window.print();

    // Show no-print elements again
    setTimeout(() => {
        noPrintElements.forEach(el => el.style.display = '');
    }, 1000);
}

function filterByPayPeriod() {
    const selectedPeriod = document.getElementById('payPeriodFilter').value;
    if (selectedPeriod) {
        // Redirect to payslip detail with selected period
        window.location.href = `${window.location.pathname}?periodId=${selectedPeriod}`;
    } else {
        // Redirect to payslip list if no period selected
        window.location.href = window.location.pathname.replace('/detail', '/list');
    }
}

// Add animation on page load
document.addEventListener('DOMContentLoaded', function () {
    const cards = document.querySelectorAll('.payslip-card');
    cards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';

        setTimeout(() => {
            card.style.transition = 'all 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });
});

// Sidebar toggle functionality
document.addEventListener('DOMContentLoaded', function () {
    // Sidebar toggle
    const sidebarToggle = document.querySelector('.sidebar-toggle');
    const body = document.body;

    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function (e) {
            e.preventDefault();
            body.classList.toggle('sidebar-collapsed');

            // Save state to localStorage
            const isCollapsed = body.classList.contains('sidebar-collapsed');
            localStorage.setItem('sidebarCollapsed', isCollapsed);
        });
    }

    // Restore sidebar state from localStorage
    const savedState = localStorage.getItem('sidebarCollapsed');
    if (savedState === 'true') {
        body.classList.add('sidebar-collapsed');
    }

    // Header dropdown functionality
    const dropdowns = document.querySelectorAll('.dropdown');

    dropdowns.forEach(dropdown => {
        const toggle = dropdown.querySelector('.dropdown-toggle');
        const menu = dropdown.querySelector('.dropdown-menu');

        if (!toggle || !menu) return;

        toggle.addEventListener('click', function (e) {
            e.preventDefault();
            e.stopPropagation();

            // Close other dropdowns
            document.querySelectorAll('.dropdown-menu').forEach(otherMenu => {
                if (otherMenu !== menu && otherMenu.classList.contains('show')) {
                    otherMenu.classList.remove('show');
                }
            });

            // Toggle current dropdown
            menu.classList.toggle('show');
        });
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', function (e) {
        if (!e.target.closest('.dropdown')) {
            document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                menu.classList.remove('show');
            });
        }
    });
});