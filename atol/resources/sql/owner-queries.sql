-- :name create-owner! :! :n
-- :doc creates a new owner record
INSERT INTO t_owner
(owner_name, email, owner_pass)
VALUES (:owner_name, :email, :owner_pass)

-- :name get-owner :? :*
-- :doc retrieves a owner record given the email
SELECT * FROM t_owner
WHERE email = :email
