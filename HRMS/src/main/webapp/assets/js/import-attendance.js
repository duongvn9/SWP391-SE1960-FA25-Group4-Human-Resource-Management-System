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
    console.log("=== COLLECTING MANUAL DATA ===");

    const table = document.getElementById("manualTable");
    console.log("Table element:", table);

    const rows = table.querySelectorAll("tbody tr.manual-row");
    console.log("Found rows:", rows.length);

    const data = [];

    rows.forEach((row, index) => {
        console.log(`\n--- Processing row ${index + 1} ---`);

        const userIdInput = row.querySelector(".employee-id-hidden");
        const dateInput = row.querySelector(".date-input");
        const checkInInput = row.querySelector(".checkin-input");
        const checkOutInput = row.querySelector(".checkout-input");

        console.log("  employee-id-hidden:", userIdInput, "value:", userIdInput?.value);
        console.log("  date-input:", dateInput, "value:", dateInput?.value);
        console.log("  checkin-input:", checkInInput, "value:", checkInInput?.value);
        console.log("  checkout-input:", checkOutInput, "value:", checkOutInput?.value);

        const userId = userIdInput?.value.trim() || null;
        const date = dateInput?.value || null;
        const checkIn = checkInInput?.value || null;
        const checkOut = checkOutInput?.value || null;

        console.log("  Processed values:", {userId, date, checkIn, checkOut});

        // Status sẽ được tính tự động, không cần thu thập
        if (userId || date || checkIn || checkOut) {
            const rowData = {
                userId: userId ? Number(userId) : null,
                date: date,
                checkIn: checkIn,
                checkOut: checkOut
            };
            console.log("  ✅ Adding row data:", rowData);
            data.push(rowData);
        } else {
            console.log("  ⏭️ Skipping empty row");
        }
    });

    console.log("\n=== FINAL COLLECTED DATA ===");
    console.log("Total records:", data.length);
    console.log("Data:", data);

    return data;
}

// ---------------------------
// SUBMIT FORM
// ---------------------------
document.getElementById("manualImportForm")?.addEventListener("submit", function (e) {
    console.log("=== FORM SUBMIT TRIGGERED ===");

    const manualDataInput = document.getElementById("manualData");
    console.log("manualDataInput element:", manualDataInput);

    const manualData = collectManualData();
    console.log("Collected manual data:", manualData);
    console.log("Manual data length:", manualData.length);

    // Validate: Check if there's any data to import
    if (manualData.length === 0) {
        console.log("❌ No data to import - preventing form submission");
        e.preventDefault();

        console.log("Checking if showToast exists:", typeof window.showToast);

        // Show toast notification if available
        if (typeof window.showToast === 'function') {
            console.log("✅ Calling window.showToast()");
            window.showToast("Please add at least one attendance record before importing!", "error");
        } else {
            console.log("⚠️ showToast not found, using alert");
            alert("Please add at least one attendance record before importing!");
        }
        return false;
    }

    console.log("✅ Data valid, proceeding with submission");
    manualDataInput.value = JSON.stringify(manualData);
    console.log("Manual Data JSON:", manualDataInput.value);
});


console.log("=== LOADING TOAST NOTIFICATION SYSTEM ===");

// Toast Notification System - Make it globally available
window.showToast = function (message, type = 'success') {
    console.log("🔔 showToast called:", {message, type});

    const container = document.getElementById('toastContainer');
    console.log("Toast container:", container);

    if (!container) {
        console.error("❌ Toast container not found!");
        return;
    }

    const icons = {
        success: '<i class="fas fa-check-circle"></i>',
        error: '<i class="fas fa-exclamation-circle"></i>',
        warning: '<i class="fas fa-exclamation-triangle"></i>'
    };

    // Fallback icons if FontAwesome not loaded
    const fallbackIcons = {
        success: '✓',
        error: '✕',
        warning: '⚠'
    };

    const titles = {
        success: 'Success',
        error: 'Error',
        warning: 'Warning'
    };

    const toast = document.createElement('div');
    toast.className = `toast-notification ${type}`;
    toast.innerHTML = `
                    <div class="toast-icon">${icons[type]}</div>
                    <div class="toast-content">
                        <div class="toast-title">${titles[type]}</div>
                        <div class="toast-message">${message}</div>
                    </div>
                    <button class="toast-close" onclick="window.closeToast(this)">&times;</button>
                    <div class="toast-progress"></div>
                `;

    console.log("✅ Appending toast to container");
    container.appendChild(toast);

    console.log("Toast element after append:", toast);
    console.log("Toast computed style:", window.getComputedStyle(toast));
    console.log("Toast display:", window.getComputedStyle(toast).display);
    console.log("Toast visibility:", window.getComputedStyle(toast).visibility);
    console.log("Toast opacity:", window.getComputedStyle(toast).opacity);
    console.log("Container children count:", container.children.length);

    // Auto remove after 15 seconds
    setTimeout(() => {
        window.closeToast(toast.querySelector('.toast-close'));
    }, 15000);
}

window.closeToast = function (button) {
    console.log("🗑️ closeToast called");
    const toast = button.closest('.toast-notification');
    toast.classList.add('hiding');
    setTimeout(() => {
        toast.remove();
    }, 300);
}

console.log("✅ Toast functions registered on window object");
console.log("window.showToast:", typeof window.showToast);
console.log("window.closeToast:", typeof window.closeToast);

// Show messages on page load
document.addEventListener('DOMContentLoaded', () => {
    console.log("=== DOM CONTENT LOADED - Checking for messages ===");
    const messages = document.querySelectorAll('.hidden-message');
    console.log("Found hidden messages:", messages.length);

    messages.forEach((msg, index) => {
        const type = msg.getAttribute('data-type');
        const message = msg.getAttribute('data-message');
        console.log(`Message ${index + 1}:`, {type, message});

        if (message) {
            console.log(`Showing toast for message ${index + 1}`);
            window.showToast(message, type);
        }
    });
});

document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("addRowBtn").addEventListener("click", () => {
        const tbody = document.querySelector("#manualTable tbody");
        const template = document.getElementById("manualRowTemplate");
        const newRow = template.content.cloneNode(true);

        // Check if error column exists and add error cell if needed
        const headerRow = document.querySelector("#manualTable thead tr");
        const hasErrorColumn = headerRow.querySelector("th:last-child").textContent.trim() === "Error";

        if (hasErrorColumn) {
            const newRowElement = newRow.querySelector("tr");
            const errorCell = document.createElement("td");
            errorCell.className = "manual-cell error-cell";
            errorCell.innerHTML = '<span class="error-text"></span>';
            newRowElement.appendChild(errorCell);
        }

        tbody.appendChild(newRow);

        // Add validation to the new row
        setTimeout(() => {
            const newRows = document.querySelectorAll(".manual-row");
            const lastRow = newRows[newRows.length - 1];
            if (lastRow && window.addValidationToRow) {
                window.addValidationToRow(lastRow);
            }
        }, 100);
    });
});

document.addEventListener("DOMContentLoaded", () => {

    function showEmployeeList(input) {
        const row = input.closest('.manual-row');
        const wrapper = input.closest('.employee-select-wrapper');
        const dropdown = wrapper.querySelector('.custom-dropdown');
        // Ẩn dropdown khác và remove class active
        document.querySelectorAll('.manual-row').forEach(r => {
            r.classList.remove('active-dropdown');
            r.querySelectorAll('.custom-dropdown').forEach(dl => dl.style.display = 'none');
        });
        // Thêm class active cho row hiện tại
        row.classList.add('active-dropdown');
        // Hiển thị dropdown hiện tại
        dropdown.style.display = 'block';
    }

    function filterEmployeeList(input) {
        const dropdown = input.closest('.employee-select-wrapper').querySelector('.custom-dropdown');
        const filter = input.value.toLowerCase();
        let hasMatch = false;
        dropdown.querySelectorAll('li').forEach(li => {
            const match = li.textContent.toLowerCase().includes(filter);
            li.style.display = match ? '' : 'none';
            if (match)
                hasMatch = true;
        });
        dropdown.style.display = hasMatch ? 'block' : 'none';
    }

// Debug function to check employee data
    function debugEmployeeData() {
        const firstDropdown = document.querySelector('.custom-dropdown');
        if (firstDropdown) {
            console.log('Employee dropdown items:');
            firstDropdown.querySelectorAll('li').forEach((li, index) => {
                console.log(`${index}: ID=${li.dataset.id}, Text="${li.textContent.trim()}"`);
            });
        }
    }

// Call debug function on page load
    setTimeout(debugEmployeeData, 1000);
    // Test function to verify JavaScript is working
    console.log('Employee dropdown JavaScript loaded successfully');
    // Simple and direct function to select employee
    window.selectEmployee = function (liElement) {
        console.log('selectEmployee called with:', liElement);
        const wrapper = liElement.closest('.employee-select-wrapper');
        const input = wrapper.querySelector('.employee-input');
        const hidden = wrapper.querySelector('.employee-id-hidden');
        const dropdown = wrapper.querySelector('.custom-dropdown');
        const fullText = liElement.textContent.trim();
        const employeeId = liElement.getAttribute('data-id');
        console.log('Setting input value to:', fullText);
        console.log('Setting hidden value to:', employeeId);
        // Set values
        input.value = fullText;
        hidden.value = employeeId;
        // Hide dropdown
        dropdown.style.display = 'none';
        console.log('Final input value:', input.value);
        console.log('Final hidden value:', hidden.value);
    };
    // Make functions global for debugging
    window.showEmployeeListJSP = showEmployeeList;
    window.filterEmployeeListJSP = filterEmployeeList;
    // Simple click outside to hide dropdown
    document.addEventListener('click', e => {
        if (!e.target.closest('.employee-select-wrapper')) {
            document.querySelectorAll('.custom-dropdown').forEach(dl => dl.style.display = 'none');
        }
    });
    // Focus hoặc click vào input
    document.addEventListener('focusin', e => {
        if (e.target.matches('.employee-input')) {
            showEmployeeList(e.target);
        }
    });
    document.addEventListener('click', e => {
        if (e.target.matches('.employee-input')) {
            showEmployeeList(e.target);
        }
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const tbody = document.querySelector("#manualTable tbody");
    const template = document.getElementById("manualRowTemplate");
    const selectAll = document.getElementById("selectAllRows");
    const deleteModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
    const deleteMessage = document.getElementById('deleteMessage');
    const confirmDeleteBtn = document.getElementById('confirmDeleteBtn');
    let rowsToDelete = [];
    // Delete Row
    document.getElementById("deleteRowBtn").addEventListener("click", () => {
        const selectedRows = tbody.querySelectorAll(".row-select:checked");
        if (selectedRows.length === 0) {
            deleteMessage.textContent = "Please select at least one row to delete!";
            deleteModal.show();
            // Hide confirm button for warning message
            confirmDeleteBtn.style.display = 'none';
            return;
        }

// Show confirm button for delete action
        confirmDeleteBtn.style.display = 'inline-block';
        rowsToDelete = Array.from(selectedRows);
        deleteMessage.textContent = `Are you sure you want to delete ${selectedRows.length} row(s)?`;
        deleteModal.show();
    });
    // Confirm Delete Action
    confirmDeleteBtn.addEventListener("click", () => {
        rowsToDelete.forEach(chk => chk.closest("tr").remove());
        // Sau khi xóa xong: bỏ chọn tất cả checkbox
        selectAll.checked = false;
        tbody.querySelectorAll(".row-select").forEach(chk => chk.checked = false);
        // Close modal
        deleteModal.hide();
        rowsToDelete = [];
    });
    // Select / Deselect All
    selectAll.addEventListener("change", () => {
        tbody.querySelectorAll(".row-select").forEach(chk => chk.checked = selectAll.checked);
    });
});

document.addEventListener("DOMContentLoaded", () => {
    const today = new Date().toISOString().split("T")[0]; // YYYY-MM-DD

    document.querySelectorAll(".date-input").forEach(input => {
        input.setAttribute("max", today); // chỉ chọn ngày hiện tại trở về
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const minTime = "06:00";
    const maxTime = "23:59";

    // Frontend validation - hiển thị lỗi trên input
    function showInputError(input, message) {
        const errorDiv = input.parentElement.querySelector(".error-message");
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.style.display = "block";
            errorDiv.style.color = "#dc3545";
            errorDiv.style.fontSize = "12px";
            errorDiv.style.marginTop = "4px";
        }
        input.style.borderColor = "#dc3545";
    }

    function clearInputError(input) {
        const errorDiv = input.parentElement.querySelector(".error-message");
        if (errorDiv) {
            errorDiv.textContent = "";
            errorDiv.style.display = "none";
        }
        input.style.borderColor = "#ccc";
    }

    // Backend validation - hiển thị lỗi trong cột Error (chỉ khi có cột Error)
    function showRowError(row, message) {
        const errorSpan = row.querySelector(".error-text");
        if (errorSpan) {
            errorSpan.textContent = message;
            errorSpan.style.display = "block";
        }
        row.classList.add("invalid-row");
    }

    function clearRowError(row) {
        const errorSpan = row.querySelector(".error-text");
        if (errorSpan) {
            errorSpan.textContent = "";
            errorSpan.style.display = "none";
        }
        row.classList.remove("invalid-row");
    }

    function isTimeInRange(time) {
        return time >= minTime && time <= maxTime;
    }

    // Frontend validation cho time inputs
    function validateTimeInput(input) {
        clearInputError(input);

        const time = input.value;
        if (!time)
            return true; // Empty is OK for individual validation

        if (!isTimeInRange(time)) {
            showInputError(input, "Time must be between 06:00 and 23:59");
            return false;
        }

        return true;
    }

    // Frontend validation cho check-in vs check-out
    function validateTimeOrder(checkinInput, checkoutInput) {
        const checkin = checkinInput.value;
        const checkout = checkoutInput.value;

        // Clear previous errors
        clearInputError(checkinInput);
        clearInputError(checkoutInput);

        if (checkin && checkout && checkin >= checkout) {
            showInputError(checkoutInput, "Check-out must be later than check-in");
            return false;
        }

        return true;
    }

    // Clear error messages from error column
    function clearAllRowErrors() {
        const rows = document.querySelectorAll(".manual-row");
        rows.forEach(row => {
            clearRowError(row);
        });
    }

    // Add error column to table when needed
    function addErrorColumn() {
        const table = document.getElementById("manualTable");
        const headerRow = table.querySelector("thead tr");
        const rows = table.querySelectorAll("tbody tr");

        // Check if error column already exists
        if (headerRow.querySelector("th:last-child").textContent.trim() === "Error") {
            return;
        }

        // Add header
        const errorHeader = document.createElement("th");
        errorHeader.textContent = "Error";
        headerRow.appendChild(errorHeader);

        // Add error cell to each row
        rows.forEach(row => {
            const errorCell = document.createElement("td");
            errorCell.className = "manual-cell error-cell";
            errorCell.innerHTML = '<span class="error-text"></span>';
            row.appendChild(errorCell);
        });
    }

    // Remove error column from table when not needed
    function removeErrorColumn() {
        const table = document.getElementById("manualTable");
        const headerRow = table.querySelector("thead tr");
        const rows = table.querySelectorAll("tbody tr");

        // Check if error column exists
        const lastHeader = headerRow.querySelector("th:last-child");
        if (lastHeader && lastHeader.textContent.trim() === "Error") {
            // Remove header
            lastHeader.remove();

            // Remove error cell from each row
            rows.forEach(row => {
                const lastCell = row.querySelector("td:last-child");
                if (lastCell && lastCell.classList.contains("error-cell")) {
                    lastCell.remove();
                }
            });
        }
    }

    // Add event listeners to existing rows for basic frontend validation only
    function addValidationToRow(row) {
        const checkinInput = row.querySelector(".checkin-input");
        const checkoutInput = row.querySelector(".checkout-input");

        // Only keep basic time validation for user experience
        if (checkinInput) {
            checkinInput.addEventListener("blur", () => {
                validateTimeInput(checkinInput);
                if (checkoutInput && checkoutInput.value) {
                    validateTimeOrder(checkinInput, checkoutInput);
                }
            });
            checkinInput.addEventListener("change", () => {
                validateTimeInput(checkinInput);
                if (checkoutInput && checkoutInput.value) {
                    validateTimeOrder(checkinInput, checkoutInput);
                }

                clearAllRowErrors();
            });
        }

        if (checkoutInput) {
            checkoutInput.addEventListener("blur", () => {
                validateTimeInput(checkoutInput);
                if (checkinInput && checkinInput.value) {
                    validateTimeOrder(checkinInput, checkoutInput);
                }
            });
            checkoutInput.addEventListener("change", () => {
                validateTimeInput(checkoutInput);
                if (checkinInput && checkinInput.value) {
                    validateTimeOrder(checkinInput, checkoutInput);
                }
            });
        }
    }

    // Clear error messages on form submission - let backend handle validation
    const importForm = document.getElementById("manualImportForm");
    if (importForm) {
        importForm.addEventListener("submit", function (e) {
            // Clear any previous error messages
            const feedbackDiv = document.getElementById("manualFeedback");
            if (feedbackDiv) {
                feedbackDiv.style.display = "none";
            }

            // Clear all error messages from error column before submission
            // Backend will populate them if there are validation errors
            clearAllRowErrors();
        });
    }

    // Make function global for use in other scripts
    window.addValidationToRow = addValidationToRow;

    // Initialize validation for existing rows
    document.querySelectorAll(".manual-row").forEach(addValidationToRow);

    // Note: addRowBtn event listener is handled in the first script above
});