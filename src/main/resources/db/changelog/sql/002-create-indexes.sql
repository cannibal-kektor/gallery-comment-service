SET search_path TO gallery;

CREATE INDEX idx_comments_recent ON comments (created_at DESC);
CREATE INDEX idx_image_comments ON comments (image_id);