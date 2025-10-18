document.addEventListener("DOMContentLoaded", function () {
    const dateInput = document.getElementById("attendanceDate");
    const logBtn = document.getElementById("selectFromLogDates"); // đảm bảo ID trùng với HTML
    const selectedLogDates = document.getElementById("selectedLogDates");
    
    if (!dateInput || !logBtn || !selectedLogDates)
        return;
    
    // Chọn ngày đơn → xóa ngày từ log
    dateInput.addEventListener("change", () => {
        if (dateInput.value) {
            selectedLogDates.value = ""; // clear log dates
        }
    });
    
    // Chọn từ log → xóa ngày đơn, chuyển trang thẳng
    logBtn.addEventListener("click", () => {
        dateInput.value = ""; // clear single date
        window.location.href = "http://localhost:9999/HRMS/attendance/record/emp";
    });
    
    // Hàm gọi từ trang log để gán các ngày đã chọn
    window.setSelectedLogDates = function (dates) {
        if (Array.isArray(dates)) {
            selectedLogDates.value = dates.join(",");
        } else {
            selectedLogDates.value = "";
        }
    };
});
