document.getElementById("exportXLSBtn").addEventListener("click", function () {
    document.getElementById("exportType").value = "xls";
    document.getElementById("exportForm").submit();
});

document.getElementById("exportCSVBtn").addEventListener("click", function () {
    document.getElementById("exportType").value = "csv";
    document.getElementById("exportForm").submit();
});

document.getElementById("exportPDFBtn").addEventListener("click", function () {
    document.getElementById("exportType").value = "pdf";
    document.getElementById("exportForm").submit();
});

