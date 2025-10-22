document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.getElementById("switchInput");
    const status = document.getElementById("sliderStatus");
    const exportForm = document.getElementById("exportForm");
    const exportTypeInput = document.getElementById("exportType");
    const editBtn = document.getElementById("editBtn");

    if (toggle) {
        toggle.addEventListener("change", () => {
            const locked = toggle.checked;
            const periodId = toggle.dataset.periodId;

            const lockForm = document.createElement("form");
            lockForm.method = "post";
            lockForm.action = `${window.location.pathname}`;

            // action
            const actionInput = document.createElement("input");
            actionInput.type = "hidden";
            actionInput.name = "action";
            actionInput.value = "toggleLock";
            lockForm.appendChild(actionInput);

            // locked
            const lockedInput = document.createElement("input");
            lockedInput.type = "hidden";
            lockedInput.name = "locked";
            lockedInput.value = locked;
            lockForm.appendChild(lockedInput);

            // periodId
            const periodIdInput = document.createElement("input");
            periodIdInput.type = "hidden";
            periodIdInput.name = "periodId";
            periodIdInput.value = periodId;
            lockForm.appendChild(periodIdInput);

            const filterForm = document.getElementById("filterForm");
            if (filterForm) {
                const filterNames = ["employeeKeyword", "department", "startDate", "endDate", "status", "source", "periodSelect"];
                filterNames.forEach(name => {
                    const orig = filterForm.querySelector(`[name="${name}"]`);
                    const clone = document.createElement("input");
                    clone.type = "hidden";
                    clone.name = name;
                    clone.value = orig ? orig.value : "";
                    lockForm.appendChild(clone);
                });
            }

            document.body.appendChild(lockForm);
            lockForm.submit();
        });
    }

    window.importAttendance = () => {
        window.location.href = `${window.location.origin}/HRMS/attendance/import`;
    };

    [
        {id: "exportXLSBtn", type: "xls"},
        {id: "exportCSVBtn", type: "csv"},
        {id: "exportPDFBtn", type: "pdf"}
    ].forEach(btn => {
        const element = document.getElementById(btn.id);
        if (element) {
            element.addEventListener("click", () => {
                if (!exportForm || !exportTypeInput)
                    return;
                exportTypeInput.value = btn.type;
                exportForm.submit();
            });
        }
    });

    window.enableEdit = () => {
        const editCols = document.querySelectorAll(".edit-col");
        const isEditing = editBtn.classList.toggle("active");

        editCols.forEach(col => col.style.display = isEditing ? "" : "none");

        if (isEditing) {
            editBtn.textContent = "Done";
            editBtn.classList.remove("btn-edit");
            editBtn.classList.add("btn-done");
        } else {
            editBtn.textContent = "Edit";
            editBtn.classList.remove("btn-done");
            editBtn.classList.add("btn-edit");
        }
    };

// submit update/delete row
    window.submitAction = (btn, actionType) => {
        const rowForm = btn.closest("form.actionForm");
        if (!rowForm)
            return;

        rowForm.querySelector(".formAction").value = actionType;

        // clone filter từ #filterForm
        const filterForm = document.getElementById("filterForm");
        if (filterForm) {
            // xóa các clone cũ
            rowForm.querySelectorAll(".filter-clone").forEach(el => el.remove());

            filterForm.querySelectorAll("input, select").forEach(input => {
                if (!input.name)
                    return;
                const hidden = document.createElement("input");
                hidden.type = "hidden";
                hidden.name = input.name;
                hidden.value = input.value;
                hidden.classList.add("filter-clone");
                rowForm.appendChild(hidden);
            });
        }

        if (actionType === "update") {
            openEditModalFromForm(rowForm);
        } else if (actionType === "delete") {
            if (confirm("Are you sure you want to delete this record?")) {
                rowForm.submit();
            }
        }
    };

    // submit từ modal edit
    window.submitEdit = () => {
        const form = document.getElementById("editForm");
        if (!form)
            return;

        // xóa clone cũ
        form.querySelectorAll(".filter-clone").forEach(el => el.remove());

        // clone filter
        const filterForm = document.getElementById("filterForm");
        if (filterForm) {
            filterForm.querySelectorAll("input, select").forEach(input => {
                if (!input.name)
                    return;
                if (input.type === "button" || input.type === "submit")
                    return;

                const hidden = document.createElement("input");
                hidden.type = "hidden";
                hidden.name = input.name;
                hidden.value = input.value;
                hidden.classList.add("filter-clone");
                form.appendChild(hidden);
            });
        }

        console.log("Submitting edit form with filters:");
        form.querySelectorAll(".filter-clone").forEach(i => console.log(i.name, i.value));

        form.action = `${window.location.origin}/HRMS/attendance/record/HR`;
        form.method = "POST";
        form.submit();
    };

    const openEditModalFromForm = (form) => {
        const getVal = (name) => form.querySelector(`[name="${name}"]`)?.value || "";

        document.getElementById("modalEmpId").value = getVal("userIdEdit");
        document.getElementById("modalEmpName").value = getVal("employeeNameEdit");
        document.getElementById("modalDepartment").value = getVal("departmentEdit");
        document.getElementById("modalDate").value = getVal("dateEdit");
        document.getElementById("modalCheckIn").value = getVal("checkInEdit");
        document.getElementById("modalCheckOut").value = getVal("checkOutEdit");
        document.getElementById("modalStatus").value = getVal("statusEdit");
        document.getElementById("modalSource").value = getVal("sourceEdit");
        document.getElementById("modalPeriod").value = getVal("periodEdit");

        document.getElementById("checkInOld").value = getVal("checkInEdit");
        document.getElementById("checkOutOld").value = getVal("checkOutEdit");

        document.getElementById("editModal").style.display = "flex";
    };


    window.closeModal = () => {
        document.getElementById("editModal").style.display = "none";
    };

    window.submitEdit = () => {
        const form = document.getElementById("editForm");
        form.action = `${window.location.origin}/HRMS/attendance/record/HR`;
        form.method = "POST";
        form.submit();
    };
});
