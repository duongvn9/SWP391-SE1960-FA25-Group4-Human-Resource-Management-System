/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */




/** Hiển thị toast nhỏ ở góc phải màn hình */
function showToast(message, type = "info") {
  const container = document.getElementById("toast-container");
  const toast = document.createElement("div");
  toast.className = `toast align-items-center text-bg-${type} border-0 show mb-2`;
  toast.role = "alert";
  toast.innerHTML = `
    <div class="d-flex">
      <div class="toast-body">${message}</div>
      <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
    </div>
  `;
  container.appendChild(toast);
  setTimeout(() => toast.remove(), 3000);
}
const urlParams = new URLSearchParams(window.location.search);
if (urlParams.get("success") === "submitted") {
  showToast("Recruitment request submitted successfully!", "success");
}

// No automatic action override - form's action is set in the JSP
