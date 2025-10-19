document.addEventListener("DOMContentLoaded", function () {
    const toggle = document.getElementById("switchInput");
    const status = document.getElementById("sliderStatus");
    const exportForm = document.getElementById("exportForm");
    const exportTypeInput = document.getElementById("exportType");

    // --- Hàm gửi trạng thái đến server ---
    function sendStatusToBackend(state) {
        fetch('http://localhost:9999/HRMS/attendance/record/HR', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({status: state})
        })
                .then(res => res.json())
                .then(data => console.log('Server response:', data))
                .catch(err => console.error('Error sending status:', err));
    }

    // --- Cập nhật trạng thái toggle ---
    function updateStatus() {
        const currentState = toggle.checked ? "Locked" : "Unlocked";
        status.textContent = currentState;
        sendStatusToBackend(currentState);
    }

    // --- Gọi một lần lúc load trang ---
    updateStatus();

    // --- Khi toggle thay đổi ---
    toggle.addEventListener("change", updateStatus);

    // --- Chọn tất cả checkbox ---
    window.selectAll = function (source) {
        const checkboxes = document.querySelectorAll('#attendanceTable tbody input[type="checkbox"]');
        checkboxes.forEach(cb => cb.checked = source.checked);
    };

    // --- Chuyển hướng import ---
    window.importAttendance = function () {
        window.location.href = "http://localhost:9999/HRMS/attendance/import";
    };

    // --- Xử lý export ---
    const exportButtons = [
        {id: "exportXLSBtn", type: "xls"},
        {id: "exportCSVBtn", type: "csv"},
        {id: "exportPDFBtn", type: "pdf"}
    ];

    exportButtons.forEach(btn => {
        const element = document.getElementById(btn.id);
        if (element) {
            element.addEventListener("click", function () {
                exportTypeInput.value = btn.type;
                exportForm.submit();
            });
        }
    });
});
