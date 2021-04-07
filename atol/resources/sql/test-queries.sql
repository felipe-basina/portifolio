-- :name get-samples :? :n
-- :doc retrieves all samples
SELECT * FROM samples

-- :name create-sample! :! :n
-- :doc creates a new sample record
INSERT INTO samples
(description, created_on)
VALUES (:description, :created_on)
