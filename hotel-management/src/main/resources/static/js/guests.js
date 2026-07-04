document.addEventListener('DOMContentLoaded', () => {
    loadGuests();

    document.getElementById('guestFilterForm').addEventListener('submit', (e) => {
        e.preventDefault();
        loadGuests();
    });

    document.getElementById('btnResetFilter').addEventListener('click', () => {
        document.getElementById('filterKeyword').value = '';
        loadGuests();
    });
});

async function loadGuests() {
    const keyword = document.getElementById('filterKeyword').value;
    const url = keyword ? `/guests/search?keyword=${encodeURIComponent(keyword)}&page=0&size=100` : `/guests?page=0&size=100`;
    
    const tbody = document.getElementById('guestsTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="6">
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text"></div>
            </td>
        </tr>`;

    try {
        const response = await apiFetch(url);
        
        if (response && response.ok) {
            const guests = response.data.result.content || response.data.result; // Handle both paginated and list depending on API
            const guestList = Array.isArray(guests) ? guests : (guests.content || []);

            if (guestList.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-muted">No guests found.</td></tr>';
                return;
            }
            
            tbody.innerHTML = '';
            guestList.forEach(guest => {
                const tr = document.createElement('tr');
                
                tr.innerHTML = `
                    <td class="ps-4 text-muted">#${guest.id}</td>
                    <td class="fw-bold">${guest.fullName}</td>
                    <td>${guest.identityNumber || '-'}</td>
                    <td>${guest.phone || '-'}</td>
                    <td>${guest.email || '-'}</td>
                    <td class="text-end pe-4">
                        <button class="btn btn-sm btn-light border" title="View Details"><i class="fa-solid fa-eye text-primary"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center py-4 text-danger">Failed to load guests.</td></tr>';
        }
    } catch (error) {
        console.error(error);
        document.getElementById('guestsTableBody').innerHTML = '<tr><td colspan="6" class="text-center py-4 text-danger">Error loading data.</td></tr>';
    }
}
