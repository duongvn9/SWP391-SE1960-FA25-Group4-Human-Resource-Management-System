function selectAll(source) {
    const checkboxes = document.querySelectorAll('#attendanceTable tbody input[type="checkbox"]');
    checkboxes.forEach(cb => cb.checked = source.checked);
}

