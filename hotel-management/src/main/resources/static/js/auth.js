document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            errorMessage.classList.add('d-none');
            errorMessage.textContent = '';

            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const loginBtn = document.getElementById('loginBtn');
            const originalBtnText = loginBtn.innerHTML;

            loginBtn.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Logging in...';
            loginBtn.disabled = true;

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ username, password })
                });

                const data = await response.json();

                if (response.ok && data.code === 200) {
                    // Save JWT and user info
                    localStorage.setItem('token', data.result.accessToken);
                    localStorage.setItem('user', JSON.stringify(data.result.user));
                    
                    // Redirect based on role
                    const role = data.result.user.roleName.toUpperCase();
                    if (role === 'CUSTOMER') {
                        window.location.href = '/customer/dashboard';
                    } else if (role === 'OWNER') {
                        window.location.href = '/owner/dashboard';
                    } else if (role === 'RECEPTIONIST') {
                        window.location.href = '/reception/dashboard';
                    } else if (role === 'HOUSEKEEPING') {
                        window.location.href = '/housekeeping/dashboard';
                    } else {
                        window.location.href = '/admin/dashboard';
                    }
                } else {
                    errorMessage.textContent = data.message || 'Login failed. Please check your credentials.';
                    errorMessage.classList.remove('d-none');
                }
            } catch (error) {
                errorMessage.textContent = 'A network error occurred. Please try again.';
                errorMessage.classList.remove('d-none');
            } finally {
                loginBtn.innerHTML = originalBtnText;
                loginBtn.disabled = false;
            }
        });
    }

    // Check for logout parameter
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.get('error') === 'unauthorized') {
        errorMessage.textContent = 'Session expired. Please log in again.';
        errorMessage.classList.remove('d-none');
    }
});
