(ns gps-tracker.core
  (:require [om.next :as om]
            [ajax.core :as ajax]
            [gps-tracker.remote-parser :as remote]
            [gps-tracker.pages.core :as pages]
            [gps-tracker.history :as history]))

(defn initial-state []
  {:page (history/get-page)
   :remotes 0})

(defmulti read om/dispatch)

(defmethod read :waypoint-paths
  [{:keys [state ast]} key params]
  (if-let [paths (@state :waypoint-paths)]
    {:value paths}
    {:value :pending
     :remote true}))

(defmethod read :default
  [{:keys [state]} key params]
  (if-let [value (@state key)]
    {:value value}
    {:value :not-found}))

(defmulti mutate om/dispatch)

(defmethod mutate 'set-page
  [{:keys [state]} key {:keys [page]}]
  {:value {:keys [:page]}
   :action (fn []
             (history/set-page page)
             (swap! state assoc :page page))})

(defmethod mutate 'inc-remotes
  [{:keys [state]} key params]
  {:value {:keys [:remotes]}
   :action (fn []
             (swap! state update :remotes inc))})

(defmethod mutate 'add-waypoint-path
  [{:keys [state]} key {:keys [path]}]
  {:value {:keys [:waypoint-paths]}
   :remote true
   :action #(swap! state update :waypoint-paths (fn [paths] (into [path] paths)))})

(defn filter-out-path [paths id]
  (filterv
   (fn [path] (not= (path :id) id))
   paths))

(defmethod mutate 'delete-waypoint-path
  [{:keys [state]} key {:keys [path-id]}]
  {:value {:keys [:waypoint-paths]}
   :remote true
   :action #(swap! state update :waypoint-paths filter-out-path path-id)})

(defmethod mutate :default
  [env key params]
  {:action #(println "Bad mutation" key params)})

(def parser (om/parser {:read read :mutate mutate}))

(def reconciler
  (let [state (atom (initial-state))]
    (om/reconciler {:state state
                    :send (fn [query callback] (remote/send query callback state))
                    :parser parser})))

(defn mount-root []
  (om/add-root! reconciler pages/View (.getElementById js/document "app")))

(defn init! []
  (mount-root))
