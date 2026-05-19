CREATE TABLE orders (
    id UUID PRIMARY KEY,
    customer_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL,
    price DECIMAL(19, 2) NOT NULL
);
