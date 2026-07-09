-- Spraxe Support -- database changes required for this app.
--
-- Run this migration against the SAME Supabase project used by spraxe-web and
-- spraxeapp (kybgrsqqvejbvjediowo.supabase.co). It:
--   1. Adds a "moderator" role alongside "customer"/"admin" on `profiles`, and gives
--      moderators the same staff-level access as admins across every table this app
--      manages (products, categories, orders, support tickets, discount codes, etc.)
--   2. Adds `assigned_to` to `support_tickets` so a conversation can be assigned to a
--      specific staff member.
--   3. Creates `support_messages`, the live-chat thread that hangs off each support
--      ticket, and enables Supabase Realtime on it so staff (and, once wired up on the
--      website/customer app, customers) see new messages instantly.

-- ---------------------------------------------------------------------------
-- 1. Allow "moderator" as a profile role, and let moderators act as staff.
-- ---------------------------------------------------------------------------
ALTER TABLE profiles DROP CONSTRAINT IF EXISTS profiles_role_check;
ALTER TABLE profiles ADD CONSTRAINT profiles_role_check
  CHECK (role IN ('customer', 'admin', 'moderator'));

-- Helper used by every "is this an admin?" RLS policy below so admins and moderators
-- are treated identically (both are "staff" for the purposes of this app).
CREATE OR REPLACE FUNCTION is_staff(uid uuid)
RETURNS boolean
LANGUAGE sql
SECURITY DEFINER
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM profiles WHERE profiles.id = uid AND profiles.role IN ('admin', 'moderator')
  );
$$;

-- Re-point every existing "role = 'admin'" style policy at is_staff() so moderators get
-- the same management access admins already have. (Policies are dropped and recreated
-- because Postgres has no CREATE OR REPLACE POLICY.)

-- profiles
DROP POLICY IF EXISTS "Admins can view all tickets" ON support_tickets;
DROP POLICY IF EXISTS "Admins can manage tickets" ON support_tickets;
CREATE POLICY "Staff can view all tickets" ON support_tickets FOR SELECT TO authenticated USING (is_staff(auth.uid()));
CREATE POLICY "Staff can manage tickets" ON support_tickets FOR UPDATE TO authenticated USING (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Admins can manage applications" ON seller_applications;
CREATE POLICY "Staff can manage applications" ON seller_applications FOR ALL TO authenticated
  USING (is_staff(auth.uid())) WITH CHECK (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Admins can manage discount codes" ON discount_codes;
CREATE POLICY "Staff can manage discount codes" ON discount_codes FOR ALL TO authenticated
  USING (is_staff(auth.uid())) WITH CHECK (is_staff(auth.uid()));

-- products / categories / orders / order_items / profiles / featured_images / feature_cards /
-- site_settings: grant staff full access in addition to whatever policies already exist.
-- (DROP POLICY IF EXISTS first since Postgres has no CREATE POLICY IF NOT EXISTS, in case
-- this migration is ever re-run.)
DROP POLICY IF EXISTS "Staff can manage products" ON products;
CREATE POLICY "Staff can manage products" ON products FOR ALL TO authenticated
  USING (is_staff(auth.uid())) WITH CHECK (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can manage categories" ON categories;
CREATE POLICY "Staff can manage categories" ON categories FOR ALL TO authenticated
  USING (is_staff(auth.uid())) WITH CHECK (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can view all orders" ON orders;
CREATE POLICY "Staff can view all orders" ON orders FOR SELECT TO authenticated
  USING (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can update all orders" ON orders;
CREATE POLICY "Staff can update all orders" ON orders FOR UPDATE TO authenticated
  USING (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can view all order items" ON order_items;
CREATE POLICY "Staff can view all order items" ON order_items FOR SELECT TO authenticated
  USING (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can view all profiles" ON profiles;
CREATE POLICY "Staff can view all profiles" ON profiles FOR SELECT TO authenticated
  USING (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can manage featured images" ON featured_images;
CREATE POLICY "Staff can manage featured images" ON featured_images FOR ALL TO authenticated
  USING (is_staff(auth.uid())) WITH CHECK (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can manage feature cards" ON feature_cards;
CREATE POLICY "Staff can manage feature cards" ON feature_cards FOR ALL TO authenticated
  USING (is_staff(auth.uid())) WITH CHECK (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can manage site settings" ON site_settings;
CREATE POLICY "Staff can manage site settings" ON site_settings FOR ALL TO authenticated
  USING (is_staff(auth.uid())) WITH CHECK (is_staff(auth.uid()));

DROP POLICY IF EXISTS "Staff can view invoices" ON invoices;
CREATE POLICY "Staff can view invoices" ON invoices FOR SELECT TO authenticated
  USING (is_staff(auth.uid()));

-- ---------------------------------------------------------------------------
-- 2. Ticket assignment.
-- ---------------------------------------------------------------------------
ALTER TABLE support_tickets ADD COLUMN IF NOT EXISTS assigned_to uuid REFERENCES auth.users(id) ON DELETE SET NULL;

-- ---------------------------------------------------------------------------
-- 3. Live chat thread per support ticket.
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS support_messages (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_id uuid NOT NULL REFERENCES support_tickets(id) ON DELETE CASCADE,
  sender_id uuid REFERENCES auth.users(id) ON DELETE SET NULL,
  sender_role text NOT NULL DEFAULT 'customer' CHECK (sender_role IN ('customer', 'staff', 'admin', 'moderator')),
  message text NOT NULL,
  is_read boolean DEFAULT false,
  created_at timestamptz DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_support_messages_ticket ON support_messages(ticket_id, created_at);

ALTER TABLE support_messages ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Customers can view their own ticket messages"
  ON support_messages FOR SELECT
  TO authenticated
  USING (
    EXISTS (
      SELECT 1 FROM support_tickets
      WHERE support_tickets.id = support_messages.ticket_id
      AND support_tickets.user_id = auth.uid()
    )
  );

CREATE POLICY "Customers can send messages on their own tickets"
  ON support_messages FOR INSERT
  TO authenticated
  WITH CHECK (
    sender_role = 'customer'
    AND EXISTS (
      SELECT 1 FROM support_tickets
      WHERE support_tickets.id = support_messages.ticket_id
      AND support_tickets.user_id = auth.uid()
    )
  );

CREATE POLICY "Staff can view all ticket messages"
  ON support_messages FOR SELECT
  TO authenticated
  USING (is_staff(auth.uid()));

CREATE POLICY "Staff can send and update ticket messages"
  ON support_messages FOR ALL
  TO authenticated
  USING (is_staff(auth.uid()))
  WITH CHECK (is_staff(auth.uid()));

-- Seed a support_messages row for every existing ticket from its original `message`, so
-- older tickets show their first message in the new chat thread view.
INSERT INTO support_messages (ticket_id, sender_id, sender_role, message, is_read, created_at)
SELECT id, user_id, 'customer', message, true, created_at
FROM support_tickets
WHERE NOT EXISTS (
  SELECT 1 FROM support_messages WHERE support_messages.ticket_id = support_tickets.id
);

-- Enable Realtime (postgres_changes) on the new table so the app's Live Chat screens get
-- pushed new messages instantly instead of polling. Wrapped in DO blocks so re-running
-- this migration doesn't fail if a table is already published.
DO $$
BEGIN
  ALTER PUBLICATION supabase_realtime ADD TABLE support_messages;
EXCEPTION WHEN duplicate_object THEN
  NULL;
END $$;

DO $$
BEGIN
  ALTER PUBLICATION supabase_realtime ADD TABLE support_tickets;
EXCEPTION WHEN duplicate_object THEN
  NULL;
END $$;
