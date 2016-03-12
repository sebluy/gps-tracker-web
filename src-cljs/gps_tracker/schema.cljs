(ns gps-tracker.schema
  (:require [gps-tracker-common.schema :as ct]
            [schema.core :as s]))

(s/defschema WaypointPathsIndexPage
  {:id (s/eq :waypoint-paths-index)})

(s/defschema WaypointPathsNewPage
  {:id (s/eq :waypoint-paths-new)
   :path ct/WaypointPath})

(s/defschema WaypointPathsShowPage
  {:id (s/eq :waypoint-paths-show)
   :path-id ct/Date})

(s/defschema NotFoundPage
  {:id (s/eq :not-found)})

(s/defschema Page (s/either WaypointPathsIndexPage
                            WaypointPathsNewPage
                            WaypointPathsShowPage
                            NotFoundPage))

(s/defschema State {:page Page
                    :waypoint-paths [ct/WaypointPath]})

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

(s/defschema WaypointPathsNewPageAction
  (s/either
   (action :submit (singleton ct/WaypointPath))
   (action :add-point (singleton ct/Waypoint))))

(s/defschema WaypointPathsShowPageAction
  (action :delete (singleton ct/PathID)))

(s/defschema PageAction
  (s/either
   (action :navigate (singleton Page))
   (action :waypoint-paths-new WaypointPathsNewPageAction)
   (action :waypoint-paths-show WaypointPathsShowPageAction)))

(s/defschema WaypointPathsAction
  (s/either
   (action :create (singleton ct/WaypointPath))
   (action :delete (singleton ct/PathID))))

(s/defschema Action (s/either
                     (s/eq '(:init))
                     (action :page PageAction)
                     (action :waypoint-paths WaypointPathsAction)))

#_(def validator (s/validator State))

;; (s/defschema ActionHandler {:action ct/Action
;;                             :callback (s/pred ifn?) ;; run on successful action
;;                             :state s/Any}) ;; state to rollback to on error

;; (s/defschema Remote
;;   {:action-queue [ActionHandler]
;;    (s/optional-key :waypoint-paths) (s/either [ct/WaypointPath]
;;                                               (s/eq :pending))})
