-- :name create-contact! :! :n
-- :doc creates a new contact record
INSERT INTO t_contact
(contact_name, email, phone_number, owner_idt)
VALUES (:contact_name, :email, :phone_number, :owner_idt)

-- :name get-contacts :? :*
-- :doc retrieves contacts record given the owner's idt
SELECT * FROM t_contact
WHERE owner_idt = :owner_idt
ORDER BY idt

-- :name get-contact :? :1
-- :doc retrieves contact record given it's idt
SELECT * FROM t_contact
WHERE idt = :idt
AND owner_idt = :owner_idt

-- :name update-contact! :! :n
-- :doc updates an existing contact record
update t_contact set
    contact_name = :contact_name,
    email = :email,
    phone_number = :phone_number,
    update_dt = current_timestamp
WHERE idt = :idt

-- :name delete-contact :? :1
-- :doc removes contact record given it's idt
DELETE FROM t_contact
WHERE idt = :idt
AND owner_idt = :owner_idt