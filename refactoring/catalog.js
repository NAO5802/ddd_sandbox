// Extract method
export function printOwing(invoice) {
    const outstanding = calculateOutstanding(invoice);
    invoice.dueDate = recordDueDate();

    return printResult(invoice, outstanding);

    function calculateOutstanding(invoice) {
        let outstanding = 0;
        for (const o of invoice.orders) {
            outstanding += o.amount;
        }
        return outstanding;
    }

    function recordDueDate() {
        const today = Clock.today;
        return new Date(today.getFullYear(), today.getMonth(), today.getDate() + 30);
    }

    function printResult(invoice, outstanding) {
        return `***********************\n**** Customer Owes ****\n***********************\nname: ${invoice.customer}\namount: ${outstanding}\ndue: ${invoice.dueDate.toLocaleDateString()}`;
    }
}

class Clock {
    static today = new Date('2024-06-30');
}

// Inline method
function reportLines(aCustomer) {
    const lines = [];
    lines.push(["name", aCustomer.name]);
    lines.push(["location", aCustomer.location]);
    return lines;
}

export function price(order) {
    const basePrice = order.quantity * order.itemPrice;
    const quantityDiscount = Math.max(0, order.quantity - 500) * order.itemPrice * 0.05;
    const shipping = Math.min(basePrice * 0.1, 100);
    return basePrice - quantityDiscount + shipping;
}



