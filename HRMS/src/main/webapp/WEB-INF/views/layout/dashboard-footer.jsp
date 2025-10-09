<%@ page contentType="text/html; charset=UTF-8" %>

    <style>
        .dashboard-footer {
            background: #fff;
            border-top: 1px solid #e9ecef;
            padding: 1.25rem 1.5rem;
            margin-top: 2rem;
            color: #6c757d;
        }

        .dashboard-footer .text-muted {
            font-size: .875rem;
        }

        #lastUpdate {
            color: var(--primary-color, #4f46e5);
            font-weight: 500;
        }
    </style>

    <footer class="dashboard-footer">
        <div class="d-flex flex-column flex-md-row align-items-md-center">
            <p class="mb-1 mb-md-0">© <span id="footYear"></span> HRMS. All rights reserved.</p>
            <span class="ms-md-auto text-muted">Last update: <span id="lastUpdate">just now</span></span>
        </div>
    </footer>

    <script>
        document.addEventListener('DOMContentLoaded', function () {
            var y = document.getElementById('footYear');
            if (y) y.textContent = new Date().getFullYear();
        });
    </script>

    <!-- Bootstrap Bundle (bắt buộc cho dropdown, v.v.) -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>