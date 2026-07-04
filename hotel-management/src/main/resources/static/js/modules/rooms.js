/**
 * Room Management Module
 */
const RoomModule = {
    allRooms: [],
    roomTypes: [],
    
    init: async () => {
        document.addEventListener('DOMContentLoaded', async () => {
            await RoomModule.loadRoomTypes();
            await RoomModule.loadRooms();
        });
    },

    loadRoomTypes: async () => {
        try {
            const response = await apiFetch('/room-types');
            if (response && response.ok) {
                RoomModule.roomTypes = response.data.result;
                const filterSelect = document.getElementById('filterRoomType');
                const modalSelect = document.getElementById('roomTypeId');
                
                RoomModule.roomTypes.forEach(type => {
                    const option = `<option value="${type.id}">${type.name}</option>`;
                    filterSelect.innerHTML += option;
                    modalSelect.innerHTML += option;
                });
            }
        } catch (error) {
            console.error('Failed to load room types', error);
        }
    },

    loadRooms: async () => {
        const tbody = document.getElementById('roomsTableBody');
        tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted"><div class="spinner-border spinner-border-sm text-primary" role="status"></div> Loading...</td></tr>`;
        
        try {
            const response = await apiFetch('/rooms');
            if (response && response.ok) {
                RoomModule.allRooms = response.data.result;
                RoomModule.renderTable();
            } else {
                tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">Failed to load rooms.</td></tr>`;
            }
        } catch (error) {
            console.error(error);
            tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-danger">An error occurred.</td></tr>`;
        }
    },

    renderTable: () => {
        const tbody = document.getElementById('roomsTableBody');
        tbody.innerHTML = '';
        
        const statusFilter = document.getElementById('filterStatus').value;
        const typeFilter = document.getElementById('filterRoomType').value;
        
        const filteredRooms = RoomModule.allRooms.filter(room => {
            let match = true;
            if (statusFilter && room.status !== statusFilter) match = false;
            if (typeFilter && room.roomType.id != typeFilter) match = false;
            return match;
        });

        if (filteredRooms.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="6" class="text-center py-5 text-muted">
                        <i class="fa-solid fa-bed fs-1 mb-3 opacity-50"></i>
                        <h5>No rooms found</h5>
                        <p class="mb-0">Try adjusting your filters.</p>
                    </td>
                </tr>`;
            return;
        }
        
        filteredRooms.forEach(room => {
            const tr = document.createElement('tr');
            
            let statusBadge = 'bg-secondary';
            if (room.status === 'AVAILABLE') statusBadge = 'bg-success';
            if (room.status === 'OCCUPIED') statusBadge = 'bg-danger';
            if (room.status === 'DIRTY') statusBadge = 'bg-warning text-dark';
            
            tr.innerHTML = `
                <td class="ps-4">#${room.id}</td>
                <td class="fw-bold text-primary">${room.roomNumber}</td>
                <td>${room.roomType.name}</td>
                <td>Level ${room.floorNumber}</td>
                <td><span class="badge ${statusBadge} px-2 py-1">${room.status}</span></td>
                <td class="text-end pe-4">
                    <button class="btn btn-sm btn-outline-primary me-1" onclick="RoomModule.openEditModal(${room.id})" title="Edit">
                        <i class="fa-solid fa-pen"></i>
                    </button>
                    <button class="btn btn-sm btn-outline-danger" onclick="RoomModule.deleteRoom(${room.id})" title="Delete">
                        <i class="fa-solid fa-trash"></i>
                    </button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    },

    resetFilter: () => {
        document.getElementById('filterStatus').value = '';
        document.getElementById('filterRoomType').value = '';
        RoomModule.renderTable();
    },

    openAddModal: () => {
        ModalUtils.resetForm('roomForm');
        document.getElementById('roomModalTitle').textContent = 'Add New Room';
        document.getElementById('roomStatus').value = 'AVAILABLE';
        ModalUtils.show('roomModal');
    },

    openEditModal: async (id) => {
        ModalUtils.resetForm('roomForm');
        document.getElementById('roomModalTitle').textContent = 'Edit Room';
        
        try {
            const response = await apiFetch(`/rooms/${id}`);
            if (response && response.ok) {
                const room = response.data.result;
                document.getElementById('roomId').value = room.id;
                document.getElementById('roomNumber').value = room.roomNumber;
                document.getElementById('floorNumber').value = room.floorNumber;
                document.getElementById('roomTypeId').value = room.roomType.id;
                document.getElementById('roomStatus').value = room.status;
                
                ModalUtils.show('roomModal');
            } else {
                ToastUtils.error('Failed to fetch room details.');
            }
        } catch (error) {
            console.error(error);
            ToastUtils.error('An error occurred.');
        }
    },

    saveRoom: async () => {
        const form = document.getElementById('roomForm');
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        const id = document.getElementById('roomId').value;
        const payload = {
            roomNumber: document.getElementById('roomNumber').value,
            floorNumber: document.getElementById('floorNumber').value,
            roomTypeId: document.getElementById('roomTypeId').value,
            status: document.getElementById('roomStatus').value
        };

        ModalUtils.setLoading('btnSaveRoom');

        try {
            let response;
            if (id) {
                // For update, the backend might just have PUT /rooms/{id} or maybe status separate
                response = await apiFetch(`/rooms/${id}`, {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
            } else {
                response = await apiFetch('/rooms', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
            }

            if (response && response.ok) {
                ToastUtils.success(id ? 'Room updated successfully!' : 'Room added successfully!');
                ModalUtils.hide('roomModal');
                RoomModule.loadRooms();
            } else {
                const errMsg = response.data?.message || 'Failed to save room.';
                ToastUtils.error(errMsg);
            }
        } catch (error) {
            console.error(error);
            ToastUtils.error('An error occurred while saving.');
        } finally {
            ModalUtils.resetLoading('btnSaveRoom');
        }
    },

    deleteRoom: (id) => {
        ToastUtils.confirmDelete(
            'Delete Room?',
            'Are you sure you want to delete this room? This might fail if it has active reservations.',
            'Yes, delete it',
            async () => {
                try {
                    const response = await apiFetch(`/rooms/${id}`, { method: 'DELETE' });
                    if (response && response.ok) {
                        ToastUtils.success('Room deleted successfully!');
                        RoomModule.loadRooms();
                    } else {
                        ToastUtils.error('Failed to delete room. It might be in use.');
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
RoomModule.init();
