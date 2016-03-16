(ns gps-tracker.schema-helpers
  (:require [gps-tracker-common.schema :as ct]
            [schema.core :as s]))

(defn singleton [type]
  (s/pred
   (fn [val]
     (and (empty? (rest val))
          (nil? (s/check type (first val)))))))

(defn list [first-type rest-type]
  (s/pred
   (fn [xs]
     (and (or
           (and (keyword? first-type) (= first-type (first xs)))
           (nil? (s/check first-type (first xs))))
          (nil? (s/check rest-type (rest xs)))))))

(defn set [type]
  (s/pred
   (fn [val]
     (and (set? val)
          (every? (fn [elem] (nil? (s/check type elem))) val)))))

;(s/validate Action '(:page :navigate {:id :waypoint-paths-new}))

#_(def validator (s/validator State))

;; (s/defschema ActionHandler {:action ct/Action
;;                             :callback (s/pred ifn?) ;; run on successful action
;;                             :state s/Any}) ;; state to rollback to on error

;; (s/defschema Remote
;;   {:action-queue [ActionHandler]
;;    (s/optional-key :waypoint-paths) (s/either [ct/WaypointPath]
;;                                               (s/eq :pending))})
