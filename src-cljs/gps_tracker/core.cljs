(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [om.next :as om]
            [om.dom :as dom]
            [ajax.core :as ajax]
            [gps-tracker.subs]
            [gps-tracker.db :as db]
            [gps-tracker.pages.core :as pages]
            [gps-tracker.navigation :as navigation]))

(def initial-state {:page {:id :waypoint-paths}})

(defmulti read om/dispatch)

(defmethod read :waypoint-paths
  [{:keys [state]} key params]
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
   :action #(swap! state assoc :page page)})

(defmethod mutate :default
  [env key params]
  {:action #(println "Bad mutation")})

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
  (println "Sending " query)
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (fn [results]
                  (callback {:waypoint-paths (first results)}))))

(def parser (om/parser {:read read :mutate mutate}))

(def reconciler (om/reconciler {:state initial-state
                                :send send
                                :parser parser}))

(defn mount-root []
  (om/add-root! reconciler pages/View (.getElementById js/document "app")))

(defn init! []
  (mount-root))
