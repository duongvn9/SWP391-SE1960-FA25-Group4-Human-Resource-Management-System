document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.getElementById("switchInput");
    const status = document.getElementById("sliderStatus");
    const exportForm = document.getElementById("exportForm");
    const exportTypeInput = document.getElementById("exportType");
    const editBtn = document.getElementById("editBtn");

    const sendToggleStatus = async (state) => {
        try {
            const res = await fetch(`${window.location.origin}/attendance/toggleStatus`, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({status: state})
            });
            const data = await res.json();
            console.log("Toggle status response:", data);
        } catch (err) {
            console.error("Error sending toggle status:", err);
        }
    };

    if (toggle) {
        toggle.addEventListener("change", () => {
            const currentState = toggle.checked ? "Locked" : "Unlocked";
            status.textContent = currentState;
            sendToggleStatus(currentState);
        });
    }

    window.importAttendance = () => {
        window.location.href = `${window.location.origin}/attendance/import`;
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

        editCols.forEach(col => {
            col.style.display = isEditing ? "" : "none";
        });

        editBtn.textContent = isEditing ? "Done" : "Edit";
    };

    window.submitAction = (btn, actionType) => {
        const form = btn.closest("form");
        if (!form)
            return;
        const actionInput = form.querySelector(".formAction");
        if (actionInput)
            actionInput.value = actionType;

        if (actionType === "update") {
            openEditModalFromForm(form);
        } else if (actionType === "delete") {
            if (confirm("Are you sure you want to delete this record?")) {
                form.submit();
            }
        }
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

        document.getElementById("editModal").style.display = "flex";
    };

    window.closeModal = () => {
        document.getElementById("editModal").style.display = "none";
    };

    window.submitEdit = () => {
        const form = document.getElementById("editForm");
        form.action = "http://localhost:9999/HRMS/attendance/record/HR";
        form.method = "POST";
        form.submit(); 
    };
});
