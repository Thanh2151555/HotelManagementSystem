document.addEventListener('DOMContentLoaded', () => {
    loadReservations();

    document.getElementById('reservationFilterForm').addEventListener('submit', (e) => {
        e.preventDefault();
        loadReservations();
    });

    document.getElementById('btnResetFilter').addEventListener('click', () => {
        document.getElementById('filterStatus').value = '';
        document.getElementById('filterGuest').value = '';
        loadReservations();
    });
});

async function loadReservations() {
    const status = document.getElementById('filterStatus').value;
    const guestSearch = document.getElementById('filterGuest').value;
    
    const tbody = document.getElementById('reservationsTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="6">
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text"></div>
            </td>
        </tr>`;

    try {
        const response = await apiFetch('/reservations?page=0&size=100');
        
        if (response && response.ok) {
            let reservations = response.data.result.content;
            
            // Client-side filtering as fallback if API doesn't support query params yet
            if (status) {
                reservations = reservations.filter(r => r.status === status);
            }
            if (guestSearch) {
                const searchLower = guestSearch.toLowerCase();
                reservations = reservations.filter(r => 
                    r.guestName.toLowerCase().includes(searchLower) || 
                    (r.bookingReference && r.bookingReference.toLowerCase().includes(searchLower))
                );
            }

            if (reservations.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No reservations found.</td></tr>';
                document.getElementById('paginationInfo').textContent = 'Showing 0 to 0 of 0 entries';
                return;
            }
            
            tbody.innerHTML = '';
            reservations.forEach(res => {
                const tr = document.createElement('tr');
                
                let badgeClass = 'bg-secondary';
                if (res.status === 'PENDING') badgeClass = 'bg-warning text-dark';
                else if (res.status === 'CONFIRMED') badgeClass = 'bg-primary';
                else if (res.status === 'CHECKED_IN') badgeClass = 'bg-success';
                else if (res.status === 'CHECKED_OUT') badgeClass = 'bg-info';
                else if (res.status === 'CANCELLED') badgeClass = 'bg-danger';
                
                const checkInDate = new Date(res.checkInExpected).toLocaleString('vi-VN');
                const roomInfo = res.roomNumber ? `${res.roomTypeName} (${res.roomNumber})` : res.roomTypeName;
                
                let actionBtns = '';
                if (res.status === 'PENDING') {
                    actionBtns += `<button class="btn btn-sm btn-outline-success me-1" onclick="confirmReservation(${res.id})" title="Confirm"><i class="fa-solid fa-check"></i></button>`;
                }
                if (res.status === 'PENDING' || res.status === 'CONFIRMED') {
                    actionBtns += `<button class="btn btn-sm btn-outline-danger" onclick="cancelReservation(${res.id})" title="Cancel"><i class="fa-solid fa-xmark"></i></button>`;
                }

                tr.innerHTML = `
                    <td class="ps-4 fw-bold text-primary-custom">${res.bookingReference || 'N/A'}</td>
                    <td class="fw-semibold">${res.guestName}</td>
                    <td>${roomInfo}</td>
                    <td>${checkInDate}</td>
                    <td><span class="badge ${badgeClass}">${res.status}</span></td>
                    <td class="text-end pe-4">${actionBtns || '<span class="text-muted small">No actions</span>'}</td>
                `;
                tbody.appendChild(tr);
            });
            
            document.getElementById('paginationInfo').textContent = `Showing 1 to ${reservations.length} of ${reservations.length} entries`;
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-danger">Failed to load reservations.</td></tr>';
        }
    } catch (error) {
        console.error(error);
        document.getElementById('reservationsTableBody').innerHTML = '<tr><td colspan="6" class="text-center py-4 text-danger">Error loading data.</td></tr>';
    }
}

async function confirmReservation(id) {
    if (!confirm('Are you sure you want to confirm this reservation?')) return;
    try {
        const response = await apiFetch(`/reservations/${id}/confirm`, { method: 'PUT' });
        if (response && response.ok) {
            alert('Reservation confirmed successfully.');
            loadReservations();
        } else {
            alert(response?.data?.message || 'Failed to confirm.');
        }
    } catch (error) {
        alert('An error occurred.');
    }
}

async function cancelReservation(id) {
    if (!confirm('Are you sure you want to cancel this reservation?')) return;
    try {
        const response = await apiFetch(`/reservations/${id}/cancel`, { method: 'PUT' });
        if (response && response.ok) {
            alert('Reservation cancelled successfully.');
            loadReservations();
        } else {
            alert(response?.data?.message || 'Failed to cancel.');
        }
    } catch (error) {
        alert('An error occurred.');
    }
}
