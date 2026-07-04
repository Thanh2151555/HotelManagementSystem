document.addEventListener('DOMContentLoaded', async () => {
    await loadDirtyRooms();
});

async function loadDirtyRooms() {
    try {
        const response = await apiFetch('/rooms');
        const grid = document.getElementById('dirtyRoomsGrid');
        
        if (response && response.ok) {
            const allRooms = response.data.result;
            const dirtyRooms = allRooms.filter(r => r.status === 'DIRTY');
            
            grid.innerHTML = '';
            
            if (dirtyRooms.length === 0) {
                grid.innerHTML = `
                    <div class="col-12 text-center text-success py-5">
                        <i class="fa-solid fa-check-circle fs-1 mb-3"></i>
                        <h5>All caught up! No dirty rooms.</h5>
                    </div>
                `;
                return;
            }
            
            dirtyRooms.forEach(room => {
                const col = document.createElement('div');
                col.className = 'col';
                
                col.innerHTML = `
                    <div class="card h-100 border-warning border-2 shadow-sm">
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start mb-3">
                                <div>
                                    <h4 class="card-title fw-bold mb-1">Room ${room.roomNumber}</h4>
                                    <span class="badge bg-secondary">${room.roomType.name}</span>
                                </div>
                                <i class="fa-solid fa-broom fs-2 text-warning opacity-75"></i>
                            </div>
                            <p class="text-muted small mb-4">Please clean this room and mark it as available.</p>
                            <button class="btn btn-success w-100" onclick="markAsClean(${room.id}, this)">
                                <i class="fa-solid fa-check me-2"></i> Mark as Clean
                            </button>
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
        document.getElementById('dirtyRoomsGrid').innerHTML = '<div class="col-12 text-center text-danger">Error loading data.</div>';
    }
}

async function markAsClean(roomId, btnElement) {
    const originalText = btnElement.innerHTML;
    btnElement.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-2"></i> Processing...';
    btnElement.disabled = true;

    try {
        const response = await apiFetch('/rooms/' + roomId + '/status?status=AVAILABLE', {
            method: 'PUT'
        });
        
        if (response && response.ok) {
            // Remove the card from the UI immediately for better UX
            const cardCol = btnElement.closest('.col');
            cardCol.style.transition = 'opacity 0.3s';
            cardCol.style.opacity = '0';
            setTimeout(async () => {
                await loadDirtyRooms();
            }, 300);
        } else {
            alert('Failed to mark room as clean');
            btnElement.innerHTML = originalText;
            btnElement.disabled = false;
        }
    } catch (error) {
        console.error('Error updating status:', error);
        alert('An error occurred while updating status.');
        btnElement.innerHTML = originalText;
        btnElement.disabled = false;
    }
}
