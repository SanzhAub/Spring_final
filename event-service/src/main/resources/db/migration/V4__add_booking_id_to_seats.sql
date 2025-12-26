ALTER TABLE seats
ADD COLUMN IF NOT EXISTS booking_id BIGINT;

CREATE INDEX IF NOT EXISTS idx_seats_booking_id ON seats(booking_id);