/**
 * Reservation Management Module
 */
const ReservationModule = {
    currentPage: 0,
    pageSize: 10,
    
    init: () => {
        document.addEventListener('DOMContentLoaded', () => {
            ReservationModule.loadReservations(0);
            
            // Setup listener for Status change warning
            const statusSelect = document.getElementById('newStatus');
            if (statusSelect) {
                statusSelect.addEventListener('change', (e) => {
                    const warning = document.getElementById('statusWarning');
                    if (e.target.value === 'CANCELLED') {
                        warning.style.display = 'block';
                    } else {
                        warning.style.display = 'none';
                    }
                });
            }
        });
    },

    loadReservations: async (page) => {
        ReservationModule.currentPage = page;
        const status = document.getElementById('filterStatus').value;
        // Backend search might only take status or page. 
        // We'll append what we have, if backend doesn't support filterKeyword, we'll gracefully pass it anyway.
        const keyword = document.getElementById('filterKeyword').value; 
        
        const tbody = document.getElementById('reservationsTableBody');
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted"><div class="spinner-border spinner-border-sm text-primary" role="status"></div> Loading...</td></tr>`;
        
        try {
            // Note: If backend doesn't have status filter on /reservations, we might need to filter frontend. 
            // But usually Pagination API accepts filters. We'll send them.
            let url = `/reservations?page=${page}&size=${ReservationModule.pageSize}`;
            if (status) url += `&status=${status}`;
            
            const response = await apiFetch(url);
            if (response && response.ok) {
                let data = response.data.result;
                // Client side filtering for keyword if backend pagination doesn't support keyword
                if (keyword && data.content) {
                    const lowerK = keyword.toLowerCase();
                    data.content = data.content.filter(r => 
                        (r.guestName && r.guestName.toLowerCase().includes(lowerK)) ||
                        (r.bookingReference && r.bookingReference.toLowerCase().includes(lowerK))
                    );
                }
                ReservationModule.renderTable(data);
                ReservationModule.renderPagination(data);
            } else {
                tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">Failed to load reservations.</td></tr>`;
            }
        } catch (error) {
            console.error(error);
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">An error occurred.</td></tr>`;
        }
    },

    renderTable: (pageData) => {
        const tbody = document.getElementById('reservationsTableBody');
        tbody.innerHTML = '';
        
        if (!pageData.content || pageData.content.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-5 text-muted">
                        <i class="fa-solid fa-calendar-xmark fs-1 mb-3 opacity-50"></i>
                        <h5>No reservations found</h5>
                        <p class="mb-0">Try adjusting your filters.</p>
                    </td>
                </tr>`;
            return;
        }
        
        pageData.content.forEach(res => {
            const tr = document.createElement('tr');
            
            let badgeClass = 'bg-secondary';
            if (res.status === 'PENDING') badgeClass = 'bg-warning text-dark';
            else if (res.status === 'CONFIRMED') badgeClass = 'bg-primary';
            else if (res.status === 'CHECKED_IN') badgeClass = 'bg-success';
            else if (res.status === 'CHECKED_OUT') badgeClass = 'bg-info';
            else if (res.status === 'CANCELLED') badgeClass = 'bg-danger';
            
            const checkInDate = new Date(res.checkInExpected).toLocaleString('vi-VN');
            const roomDisplay = res.roomNumber || res.roomTypeName;
            
            tr.innerHTML = `
                <td class="ps-4 fw-bold text-primary">${res.bookingReference || 'N/A'}</td>
                <td class="fw-semibold">${res.guestName}</td>
                <td>${roomDisplay}</td>
                <td>${checkInDate}</td>
                <td><span class="badge ${badgeClass} px-2 py-1">${res.status}</span></td>
                <td class="text-end pe-4">
                    <button class="btn btn-sm btn-outline-secondary me-1" onclick="ReservationModule.openStatusModal(${res.id}, '${res.bookingReference || ''}', '${res.status}')" title="Update Status">
                        <i class="fa-solid fa-list-check"></i>
                    </button>
                    <!-- More complex actions could go here, e.g., view invoice -->
                </td>
            `;
            tbody.appendChild(tr);
        });
    },

    renderPagination: (pageData) => {
        const pagination = document.getElementById('reservationsPagination');
        pagination.innerHTML = '';
        
        if (pageData.totalPages <= 1) return;
        
        const prevLi = document.createElement('li');
        prevLi.className = `page-item ${pageData.first ? 'disabled' : ''}`;
        prevLi.innerHTML = `<a class="page-link" href="#" onclick="event.preventDefault(); ReservationModule.loadReservations(${pageData.number - 1})">Previous</a>`;
        pagination.appendChild(prevLi);
        
        for (let i = 0; i < pageData.totalPages; i++) {
            const li = document.createElement('li');
            li.className = `page-item ${i === pageData.number ? 'active' : ''}`;
            li.innerHTML = `<a class="page-link" href="#" onclick="event.preventDefault(); ReservationModule.loadReservations(${i})">${i + 1}</a>`;
            pagination.appendChild(li);
        }
        
        const nextLi = document.createElement('li');
        nextLi.className = `page-item ${pageData.last ? 'disabled' : ''}`;
        nextLi.innerHTML = `<a class="page-link" href="#" onclick="event.preventDefault(); ReservationModule.loadReservations(${pageData.number + 1})">Next</a>`;
        pagination.appendChild(nextLi);
    },

    resetFilter: () => {
        document.getElementById('filterStatus').value = '';
        document.getElementById('filterKeyword').value = '';
        ReservationModule.loadReservations(0);
    },

    openStatusModal: (id, ref, currentStatus) => {
        ModalUtils.resetForm('statusForm');
        document.getElementById('statusReservationId').value = id;
        document.getElementById('statusRefText').textContent = ref || `#${id}`;
        document.getElementById('newStatus').value = currentStatus;
        document.getElementById('statusWarning').style.display = 'none';
        
        ModalUtils.show('statusModal');
    },

    updateStatus: async () => {
        const id = document.getElementById('statusReservationId').value;
        const newStatus = document.getElementById('newStatus').value;
        
        ModalUtils.setLoading('btnUpdateStatus');
        try {
            // Depending on backend, might be PUT /reservations/{id}/status?status=...
            const response = await apiFetch(`/reservations/${id}/status?status=${newStatus}`, { method: 'PUT' });
            
            if (response && response.ok) {
                ToastUtils.success('Status updated successfully');
                ModalUtils.hide('statusModal');
                ReservationModule.loadReservations(ReservationModule.currentPage);
            } else {
                ToastUtils.error(response.data?.message || 'Failed to update status');
            }
        } catch (error) {
            console.error(error);
            ToastUtils.error('An error occurred.');
        } finally {
            ModalUtils.resetLoading('btnUpdateStatus');
        }
    }
};

// Initialize
ReservationModule.init();
