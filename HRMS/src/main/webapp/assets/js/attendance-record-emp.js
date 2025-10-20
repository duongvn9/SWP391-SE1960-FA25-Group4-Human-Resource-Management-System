document.addEventListener("DOMContentLoaded", () => {
    const selectedCountSpan = document.getElementById('selectedCount');
    const hiddenInput = document.getElementById('selectedLogDates');
    const selectedRecords = new Set();

    // Hàm cập nhật số lượng hiển thị
    function updateSelectedCount() {
        if (selectedCountSpan)
            selectedCountSpan.textContent = selectedRecords.size;
    }

    // Khởi tạo từ hidden nếu có dữ liệu cũ
    if (hiddenInput.value) {
        hiddenInput.value.split(',').forEach(v => {
            if (v)
                selectedRecords.add(v);
        });
    }
    updateSelectedCount();

    // Hàm gắn sự kiện checkbox
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

    // Gọi bind lần đầu
    bindCheckboxes();

    // Khi submit filter hoặc pagination
    const filterForm = document.getElementById('filterForm');
    filterForm.addEventListener('submit', function () {
        hiddenInput.value = Array.from(selectedRecords).join(',');
    });

    // Submit sang trang khác
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

    // Export buttons
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
