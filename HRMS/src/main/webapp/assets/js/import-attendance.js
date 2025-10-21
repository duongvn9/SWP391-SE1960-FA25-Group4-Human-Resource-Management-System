function showTab(tabId) {
    document.querySelectorAll(".tab-content").forEach(tab => {
        tab.style.display = "none";
    });

    const selectedTab = document.getElementById(tabId);
    if (selectedTab) {
        selectedTab.style.display = "block";
    }

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

document.addEventListener("DOMContentLoaded", function () {
    const table = document.getElementById("manualTable").querySelector("tbody");
    const form = document.getElementById("manualImportForm");
    const feedback = document.getElementById("manualFeedback");
    const manualDataInput = document.getElementById("manualData");

    let isMouseDown = false, startCell = null;
    const selectedCells = new Set();

    table.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            const currentCell = document.activeElement;
            if (currentCell?.tagName === "TD") {
                const newRow = createRow();
                table.appendChild(newRow);
                newRow.cells[0].focus();
            }
        }
    });

    table.addEventListener("keydown", function (e) {
        if (e.key === "Delete" || e.key === "Backspace") {
            if (selectedCells.size > 0) {
                e.preventDefault();
                selectedCells.forEach(cell => {
                    cell.innerText = "";
                    cell.classList.remove("selected");
                });
                selectedCells.clear();
            }
        }
    });

    table.addEventListener("input", function (e) {
        const cell = e.target;
        if (cell.tagName === "TD") {
            cell.style.width = "auto";
            const sw = cell.scrollWidth;
            cell.style.width = sw < 500 ? `${sw}px` : "500px";
            cell.style.whiteSpace = sw < 500 ? "nowrap" : "pre-wrap";
        }
    });

    table.addEventListener("mousedown", function (e) {
        if (e.target.tagName === "TD") {
            clearSelection();
            isMouseDown = true;
            startCell = e.target;
            startCell.classList.add("selected");
            selectedCells.add(startCell);
            e.preventDefault();
        }
    });

    table.addEventListener("mouseover", function (e) {
        if (isMouseDown && e.target.tagName === "TD")
            highlightRange(startCell, e.target);
    });

    document.addEventListener("mouseup", function () {
        isMouseDown = false;
        startCell = null;
    });

    table.addEventListener("click", function (e) {
        if (e.target.tagName === "TD" && !isMouseDown) {
            clearSelection();
            e.target.classList.add("selected");
            selectedCells.add(e.target);
            e.target.focus();
        }
    });

    form.addEventListener("submit", function (e) {
        e.preventDefault();

        const rows = Array.from(table.querySelectorAll("tbody tr"));
        const data = [];
        const errors = [];

        const dateRegex = /^([0-2]?[0-9]|3[01])\/(0?[1-9]|1[0-2])\/\d{4}$/; // dd/MM/yyyy
        const timeRegex = /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/; // H:mm or HH:mm

        rows.forEach((row, index) => {
            const cells = row.querySelectorAll("td");
            const rowNum = index + 1;

            const employeeId = cells[0]?.innerText.trim();
            const dateStr = cells[1]?.innerText.trim();
            const checkIn = cells[2]?.innerText.trim();
            const checkOut = cells[3]?.innerText.trim();
            const status = cells[4]?.innerText.trim();

            // Validate trống
            if (!employeeId)
                errors.push(`Row ${rowNum}: Employee ID cannot be empty.`);
            if (!dateStr)
                errors.push(`Row ${rowNum}: Date cannot be empty.`);
            if (!checkIn)
                errors.push(`Row ${rowNum}: Check-in cannot be empty.`);
            if (!checkOut)
                errors.push(`Row ${rowNum}: Check-out cannot be empty.`);
            if (!status)
                errors.push(`Row ${rowNum}: Status cannot be empty.`);

            if (dateStr && !dateRegex.test(dateStr)) {
                errors.push(`Row ${rowNum}: Date must be in dd/MM/yyyy format.`);
            }

            if (checkIn && !timeRegex.test(checkIn)) {
                errors.push(`Row ${rowNum}: Check-in must be in H:mm format.`);
            }
            if (checkOut && !timeRegex.test(checkOut)) {
                errors.push(`Row ${rowNum}: Check-out must be in H:mm format.`);
            }

            data.push({
                employeeId,
                date: dateStr,
                checkIn,
                checkOut,
                status
            });
        });

        if (errors.length > 0) {
            feedback.style.display = "block";
            feedback.style.color = "red";
            feedback.innerHTML = errors.join("<br/>");
            return; // dừng submit
        }

        manualDataInput.value = JSON.stringify(data);
        form.submit();
    });

    function createRow() {
        const newRow = document.createElement("tr");
        for (let i = 0; i < 5; i++) {
            const td = document.createElement("td");
            td.contentEditable = "true";
            td.style.border = "1px solid #ccc";
            td.style.minWidth = "100px";
            td.style.maxWidth = "200px";
            td.style.whiteSpace = "pre-wrap";
            td.style.wordBreak = "break-word";
            newRow.appendChild(td);
        }
        return newRow;
    }

    function clearSelection() {
        selectedCells.forEach(c => c.classList.remove("selected"));
        selectedCells.clear();
    }

    function highlightRange(start, end) {
        clearSelection();
        const allCells = Array.from(table.querySelectorAll("td"));
        const [min, max] = [Math.min(allCells.indexOf(start), allCells.indexOf(end)), Math.max(allCells.indexOf(start), allCells.indexOf(end))];
        for (let i = min; i <= max; i++) {
            allCells[i].classList.add("selected");
            selectedCells.add(allCells[i]);
        }
    }
});

