-- ==================== DROP + CREATE DATABASE ====================
DROP DATABASE IF EXISTS hrms;
CREATE DATABASE hrms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hrms;

-- ==================== CORE ORG & RBAC ====================

CREATE TABLE roles (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  priority INT NOT NULL DEFAULT 0,
  is_system BOOLEAN NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP())
) ENGINE=InnoDB;

CREATE TABLE positions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(255) NOT NULL,
  job_level INT NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP())
) ENGINE=InnoDB;

CREATE TABLE departments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  head_account_id BIGINT NULL, -- department head (by account)
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP())
) ENGINE=InnoDB;

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_code VARCHAR(64) NULL UNIQUE,
  full_name VARCHAR(255) NOT NULL,
  -- Personal
  dob DATE NULL,
  gender VARCHAR(16) NULL,
  hometown VARCHAR(255) NULL,
  cccd VARCHAR(32) NULL UNIQUE,
  cccd_issued_date DATE NULL,
  cccd_issued_place VARCHAR(255) NULL,
  cccd_front_path VARCHAR(1024) NULL,
  cccd_back_path VARCHAR(1024) NULL,
  -- Contact
  email_company VARCHAR(255) NULL UNIQUE,
  phone VARCHAR(32) NULL,
  -- Org
  department_id BIGINT NULL,
  position_id BIGINT NULL,
  status VARCHAR(24) NOT NULL DEFAULT 'active',
  -- Employment
  date_joined DATE NULL,
  date_left DATE NULL,
  start_work_date DATE NULL,
  -- Address
  address_line1 VARCHAR(255) NULL,
  address_line2 VARCHAR(255) NULL,
  city VARCHAR(100) NULL,
  state VARCHAR(100) NULL,
  postal_code VARCHAR(20) NULL,
  country VARCHAR(100) NULL,
  -- Application link
  application_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  CONSTRAINT FK_users_dept FOREIGN KEY (department_id) REFERENCES departments(id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_users_pos FOREIGN KEY (position_id) REFERENCES positions(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE accounts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  username VARCHAR(128) NULL UNIQUE,
  email_login VARCHAR(255) NULL UNIQUE,
  status VARCHAR(24) NOT NULL DEFAULT 'active',
  failed_attempts INT NOT NULL DEFAULT 0,
  last_login_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_accounts_user (user_id),
  CONSTRAINT FK_accounts_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

ALTER TABLE departments
  ADD CONSTRAINT FK_dept_head FOREIGN KEY (head_account_id)
  REFERENCES accounts(id)
  ON DELETE SET NULL ON UPDATE CASCADE;

-- ==================== AUTH ====================
CREATE TABLE auth_identities (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id BIGINT NOT NULL,
  provider VARCHAR(32) NOT NULL,
  provider_user_id VARCHAR(255) NOT NULL,
  email VARCHAR(255) NULL,
  email_verified BOOLEAN NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_auth_provider_subject (provider, provider_user_id),
  INDEX IX_auth_account (account_id),
  CONSTRAINT FK_identity_account FOREIGN KEY (account_id)
    REFERENCES accounts(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE auth_local_credentials (
  identity_id BIGINT NOT NULL PRIMARY KEY,
  password_hash VARCHAR(255) NOT NULL,
  password_updated_at DATETIME NULL,
  CONSTRAINT FK_local_identity FOREIGN KEY (identity_id)
    REFERENCES auth_identities(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ==================== RBAC MAPPINGS ====================
CREATE TABLE position_roles (
  position_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (position_id, role_id),
  CONSTRAINT FK_pos_roles_position FOREIGN KEY (position_id)
    REFERENCES positions(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_pos_roles_role FOREIGN KEY (role_id)
    REFERENCES roles(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE features (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(64) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(255) NULL,
  route VARCHAR(255) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  is_active BOOLEAN NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP())
) ENGINE=InnoDB;

CREATE TABLE role_features (
  role_id BIGINT NOT NULL,
  feature_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, feature_id),
  CONSTRAINT FK_rf_role FOREIGN KEY (role_id)
    REFERENCES roles(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_rf_feature FOREIGN KEY (feature_id)
    REFERENCES features(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE department_features (
  department_id BIGINT NOT NULL,
  feature_id BIGINT NOT NULL,
  effect ENUM('GRANT', 'DENY') NOT NULL,
  PRIMARY KEY (department_id, feature_id),
  CONSTRAINT FK_df_dept FOREIGN KEY (department_id)
    REFERENCES departments(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_df_feature FOREIGN KEY (feature_id)
    REFERENCES features(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE account_features (
  account_id BIGINT NOT NULL,
  feature_id BIGINT NOT NULL,
  effect ENUM('GRANT', 'DENY') NOT NULL,
  PRIMARY KEY (account_id, feature_id),
  CONSTRAINT FK_af_account FOREIGN KEY (account_id)
    REFERENCES accounts(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_af_feature FOREIGN KEY (feature_id)
    REFERENCES features(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ==================== WORKFLOWS / REQUESTS ====================
CREATE TABLE request_types (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP())
) ENGINE=InnoDB;

CREATE TABLE requests (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_type_id BIGINT NOT NULL,
  title VARCHAR(255) NULL,
  detail JSON NULL,
  created_by_account_id BIGINT NOT NULL,
  created_by_user_id BIGINT NOT NULL,
  department_id BIGINT NULL,
  status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
  current_approver_account_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_requests_type_status (request_type_id, status),
  INDEX IX_requests_creator (created_by_account_id),
  CONSTRAINT FK_requests_type FOREIGN KEY (request_type_id)
    REFERENCES request_types(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_requests_creator_account FOREIGN KEY (created_by_account_id)
    REFERENCES accounts(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_requests_creator_user FOREIGN KEY (created_by_user_id)
    REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_requests_dept FOREIGN KEY (department_id)
    REFERENCES departments(id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_requests_current_approver FOREIGN KEY (current_approver_account_id)
    REFERENCES accounts(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE request_transitions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id BIGINT NOT NULL,
  from_status VARCHAR(24) NULL,
  to_status VARCHAR(24) NOT NULL,
  action VARCHAR(24) NOT NULL,
  actor_account_id BIGINT NOT NULL,
  actor_user_id BIGINT NOT NULL,
  note VARCHAR(512) NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_rt_request (request_id, created_at),
  CONSTRAINT FK_rt_request FOREIGN KEY (request_id)
    REFERENCES requests(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_rt_actor_account FOREIGN KEY (actor_account_id)
    REFERENCES accounts(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_rt_actor_user FOREIGN KEY (actor_user_id)
    REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ==================== TIMESHEET / ATTENDANCE ====================
CREATE TABLE timesheet_periods (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(64) NOT NULL,
  date_start DATE NOT NULL,
  date_end DATE NOT NULL,
  is_locked BOOLEAN NOT NULL DEFAULT 0,
  locked_by BIGINT NULL,
  locked_at DATETIME NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_tsp (date_start, date_end),
  CONSTRAINT FK_tsp_locked_by FOREIGN KEY (locked_by)
    REFERENCES accounts(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE attendance_logs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  check_type VARCHAR(3) NOT NULL, -- IN/OUT/etc.
  checked_at DATETIME NOT NULL,
  source VARCHAR(32) NULL,
  note VARCHAR(255) NULL,
  period_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_att_user_time (user_id, checked_at),
  CONSTRAINT FK_att_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_att_period FOREIGN KEY (period_id)
    REFERENCES timesheet_periods(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ==================== LEAVE ====================
CREATE TABLE leave_types (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  default_days DECIMAL(5,2) NOT NULL DEFAULT 0.00,
  is_paid BOOLEAN NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP())
) ENGINE=InnoDB;

CREATE TABLE leave_balances (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  leave_type_id BIGINT NOT NULL,
  year INT NOT NULL,
  balance_days DECIMAL(6,2) NOT NULL DEFAULT 0.00,
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_lb (user_id, leave_type_id, year),
  CONSTRAINT FK_lb_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_lb_type FOREIGN KEY (leave_type_id)
    REFERENCES leave_types(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE leave_ledger (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  leave_type_id BIGINT NOT NULL,
  request_id BIGINT NULL,
  delta_days DECIMAL(6,2) NOT NULL,
  note VARCHAR(255) NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_ll_user_time (user_id, created_at),
  CONSTRAINT FK_ll_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_ll_type FOREIGN KEY (leave_type_id)
    REFERENCES leave_types(id)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT FK_ll_request FOREIGN KEY (request_id)
    REFERENCES requests(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ==================== RECRUITMENT ====================
CREATE TABLE job_postings (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_id BIGINT NULL,
  title VARCHAR(255) NOT NULL,
  department_id BIGINT NULL,
  description TEXT NULL,
  status VARCHAR(24) NOT NULL DEFAULT 'draft',
  published_at DATETIME NULL,
  created_by_account_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  CONSTRAINT FK_job_request FOREIGN KEY (request_id)
    REFERENCES requests(id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_job_dept FOREIGN KEY (department_id)
    REFERENCES departments(id)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT FK_job_creator FOREIGN KEY (created_by_account_id)
    REFERENCES accounts(id)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE TABLE applications (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  job_id BIGINT NOT NULL,
  status VARCHAR(24) NOT NULL DEFAULT 'new',
  note VARCHAR(512) NULL,
  -- Personal info
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NULL,
  phone VARCHAR(32) NULL,
  dob DATE NULL,
  gender VARCHAR(16) NULL,
  hometown VARCHAR(255) NULL,
  -- Address
  address_line1 VARCHAR(255) NULL,
  address_line2 VARCHAR(255) NULL,
  city VARCHAR(100) NULL,
  state VARCHAR(100) NULL,
  postal_code VARCHAR(20) NULL,
  country VARCHAR(100) NULL,
  resume_path VARCHAR(1024) NULL,
  -- CCCD
  cccd VARCHAR(32) NULL,
  cccd_issued_date DATE NULL,
  cccd_issued_place VARCHAR(255) NULL,
  cccd_front_path VARCHAR(1024) NULL,
  cccd_back_path VARCHAR(1024) NULL,
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  CONSTRAINT FK_app_job FOREIGN KEY (job_id)
    REFERENCES job_postings(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- MySQL không hỗ trợ UNIQUE INDEX ... WHERE, nên ta mô phỏng bằng TRIGGER hoặc bỏ qua constraint.
-- Ở đây, ta chỉ tạo index thông thường.
CREATE INDEX IX_app_job_email ON applications(job_id, email);
CREATE INDEX IX_app_job_cccd ON applications(job_id, cccd);

-- Link one-to-one application_id in users
ALTER TABLE users
  ADD CONSTRAINT FK_users_application FOREIGN KEY (application_id)
  REFERENCES applications(id)
  ON DELETE SET NULL ON UPDATE CASCADE;

CREATE UNIQUE INDEX UQ_users_application_id ON users(application_id);

-- ==================== PAYROLL ====================
CREATE TABLE payslips (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  period_start DATE NOT NULL,
  period_end DATE NOT NULL,
  currency VARCHAR(8) NULL,
  gross_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  net_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00,
  details_json JSON NULL,
  file_path VARCHAR(1024) NULL,
  status VARCHAR(24) NOT NULL DEFAULT 'approved',
  created_at DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_ps_user_period (user_id, period_start, period_end),
  CONSTRAINT FK_ps_user FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB;

-- ==================== UTILITIES ====================

CREATE TABLE attachments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  owner_type VARCHAR(64) NOT NULL,
  owner_id BIGINT NOT NULL,
  path VARCHAR(1024) NOT NULL,
  original_name VARCHAR(255) NOT NULL,
  content_type VARCHAR(128) NULL,
  size_bytes BIGINT NULL,
  checksum_sha256 CHAR(64) NULL,
  uploaded_by_account_id BIGINT NULL,
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_attach_owner (owner_type, owner_id),
  CONSTRAINT FK_attach_uploader FOREIGN KEY (uploaded_by_account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== OUTBOX ====================

CREATE TABLE outbox_messages (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  topic VARCHAR(128) NOT NULL,
  payload_json JSON NOT NULL,
  headers_json JSON NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'NEW',
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  sent_at DATETIME(0) NULL,
  CHECK (JSON_VALID(payload_json)),
  CHECK (headers_json IS NULL OR JSON_VALID(headers_json))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== AUDIT ====================

CREATE TABLE audit_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  account_id BIGINT NULL,
  event_type VARCHAR(64) NOT NULL,
  entity_type VARCHAR(64) NULL,
  entity_id BIGINT NULL,
  ip VARCHAR(64) NULL,
  user_agent VARCHAR(255) NULL,
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_audit_entity (entity_type, entity_id, created_at),
  CONSTRAINT FK_audit_account FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== SYSTEM PARAMETERS ====================

CREATE TABLE system_parameters (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  scope_type VARCHAR(16) NOT NULL DEFAULT 'GLOBAL',
  scope_id BIGINT NULL,
  namespace VARCHAR(64) NOT NULL,
  param_key VARCHAR(64) NOT NULL,
  value_json JSON NOT NULL,
  description VARCHAR(255) NULL,
  updated_by_account_id BIGINT NULL,
  updated_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_sp (scope_type, scope_id, namespace, param_key),
  CHECK (JSON_VALID(value_json)),
  CONSTRAINT FK_sp_updated_by FOREIGN KEY (updated_by_account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== HOLIDAY CALENDAR ====================

CREATE TABLE holiday_calendar (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  year INT NOT NULL,
  name VARCHAR(64) NOT NULL,
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_hcal (year, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE holidays (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  calendar_id BIGINT NOT NULL,
  date_holiday DATE NOT NULL,
  name VARCHAR(128) NOT NULL,
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_holidays (calendar_id, date_holiday),
  CONSTRAINT FK_holidays_calendar FOREIGN KEY (calendar_id) REFERENCES holiday_calendar(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== OT POLICY (merged JSON) ====================

CREATE TABLE ot_policies (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(128) NOT NULL,
  description VARCHAR(255) NULL,
  rules_json JSON NULL,
  assignments_json JSON NULL,
  updated_by_account_id BIGINT NULL,
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  CHECK (rules_json IS NULL OR JSON_VALID(rules_json)),
  CHECK (assignments_json IS NULL OR JSON_VALID(assignments_json)),
  CONSTRAINT FK_ot_updated_by FOREIGN KEY (updated_by_account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ==================== CONTRACTS & SALARY ====================

CREATE TABLE employment_contracts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  contract_no VARCHAR(50) NULL,
  contract_type VARCHAR(32) NULL,
  start_date DATE NOT NULL,
  end_date DATE NULL,
  base_salary DECIMAL(18,2) NOT NULL,
  currency VARCHAR(8) NULL,
  status VARCHAR(24) NOT NULL DEFAULT 'active',
  file_path VARCHAR(1024) NULL,
  note VARCHAR(255) NULL,
  created_by_account_id BIGINT NULL,
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  updated_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  INDEX IX_contract_user_start (user_id, start_date),
  CONSTRAINT FK_contract_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT FK_contract_creator FOREIGN KEY (created_by_account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================== SALARY HISTORY ====================

CREATE TABLE salary_history (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  amount DECIMAL(18,2) NOT NULL,
  currency VARCHAR(8) NULL,
  effective_from DATE NOT NULL,
  effective_to DATE NULL,
  reason VARCHAR(255) NULL,
  created_by_account_id BIGINT NULL,
  created_at DATETIME(0) NOT NULL DEFAULT (UTC_TIMESTAMP()),
  UNIQUE KEY UQ_salary_period (user_id, effective_from),
  CONSTRAINT FK_salary_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT FK_salary_created_by FOREIGN KEY (created_by_account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
