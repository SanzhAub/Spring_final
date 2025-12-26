ALTER TABLE seats
DROP CONSTRAINT IF EXISTS seats_event_id_fkey;

ALTER TABLE seats
ADD CONSTRAINT seats_event_id_fkey
FOREIGN KEY (event_id)
REFERENCES events(id)
ON DELETE CASCADE;
