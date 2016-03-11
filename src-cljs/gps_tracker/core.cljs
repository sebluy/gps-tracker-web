(ns gps-tracker.core
  (:require [quiescent.core :as q]
            [sablono.core :as s]
            [cljs.pprint :as pp]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.pages.core :as p]))

(defonce state (atom nil))

(declare handle)

(defn intercept-page-actions [action state]
  (cond
    (= (take 2 action) '(:new-waypoint-path :create))
    (let [path (nth action 2)]
      (cond-> state
        (wp/valid? path)
        (->>
         (handle '(:page :navigate {:id :waypoint-paths}))
         (handle `(:waypoint-paths :create ~path)))))

    (= (take 2 action) '(:waypoint-path :delete))
    (let [path-id (nth action 2)]
      (->> state
           (handle '(:page :navigate {:id :waypoint-paths}))
           (handle `(:waypoint-paths :delete ~path-id))))

    :else
    state))

(defn handle [action state]
  (case (first action)
    :init
    {:page {:id :waypoint-paths}
     :waypoint-paths []}

    :page
    (-> state
        (update :page (partial p/handle (rest action)))
        (->> (intercept-page-actions (rest action))))

    :waypoint-paths
    (update state :waypoint-paths (partial wp/handle (rest action)))

    state))

(declare render)

(defn address [action]
  (pp/pprint action)
  (swap! state (partial handle action))
  (pp/pprint @state)
  (render @state))

(defn render [state]
  (q/render (s/html (p/view address state))
            (.getElementById js/document "app")))

(defn init! []
  (address '(:init)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; (defn initial-state []                                                              ;;
;;   {:page (history/get-page)                                                         ;;
;;    :remotes 0})                                                                     ;;
;;                                                                                     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
