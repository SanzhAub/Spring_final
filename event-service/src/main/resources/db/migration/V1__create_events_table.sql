CREATE TABLE IF NOT EXISTS events (
  id BIGSERIAL PRIMARY KEY,
  movie_title VARCHAR(255) NOT NULL,
  start_time TIMESTAMP NOT NULL,
  price_per_ticket NUMERIC(10,2) NOT NULL,
  available_seats INT NOT NULL
);
