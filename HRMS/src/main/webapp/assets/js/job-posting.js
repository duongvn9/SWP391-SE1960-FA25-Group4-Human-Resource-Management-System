// Client-side validation and dynamic behavior for job posting form
document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form.needs-validation');
    const salaryTypeSelect = document.getElementById('salaryType');
    const minSalaryInput = document.getElementById('minSalary');
    const maxSalaryInput = document.getElementById('maxSalary');

    // Handle salary type changes
    salaryTypeSelect.addEventListener('change', function() {
        const type = this.value;
        if (type === 'NEGOTIABLE') {
            minSalaryInput.value = '';
            maxSalaryInput.value = '';
            minSalaryInput.disabled = true;
            maxSalaryInput.disabled = true;
            minSalaryInput.required = false;
            maxSalaryInput.required = false;
        } else if (type === 'FROM') {
            minSalaryInput.disabled = false;
            maxSalaryInput.value = '';
            maxSalaryInput.disabled = true;
            minSalaryInput.required = true;
            maxSalaryInput.required = false;
        } else { // RANGE
            minSalaryInput.disabled = false;
            maxSalaryInput.disabled = false;
            minSalaryInput.required = true;
            maxSalaryInput.required = true;
        }
    });

    // Format salary inputs with thousand separators
    [minSalaryInput, maxSalaryInput].forEach(input => {
        input.addEventListener('input', function() {
            // Remove non-digit characters
            let value = this.value.replace(/\D/g, '');
            // Format with thousand separators
            if (value) {
                this.value = Number(value).toLocaleString('en-US');
            }
        });
    });

    // Custom form validation
    form.addEventListener('submit', function(event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }

        // Custom salary range validation
        if (salaryTypeSelect.value === 'RANGE') {
            const min = parseFloat(minSalaryInput.value.replace(/,/g, ''));
            const max = parseFloat(maxSalaryInput.value.replace(/,/g, ''));
            if (min >= max) {
                event.preventDefault();
                maxSalaryInput.setCustomValidity('Maximum salary must be greater than minimum salary');
            } else {
                maxSalaryInput.setCustomValidity('');
            }
        }

        form.classList.add('was-validated');
    }, false);

    // Initialize salary type state
    salaryTypeSelect.dispatchEvent(new Event('change'));
});