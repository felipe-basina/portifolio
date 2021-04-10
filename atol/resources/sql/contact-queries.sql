-- :name get-contacts :? :*
-- :doc retrieves contacts record given the owner's idt
SELECT * FROM t_contact
WHERE owner_idt = :owner_idt
ORDER BY idt
