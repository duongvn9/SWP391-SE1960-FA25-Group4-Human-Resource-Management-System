-- Test query to verify subordinates logic
-- This shows what subordinates each position can see

-- Test 1: ADMIN (job_level = 1) should see everyone with job_level > 1
SELECT 'ADMIN can see:' as test_case, u.id, u.full_name, u.employee_code, p.name as position, p.job_level, d.name as department
FROM users u
LEFT JOIN positions p ON u.position_id = p.id
LEFT JOIN departments d ON u.department_id = d.id
WHERE u.status = 'ACTIVE'
  AND p.job_level > 1
ORDER BY p.job_level, u.full_name;

-- Test 2: HR_MANAGER (job_level = 2) should see everyone with job_level > 2 (all departments)
SELECT 'HR_MANAGER can see:' as test_case, u.id, u.full_name, u.employee_code, p.name as position, p.job_level, d.name as department
FROM users u
LEFT JOIN positions p ON u.position_id = p.id
LEFT JOIN departments d ON u.department_id = d.id
WHERE u.status = 'ACTIVE'
  AND p.job_level > 2
ORDER BY p.job_level, u.full_name;

-- Test 3: DEPT_MANAGER (job_level = 4) should see only STAFF (job_level = 5) in SAME department
-- Example: DEPT_MANAGER in IT department
SELECT 'DEPT_MANAGER (IT) can see:' as test_case, u.id, u.full_name, u.employee_code, p.name as position, p.job_level, d.name as department
FROM users u
LEFT JOIN positions p ON u.position_id = p.id
LEFT JOIN departments d ON u.department_id = d.id
WHERE u.status = 'ACTIVE'
  AND p.job_level > 4
  AND u.department_id = (SELECT department_id FROM users WHERE position_id = (SELECT id FROM positions WHERE code = 'DEPT_MANAGER') LIMIT 1)
ORDER BY p.job_level, u.full_name;

-- Test 4: STAFF (job_level = 5) should see NO ONE
SELECT 'STAFF can see:' as test_case, u.id, u.full_name, u.employee_code, p.name as position, p.job_level, d.name as department
FROM users u
LEFT JOIN positions p ON u.position_id = p.id
LEFT JOIN departments d ON u.department_id = d.id
WHERE u.status = 'ACTIVE'
  AND p.job_level > 5
ORDER BY p.job_level, u.full_name;

-- Summary: Position hierarchy
SELECT 'Position Hierarchy:' as info, code, name, job_level,
       CASE
           WHEN code IN ('ADMIN', 'HR_MANAGER', 'HR_STAFF') THEN 'Can see all departments'
           WHEN code = 'DEPT_MANAGER' THEN 'Can see only same department'
           ELSE 'Cannot create for others'
       END as scope
FROM positions
ORDER BY job_level;
