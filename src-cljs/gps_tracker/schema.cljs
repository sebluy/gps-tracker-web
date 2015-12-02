(ns gps-tracker.schema
  (:require [gps-tracker-common.schema :as ct]
            [schema.core :as s]))

;; find a solution to "params"

(s/defschema ShowWaypointPage
  {:id (s/eq :waypoint-path)
   :params {:path-id ct/Date}})

(s/defschema ListWaypointsPage
  {:id (s/eq :waypoint-paths)
   (s/optional-key :params) (s/pred nil?)})

(s/defschema NewWaypointPage
  {:id (s/eq :new-waypoint-path)
   (s/optional-key :params) (s/pred nil?)
   :waypoint-path ct/WaypointPath})

(s/defschema Page (s/either ShowWaypointPage
                            ListWaypointsPage
                            NewWaypointPage))

(s/defschema Remote {:waypoint-paths (s/either [ct/WaypointPath]
                                               (s/eq :pending))})

(s/defschema State {:page Page
                    (s/optional-key :remote) Remote})

;; still have to fix {:page {:id nil :params nil}}
(def validator (s/validator State))
