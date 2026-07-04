document.addEventListener('DOMContentLoaded', async () => {
    await loadRoomMap();
});

async function loadRoomMap() {
    try {
        const response = await apiFetch('/rooms');
        const grid = document.getElementById('roomMapGrid');
        
        if (response && response.ok) {
            const rooms = response.data.result;
            grid.innerHTML = '';
            
            if (rooms.length === 0) {
                grid.innerHTML = '<div class="col-12 text-center text-muted py-5">No rooms configured.</div>';
                return;
            }
            
            rooms.forEach(room => {
                const col = document.createElement('div');
                col.className = 'col';
                
                // Determine color based on status
                let bgClass = 'bg-secondary';
                let textClass = 'text-white';
                let icon = 'fa-door-closed';
                
                if (room.status === 'AVAILABLE') {
                    bgClass = 'bg-success';
                    icon = 'fa-check';
                } else if (room.status === 'OCCUPIED') {
                    bgClass = 'bg-danger';
                    icon = 'fa-bed';
                } else if (room.status === 'DIRTY') {
                    bgClass = 'bg-warning';
                    textClass = 'text-dark';
                    icon = 'fa-broom';
                } else if (room.status === 'MAINTENANCE') {
                    bgClass = 'bg-secondary';
                    icon = 'fa-wrench';
                }
                
                col.innerHTML = `
                    <div class="card h-100 ${bgClass} ${textClass} border-0 shadow-sm room-card" 
                         style="cursor: pointer; transition: transform 0.2s;"
                         onclick="openRoomActionModal(${room.id}, '${room.roomNumber}', '${room.status}')"
                         onmouseover="this.style.transform='scale(1.05)'"
                         onmouseout="this.style.transform='scale(1)'">
                        <div class="card-body d-flex flex-column align-items-center justify-content-center text-center p-3">
                            <i class="fa-solid ${icon} fs-3 mb-2 opacity-75"></i>
                            <h5 class="card-title fw-bold mb-1">${room.roomNumber}</h5>
                            <small class="text-uppercase fw-semibold" style="font-size: 0.7rem; letter-spacing: 1px;">
                                ${room.roomType.name}
                            </small>
                        </div>
                    </div>
                `;
                grid.appendChild(col);
            });
        } else {
            grid.innerHTML = '<div class="col-12 text-center text-danger">Failed to load rooms.</div>';
        }
    } catch (error) {
        console.error('Error loading rooms:', error);
        document.getElementById('roomMapGrid').innerHTML = '<div class="col-12 text-center text-danger">Error loading data.</div>';
    }
}

function openRoomActionModal(roomId, roomNumber, status) {
    document.getElementById('roomActionModalTitle').textContent = `Room ${roomNumber} - ${status}`;
    
    const actionButtons = document.getElementById('roomActionButtons');
    actionButtons.innerHTML = '';
    
    if (status === 'AVAILABLE') {
        actionButtons.innerHTML = `
            <a href="/admin/reservations?roomId=${roomId}&action=new" class="btn btn-primary w-100 text-start py-2">
                <i class="fa-solid fa-calendar-plus fa-fw me-2"></i> Create Reservation
            </a>
            <a href="/admin/reservations?roomId=${roomId}&action=checkin" class="btn btn-success w-100 text-start py-2">
                <i class="fa-solid fa-key fa-fw me-2"></i> Direct Check-in
            </a>
        `;
    } else if (status === 'OCCUPIED') {
        actionButtons.innerHTML = `
            <a href="/admin/reservations?roomId=${roomId}&action=checkout" class="btn btn-danger w-100 text-start py-2">
                <i class="fa-solid fa-sign-out-alt fa-fw me-2"></i> Check-out & Payment
            </a>
            <a href="/admin/reservations?roomId=${roomId}&action=service" class="btn btn-info text-white w-100 text-start py-2">
                <i class="fa-solid fa-bell-concierge fa-fw me-2"></i> Add Service Order
            </a>
            <button onclick="updateRoomStatus(${roomId}, 'DIRTY')" class="btn btn-outline-warning w-100 text-start py-2">
                <i class="fa-solid fa-broom fa-fw me-2"></i> Mark as Dirty
            </button>
        `;
    } else if (status === 'DIRTY') {
        actionButtons.innerHTML = `
            <button onclick="updateRoomStatus(${roomId}, 'AVAILABLE')" class="btn btn-success w-100 text-start py-2">
                <i class="fa-solid fa-check fa-fw me-2"></i> Mark as Clean (Available)
            </button>
            <button onclick="updateRoomStatus(${roomId}, 'MAINTENANCE')" class="btn btn-secondary w-100 text-start py-2">
                <i class="fa-solid fa-wrench fa-fw me-2"></i> Send to Maintenance
            </button>
        `;
    } else if (status === 'MAINTENANCE') {
        actionButtons.innerHTML = `
            <button onclick="updateRoomStatus(${roomId}, 'AVAILABLE')" class="btn btn-success w-100 text-start py-2">
                <i class="fa-solid fa-check fa-fw me-2"></i> Maintenance Done (Available)
            </button>
        `;
    }
    
    const modal = new bootstrap.Modal(document.getElementById('roomActionModal'));
    modal.show();
}

async function updateRoomStatus(roomId, newStatus) {
    try {
        const response = await apiFetch('/rooms/' + roomId + '/status?status=' + newStatus, {
            method: 'PUT'
        });
        
        if (response && response.ok) {
            bootstrap.Modal.getInstance(document.getElementById('roomActionModal')).hide();
            await loadRoomMap(); // Reload map
        } else {
            alert('Failed to update room status');
        }
    } catch (error) {
        console.error('Error updating status:', error);
        alert('An error occurred while updating status.');
    }
}
