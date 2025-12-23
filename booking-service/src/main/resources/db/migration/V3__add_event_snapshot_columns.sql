ALTER TABLE bookings
    ADD COLUMN IF NOT EXISTS event_id BIGINT,
    ADD COLUMN IF NOT EXISTS event_start_time TIMESTAMP,
    ADD COLUMN IF NOT EXISTS price_per_ticket NUMERIC(10,2);

-- Backfill for existing rows (if any)
UPDATE bookings
SET event_id = COALESCE(event_id, 0),
    event_start_time = COALESCE(event_start_time, booking_time),
    price_per_ticket = COALESCE(price_per_ticket, 10.0)
WHERE event_id IS NULL OR event_start_time IS NULL OR price_per_ticket IS NULL;

ALTER TABLE bookings
    ALTER COLUMN event_id SET NOT NULL,
    ALTER COLUMN event_start_time SET NOT NULL,
    ALTER COLUMN price_per_ticket SET NOT NULL;
