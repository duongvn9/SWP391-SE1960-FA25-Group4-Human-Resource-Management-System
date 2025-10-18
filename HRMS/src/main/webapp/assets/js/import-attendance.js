function showTab(tabId) {
    // Ẩn tất cả tab
    document.querySelectorAll(".tab-content").forEach(tab => {
        tab.style.display = "none";
    });

    // Hiển thị tab được chọn
    const selectedTab = document.getElementById(tabId);
    if (selectedTab) {
        selectedTab.style.display = "block";
    }

    // Cập nhật trạng thái active cho button
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

// ==== Gán sự kiện và hiển thị tab mặc định ====
document.addEventListener("DOMContentLoaded", () => {
    // Gán click event cho tất cả button
    document.querySelectorAll(".tab-btn").forEach(btn => {
        const tabId = btn.id.replace("-btn", "");
        btn.addEventListener("click", () => showTab(tabId));
    });

    // Hiển thị Upload tab làm mặc định
    showTab("upload");
});
