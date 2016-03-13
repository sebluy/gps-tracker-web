(ns gps-tracker.schema-helpers
  (:require [gps-tracker-common.schema :as ct]
            [schema.core :as s]))

(defn singleton [type]
  (s/pred
   (fn [val]
     (and (= (count val) 1)
          (nil? (s/check type (first val)))))))

(defn action [f r]
  (s/pred
   (fn [action]
     (and (= f (first action))
          (nil? (s/check r (rest action)))))))


;(s/validate Action '(:page :navigate {:id :waypoint-paths-new}))

#_(def validator (s/validator State))

;; (s/defschema ActionHandler {:action ct/Action
;;                             :callback (s/pred ifn?) ;; run on successful action
;;                             :state s/Any}) ;; state to rollback to on error

;; (s/defschema Remote
;;   {:action-queue [ActionHandler]
;;    (s/optional-key :waypoint-paths) (s/either [ct/WaypointPath]
;;                                               (s/eq :pending))})
