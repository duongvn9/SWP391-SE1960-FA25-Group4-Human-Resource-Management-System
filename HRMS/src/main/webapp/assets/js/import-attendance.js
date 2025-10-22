function showTab(tabId) {
    // BƯỚC 1: ẨN TẤT CẢ TAB (cực kỳ quan trọng!)
    document.querySelectorAll(".tab-content").forEach(tab => {
        tab.classList.remove("active");
        tab.style.display = "none"; // ẨN HOÀN TOÀN
    });

    // BƯỚC 2: HIỆN TAB ĐƯỢC CHỌN
    const selectedTab = document.getElementById(tabId);
    if (selectedTab) {
        selectedTab.classList.add("active");
        selectedTab.style.display = "block"; // HIỆN LẠI
    }

    // BƯỚC 3: CẬP NHẬT NÚT
    document.querySelectorAll(".tab-btn").forEach(btn => {
        btn.classList.remove("active");
        btn.setAttribute("aria-selected", "false");
    });

    const activeBtn = document.getElementById(tabId + "-btn");
    if (activeBtn) {
        activeBtn.classList.add("active");
        activeBtn.setAttribute("aria-selected", "true");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".tab-btn").forEach(btn => {
        const tabId = btn.id.replace("-btn", "");
        btn.addEventListener("click", () => showTab(tabId));
    });

    // Mở tab Upload mặc định
    showTab("upload");
});

function collectManualData() {
    const table = document.getElementById("manualTable");
    const rows = table.querySelectorAll("tbody tr.manual-row");
    const data = [];

    rows.forEach(row => {
        const userId = row.querySelector(".employee-input").value.trim(); // phải là userId (hoặc parse số nếu cần)
        const date = row.querySelector(".date-input").value; // yyyy-MM-dd
        const checkIn = row.querySelector(".checkin-input").value; // HH:mm
        const checkOut = row.querySelector(".checkout-input").value; // HH:mm
        const status = row.querySelector(".status-input").value;

        // Bỏ qua các hàng trống hoàn toàn
        if (userId || date || checkIn || checkOut || status) {
            data.push({
                userId: userId ? Number(userId) : null, // convert sang Long ở server
                date: date || null,
                checkIn: checkIn || null,
                checkOut: checkOut || null,
                status: status || null
            });
        }
    });

    return data;
}

// Gán dữ liệu vào input ẩn khi submit form
document.getElementById("manualImportForm").addEventListener("submit", function (e) {
    const manualDataInput = document.getElementById("manualData");
    const manualData = collectManualData();

    // Chuyển thành JSON để gửi lên server
    manualDataInput.value = JSON.stringify(manualData);

    // Nếu muốn kiểm tra trước khi submit
    console.log("Manual Data:", manualData);
});
