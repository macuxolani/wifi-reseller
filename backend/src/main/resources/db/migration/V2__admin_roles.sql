INSERT INTO user_roles (user_id, role_id)
SELECT '99999999-9999-9999-9999-999999999999', id
FROM roles
WHERE name = 'SUPER_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM user_roles
    WHERE user_id = '99999999-9999-9999-9999-999999999999'
      AND role_id = roles.id
  );