--name: add-coordinate!
INSERT INTO coordinates
(latitude, longitude)
VALUES (:latitude, :longitude)

--name: get-coordinates
SELECT * FROM coordinates

--name: delete-all-coordinates!
DELETE FROM coordinates
