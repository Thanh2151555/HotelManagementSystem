/**
 * api.js - Core API Fetch wrapper handling JWT auth
 */

const API_BASE_URL = '/api';

/**
 * Wrapper for fetch API that auto-attaches JWT token
 * @param {string} endpoint - API endpoint (e.g. '/dashboard/summary')
 * @param {object} options - fetch options
 */
async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem('token');
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
        ...options,
        headers
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

        if (response.status === 401 || response.status === 403) {
            // Unauthorized, clear token and redirect
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = '/login?error=unauthorized';
            return null;
        }

        const data = await response.json();
        return { ok: response.ok, status: response.status, data };
    } catch (error) {
        console.error('API Fetch Error:', error);
        throw error;
    }
}

/**
 * Perform logout
 */
function logout() {
    // Optionally call backend logout endpoint here
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
}

function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    if (!userStr) return null;
    try {
        return JSON.parse(userStr);
    } catch (e) {
        return null;
    }
}
