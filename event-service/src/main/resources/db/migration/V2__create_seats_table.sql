CREATE TABLE seats (
    id SERIAL PRIMARY KEY,
    seat_number INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(id)
);

