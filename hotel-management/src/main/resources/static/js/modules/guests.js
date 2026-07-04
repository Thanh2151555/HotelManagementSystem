/**
 * Guest Management Module
 */
const GuestModule = {
    currentPage: 0,
    pageSize: 10,
    
    init: () => {
        document.addEventListener('DOMContentLoaded', () => {
            GuestModule.loadGuests(0);
        });
    },

    loadGuests: async (page) => {
        GuestModule.currentPage = page;
        const keyword = document.getElementById('filterKeyword').value;
        const tbody = document.getElementById('guestsTableBody');
        
        tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted"><div class="spinner-border spinner-border-sm text-primary" role="status"></div> Loading...</td></tr>`;
        
        try {
            let url = `/guests?page=${page}&size=${GuestModule.pageSize}`;
            if (keyword) {
                url += `&keyword=${encodeURIComponent(keyword)}`;
            }
            
            const response = await apiFetch(url);
            if (response && response.ok) {
                GuestModule.renderTable(response.data.result);
                GuestModule.renderPagination(response.data.result);
            } else {
                tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-danger">Failed to load guests.</td></tr>`;
            }
        } catch (error) {
            console.error(error);
            tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-danger">An error occurred.</td></tr>`;
        }
    },

    renderTable: (pageData) => {
        const tbody = document.getElementById('guestsTableBody');
        tbody.innerHTML = '';
        
        if (!pageData.content || pageData.content.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center py-5 text-muted">
                        <i class="fa-solid fa-users fs-1 mb-3 opacity-50"></i>
                        <h5>No guests found</h5>
                        <p class="mb-0">Try adjusting your search filters or add a new guest.</p>
                    </td>
                </tr>`;
            return;
        }
        
        pageData.content.forEach(guest => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="ps-4">#${guest.id}</td>
                <td class="fw-bold">${guest.fullName}</td>
                <td>${guest.identityNumber}</td>
                <td>${guest.phone || '-'}</td>
                <td class="text-end pe-4">
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="GuestModule.openEditModal(${guest.id})" title="Edit">
                        <i class="fa-solid fa-pen"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="GuestModule.deleteGuest(${guest.id})" title="Delete">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    },

    renderPagination: (pageData) => {
        const pagination = document.getElementById('guestsPagination');
        pagination.innerHTML = '';
        
        if (pageData.totalPages <= 1) return;
        
        // Prev button
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${pageData.first ? 'disabled' : ''}`;
        prevLi.innerHTML = `<a class="page-link" href="#" onclick="event.preventDefault(); GuestModule.loadGuests(${pageData.number - 1})">Previous</a>`;
        pagination.appendChild(prevLi);
        
        // Pages
        for (let i = 0; i < pageData.totalPages; i++) {
            const li = document.createElement('li');
            li.className = `page-item ${i === pageData.number ? 'active' : ''}`;
            li.innerHTML = `<a class="page-link" href="#" onclick="event.preventDefault(); GuestModule.loadGuests(${i})">${i + 1}</a>`;
            pagination.appendChild(li);
        }
        
        // Next button
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${pageData.last ? 'disabled' : ''}`;
        nextLi.innerHTML = `<a class="page-link" href="#" onclick="event.preventDefault(); GuestModule.loadGuests(${pageData.number + 1})">Next</a>`;
        pagination.appendChild(nextLi);
    },

    resetSearch: () => {
        document.getElementById('filterKeyword').value = '';
        GuestModule.loadGuests(0);
    },

    openAddModal: () => {
        ModalUtils.resetForm('guestForm');
        document.getElementById('guestModalTitle').textContent = 'Add New Guest';
        ModalUtils.show('guestModal');
    },

    openEditModal: async (id) => {
        ModalUtils.resetForm('guestForm');
        document.getElementById('guestModalTitle').textContent = 'Edit Guest';
        
        try {
            const response = await apiFetch(`/guests/${id}`);
            if (response && response.ok) {
                const guest = response.data.result;
                document.getElementById('guestId').value = guest.id;
                document.getElementById('fullName').value = guest.fullName;
                document.getElementById('identityNumber').value = guest.identityNumber;
                document.getElementById('phone').value = guest.phone || '';
                
                ModalUtils.show('guestModal');
            } else {
                ToastUtils.error('Failed to fetch guest details.');
            }
        } catch (error) {
            console.error(error);
            ToastUtils.error('An error occurred.');
        }
    },

    saveGuest: async () => {
        const form = document.getElementById('guestForm');
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        const id = document.getElementById('guestId').value;
        const payload = {
            fullName: document.getElementById('fullName').value,
            identityNumber: document.getElementById('identityNumber').value,
            phone: document.getElementById('phone').value
        };

        ModalUtils.setLoading('btnSaveGuest');

        try {
            let response;
            if (id) {
                response = await apiFetch(`/guests/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
            } else {
                response = await apiFetch('/guests', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
            }

            if (response && response.ok) {
                ToastUtils.success(id ? 'Guest updated successfully!' : 'Guest added successfully!');
                ModalUtils.hide('guestModal');
                GuestModule.loadGuests(GuestModule.currentPage);
            } else {
                // If there's validation error from backend, it comes in response.data.result
                const errMsg = response.data?.message || 'Failed to save guest.';
                ToastUtils.error(errMsg);
            }
        } catch (error) {
            console.error(error);
            ToastUtils.error('An error occurred while saving.');
        } finally {
            ModalUtils.resetLoading('btnSaveGuest');
        }
    },

    deleteGuest: (id) => {
        ToastUtils.confirmDelete(
            'Delete Guest?',
            'This action cannot be undone. Any related data might be affected.',
            'Yes, delete it',
            async () => {
                try {
                    const response = await apiFetch(`/guests/${id}`, { method: 'DELETE' });
                    if (response && response.ok) {
                        ToastUtils.success('Guest deleted successfully!');
                        GuestModule.loadGuests(0);
                    } else {
                        ToastUtils.error('Failed to delete guest. They might have active reservations.');
                    }
                } catch (error) {
                    console.error(error);
                    ToastUtils.error('An error occurred.');
                }
            }
        );
    }
};

// Initialize
GuestModule.init();
