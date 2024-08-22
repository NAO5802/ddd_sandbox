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

// Extract variable
export function price(order) {
    const basePrice = order.quantity * order.itemPrice;
    const quantityDiscount = Math.max(0, order.quantity - 500) * order.itemPrice * 0.05;
    const shipping = Math.min(basePrice * 0.1, 100);
    return basePrice - quantityDiscount + shipping;
}

class Order {
    constructor(aRecord) {
        this._data = aRecord;
    }

    get quantity() {
        return this._data.quantity;
    }

    get itemPrice() {
        return this._data.itemPrice;
    }

    get price() {
        return this.basePrice - this.quantityDiscount + this.shipping;
    }

    get basePrice() {
        return this.quantity * this.itemPrice
    }

    get quantityDiscount() {
        return Math.max(0, this.quantity - 500) * this.itemPrice * 0.05
    }

    get shipping() {
        return Math.min(this.quantity * this.itemPrice * 0.1, 100)
    }
}


// Change function declaration
/*
    @deprecated Use `circumference` instead
 */
function circum(radius) {
    return circumference(radius);
}

function circumference(radius) {
    return 2 * Math.PI * radius;
}

// Add parameter
class Book {
    constructor() {
        this._reservations = [];
    }

    addReservation(customer) {
        this.new_addReservation(customer, false);
    }

    new_addReservation(customer, isPriority) {
        assert(isPriority === true || isPriority === false);
        this._reservations.push(customer);
    }
}

// Change parameter to property
// function inNewEngland(aCustomer) {
//     return new_newEnglanders(aCustomer.address.state);
// }

function inNewEngland(statusCode) {
    return ["MA", "CT", "ME", "VT", "NH", "RI"].includes(statusCode);
}

const someCustomers = [{address: {state: "MA"}}, {address: {state: "NY"}}];
const newEnglanders = someCustomers.filter(c => inNewEngland(c.address.state));



