document.addEventListener('DOMContentLoaded', () => {
    // Default to last 30 days
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(endDate.getDate() - 30);
    
    document.getElementById('startDate').value = startDate.toISOString().split('T')[0];
    document.getElementById('endDate').value = endDate.toISOString().split('T')[0];

    loadReports();

    document.getElementById('reportFilterForm').addEventListener('submit', (e) => {
        e.preventDefault();
        loadReports();
    });
});

let revenueChartInstance = null;
let paymentChartInstance = null;
let roomChartInstance = null;

async function loadReports() {
    const startStr = document.getElementById('startDate').value;
    const endStr = document.getElementById('endDate').value;
    
    await Promise.all([
        loadRevenueChart(startStr, endStr),
        loadPaymentChart(startStr, endStr),
        loadRoomStatusChart() // Not typically date-bound, but we can reload it
    ]);
}

async function loadRevenueChart(start, end) {
    try {
        const response = await apiFetch(`/reports/revenue?startDate=${start}&endDate=${end}`);
        if (response && response.ok) {
            const data = response.data.result.dailyRevenue;
            const labels = Object.keys(data);
            const values = Object.values(data);
            
            const ctx = document.getElementById('revenueLineChart').getContext('2d');
            
            if (revenueChartInstance) {
                revenueChartInstance.destroy();
            }
            
            revenueChartInstance = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Revenue (VND)',
                        data: values,
                        borderColor: '#0d6efd',
                        backgroundColor: 'rgba(13, 110, 253, 0.1)',
                        tension: 0.3,
                        fill: true,
                        pointBackgroundColor: '#0d6efd'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { position: 'top' } }
                }
            });
        }
    } catch (e) { console.error('Failed to load revenue chart', e); }
}

async function loadPaymentChart(start, end) {
    try {
        const response = await apiFetch(`/reports/payment-methods?startDate=${start}&endDate=${end}`);
        if (response && response.ok) {
            const data = response.data.result.revenueByMethod;
            const labels = Object.keys(data);
            const values = Object.values(data);
            
            const ctx = document.getElementById('paymentMethodChart').getContext('2d');
            
            if (paymentChartInstance) {
                paymentChartInstance.destroy();
            }
            
            paymentChartInstance = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: labels,
                    datasets: [{
                        data: values,
                        backgroundColor: ['#198754', '#0dcaf0', '#ffc107', '#dc3545', '#6f42c1']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { position: 'right' } }
                }
            });
        }
    } catch (e) { console.error('Failed to load payment chart', e); }
}

async function loadRoomStatusChart() {
    try {
        const response = await apiFetch('/dashboard/summary');
        if (response && response.ok) {
            const data = response.data.result;
            const ctx = document.getElementById('roomStatusChart').getContext('2d');
            
            if (roomChartInstance) {
                roomChartInstance.destroy();
            }
            
            roomChartInstance = new Chart(ctx, {
                type: 'doughnut',
                data: {
                    labels: ['Available', 'Occupied', 'Dirty', 'Maintenance'],
                    datasets: [{
                        data: [data.availableRooms, data.occupiedRooms, data.dirtyRooms, data.maintenanceRooms || 0],
                        backgroundColor: ['#198754', '#dc3545', '#ffc107', '#6c757d']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: { legend: { position: 'right' } }
                }
            });
        }
    } catch (e) { console.error('Failed to load room chart', e); }
}
