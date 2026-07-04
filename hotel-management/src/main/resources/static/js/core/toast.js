/**
 * Utility functions for SweetAlert2 Toasts and Confirm Dialogs
 */

const Toast = Swal.mixin({
    toast: true,
    position: 'top-end',
    showConfirmButton: false,
    timer: 3000,
    timerProgressBar: true,
    didOpen: (toast) => {
        toast.onmouseenter = Swal.stopTimer;
        toast.onmouseleave = Swal.resumeTimer;
    }
});

const ToastUtils = {
    success: (message) => {
        Toast.fire({
            icon: 'success',
            title: message
        });
    },
    error: (message) => {
        Toast.fire({
            icon: 'error',
            title: message
        });
    },
    warning: (message) => {
        Toast.fire({
            icon: 'warning',
            title: message
        });
    },
    info: (message) => {
        Toast.fire({
            icon: 'info',
            title: message
        });
    },
    /**
     * Show a confirmation dialog for destructive actions
     * @param {string} title - The title of the dialog
     * @param {string} text - The text of the dialog
     * @param {string} confirmButtonText - The text of the confirm button
     * @param {Function} onConfirm - The callback to execute if confirmed
     */
    confirmDelete: (title, text, confirmButtonText, onConfirm) => {
        Swal.fire({
            title: title || 'Are you sure?',
            text: text || "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            confirmButtonText: confirmButtonText || 'Yes, delete it!'
        }).then((result) => {
            if (result.isConfirmed) {
                onConfirm();
            }
        });
    }
};
