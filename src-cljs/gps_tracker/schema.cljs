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

(s/defschema ActionHandler {:action ct/Action
                            :callback (s/pred ifn?) ;; run on successful action
                            :state s/Any}) ;; state to rollback to on error

(s/defschema Remote
  {:action-queue [ActionHandler]
   (s/optional-key :waypoint-paths) (s/either [ct/WaypointPath]
                                              (s/eq :pending))})

(s/defschema State {:page Page
                    :remote Remote})

(def validator (s/validator State))
