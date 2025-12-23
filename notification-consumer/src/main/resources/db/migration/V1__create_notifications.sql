CREATE TABLE IF NOT EXISTS notifications (
  id BIGSERIAL PRIMARY KEY,
  booking_id BIGINT NOT NULL,
  email VARCHAR(255) NOT NULL,
  status VARCHAR(50) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  sent_at TIMESTAMP NULL,
  error TEXT NULL
);

CREATE INDEX IF NOT EXISTS idx_notifications_booking_id
  ON notifications (booking_id);