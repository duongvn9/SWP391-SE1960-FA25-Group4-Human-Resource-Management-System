document.addEventListener("DOMContentLoaded", () => {
    const selectedDates = new Set();
    const dateInput = document.getElementById('attendanceDate');
    const addBtn = document.getElementById('addDateBtn');
    const selectedListDiv = document.getElementById('selectedDatesList');
    const hiddenInput = document.getElementById('selectedLogDates');

    addBtn.addEventListener('click', () => {
        const date = dateInput.value;
        if (!date)
            return; // nếu chưa chọn ngày thì bỏ qua
        if (selectedDates.has(date)) {
            alert("Date already selected!");
            return;
        }

        selectedDates.add(date);

        // Tạo badge hiển thị ngày đã chọn
        const span = document.createElement('span');
        span.textContent = date;
        span.classList.add('badge', 'bg-primary', 'me-1', 'mb-1');
        selectedListDiv.appendChild(span);

        // Cập nhật hidden input
        hiddenInput.value = Array.from(selectedDates).join(',');
    });
});
