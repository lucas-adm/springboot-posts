-- TRIGGER FOR UPVOTES
CREATE OR REPLACE FUNCTION update_upvote_count() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE posts SET upvote_count = upvote_count + 1 WHERE id = NEW.id_post;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE posts SET upvote_count = upvote_count - 1 WHERE id = OLD.id_post;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_upvote_count
AFTER INSERT OR DELETE ON upvotes
FOR EACH ROW EXECUTE FUNCTION update_upvote_count();



-- TRIGGER FOR COMMENTS
CREATE OR REPLACE FUNCTION update_comment_count() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE posts SET comment_count = comment_count + 1 WHERE id = NEW.id_post;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE posts SET comment_count = comment_count - 1 WHERE id = OLD.id_post;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_comment_count
AFTER INSERT OR DELETE ON comments
FOR EACH ROW EXECUTE FUNCTION update_comment_count();



-- TRIGGER FOR ANSWERS
CREATE OR REPLACE FUNCTION update_answer_count() RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        UPDATE comments SET answer_count = answer_count + 1 WHERE id = NEW.id_comment;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE comments SET answer_count = answer_count - 1 WHERE id = OLD.id_comment;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_answer_count
AFTER INSERT OR DELETE ON answers
FOR EACH ROW EXECUTE FUNCTION update_answer_count();