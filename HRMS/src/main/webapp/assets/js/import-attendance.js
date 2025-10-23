// ---------------------------
// TAB FUNCTION
// ---------------------------
function showTab(tabId) {
    // Ẩn tất cả tab
    document.querySelectorAll(".tab-content").forEach(tab => {
        tab.classList.remove("active");
        tab.style.display = "none";
    });

    // Hiện tab được chọn
    const selectedTab = document.getElementById(tabId);
    if (selectedTab) {
        selectedTab.classList.add("active");
        selectedTab.style.display = "block";
    }

    // Cập nhật nút
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

    showTab("upload");
});

// ---------------------------
// MANUAL ROW DROPDOWN FUNCTIONS
// ---------------------------
function showEmployeeList(input) {
    const dropdown = input.parentElement.querySelector(".custom-dropdown");
    if (!dropdown)
        return;
    dropdown.style.display = "block";

    const items = dropdown.querySelectorAll("li");
    items.forEach(li => li.style.display = "");
}

function filterEmployeeList(input) {
    const dropdown = input.parentElement.querySelector(".custom-dropdown");
    if (!dropdown)
        return;

    const filter = input.value.toLowerCase();
    const items = dropdown.querySelectorAll("li");

    let hasMatch = false;
    items.forEach(li => {
        const match = li.textContent.toLowerCase().includes(filter);
        li.style.display = match ? "" : "none";
        if (match)
            hasMatch = true;
    });

    dropdown.style.display = hasMatch ? "block" : "none";
}

// ---------------------------
// CLICK EVENT CHỌN NHÂN VIÊN
// ---------------------------
document.addEventListener("click", e => {
    // Click chọn 1 nhân viên trong dropdown
    if (e.target.matches(".custom-dropdown li")) {
        const li = e.target;
        const wrapper = li.closest(".employee-select-wrapper");
        const input = wrapper.querySelector(".employee-input");
        const hidden = wrapper.querySelector(".employee-id-hidden");

        input.value = li.textContent.trim(); 
        hidden.value = li.dataset.id;        
        wrapper.querySelector(".custom-dropdown").style.display = "none";
    }
    // Click ra ngoài dropdown -> ẩn
    else if (!e.target.closest(".employee-select-wrapper")) {
        document.querySelectorAll(".custom-dropdown").forEach(d => d.style.display = "none");
    }
});

// ---------------------------
// COLLECT MANUAL DATA
// ---------------------------
function collectManualData() {
    const table = document.getElementById("manualTable");
    const rows = table.querySelectorAll("tbody tr.manual-row");
    const data = [];

    rows.forEach(row => {
        const userId = row.querySelector(".employee-id-hidden")?.value.trim() || null;
        const date = row.querySelector(".date-input")?.value || null;
        const checkIn = row.querySelector(".checkin-input")?.value || null;
        const checkOut = row.querySelector(".checkout-input")?.value || null;
        const status = row.querySelector(".status-input")?.value || null;

        if (userId || date || checkIn || checkOut || status) {
            data.push({
                userId: userId ? Number(userId) : null,
                date: date,
                checkIn: checkIn,
                checkOut: checkOut,
                status: status
            });
        }
    });

    return data;
}

// ---------------------------
// SUBMIT FORM
// ---------------------------
document.getElementById("manualImportForm")?.addEventListener("submit", function (e) {
    const manualDataInput = document.getElementById("manualData");
    const manualData = collectManualData();

    manualDataInput.value = JSON.stringify(manualData);

    console.log("Manual Data:", manualData);
});
