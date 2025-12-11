CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    movie_title        VARCHAR(255) NOT NULL,
    customer_name      VARCHAR(255) NOT NULL,
    customer_email     VARCHAR(255) NOT NULL,
    number_of_tickets  INT          NOT NULL,
    total_price        NUMERIC(10,2) NOT NULL,
    booking_time       TIMESTAMP    NOT NULL DEFAULT NOW(),
    status             VARCHAR(50)  NOT NULL
);