(ns gps-tracker.core
  (:require [quiescent.core :as q]
            [sablono.core :as s]
            [cljs.pprint :as pp]
            [gps-tracker.history :as h]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.pages.core :as p]))

(defonce state (atom nil))
(defonce debug (atom {:state '() :actions '()}))

;(-> @debug :state first)
;(->> @debug :actions (take 3))
;(swap! debug assoc :actions '())
;(-> @debug)

(declare handle)

(defn intercept-page-actions [action state]
  (cond
    (= (take 2 action) '(:waypoint-paths-new :create))
    (let [path (nth action 2)]
      (if (wp/valid? path)
        (->> state
             (handle '(:page :navigate {:id :waypoint-paths-index}))
             (handle `(:waypoint-paths :create ~path)))
        (do (println "Path cannot be empty.")
            state)))

    (= (take 2 action) '(:waypoint-paths-show :delete))
    (let [path-id (nth action 2)]
      (->> state
           (handle '(:page :navigate {:id :waypoint-paths-index}))
           (handle `(:waypoint-paths :delete ~path-id))))

    :else
    state))

(defn handle [action state]
  (case (first action)
    :init
    (let [page (second action)]
      {:page (p/init page)
       :waypoint-paths []})

    :page
    (-> state
        (update :page (partial p/handle (rest action)))
        (->> (intercept-page-actions (rest action))))

    :waypoint-paths
    (update state :waypoint-paths (partial wp/handle (rest action)))

    state))

(declare render)

(defn address [action]
  (swap! debug update :actions conj action)
  (swap! state (partial handle action))
  (swap! debug update :state conj @state)
  (render @state))

(defn render [state]
  (q/render (s/html (p/view address state))
            (.getElementById js/document "app")))

(defn init! []
  (let [initial-page (h/get-page)]
    (h/hook-browser
     (fn [page] (address `(:page :navigate ~page)))
     initial-page)
    (address `(:init ~initial-page))))
