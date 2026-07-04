document.addEventListener('DOMContentLoaded', async () => {
    // 1. Load Summary Cards
    await loadSummaryCards();
    
    // 2. Load Charts
    await loadCharts();
    
    // 3. Load Top Services
    await loadTopServices();
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

async function loadTopServices() {
    // Calculate dates for the last 30 days (for a broader top services view)
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(endDate.getDate() - 30);
    
    const startStr = startDate.toISOString().split('T')[0];
    const endStr = endDate.toISOString().split('T')[0];

    try {
        const response = await apiFetch(`/reports/services?startDate=${startStr}&endDate=${endStr}`);
        const tbody = document.getElementById('topServicesBody');
        
        if (response && response.ok) {
            const services = response.data.result.data; // Response is ApiResponse<ServiceReportResponse> and ServiceReportResponse has data field
            
            if (!services || services.length === 0) {
                tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">No services sold in the last 30 days.</td></tr>';
                return;
            }
            
            tbody.innerHTML = '';
            // Only show top 5
            const topServices = services.slice(0, 5);
            
            topServices.forEach(srv => {
                const tr = document.createElement('tr');
                
                const amountStr = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(srv.totalAmount);
                    
                tr.innerHTML = `
                    <td class="fw-bold text-primary"><i class="fa-solid fa-bell-concierge me-2"></i>${srv.serviceName}</td>
                    <td><span class="badge bg-secondary rounded-pill px-3 py-2">${srv.quantity}</span></td>
                    <td class="fw-bold text-success">${amountStr}</td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="3" class="text-center text-danger">Failed to load top services.</td></tr>';
        }
    } catch (error) {
        console.error('Error loading top services:', error);
        document.getElementById('topServicesBody').innerHTML = '<tr><td colspan="3" class="text-center text-danger">Error loading data.</td></tr>';
    }
}
