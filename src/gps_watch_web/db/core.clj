(ns gps-watch-web.db.core
  (:require
    [yesql.core :refer [defqueries]]))

(def db-spec {:subprotocol "postgresql"
              :subname "//localhost/gpswatch"
              :user "admin"
              :password "admin"})

(defqueries "sql/queries.sql" {:connection db-spec})

