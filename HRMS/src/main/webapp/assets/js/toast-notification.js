// Toast Notification System
(function() {
    'use strict';

    // Make showToast globally available
    window.showToast = function(message, type = 'success') {
        const container = document.getElementById('toastContainer');
        if (!container) {
            console.error('Toast container not found!');
            return;
        }

        const icons = {
            success: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
            error: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 8V12M12 16H12.01M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
            warning: '<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 9V13M12 17H12.01M10.29 3.86L1.82 18C1.64537 18.3024 1.55296 18.6453 1.55199 18.9945C1.55101 19.3437 1.64151 19.6871 1.81445 19.9905C1.98738 20.2939 2.23675 20.5467 2.53773 20.7239C2.83871 20.9011 3.18082 20.9962 3.53 21H20.47C20.8192 20.9962 21.1613 20.9011 21.4623 20.7239C21.7633 20.5467 22.0126 20.2939 22.1856 19.9905C22.3585 19.6871 22.449 19.3437 22.448 18.9945C22.447 18.6453 22.3546 18.3024 22.18 18L13.71 3.86C13.5317 3.56611 13.2807 3.32312 12.9812 3.15448C12.6817 2.98585 12.3437 2.89725 12 2.89725C11.6563 2.89725 11.3183 2.98585 11.0188 3.15448C10.7193 3.32312 10.4683 3.56611 10.29 3.86Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>',
        };

        const titles = {
            success: 'Success',
            error: 'Error',
            warning: 'Warning'
        };

        const toast = document.createElement('div');
        toast.className = `toast-notification ${type}`;
        toast.innerHTML = `
            <div class="toast-icon">${icons[type] || icons.success}</div>
            <div class="toast-content">
                <div class="toast-title">${titles[type] || titles.success}</div>
                <div class="toast-message">${message}</div>
            </div>
            <button class="toast-close" onclick="window.closeToast(this)">&times;</button>
            <div class="toast-progress"></div>
        `;

        container.appendChild(toast);

        // Auto remove after 15 seconds
        setTimeout(() => {
            window.closeToast(toast.querySelector('.toast-close'));
        }, 15000);
    };

    window.closeToast = function(button) {
        const toast = button.closest('.toast-notification');
        if (!toast) return;
        
        toast.classList.add('hiding');
        setTimeout(() => {
            toast.remove();
        }, 300);
    };

    // Show messages on page load
    document.addEventListener('DOMContentLoaded', () => {
        const messages = document.querySelectorAll('.hidden-message');
        messages.forEach((msg) => {
            const type = msg.getAttribute('data-type');
            const message = msg.getAttribute('data-message');
            
            if (message) {
                window.showToast(message, type);
            }
        });
    });
})();
