INSERT INTO gallery.comments (id, image_id, user_id, content, created_at)
VALUES
    (1, 1, 1, 'test comment 1', NOW() - INTERVAL '5 hour'),
    (2, 1, 2, 'test comment 2', NOW() - INTERVAL '2 hour'),
    (3, 2, 1, 'test comment 3', NOW()),
    (4, 2, 2, 'test comment 4', NOW() - INTERVAL '4 hour'),
    (5, 3, 1, 'test comment 5', NOW() - INTERVAL '5 hour'),
    (6, 3, 3, 'test comment 6', NOW() - INTERVAL '6 hour'),
    (7, 3, 2, 'test comment 7', NOW() - INTERVAL '7 hour'),
    (8, 5, 1, 'test comment 8', NOW() - INTERVAL '10 hour');