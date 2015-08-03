(ns gps-tracker.db
  (:require [yesql.core :refer [defqueries]]))

(def db-spec {:subprotocol "postgresql"
              :subname "//localhost/gpstracker"
              :user "admin"
              :password "admin"})

(defqueries "sql/queries.sql" {:connection db-spec})


