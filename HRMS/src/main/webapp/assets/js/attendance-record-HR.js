document.addEventListener("DOMContentLoaded", function () {
    const toggle = document.getElementById("switchInput");
    const status = document.getElementById("sliderStatus");
    const exportForm = document.getElementById("exportForm");
    const exportTypeInput = document.getElementById("exportType");

    // --- Toggle switch --- //
    function sendToggleStatus(state) {
        fetch(`${window.location.origin}/attendance/toggleStatus`, {// endpoint riêng
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({status: state})
        })
                .then(res => res.json())
                .then(data => console.log('Toggle status response:', data))
                .catch(err => console.error('Error sending toggle status:', err));
    }

    toggle.addEventListener("change", function () {
        const currentState = toggle.checked ? "Locked" : "Unlocked";
        status.textContent = currentState;
        sendToggleStatus(currentState); // chỉ gửi khi click
    });

    // --- Import button --- //
    window.importAttendance = function () {
        window.location.href = `${window.location.origin}/attendance/import`;
    };

    // --- Export buttons --- //
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
                exportForm.submit(); // POST riêng
            });
        }
    });

    // --- Enable/disable edit columns --- //
    window.enableEdit = function () {
        const editCols = document.querySelectorAll('.edit-col');
        const editBtn = document.getElementById('editBtn');
        const isEditing = editBtn.classList.toggle('active');

        editCols.forEach(col => {
            col.style.display = isEditing ? '' : 'none';
        });

        editBtn.textContent = isEditing ? 'Done' : 'Edit';
    };

    // --- Submit Update/Delete action --- //
    window.submitAction = function (btn, actionType) {
        const form = btn.closest('form');
        form.querySelector('.formAction').value = actionType;

        if (actionType === 'update') {
            openEditModalFromForm(form);
        } else if (actionType === 'delete') {
            form.submit(); // POST xóa
        }
    };

    // --- Modal edit --- //
    function openEditModalFromForm(form) {
        document.getElementById('modalAttendanceId').value = form.querySelector('[name="attendanceId"]') ? form.querySelector('[name="attendanceId"]').value : '';
        document.getElementById('modalCheckIn').value = form.querySelector('[name="checkIn"]').value;
        document.getElementById('modalCheckOut').value = form.querySelector('[name="checkOut"]').value;
        document.getElementById('modalStatus').value = form.querySelector('[name="status"]').value;
        document.getElementById('modalSource').value = form.querySelector('[name="source"]').value;

        document.getElementById('editModal').style.display = 'flex';
    }


    window.closeModal = function () {
        document.getElementById('editModal').style.display = 'none';
    };

    window.submitEdit = function () {
        const form = document.getElementById('editForm');
        const formData = new FormData(form);

        fetch(`${window.location.origin}/attendance/record/HR`, {
            method: 'POST',
            body: formData
        }).then(res => {
            if (res.ok) {
                alert('Updated successfully');
                closeModal();
                location.reload();
            } else {
                alert('Update failed');
            }
        });
    };
});
