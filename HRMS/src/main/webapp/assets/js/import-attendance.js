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
// CLICK EVENT CHỌN NHÂN VIÊN - REMOVED
// Xử lý trong JSP inline script để tránh conflict
// ---------------------------

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
        // Status sẽ được tính tự động, không cần thu thập

        if (userId || date || checkIn || checkOut) {
            data.push({
                userId: userId ? Number(userId) : null,
                date: date,
                checkIn: checkIn,
                checkOut: checkOut
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
