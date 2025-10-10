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
document.getElementById("saveDraftBtn").addEventListener("click", function() {
    const form = document.getElementById("recruitmentRequestForm");
    
    form.action = `${contextPath}/requests/save-draft`;
    
    // 3. Gửi form (bao gồm cả file đính kèm)
    form.submit();
});

document.getElementById("recruitmentRequestForm").addEventListener("submit", function(event) {
    // Luôn đảm bảo action là /requests/create cho việc SUBMIT chính thức
    // (Trong trường hợp người dùng nhấn SUBMIT REQUEST)
    this.action = `${contextPath}/requests/create`;
});
