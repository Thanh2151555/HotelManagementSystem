/**
 * Utility functions for Bootstrap Modals
 */

const ModalUtils = {
    /**
     * Show a modal by ID
     * @param {string} modalId 
     */
    show: (modalId) => {
        const el = document.getElementById(modalId);
        if (el) {
            let modal = bootstrap.Modal.getInstance(el);
            if (!modal) {
                modal = new bootstrap.Modal(el);
            }
            modal.show();
        }
    },
    /**
     * Hide a modal by ID
     * @param {string} modalId 
     */
    hide: (modalId) => {
        const el = document.getElementById(modalId);
        if (el) {
            const modal = bootstrap.Modal.getInstance(el);
            if (modal) {
                modal.hide();
            }
        }
    },
    /**
     * Reset a form inside a modal
     * @param {string} formId 
     */
    resetForm: (formId) => {
        const form = document.getElementById(formId);
        if (form) {
            form.reset();
            form.classList.remove('was-validated');
            // Reset any hidden IDs that might be used for edits
            const idInput = form.querySelector('input[name="id"]');
            if (idInput) {
                idInput.value = '';
            }
        }
    },
    /**
     * Change button state to loading
     * @param {string} buttonId 
     * @param {string} loadingText 
     */
    setLoading: (buttonId, loadingText = 'Processing...') => {
        const btn = document.getElementById(buttonId);
        if (btn) {
            btn.dataset.originalText = btn.innerHTML;
            btn.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> ${loadingText}`;
            btn.disabled = true;
        }
    },
    /**
     * Restore button state from loading
     * @param {string} buttonId 
     */
    resetLoading: (buttonId) => {
        const btn = document.getElementById(buttonId);
        if (btn && btn.dataset.originalText) {
            btn.innerHTML = btn.dataset.originalText;
            btn.disabled = false;
        }
    }
};
