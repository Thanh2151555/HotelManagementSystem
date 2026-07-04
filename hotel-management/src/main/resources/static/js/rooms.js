document.addEventListener('DOMContentLoaded', async () => {
    await loadRooms();

    // Attach refresh event
    const refreshBtn = document.querySelector('.fa-rotate-right').parentElement;
    if (refreshBtn) {
        refreshBtn.addEventListener('click', loadRooms);
    }
});

let roomModalInstance = null;
let currentSelectedRoomId = null;

async function loadRooms() {
    const grid = document.getElementById('roomGrid');
    let skeletonHtml = '';
    for(let i=0; i<12; i++) {
        skeletonHtml += `
        <div class="col-xl-2 col-lg-3 col-md-4 col-sm-6">
            <div class="card shadow-sm border-0 room-card text-center h-100 p-4">
                <div class="skeleton skeleton-title mx-auto"></div>
                <div class="skeleton skeleton-text mx-auto w-75"></div>
                <div class="skeleton skeleton-text mx-auto w-50"></div>
            </div>
        </div>`;
    }
    grid.innerHTML = skeletonHtml;
    
    try {
        const response = await apiFetch('/rooms?page=0&size=100');
        if (response && response.ok) {
            const rooms = response.data.result.content;
            
            grid.innerHTML = '';
            
            rooms.forEach(room => {
                // Determine color based on status
                let bgClass = 'bg-secondary';
                let textClass = 'text-white';
                
                if (room.status === 'AVAILABLE') {
                    bgClass = 'bg-success';
                } else if (room.status === 'OCCUPIED') {
                    bgClass = 'bg-danger';
                } else if (room.status === 'DIRTY') {
                    bgClass = 'bg-warning';
                    textClass = 'text-dark';
                }

                const cardHtml = `
                    <div class="col-xl-2 col-lg-3 col-md-4 col-sm-6">
                        <div class="card shadow-sm border-0 room-card text-center text-decoration-none cursor-pointer h-100 ${bgClass} ${textClass}" 
                             onclick='openRoomModal(${JSON.stringify(room)})' style="cursor: pointer; transition: transform 0.2s;">
                            <div class="card-body p-4">
                                <h3 class="fw-bold mb-1">${room.roomNumber}</h3>
                                <div class="small text-uppercase fw-semibold opacity-75 mb-2">${room.status}</div>
                                <div class="badge bg-light text-dark rounded-pill">${room.roomTypeName}</div>
                            </div>
                        </div>
                    </div>
                `;
                grid.innerHTML += cardHtml;
            });

            // Add hover effect via CSS class dynamically
            document.querySelectorAll('.room-card').forEach(card => {
                card.addEventListener('mouseenter', () => card.style.transform = 'translateY(-5px)');
                card.addEventListener('mouseleave', () => card.style.transform = 'translateY(0)');
            });
        }
    } catch (error) {
        console.error('Failed to load rooms:', error);
        grid.innerHTML = '<div class="col-12 text-center text-danger">Error loading rooms.</div>';
    }
}

function openRoomModal(room) {
    if (!roomModalInstance) {
        roomModalInstance = new bootstrap.Modal(document.getElementById('roomModal'));
    }

    currentSelectedRoomId = room.id;
    document.getElementById('modalRoomNumber').textContent = room.roomNumber;
    document.getElementById('modalRoomType').textContent = room.roomTypeName;
    
    const statusEl = document.getElementById('modalRoomStatus');
    statusEl.textContent = room.status;
    
    statusEl.className = 'badge';
    let actionHtml = '';

    if (room.status === 'AVAILABLE') {
        statusEl.classList.add('bg-success');
        actionHtml = `<button class="btn btn-primary" onclick="alert('Proceeding to Walk-In Reservation for Room ${room.roomNumber}')"><i class="fa-solid fa-user-plus me-2"></i> Walk-In Check-In</button>`;
    } else if (room.status === 'DIRTY') {
        statusEl.classList.add('bg-warning', 'text-dark');
        actionHtml = `<button class="btn btn-success" onclick="completeCleaning(${room.id})"><i class="fa-solid fa-broom me-2"></i> Complete Cleaning</button>`;
    } else if (room.status === 'OCCUPIED') {
        statusEl.classList.add('bg-danger');
        actionHtml = `<button class="btn btn-danger" onclick="alert('Check-out flow not fully wired here yet. Go to Reservations.')"><i class="fa-solid fa-right-from-bracket me-2"></i> Check-Out</button>`;
    } else {
        statusEl.classList.add('bg-secondary');
    }

    document.getElementById('modalActionContainer').innerHTML = actionHtml;
    roomModalInstance.show();
}

async function completeCleaning(roomId) {
    try {
        const response = await apiFetch(`/housekeeping/rooms/${roomId}/complete-cleaning`, {
            method: 'PATCH'
        });
        
        if (response && response.ok) {
            alert('Room marked as AVAILABLE successfully.');
            roomModalInstance.hide();
            loadRooms(); // refresh grid
        } else {
            alert(response?.data?.message || 'Failed to complete cleaning.');
        }
    } catch (error) {
        console.error(error);
        alert('An error occurred.');
    }
}
