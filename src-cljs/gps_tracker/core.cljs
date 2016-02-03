(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [om.next :as om]
            [om.dom :as dom]
            [ajax.core :as ajax]
            [gps-tracker.subs]
            [gps-tracker.db :as db]
            [gps-tracker.pages.core :as pages]
            [gps-tracker.navigation :as navigation]))

(def initial-state {:page {:id :waypoint-paths}
                    :remote {:action-queue []}})

(defmulti read om/dispatch)

(defmethod read :waypoint-paths
  [{:keys [state]} key params]
  (if-let [paths (@state :waypoint-paths)]
    {:value paths}
    {:value :pending
     :remote true}))

(defmethod read :default
  [env key params]
  {:value :not-found})

(defn on-error
  [_]
  (js/alert "Remote error... You may need to refresh the page."))

(defn post-actions
  [actions on-success]
  (ajax/POST
   "/api"
    {:params          actions
     :handler         on-success
     :error-handler   on-error
     :format          :edn
     :response-format :edn}))

(defn send
  [query callback]
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (fn [results]
                  (callback {:waypoint-paths (first results)}))))

(def parser (om/parser {:read read}))

(def reconciler (om/reconciler {:state initial-state
                                :send send
                                :parser parser}))

(defn mount-root []
  (om/add-root! reconciler pages/View (.getElementById js/document "app")))

(defn init! []
  (mount-root))
