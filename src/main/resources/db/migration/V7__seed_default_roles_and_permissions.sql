-- Seed the default roles and their permission boundaries.
-- Flyway records this migration, so it is applied once per database.

INSERT INTO tbl_role (code, name, description, status)
VALUES
    ('ROLE_SUPER_ADMIN', 'Super Administrator', 'Full system access', 'ACTIVE'),
    ('ROLE_ADMIN', 'Admin', 'Operational and management access', 'ACTIVE'),
    ('ROLE_USER', 'User', 'Read and basic operational access', 'ACTIVE')
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name,
    description = EXCLUDED.description,
    status = EXCLUDED.status;

-- Super administrators receive every permission in the catalog.
INSERT INTO tbl_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM tbl_role r
CROSS JOIN tbl_permissions p
WHERE r.code = 'ROLE_SUPER_ADMIN'
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Administrators cannot manage users, roles, permissions, or permission groups.
INSERT INTO tbl_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM tbl_role r
CROSS JOIN tbl_permissions p
WHERE r.code = 'ROLE_ADMIN'
  AND split_part(p.code, ':', 1) NOT IN ('user', 'role', 'permission', 'permissionGroup')
ON CONFLICT (role_id, permission_id) DO NOTHING;

-- Users can read resources and perform limited day-to-day operations.
INSERT INTO tbl_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM tbl_role r
CROSS JOIN tbl_permissions p
WHERE r.code = 'ROLE_USER'
  AND (
      p.code LIKE '%:read'
      OR p.code = 'sale:create'
      OR p.code = 'sale:update'
      OR p.code = 'quote:create'
      OR p.code = 'payment:create'
      OR p.code = 'customer:create'
      OR p.code = 'file:upload'
  )
ON CONFLICT (role_id, permission_id) DO NOTHING;
