INSERT INTO admin(account, password)
SELECT 'root', SHA2('toor',256)
    WHERE NOT EXISTS (
    SELECT 1 FROM admin WHERE account = 'root'
);

