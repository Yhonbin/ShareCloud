INSERT INTO admin(account, password, create_time, update_time, is_deleted)
SELECT 'root', SHA2('toor',256), NOW(), NOW(), 0
    WHERE NOT EXISTS (
    SELECT 1 FROM admin WHERE account = 'root'
);