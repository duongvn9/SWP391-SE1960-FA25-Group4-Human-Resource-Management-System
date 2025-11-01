// Initialize header dropdown functionality
function initializeHeaderDropdown() {
    console.log('Initializing header dropdown...');
    setTimeout(() => {
        const dropdowns = document.querySelectorAll('.dropdown');
        console.log('Found dropdowns:', dropdowns.length);

        dropdowns.forEach((dropdown, index) => {
            const toggle = dropdown.querySelector('.dropdown-toggle');
            const menu = dropdown.querySelector('.dropdown-menu');

            console.log(`Dropdown ${index}:`, {
                toggle: !!toggle,
                menu: !!menu,
                toggleHref: toggle ? toggle.getAttribute('href') : null
            });

            if (!toggle || !menu)
                return;

            toggle.addEventListener('click', function (e) {
                console.log('Dropdown toggle clicked');
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
                console.log('Dropdown menu show:', menu.classList.contains('show'));
            });
        });

        // Close dropdowns when clicking outside (only add once)
        if (!document._dropdownClickHandlerAdded) {
            document.addEventListener('click', function (e) {
                if (!e.target.closest('.dropdown')) {
                    document.querySelectorAll('.dropdown-menu.show').forEach(menu => {
                        menu.classList.remove('show');
                    });
                }
            });
            document._dropdownClickHandlerAdded = true;
        }
    }, 100);
}

document.addEventListener("DOMContentLoaded", () => {
    // Initialize dropdown functionality for header
    initializeHeaderDropdown();

    // Existing functionality for checkboxes and export
    const selectedCountSpan = document.getElementById('selectedCount');
    const hiddenInput = document.getElementById('selectedLogDates');
    const selectedRecords = new Set();

    function updateSelectedCount() {
        if (selectedCountSpan)
            selectedCountSpan.textContent = selectedRecords.size;
    }

    if (hiddenInput && hiddenInput.value) {
        hiddenInput.value.split(',').forEach(v => {
            if (v)
                selectedRecords.add(v);
        });
    }
    updateSelectedCount();

    function bindCheckboxes() {
        document.querySelectorAll('input[name="record_checkbox"]').forEach(cb => {
            cb.addEventListener('change', function () {
                const key = this.value;
                if (this.checked)
                    selectedRecords.add(key);
                else
                    selectedRecords.delete(key);
                if (hiddenInput)
                    hiddenInput.value = Array.from(selectedRecords).join(',');
                updateSelectedCount();
            });
        });
    }

    bindCheckboxes();

    const filterForm = document.getElementById('filterForm');
    if (filterForm) {
        filterForm.addEventListener('submit', function () {
            if (hiddenInput)
                hiddenInput.value = Array.from(selectedRecords).join(',');
        });
    }

    const submitBtn = document.getElementById('selectLogOkBtn');
    if (submitBtn) {
        submitBtn.addEventListener('click', function () {
            const submitForm = document.createElement('form');
            submitForm.method = 'post';
            submitForm.action = '/target-page';
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'selected_log_dates';
            input.value = hiddenInput ? hiddenInput.value : '';
            submitForm.appendChild(input);
            document.body.appendChild(submitForm);
            submitForm.submit();
        });
    }

    // Export buttons functionality
    ["XLS", "CSV", "PDF"].forEach(type => {
        const btn = document.getElementById(`export${type}Btn`);
        if (btn) {
            btn.addEventListener('click', function () {
                const exportTypeInput = document.getElementById("exportType");
                const exportForm = document.getElementById("exportForm");
                if (exportTypeInput && exportForm) {
                    exportTypeInput.value = type.toLowerCase();
                    exportForm.submit();
                }
            });
        }
    });

    // Summary Modal functionality
    initializeSummaryModal();
});

function initializeSummaryModal() {
    const viewSummaryBtn = document.getElementById('viewSummaryBtn');
    const summaryModal = document.getElementById('summaryModal');
    const closeSummaryModal = document.getElementById('closeSummaryModal');
    const closeSummaryBtn = document.getElementById('closeSummaryBtn');

    console.log('Initializing summary modal...');
    console.log('Elements found:', {
        viewSummaryBtn: !!viewSummaryBtn,
        summaryModal: !!summaryModal,
        closeSummaryModal: !!closeSummaryModal,
        closeSummaryBtn: !!closeSummaryBtn
    });

    // Open summary modal
    if (viewSummaryBtn && summaryModal) {
        viewSummaryBtn.addEventListener('click', function (e) {
            e.preventDefault();
            console.log('View Summary button clicked');
            // TODO: Call backend API to get summary data
            summaryModal.classList.add('show');
        });
    } else {
        console.error('Summary button or modal not found!');
    }

    // Close modal functions
    function closeSummary() {
        if (summaryModal) {
            summaryModal.classList.remove('show');
        }
    }

    if (closeSummaryModal) {
        closeSummaryModal.addEventListener('click', closeSummary);
    }

    if (closeSummaryBtn) {
        closeSummaryBtn.addEventListener('click', closeSummary);
    }

    // Close modal when clicking outside
    if (summaryModal) {
        summaryModal.addEventListener('click', function (e) {
            if (e.target === summaryModal) {
                closeSummary();
            }
        });
    }

    // Close modal with Escape key
    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && summaryModal && summaryModal.classList.contains('show')) {
            closeSummary();
        }
    });
}