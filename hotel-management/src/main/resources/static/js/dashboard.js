document.addEventListener('DOMContentLoaded', async () => {
    // 1. Load Summary Cards
    await loadSummaryCards();
    
    // 2. Load Charts
    await loadCharts();
    
    // 3. Load Recent Reservations
    await loadRecentReservations();
});

async function loadSummaryCards() {
    try {
        const response = await apiFetch('/dashboard/summary');
        if (response && response.ok) {
            const data = response.data.result;
            
            document.getElementById('cardRevenue').textContent = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(data.todayRevenue);
            
            const occupancy = data.totalRooms > 0 ? Math.round((data.occupiedRooms / data.totalRooms) * 100) : 0;
            document.getElementById('cardOccupancy').textContent = occupancy + '%';
            
            document.getElementById('cardCheckin').textContent = data.todayCheckIns;
            document.getElementById('cardCheckout').textContent = data.todayCheckOuts;
            document.getElementById('cardDirty').textContent = data.dirtyRooms;
            document.getElementById('cardMaintenance').textContent = data.maintenanceRooms || 0;
        }
    } catch (error) {
        console.error('Error loading summary cards:', error);
    }
}

async function loadCharts() {
    // Calculate dates for the last 7 days
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(endDate.getDate() - 6);
    
    const startStr = startDate.toISOString().split('T')[0];
    const endStr = endDate.toISOString().split('T')[0];
    
    try {
        // Fetch Revenue
        const revResponse = await apiFetch(`/reports/revenue?startDate=${startStr}&endDate=${endStr}`);
        if (revResponse && revResponse.ok) {
            const revData = revresponse.data.result.dailyRevenue;
            const labels = Object.keys(revData);
            const values = Object.values(revData);
            
            const ctxRev = document.getElementById('revenueChart').getContext('2d');
            new Chart(ctxRev, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Revenue (VND)',
                        data: values,
                        borderColor: '#0d6efd',
                        backgroundColor: 'rgba(13, 110, 253, 0.1)',
                        tension: 0.3,
                        fill: true
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
        
        // Fetch Payment Methods
        const payResponse = await apiFetch(`/reports/payment-methods?startDate=${startStr}&endDate=${endStr}`);
        if (payResponse && payResponse.ok) {
            const payData = payresponse.data.result.revenueByMethod;
            const labels = Object.keys(payData);
            const values = Object.values(payData);
            
            const ctxPay = document.getElementById('paymentPieChart').getContext('2d');
            new Chart(ctxPay, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: ['#198754', '#0dcaf0', '#ffc107', '#dc3545']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
        
        // Fetch Room Status (Using Summary endpoint again for simplicity)
        const sumResponse = await apiFetch('/dashboard/summary');
        if (sumResponse && sumResponse.ok) {
            const data = sumresponse.data.result;
            const ctxRoom = document.getElementById('roomStatusPieChart').getContext('2d');
            new Chart(ctxRoom, {
                type: 'pie',
                data: {
                    labels: ['Available', 'Occupied', 'Dirty'],
                    datasets: [{
                        data: [data.availableRooms, data.occupiedRooms, data.dirtyRooms],
                        backgroundColor: ['#198754', '#dc3545', '#ffc107']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
        
    } catch (error) {
        console.error('Error loading charts:', error);
    }
}

async function loadRecentReservations() {
    try {
        const response = await apiFetch('/reservations?page=0&size=5');
        const tbody = document.getElementById('recentReservationsBody');
        
        if (response && response.ok) {
            const reservations = response.data.result.content;
            
            if (reservations.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No reservations found.</td></tr>';
                return;
            }
            
            tbody.innerHTML = '';
            reservations.forEach(res => {
                const tr = document.createElement('tr');
                
                // Status Badge mapping
                let badgeClass = 'bg-secondary';
                if (res.status === 'PENDING') badgeClass = 'bg-warning text-dark';
                else if (res.status === 'CONFIRMED') badgeClass = 'bg-primary';
                else if (res.status === 'CHECKED_IN') badgeClass = 'bg-success';
                else if (res.status === 'CHECKED_OUT') badgeClass = 'bg-info';
                else if (res.status === 'CANCELLED') badgeClass = 'bg-danger';
                
                const depositStr = res.depositAmount > 0 
                    ? new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(res.depositAmount) 
                    : '-';
                    
                const checkInDate = new Date(res.checkInExpected).toLocaleString('vi-VN');
                
                tr.innerHTML = `
                    <td class="fw-bold text-primary">${res.bookingReference || 'N/A'}</td>
                    <td>${res.guestName}</td>
                    <td>${res.roomNumber || res.roomTypeName}</td>
                    <td>${checkInDate}</td>
                    <td><span class="badge ${badgeClass}">${res.status}</span></td>
                    <td>${depositStr}</td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Failed to load reservations.</td></tr>';
        }
    } catch (error) {
        console.error('Error loading recent reservations:', error);
        document.getElementById('recentReservationsBody').innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error loading data.</td></tr>';
    }
}
