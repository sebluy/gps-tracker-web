(ns gps-watch-web.db.core
  (:require
    [yesql.core :refer [defqueries]]))

(def db-spec {:subprotocol "postgresql"
              :subname "//localhost/gpswatch"
              :user "admin"
              :password "admin"})

(defqueries "sql/queries.sql" {:connection db-spec})

(add-coordinate! {:latitude 44.9022426 :longitude -68.6624197})
(get-coordinates)

