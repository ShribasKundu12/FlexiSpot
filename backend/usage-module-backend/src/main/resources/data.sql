INSERT INTO employees (id, name, email) VALUES (1, 'Nitish', 'nitish@example.com');
INSERT INTO workspaces (id, name, type, floor, is_active) VALUES
(1, 'Desk A101', 'desk', '1st', true),
(2, 'Desk A102', 'desk', '1st', true),
(3, 'Meeting Room 1', 'meeting_room', '2nd', true);

INSERT INTO bookings (employee_id, workspace_id, start_time, end_time) VALUES
(1, 1, '2025-07-08T09:00:00', '2025-07-08T13:00:00'),
(1, 2, '2025-07-08T14:00:00', '2025-07-08T17:00:00');
