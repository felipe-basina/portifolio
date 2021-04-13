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