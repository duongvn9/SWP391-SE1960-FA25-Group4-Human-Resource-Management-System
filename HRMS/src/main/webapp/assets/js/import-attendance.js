let selectedCells = new Set(); // Lưu các td được chọn
let isSelecting = false;
let startCell = null;

function showTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.style.display = 'none');
    document.getElementById(tabId).style.display = 'block';
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
        btn.setAttribute('aria-selected', 'false');
    });
    document.getElementById(tabId + '-btn').classList.add('active');
    document.getElementById(tabId + '-btn').setAttribute('aria-selected', 'true');
}

// ==== Manual Entry ====
function addRow(tableBody, data = Array(7).fill('')) {
    const newRow = tableBody.insertRow();
    const cols = ['employeeId', 'date', 'checkIn', 'checkOut', 'status', 'source', 'note'];

    cols.forEach((col, index) => {
        const cell = newRow.insertCell();
        const input = document.createElement('input');
        input.type = 'text';
        input.placeholder = col;
        input.value = data[index] || '';
        input.addEventListener('input', checkLastRow);
        input.addEventListener('keydown', handleKeyNavigation);
        input.addEventListener('paste', handlePaste);
        cell.appendChild(input);
    });

    const deleteCell = newRow.insertCell();
    const delBtn = document.createElement('button');
    delBtn.textContent = 'Delete';
    delBtn.className = 'delete-btn';
    delBtn.onclick = () => {
        tableBody.deleteRow(newRow.rowIndex - 1);
        checkLastRow(); // Ensure at least one row remains
    };
    deleteCell.appendChild(delBtn);
}

function checkLastRow() {
    const table = document.getElementById('manualTable').getElementsByTagName('tbody')[0];
    const lastRow = table.rows[table.rows.length - 1];
    const inputs = lastRow.querySelectorAll('input');
    const hasData = Array.from(inputs).some(input => input.value.trim());
    if (hasData) {
        addRow(table);
    }
}

function handleKeyNavigation(event) {
    const input = event.target;
    const cell = input.parentElement;
    const row = cell.parentElement;
    const table = row.parentElement;
    const rowIndex = row.rowIndex - 1; // Adjust for thead
    const cellIndex = Array.from(row.cells).indexOf(cell);

    if (event.key === 'Enter') {
        event.preventDefault();
        const nextRow = table.rows[rowIndex + 1];
        if (nextRow) {
            nextRow.cells[cellIndex].querySelector('input').focus();
        } else {
            addRow(table);
            table.rows[rowIndex + 1].cells[cellIndex].querySelector('input').focus();
        }
    } else if (event.key === 'Tab' && !event.shiftKey) {
        event.preventDefault();
        const nextCell = row.cells[cellIndex + 1];
        if (nextCell && nextCell.querySelector('input')) {
            nextCell.querySelector('input').focus();
        } else if (rowIndex < table.rows.length - 1) {
            table.rows[rowIndex + 1].cells[0].querySelector('input').focus();
        } else {
            addRow(table);
            table.rows[rowIndex + 1].cells[0].querySelector('input').focus();
        }
    }
}

function handlePaste(event) {
    event.preventDefault();
    const clipboardData = event.clipboardData.getData('text');
    const rows = clipboardData.split('\n').map(row => row.split('\t').map(cell => cell.trim()));

    const table = document.getElementById('manualTable').getElementsByTagName('tbody')[0];
    const currentRow = event.target.parentElement.parentElement;
    const startRowIndex = currentRow.rowIndex - 1; // Adjust for thead
    const startCellIndex = Array.from(currentRow.cells).indexOf(event.target.parentElement);

    rows.forEach((rowData, rowOffset) => {
        if (rowOffset > 0 && startRowIndex + rowOffset >= table.rows.length) {
            addRow(table, rowData);
        }
        const targetRow = table.rows[startRowIndex + rowOffset];
        rowData.forEach((cellData, cellOffset) => {
            const targetCellIndex = startCellIndex + cellOffset;
            if (targetCellIndex < targetRow.cells.length - 1) { // Exclude delete button cell
                const input = targetRow.cells[targetCellIndex].querySelector('input');
                if (input)
                    input.value = cellData;
            }
        });
    });

    checkLastRow();
}

function validateManual() {
    const rows = document.querySelectorAll('#manualTable tbody tr');
    let errors = 0;
    const dateRegex = /^\d{4}-\d{2}-\d{2}$/;
    const timeRegex = /^\d{2}:\d{2}$/;
    rows.forEach((row, i) => {
        const inputs = row.querySelectorAll('input');
        inputs.forEach((input, j) => {
            input.style.border = '';
            if (j === 0 && !input.value.trim()) { // Employee ID
                input.style.border = '1px solid red';
                errors++;
            }
            if (j === 1 && (!input.value.trim() || !dateRegex.test(input.value))) { // Date
                input.style.border = '1px solid red';
                errors++;
            }
            if ((j === 2 || j === 3) && input.value && !timeRegex.test(input.value)) { // Check-in, Check-out
                input.style.border = '1px solid red';
                errors++;
            }
        });
    });
    alert(errors > 0 ? `Có ${errors} lỗi cần sửa.` : 'Dữ liệu hợp lệ.');
}

// ==== Chức năng chọn vùng và xóa nội dung ====
function clearSelection() {
    selectedCells.forEach(cell => cell.classList.remove('selected'));
    selectedCells.clear();
}

function highlightRange(startRow, startCol, endRow, endCol) {
    clearSelection();
    const tableBody = document.getElementById('manualTable').querySelector('tbody');
    const minRow = Math.min(startRow, endRow);
    const maxRow = Math.max(startRow, endRow);
    const minCol = Math.min(startCol, endCol);
    const maxCol = Math.max(startCol, endCol);

    for (let r = minRow; r <= maxRow; r++) {
        const row = tableBody.rows[r];
        for (let c = minCol; c <= maxCol; c++) {
            if (c < row.cells.length - 1) { // Exclude Action column
                const cell = row.cells[c];
                cell.classList.add('selected');
                selectedCells.add(cell);
            }
        }
    }
}

function initSelection() {
    const table = document.getElementById('manualTable');
    table.addEventListener('mousedown', (event) => {
        if (event.target.tagName === 'INPUT') {
            const cell = event.target.parentElement;
            if (cell.cellIndex < 7) { // Chỉ chọn ở các cột dữ liệu
                isSelecting = true;
                startCell = {row: cell.parentElement.rowIndex - 1, col: cell.cellIndex};
                clearSelection();
                highlightRange(startCell.row, startCell.col, startCell.row, startCell.col);
            }
        }
    });

    table.addEventListener('mousemove', (event) => {
        if (isSelecting && event.target.tagName === 'INPUT') {
            const cell = event.target.parentElement;
            if (cell.cellIndex < 7) {
                const endCell = {row: cell.parentElement.rowIndex - 1, col: cell.cellIndex};
                highlightRange(startCell.row, startCell.col, endCell.row, endCell.col);
            }
        }
    });

    document.addEventListener('mouseup', () => {
        isSelecting = false;
    });

    // Xóa nội dung khi nhấn Delete/Backspace
    document.addEventListener('keydown', (event) => {
        if ((event.key === 'Delete' || event.key === 'Backspace') && selectedCells.size > 0) {
            selectedCells.forEach(cell => {
                const input = cell.querySelector('input');
                if (input)
                    input.value = '';
            });
            event.preventDefault();
        } else if (event.ctrlKey && event.key === 'a') {
            event.preventDefault();
            const tableBody = table.querySelector('tbody');
            clearSelection();
            Array.from(tableBody.rows).forEach((row, rowIndex) => {
                Array.from(row.cells).forEach((cell, colIndex) => {
                    if (colIndex < 7) { // Exclude Action
                        cell.classList.add('selected');
                        selectedCells.add(cell);
                    }
                });
            });
        }
    });
}

window.onload = () => {
    showTab('manual');
    const table = document.getElementById('manualTable').getElementsByTagName('tbody')[0];
    addRow(table); // Ensure at least one empty row
    initSelection(); // Khởi tạo chức năng chọn vùng
};