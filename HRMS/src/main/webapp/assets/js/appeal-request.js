document.addEventListener("DOMContentLoaded", function () {
    const selectRecordBtn = document.getElementById("selectRecordBtn");
    const popup = document.getElementById("selectRecordPopup");
    const closeBtns = document.querySelectorAll("#closePopupBtn, #closePopupBtn2");
    const tableBody = document.getElementById("attendanceTableBody");
    const periodFilter = document.getElementById("periodFilter");
    const paginationContainer = document.getElementById("paginationContainer");
    const submitBtn = document.getElementById("submitSelectedRecords");
    const recordsInput = document.getElementById("recordsInput"); // hidden input in form
    const rowsPerPage = 5;
    let currentPage = 1;
    let selectedRecords = [];

    const allRows = Array.from(tableBody.querySelectorAll("tr"));

    function renderRows() {
        const selectedPeriodValue = periodFilter.value;
        const selectedPeriodName = periodFilter.options[periodFilter.selectedIndex]?.text;

        const filtered = allRows.filter(row => {
            const rowPeriodName = row.dataset.periodName;

            // Nếu không chọn period nào (value rỗng) thì hiển thị tất cả
            if (!selectedPeriodValue || selectedPeriodValue === "") {
                return true;
            }

            // So sánh tên period (trim để loại bỏ khoảng trắng thừa)
            const match = rowPeriodName && rowPeriodName.trim() === selectedPeriodName.trim();
            return match;
        });
        const totalPages = Math.ceil(filtered.length / rowsPerPage) || 1;
        if (currentPage > totalPages)
            currentPage = totalPages;

        allRows.forEach(r => (r.style.display = "none"));

        const start = (currentPage - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        const visible = filtered.slice(start, end);
        visible.forEach(row => (row.style.display = ""));

        // Render pagination
        paginationContainer.innerHTML = "";
        if (totalPages > 1) {
            if (currentPage > 1) {
                const prev = document.createElement("li");
                prev.className = "page-item";
                prev.innerHTML = `<a class="page-link" href="#">Prev</a>`;
                prev.addEventListener("click", e => {
                    e.preventDefault();
                    currentPage--;
                    renderRows();
                });
                paginationContainer.appendChild(prev);
            }

            for (let i = 1; i <= totalPages; i++) {
                const li = document.createElement("li");
                li.className = "page-item" + (i === currentPage ? " active" : "");
                li.innerHTML = `<a class="page-link" href="#">${i}</a>`;
                li.addEventListener("click", e => {
                    e.preventDefault();
                    currentPage = i;
                    renderRows();
                });
                paginationContainer.appendChild(li);
            }

            if (currentPage < totalPages) {
                const next = document.createElement("li");
                next.className = "page-item";
                next.innerHTML = `<a class="page-link" href="#">Next</a>`;
                next.addEventListener("click", e => {
                    e.preventDefault();
                    currentPage++;
                    renderRows();
                });
                paginationContainer.appendChild(next);
            }
        }
    }

    // --- Popup events ---
    selectRecordBtn?.addEventListener("click", () => popup.classList.remove("d-none"));
    closeBtns.forEach(btn => btn.addEventListener("click", () => popup.classList.add("d-none")));
    popup.addEventListener("click", e => {
        if (e.target === popup)
            popup.classList.add("d-none");
    });
    periodFilter?.addEventListener("change", () => {
        currentPage = 1;
        renderRows();
    });

    // --- Checkbox selection ---
    tableBody.addEventListener("change", event => {
        const checkbox = event.target.closest(".select-checkbox");
        if (!checkbox)
            return;

        const row = checkbox.closest("tr");
        const cells = row.querySelectorAll("td");
        const record = {
            date: cells[1].innerText.trim(),
            checkIn: cells[2].innerText.trim(),
            checkOut: cells[3].innerText.trim(),
            status: cells[4].innerText.trim(),
            source: cells[5].innerText.trim(),
            period: cells[6].innerText.trim()
        };

        if (checkbox.checked) {
            const exists = selectedRecords.some(r =>
                r.date === record.date && r.checkIn === record.checkIn && r.checkOut === record.checkOut
            );
            if (!exists) {
                selectedRecords.push(record);
                row.classList.add("table-success");
            }
        } else {
            selectedRecords = selectedRecords.filter(r =>
                !(r.date === record.date && r.checkIn === record.checkIn && r.checkOut === record.checkOut)
            );
            row.classList.remove("table-success");
        }
    });

    // --- Submit form ---
    submitBtn.closest("form").addEventListener("submit", function (e) {
        if (selectedRecords.length === 0) {
            e.preventDefault();
            alert("⚠️ Please select at least one record before submitting!");
            return;
        }

        // Set hidden input value trước khi submit
        recordsInput.value = JSON.stringify(selectedRecords);
    });

    // --- Initial render ---
    renderRows();
});

document.addEventListener("DOMContentLoaded", function () {
    // Xử lý nút X để xóa bản ghi
    document.querySelectorAll('.remove-record-btn').forEach(btn => {
        btn.addEventListener('click', e => {
            const parentDiv = e.target.closest('.col-12');
            if (parentDiv)
                parentDiv.remove();
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("appealForm");

    form.addEventListener("submit", function (e) {
        const recordsDiv = document.getElementById("selectedRecordsList");
        const records = [];

        if (recordsDiv) {
            const recordItems = recordsDiv.querySelectorAll(".col-12.border");

            recordItems.forEach(div => {
                const removeBtn = div.querySelector(".remove-record-btn");
                if (!removeBtn)
                    return;

                // --- Lấy dữ liệu cũ từ table ---
                const tds = div.querySelectorAll("table tbody tr td");
                const oldRecord = {
                    date: tds[0]?.innerText.trim() || "",
                    checkIn: tds[1]?.innerText.trim() || "",
                    checkOut: tds[2]?.innerText.trim() || "",
                    status: tds[3]?.innerText.trim() || "",
                    source: tds[4]?.innerText.trim() || "",
                    period: tds[5]?.innerText.trim() || ""
                };

                // --- Lấy dữ liệu mới từ input edit trong div ---
                const editDateEl = div.querySelector("input[name='editDate']");
                const editCheckInEl = div.querySelector("input[name='editCheckIn']");
                const editCheckOutEl = div.querySelector("input[name='editCheckOut']");
                const editStatusEl = div.querySelector("select[name='editStatus']");

                const newRecord = {
                    date: editDateEl?.value || "",
                    checkIn: editCheckInEl?.value || "",
                    checkOut: editCheckOutEl?.value || "",
                    status: editStatusEl?.value || ""
                };

                records.push({
                    oldRecord: oldRecord,
                    newRecord: newRecord
                });
            });
        }

        // --- Gán JSON vào input hidden trong form ---
        const hiddenInput = document.getElementById("selected_logs_data");
        hiddenInput.value = JSON.stringify(records);
    });

    // --- Xử lý nút X để xóa bản ghi ---
    const removeBtns = document.querySelectorAll(".remove-record-btn");
    removeBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            const div = btn.closest(".col-12.border");
            if (div)
                div.remove();
        });
    });
});

document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("appealForm");
    const selectRecordBtn = document.getElementById("selectRecordBtn");
    const recordsDiv = document.getElementById("selectedRecordsList");
    const errorDiv = document.getElementById("attendanceRecordError");
    const editRadio = document.getElementById("editExistingRecord");

    // Hide error message when user interacts with the form (but don't validate yet)
    function hideAttendanceRecordError() {
        if (errorDiv) {
            errorDiv.style.display = "none";
        }
    }

    // Hide error when user clicks select record button
    if (selectRecordBtn) {
        selectRecordBtn.addEventListener("click", hideAttendanceRecordError);
    }

    // Hide error when user switches to add new record mode
    const addRadio = document.getElementById("addNewRecord");
    if (addRadio) {
        addRadio.addEventListener("change", hideAttendanceRecordError);
    }

    // Note: The actual validation logic is now handled in the main attachment validation script
    // This ensures all validations happen only on form submit
});

document.addEventListener("DOMContentLoaded", function () {
    const checkInInput = document.getElementById("editCheckIn");
    const checkOutInput = document.getElementById("editCheckOut");

    // Chỉ thực hiện nếu các elements tồn tại
    if (!checkInInput || !checkOutInput)
        return;

    const minTime = "06:00";
    const maxTime = "23:59";

    function showError(input, message) {
        const errorDiv = input.parentElement.querySelector(".error-message");
        if (errorDiv) {
            errorDiv.textContent = message;
            errorDiv.style.display = "block";
        }
    }

    function clearError(input) {
        const errorDiv = input.parentElement.querySelector(".error-message");
        if (errorDiv) {
            errorDiv.textContent = "";
            errorDiv.style.display = "none";
        }
    }

    function isTimeInRange(time) {
        return time >= minTime && time <= maxTime;
    }

    function validateEditTimes() {
        clearError(checkInInput);
        clearError(checkOutInput);

        const checkIn = checkInInput.value;
        const checkOut = checkOutInput.value;

        if (!checkIn && !checkOut)
            return;

        if (checkIn && !isTimeInRange(checkIn)) {
            showError(checkInInput, "Check-in time must be between 06:00 and 23:59.");
            checkInInput.value = "";
            return;
        }

        if (checkOut && !isTimeInRange(checkOut)) {
            showError(checkOutInput, "Check-out time must be between 06:00 and 23:59.");
            checkOutInput.value = "";
            return;
        }

        if (checkIn && checkOut && checkIn >= checkOut) {
            showError(checkOutInput, "Check-in time must be earlier than check-out time.");
            checkOutInput.value = "";
        }
    }

    checkInInput.addEventListener("change", validateEditTimes);
    checkOutInput.addEventListener("change", validateEditTimes);
});

// Simple inline word count function
function updateWordCountInline(textarea) {
    const text = textarea.value;
    const words = text.trim() === "" ? 0 : text.trim().split(/\s+/).length;
    const maxWords = 200;

    const display = document.getElementById("wordCount");
    const error = document.getElementById("wordLimitError");

    if (display) {
        display.textContent = words + "/" + maxWords + " words";

        if (words > maxWords) {
            display.classList.add("text-danger");
            display.classList.remove("text-muted");
            if (error)
                error.style.display = "block";
            textarea.classList.add("is-invalid");
        } else {
            display.classList.remove("text-danger");
            display.classList.add("text-muted");
            if (error)
                error.style.display = "none";
            textarea.classList.remove("is-invalid");
        }
    }
}

// Initialize word count
document.addEventListener("DOMContentLoaded", function () {
    const textarea = document.getElementById("detail");
    if (textarea) {
        updateWordCountInline(textarea);
    }
});

// Word count validation for detail textarea
document.addEventListener("DOMContentLoaded", function () {
    const detailTextarea = document.getElementById("detail");
    const wordCountDisplay = document.getElementById("wordCount");
    const wordLimitError = document.getElementById("wordLimitError");
    const form = document.getElementById("appealForm");
    const maxWords = 200;

    if (!detailTextarea || !wordCountDisplay) {
        return;
    }

    function countWords(text) {
        const words = text.trim().split(/\s+/);
        return text.trim() === "" ? 0 : words.length;
    }

    function updateWordCount() {
        const text = detailTextarea.value;
        const wordCount = countWords(text);

        wordCountDisplay.textContent = `${wordCount}/${maxWords} words`;

        if (wordCount > maxWords) {
            wordCountDisplay.classList.add("text-danger");
            wordCountDisplay.classList.remove("text-muted");
            if (wordLimitError)
                wordLimitError.style.display = "block";
            detailTextarea.classList.add("is-invalid");
        } else {
            wordCountDisplay.classList.remove("text-danger");
            wordCountDisplay.classList.add("text-muted");
            if (wordLimitError)
                wordLimitError.style.display = "none";
            detailTextarea.classList.remove("is-invalid");
        }
    }

    // Add event listeners
    detailTextarea.addEventListener("input", updateWordCount);
    detailTextarea.addEventListener("keyup", updateWordCount);
    detailTextarea.addEventListener("paste", function () {
        setTimeout(updateWordCount, 50);
    });

    // Form validation
    if (form) {
        form.addEventListener("submit", function (e) {
            const wordCount = countWords(detailTextarea.value);
            if (wordCount > maxWords) {
                e.preventDefault();
                if (wordLimitError)
                    wordLimitError.style.display = "block";
                detailTextarea.focus();
                detailTextarea.scrollIntoView({behavior: 'smooth', block: 'center'});
            }
        });
    }

    // Initial count
    setTimeout(updateWordCount, 100);
});

// Handle attachment type switching
document.addEventListener("DOMContentLoaded", function () {
    const fileRadio = document.getElementById("attachmentTypeFile");
    const linkRadio = document.getElementById("attachmentTypeLink");
    const fileSection = document.getElementById("fileUploadSection");
    const linkSection = document.getElementById("driveLinkSection");
    const fileInput = document.getElementById("attachments");
    const linkInput = document.getElementById("driveLink");

    // Preserve attachment type from server (if validation error occurred)
    const serverAttachmentType = "${attachmentType}";
    if (serverAttachmentType === "link") {
        linkRadio.checked = true;
        fileRadio.checked = false;
    } else {
        fileRadio.checked = true;
        linkRadio.checked = false;
    }

    function toggleAttachmentType() {
        if (fileRadio.checked) {
            fileSection.style.display = "block";
            linkSection.style.display = "none";
            // Only clear link input when switching from link to file (not on page load)
            if (serverAttachmentType !== "link") {
                linkInput.value = "";
            }
        } else if (linkRadio.checked) {
            fileSection.style.display = "none";
            linkSection.style.display = "block";
            // Clear file input when switching to link
            fileInput.value = "";
        }
    }

    // Add event listeners
    fileRadio.addEventListener("change", toggleAttachmentType);
    linkRadio.addEventListener("change", toggleAttachmentType);

    // Initial state
    toggleAttachmentType();
});

// Handle request type switching
document.addEventListener("DOMContentLoaded", function () {
    const editRadio = document.getElementById("editExistingRecord");
    const addRadio = document.getElementById("addNewRecord");
    const editSection = document.getElementById("editRecordSection");
    const addSection = document.getElementById("addRecordSection");

    function toggleRequestType() {
        if (editRadio.checked) {
            editSection.style.display = "block";
            addSection.style.display = "none";

            // Disable required for add section inputs
            const addInputs = addSection.querySelectorAll('input[required]');
            addInputs.forEach(input => input.removeAttribute('required'));

        } else if (addRadio.checked) {
            editSection.style.display = "none";
            addSection.style.display = "block";

            // Enable required for add section inputs that are visible
            const addInputs = addSection.querySelectorAll('.new-record-date, .new-record-checkin, .new-record-checkout');
            addInputs.forEach(input => {
                const recordItem = input.closest('.new-record-item');
                if (recordItem && recordItem.style.display !== 'none') {
                    input.setAttribute('required', 'required');
                }
            });
        }
    }

    // Add event listeners
    editRadio.addEventListener("change", toggleRequestType);
    addRadio.addEventListener("change", toggleRequestType);

    // Initial state
    toggleRequestType();
});

// Handle multiple new records
document.addEventListener("DOMContentLoaded", function () {
    const addNewRecordBtn = document.getElementById("addNewRecordBtn");
    const newRecordsContainer = document.getElementById("newRecordsContainer");
    
    // Initialize recordCounter based on existing preserved records
    let recordCounter = newRecordsContainer.querySelectorAll('.new-record-item').length;

    // Function to create a new record form
    function createNewRecordForm() {
        recordCounter++;
        const recordDiv = document.createElement("div");
        recordDiv.className = "card border-primary mb-3 new-record-item";
        recordDiv.setAttribute("data-record-id", recordCounter);

        recordDiv.innerHTML = `
                        <div class="card-header d-flex justify-content-between align-items-center py-2">
                            <small class="text-muted">Record #${recordCounter}</small>
                            <button type="button" class="btn btn-sm btn-danger remove-record-btn">
                                <i class="fas fa-times"></i>
                            </button>
                        </div>
                        <div class="card-body">
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label class="form-label">Date <span class="text-danger">*</span></label>
                                    <input type="date" name="newRecordDate_${recordCounter}" 
                                           class="form-control new-record-date" required />
                                    <div class="error-message"></div>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Check-in Time <span class="text-danger">*</span></label>
                                    <input type="time" name="newRecordCheckIn_${recordCounter}" 
                                           class="form-control new-record-checkin" required />
                                    <div class="error-message"></div>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Check-out Time <span class="text-danger">*</span></label>
                                    <input type="time" name="newRecordCheckOut_${recordCounter}" 
                                           class="form-control new-record-checkout" required />
                                    <div class="error-message"></div>
                                </div>
                            </div>
                        </div>
                    `;

        return recordDiv;
    }

    // Add new record when button clicked
    addNewRecordBtn.addEventListener("click", function () {
        const newRecord = createNewRecordForm();
        newRecordsContainer.appendChild(newRecord);

        // Add event listener for remove button
        const removeBtn = newRecord.querySelector(".remove-record-btn");
        removeBtn.addEventListener("click", function () {
            newRecord.remove();
            updateRecordNumbers();
        });

        // Add validation for time inputs
        addTimeValidation(newRecord);
    });

    // Function to update record numbers after removal
    function updateRecordNumbers() {
        const records = newRecordsContainer.querySelectorAll(".new-record-item");
        records.forEach((record, index) => {
            const header = record.querySelector(".card-header small");
            header.textContent = `Record #${index + 1}`;
        });
    }

    // Function to add time validation
    function addTimeValidation(recordDiv) {
        const checkInInput = recordDiv.querySelector(".new-record-checkin");
        const checkOutInput = recordDiv.querySelector(".new-record-checkout");
        const minTime = "06:00";
        const maxTime = "23:59";

        function showError(input, message) {
            const errorDiv = input.parentElement.querySelector(".error-message");
            if (errorDiv) {
                errorDiv.textContent = message;
                errorDiv.style.display = "block";
            }
        }

        function clearError(input) {
            const errorDiv = input.parentElement.querySelector(".error-message");
            if (errorDiv) {
                errorDiv.textContent = "";
                errorDiv.style.display = "none";
            }
        }

        function isTimeInRange(time) {
            return time >= minTime && time <= maxTime;
        }

        function validateTimes() {
            clearError(checkInInput);
            clearError(checkOutInput);

            const checkIn = checkInInput.value;
            const checkOut = checkOutInput.value;

            if (!checkIn && !checkOut)
                return;

            if (checkIn && !isTimeInRange(checkIn)) {
                showError(checkInInput, "Check-in time must be between 06:00 and 23:59.");
                checkInInput.value = "";
                return;
            }

            if (currentPage < totalPages) {
                const next = document.createElement("li");
                next.className = "page-item";
                next.innerHTML = `<a class="page-link" href="#">Next</a>`;
                next.addEventListener("click", e => {
                    e.preventDefault();
                    currentPage++;
                    renderRows();
                });
                paginationContainer.appendChild(next);
            }

            if (checkIn && checkOut && checkIn >= checkOut) {
                showError(checkOutInput, "Check-in time must be earlier than check-out time.");
                checkOutInput.value = "";
            }
        }

        checkInInput.addEventListener("change", validateTimes);
        checkOutInput.addEventListener("change", validateTimes);
    }

    // Collect new records data when form is submitted
    const appealForm = document.getElementById("appealForm");
    if (appealForm) {
        appealForm.addEventListener("submit", function (e) {
            const addRadio = document.getElementById("addNewRecord");
            if (addRadio && addRadio.checked) {
                const newRecords = [];
                const recordItems = newRecordsContainer.querySelectorAll(".new-record-item");

                recordItems.forEach(item => {
                    const date = item.querySelector(".new-record-date").value;
                    const checkIn = item.querySelector(".new-record-checkin").value;
                    const checkOut = item.querySelector(".new-record-checkout").value;

                    if (date && checkIn && checkOut) {
                        newRecords.push({
                            date: date,
                            checkIn: checkIn,
                            checkOut: checkOut
                        });
                    }
                });

                // Store in hidden input
                const hiddenInput = document.getElementById("new_records_data");
                if (hiddenInput) {
                    hiddenInput.value = JSON.stringify(newRecords);
                }
            }
        });
    }

    // Add event listeners for existing preserved records
    newRecordsContainer.querySelectorAll('.new-record-item').forEach(item => {
        const removeBtn = item.querySelector('.remove-record-btn');
        if (removeBtn) {
            removeBtn.addEventListener('click', function () {
                item.remove();
                updateRecordNumbers();
            });
        }
        addTimeValidation(item);
    });

    // Add first record automatically when "Add New Record" is selected
    const addRadio = document.getElementById("addNewRecord");
    if (addRadio) {
        addRadio.addEventListener("change", function () {
            if (this.checked && newRecordsContainer.children.length === 0) {
                addNewRecordBtn.click();
            }
        });
    }
});

// Attachment validation - require either file or link
document.addEventListener("DOMContentLoaded", function () {
    const appealForm = document.getElementById("appealForm");
    const fileInput = document.getElementById("attachments");
    const linkInput = document.getElementById("driveLink");
    const fileRadio = document.getElementById("attachmentTypeFile");
    const linkRadio = document.getElementById("attachmentTypeLink");
    const attachmentError = document.getElementById("attachmentError");

    function validateAttachment() {
        let hasFile = false;
        let hasLink = false;

        // Check if file is selected
        if (fileRadio && fileRadio.checked && fileInput && fileInput.files && fileInput.files.length > 0) {
            hasFile = true;
        }

        // Check if link is provided
        if (linkRadio && linkRadio.checked && linkInput && linkInput.value && linkInput.value.trim() !== "") {
            hasLink = true;
        }

        return hasFile || hasLink;
    }

    // Hide error message when user makes changes (but don't validate yet)
    function hideAttachmentError() {
        if (attachmentError) {
            attachmentError.style.display = "none";
        }
        // Clear custom validity messages
        if (fileInput)
            fileInput.setCustomValidity("");
        if (linkInput)
            linkInput.setCustomValidity("");
    }

    // Add validation only on form submit
    if (appealForm) {
        appealForm.addEventListener("submit", function (e) {
            let isValid = true;

            // Validate request type selection
            if (!validateRequestType()) {
                isValid = false;
            }

            // Validate attachment - only show error on submit
            if (!validateAttachment()) {
                isValid = false;
                if (attachmentError) {
                    attachmentError.style.display = "block";
                    if (fileRadio && fileRadio.checked && fileInput) {
                        fileInput.setCustomValidity("Please select at least one file or switch to Google Drive link.");
                    } else if (linkInput) {
                        linkInput.setCustomValidity("Please provide a Google Drive link or switch to file upload.");
                    }
                }
            } else {
                // Hide error if validation passes
                if (attachmentError) {
                    attachmentError.style.display = "none";
                }
                if (fileInput)
                    fileInput.setCustomValidity("");
                if (linkInput)
                    linkInput.setCustomValidity("");
            }

            if (!isValid) {
                e.preventDefault();
                e.stopPropagation();

                // Scroll to first error
                const firstError = document.querySelector('#requestTypeError[style*="block"], #attachmentError[style*="block"]');
                if (firstError) {
                    firstError.scrollIntoView({behavior: 'smooth', block: 'center'});
                }
                return false;
            }
        });
    }

    // Hide error messages when user makes changes (but don't validate)
    if (fileInput) {
        fileInput.addEventListener("change", hideAttachmentError);
    }

    if (linkInput) {
        linkInput.addEventListener("input", hideAttachmentError);
    }

    // Hide error when switching attachment types
    if (fileRadio) {
        fileRadio.addEventListener("change", hideAttachmentError);
    }

    if (linkRadio) {
        linkRadio.addEventListener("change", hideAttachmentError);
    }

    // Request type validation function
    function validateRequestType() {
        const editRadio = document.getElementById("editExistingRecord");
        const addRadio = document.getElementById("addNewRecord");
        const requestTypeError = document.getElementById("requestTypeError");
        const selectedRecordsList = document.getElementById("selectedRecordsList");
        const newRecordsContainer = document.getElementById("newRecordsContainer");

        let hasValidSelection = false;

        if (editRadio && editRadio.checked) {
            // Check if user has selected records to edit
            if (selectedRecordsList && selectedRecordsList.children.length > 0) {
                hasValidSelection = true;
            }
        } else if (addRadio && addRadio.checked) {
            // Check if user has added new records
            if (newRecordsContainer && newRecordsContainer.children.length > 0) {
                // Also check if the records have required fields filled
                const recordItems = newRecordsContainer.querySelectorAll(".new-record-item");
                let hasCompleteRecord = false;

                recordItems.forEach(item => {
                    const date = item.querySelector(".new-record-date").value;
                    const checkIn = item.querySelector(".new-record-checkin").value;
                    const checkOut = item.querySelector(".new-record-checkout").value;

                    if (date && checkIn && checkOut) {
                        hasCompleteRecord = true;
                    }
                });

                if (hasCompleteRecord) {
                    hasValidSelection = true;
                }
            }
        }

        if (requestTypeError) {
            if (hasValidSelection) {
                requestTypeError.style.display = "none";
            } else {
                requestTypeError.style.display = "block";
            }
        }

        return hasValidSelection;
    }
});

// ===== SIMPLE DROPDOWN FIX =====
document.addEventListener('DOMContentLoaded', function() {
    setTimeout(function() {
        console.log('=== FIXING DROPDOWN ===');
        
        const dropdownToggles = document.querySelectorAll('[data-bs-toggle="dropdown"]');
        console.log('Found dropdown toggles:', dropdownToggles.length);
        
        dropdownToggles.forEach((toggle, index) => {
            // Check if dropdown already has an instance
            let existingInstance = bootstrap.Dropdown.getInstance(toggle);
            
            if (existingInstance) {
                console.log(`Dropdown ${index + 1} already initialized, disposing...`);
                existingInstance.dispose();
            }
            
            // Remove the problematic data-bs-auto-close="outside" attribute
            toggle.removeAttribute('data-bs-auto-close');
            
            // Create new instance with default behavior
            const newInstance = new bootstrap.Dropdown(toggle);
            
            // Ensure the toggle element is properly configured
            toggle.setAttribute('role', 'button');
            toggle.setAttribute('aria-haspopup', 'true');
            toggle.setAttribute('aria-expanded', 'false');
            
            console.log(`✅ Fixed dropdown ${index + 1}:`, {
                element: toggle,
                instance: newInstance,
                autoClose: 'default (true)'
            });
            
            // Add Bootstrap event listeners for debugging
            toggle.addEventListener('show.bs.dropdown', function(e) {
                console.log(`🔽 Dropdown ${index + 1} showing...`);
            });
            
            toggle.addEventListener('shown.bs.dropdown', function(e) {
                console.log(`✅ Dropdown ${index + 1} shown!`);
            });
            
            toggle.addEventListener('hide.bs.dropdown', function(e) {
                console.log(`🔼 Dropdown ${index + 1} hiding...`);
            });
            
            toggle.addEventListener('hidden.bs.dropdown', function(e) {
                console.log(`❌ Dropdown ${index + 1} hidden!`);
            });
            
            // Add click test and ensure proper event handling
            toggle.addEventListener('click', function(e) {
                console.log(`Dropdown ${index + 1} clicked:`, {
                    target: e.target.tagName,
                    currentTarget: e.currentTarget.tagName,
                    instance: bootstrap.Dropdown.getInstance(toggle),
                    defaultPrevented: e.defaultPrevented
                });
                
                // Ensure the click is not prevented by other scripts
                if (e.defaultPrevented) {
                    console.log('Click was prevented, manually toggling dropdown...');
                    const instance = bootstrap.Dropdown.getInstance(toggle);
                    if (instance) {
                        instance.toggle();
                    }
                }
            });
        });
        
        console.log('=== DROPDOWN FIX COMPLETE ===');
        
        // Add global test function
        window.testDropdown = function() {
            const dropdown = document.querySelector('[data-bs-toggle="dropdown"]');
            if (dropdown) {
                const instance = bootstrap.Dropdown.getInstance(dropdown);
                console.log('Testing dropdown:', {
                    element: dropdown,
                    instance: instance,
                    isShown: dropdown.classList.contains('show')
                });
                
                if (instance) {
                    if (dropdown.classList.contains('show')) {
                        instance.hide();
                        console.log('Hiding dropdown...');
                    } else {
                        instance.show();
                        console.log('Showing dropdown...');
                    }
                } else {
                    console.error('No dropdown instance found!');
                }
            }
        };
        
        console.log('Run testDropdown() to manually test');
    }, 300); // Increase delay to ensure no conflicts
});
