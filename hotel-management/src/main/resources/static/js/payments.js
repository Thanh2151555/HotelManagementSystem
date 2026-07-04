document.addEventListener('DOMContentLoaded', () => {
    loadPayments();

    document.getElementById('paymentFilterForm').addEventListener('submit', (e) => {
        e.preventDefault();
        loadPayments();
    });

    document.getElementById('btnResetFilter').addEventListener('click', () => {
        document.getElementById('filterInvoiceId').value = '';
        loadPayments();
    });
});

async function loadPayments() {
    const invoiceId = document.getElementById('filterInvoiceId').value.trim();
    const url = invoiceId ? `/invoices/${invoiceId}/payments` : `/payments?page=0&size=100`;
    
    const tbody = document.getElementById('paymentsTableBody');
    tbody.innerHTML = `
        <tr>
            <td colspan="7">
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text"></div>
                <div class="skeleton skeleton-text"></div>
            </td>
        </tr>`;

    try {
        const response = await apiFetch(url);
        
        if (response && response.ok) {
            const data = response.data.result;
            const payments = Array.isArray(data) ? data : (data.content || []);

            if (payments.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No payments found.</td></tr>';
                return;
            }
            
            tbody.innerHTML = '';
            payments.forEach(pay => {
                const tr = document.createElement('tr');
                
                let methodClass = 'bg-secondary';
                if (pay.paymentMethod === 'CASH') methodClass = 'bg-success';
                else if (pay.paymentMethod === 'CREDIT_CARD') methodClass = 'bg-primary';
                else if (pay.paymentMethod === 'BANK_TRANSFER') methodClass = 'bg-info';
                
                const amountStr = new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(pay.paymentAmount);
                const timeStr = pay.paymentTime ? new Date(pay.paymentTime).toLocaleString('vi-VN') : '-';

                tr.innerHTML = `
                    <td class="ps-4 fw-bold text-primary-custom">${pay.paymentReference || `#PAY-${pay.paymentId}`}</td>
                    <td><a href="#" class="text-decoration-none">#INV-${pay.invoiceId}</a></td>
                    <td><span class="badge ${methodClass}">${pay.paymentMethod}</span></td>
                    <td class="fw-semibold text-success">${amountStr}</td>
                    <td><span class="badge bg-light text-dark border">${pay.invoiceStatus}</span></td>
                    <td>${timeStr}</td>
                    <td>${pay.receivedByUsername || '-'}</td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-danger">Failed to load payments.</td></tr>';
        }
    } catch (error) {
        console.error(error);
        document.getElementById('paymentsTableBody').innerHTML = '<tr><td colspan="7" class="text-center py-4 text-danger">Error loading data.</td></tr>';
    }
}
