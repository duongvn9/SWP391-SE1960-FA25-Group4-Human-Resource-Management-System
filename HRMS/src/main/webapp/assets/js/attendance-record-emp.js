document.addEventListener("DOMContentLoaded", () => {
    const selectedCountSpan = document.getElementById('selectedCount');
    const hiddenInput = document.getElementById('selectedLogDates');
    const selectedRecords = new Set();

    function updateSelectedCount() {
        if (selectedCountSpan)
            selectedCountSpan.textContent = selectedRecords.size;
    }

    if (hiddenInput.value) {
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
                hiddenInput.value = Array.from(selectedRecords).join(',');
                updateSelectedCount();
            });
        });
    }

    bindCheckboxes();

    const filterForm = document.getElementById('filterForm');
    filterForm.addEventListener('submit', function () {
        hiddenInput.value = Array.from(selectedRecords).join(',');
    });

    const submitBtn = document.getElementById('selectLogOkBtn');
    if (submitBtn) {
        submitBtn.addEventListener('click', function () {
            const submitForm = document.createElement('form');
            submitForm.method = 'post';
            submitForm.action = '/target-page';
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'selected_log_dates';
            input.value = hiddenInput.value;
            submitForm.appendChild(input);
            document.body.appendChild(submitForm);
            submitForm.submit();
        });
    }

    ["XLS", "CSV", "PDF"].forEach(type => {
        const btn = document.getElementById(`export${type}Btn`);
        if (btn) {
            btn.addEventListener('click', function () {
                document.getElementById("exportType").value = type.toLowerCase();
                document.getElementById("exportForm").submit();
            });
        }
    });
});
