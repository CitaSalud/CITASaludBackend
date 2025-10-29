-- V1__create_schema.sql
-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Roles table (optional)
CREATE TABLE roles (
  role_id SMALLSERIAL PRIMARY KEY,
  name TEXT UNIQUE NOT NULL,
  description TEXT
);

INSERT INTO roles(name, description)
  VALUES ('affiliate', 'Afiliado'),
         ('professional', 'Profesional'),
         ('coordinator', 'Coordinador'),
         ('admin', 'Administrador')
ON CONFLICT DO NOTHING;

-- Users
CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  email TEXT UNIQUE NOT NULL,
  nombre TEXT NOT NULL,
  telefono TEXT,
  preferred_contact_method TEXT,
  language TEXT,
  hashed_password TEXT,
  role_id SMALLINT REFERENCES roles(role_id),
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX ux_users_email ON users (email);

-- Sites
CREATE TABLE sites (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  nombre TEXT NOT NULL,
  direccion TEXT,
  time_zone TEXT, -- e.g. 'America/Bogota'
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Specialities
CREATE TABLE specialities (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  nombre TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT now()
);

-- Professionals (link to users)
CREATE TABLE professionals (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  specialty_id UUID REFERENCES specialities(id),
  title TEXT,
  active BOOLEAN DEFAULT true,
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_professionals_user ON professionals(user_id);

-- Availability slots (recurring definitions)
CREATE TABLE availability_slots (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  professional_id UUID NOT NULL REFERENCES professionals(id) ON DELETE CASCADE,
  site_id UUID REFERENCES sites(id),
  -- local times (time of day) for recurring pattern
  start_time_local TIME NOT NULL,
  end_time_local TIME NOT NULL,
  day_of_week SMALLINT,        -- 0=Sunday .. 6=Saturday OR use enum
  start_date DATE,            -- start of recurrence period (nullable)
  end_date DATE,              -- end of recurrence (nullable)
  slot_duration_minutes INT NOT NULL DEFAULT 30,
  recurrence_rule TEXT,       -- optional RFC5545 RRULE (if used)
  allow_cancel BOOLEAN DEFAULT true,
  min_hours_before INT DEFAULT 24,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_availability_professional ON availability_slots(professional_id);

-- Available slot instances (materialized instances of availability)
CREATE TABLE available_slot_instances (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  availability_slot_id UUID REFERENCES availability_slots(id) ON DELETE CASCADE,
  start_at TIMESTAMPTZ NOT NULL,
  end_at TIMESTAMPTZ NOT NULL,
  status TEXT NOT NULL DEFAULT 'available', -- available, booked, cancelled, blocked
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_slot_instances_prof_start ON available_slot_instances(availability_slot_id, start_at);
CREATE INDEX idx_slot_instances_prof ON available_slot_instances(start_at);

-- Appointments
CREATE TABLE appointments (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  affiliate_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  professional_id UUID NOT NULL REFERENCES professionals(id) ON DELETE CASCADE,
  site_id UUID REFERENCES sites(id),
  availability_slot_instance_id UUID REFERENCES available_slot_instances(id),
  start_at TIMESTAMPTZ NOT NULL,
  end_at TIMESTAMPTZ NOT NULL,
  status TEXT NOT NULL DEFAULT 'scheduled', -- scheduled, cancelled, completed, no_show
  reason TEXT,
  created_by UUID REFERENCES users(id),
  created_at TIMESTAMPTZ DEFAULT now(),
  updated_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_appointments_prof_start ON appointments(professional_id, start_at);
CREATE INDEX idx_appointments_affiliate_start ON appointments(affiliate_id, start_at);

-- Exclusion constraint to prevent overlapping appointments per professional:
-- requires btree_gist extension for uuid equality operator in gist index
ALTER TABLE appointments
  ADD CONSTRAINT appointments_no_overlap EXCLUDE USING GIST (
    professional_id WITH =,
    tstzrange(start_at, end_at) WITH &&
  );

-- Notifications queue table (simple)
CREATE TABLE notifications (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  appointment_id UUID REFERENCES appointments(id),
  type TEXT NOT NULL, -- email, sms, push
  payload JSONB,
  status TEXT NOT NULL DEFAULT 'pending', -- pending, sent, failed
  attempts INT DEFAULT 0,
  available_at TIMESTAMPTZ DEFAULT now(),
  created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_notifications_status ON notifications(status, available_at);

-- Audit logs
CREATE TABLE audit_logs (
  id BIGSERIAL PRIMARY KEY,
  table_name TEXT NOT NULL,
  record_id UUID,
  operation TEXT NOT NULL, -- INSERT, UPDATE, DELETE
  changed_by UUID,
  changed_at TIMESTAMPTZ DEFAULT now(),
  diff JSONB
);

CREATE INDEX idx_audit_table ON audit_logs(table_name, record_id);

-- Trigger to populate audit_logs on appointments change
CREATE OR REPLACE FUNCTION fn_audit_appointments() RETURNS trigger AS $$
DECLARE
  payload JSONB;
BEGIN
  IF (TG_OP = 'DELETE') THEN
    payload := to_jsonb(OLD);
    INSERT INTO audit_logs(table_name, record_id, operation, changed_by, changed_at, diff)
      VALUES ('appointments', OLD.id, 'DELETE', current_setting('app.current_user', true)::uuid, now(), payload);
    RETURN OLD;
  ELSIF (TG_OP = 'INSERT') THEN
    payload := to_jsonb(NEW);
    INSERT INTO audit_logs(table_name, record_id, operation, changed_by, changed_at, diff)
      VALUES ('appointments', NEW.id, 'INSERT', current_setting('app.current_user', true)::uuid, now(), payload);
    RETURN NEW;
  ELSE
    -- UPDATE
    payload := jsonb_build_object('old', to_jsonb(OLD), 'new', to_jsonb(NEW));
    INSERT INTO audit_logs(table_name, record_id, operation, changed_by, changed_at, diff)
      VALUES ('appointments', NEW.id, 'UPDATE', current_setting('app.current_user', true)::uuid, now(), payload);
    RETURN NEW;
  END IF;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_audit_appointments ON appointments;
CREATE TRIGGER trg_audit_appointments
  AFTER INSERT OR UPDATE OR DELETE ON appointments
  FOR EACH ROW EXECUTE FUNCTION fn_audit_appointments();

-- Trigger to enqueue notification on appointment creation / cancellation
CREATE OR REPLACE FUNCTION fn_notify_on_appointment_change() RETURNS trigger AS $$
DECLARE
  payload JSONB;
  ntype TEXT;
BEGIN
  IF TG_OP = 'INSERT' THEN
    ntype := 'appointment_created';
    payload := jsonb_build_object('appointment_id', NEW.id, 'affiliate_id', NEW.affiliate_id, 'professional_id', NEW.professional_id);
    INSERT INTO notifications(appointment_id, type, payload, status) VALUES (NEW.id, ntype, payload, 'pending');
    RETURN NEW;
  ELSIF TG_OP = 'UPDATE' THEN
    IF OLD.status IS DISTINCT FROM NEW.status THEN
      ntype := 'appointment_status_changed';
      payload := jsonb_build_object('appointment_id', NEW.id, 'old_status', OLD.status, 'new_status', NEW.status);
      INSERT INTO notifications(appointment_id, type, payload, status) VALUES (NEW.id, ntype, payload, 'pending');
    END IF;
    RETURN NEW;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_notify_appointments ON appointments;
CREATE TRIGGER trg_notify_appointments
  AFTER INSERT OR UPDATE ON appointments
  FOR EACH ROW EXECUTE FUNCTION fn_notify_on_appointment_change();

-- Optionally: table to store system config / global policies
CREATE TABLE system_config (
  key TEXT PRIMARY KEY,
  value TEXT,
  updated_at TIMESTAMPTZ DEFAULT now()
);