(ns gps-tracker.core
  (:require [quiescent.core :as q]
            [sablono.core :as sab]
            [cljs.pprint :as pp]
            [schema.core :as s]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker.history :as h]
            [gps-tracker.remote :as r]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.address :as a]
            [gps-tracker.pages.core :as p]))

(declare handle)
(declare address)
(declare render)

(defonce state (atom nil))
(defonce debug (atom {:state '() :actions '()}))

;(-> @state keys)
;(-> @debug :state first)
;(->> @debug :actions (take 2))
;(swap! debug assoc :actions '())
;(-> @debug)

(s/defschema State {:page p/Page
                    :waypoint-paths [cs/WaypointPath]
                    :remote r/Remote})

(s/defschema Action (s/either
                     (sh/list :page p/Action)
                     (sh/list :waypoint-paths wp/Action)
                     (sh/list :remote r/Action)))

(s/defn init :- State [page :- p/PageID]
  {:page (p/init page)
   :waypoint-paths []
   :remote (r/init)})

(s/defn eavesdrop :- State [action :- Action state :- State]
  (cond
    (= (take 3 action) '(:page :waypoint-paths-new :create))
    (let [path (last action)]
      (if (wp/valid? path)
        (let [remote-id (get-in state [:remote :next-id])]
          (->> state
               (handle `(:remote :send ~remote-id :create-waypoint-path ~path))
               (handle `(:page :navigate {:id :waypoint-paths-index}))
               (handle `(:waypoint-paths :create ~path))))
        (do (println "Path cannot be empty.")
            state)))

    (= (take 3 action) '(:page :waypoint-paths-show :delete))
    (let [path-id (last action)
          remote-id (get-in state [:remote :next-id])]
        (->> state
             (handle `(:remote :send ~remote-id :delete-waypoint-path ~path-id))
             (handle `(:page :navigate {:id :waypoint-paths-index}))
             (handle `(:waypoint-paths :delete ~path-id))))

    (= action `(:page :waypoint-paths-index :refresh))
    (let [remote-id (get-in state [:remote :next-id])]
      (handle `(:remote :send ~remote-id :get-waypoint-paths) state))

    (and (= (take 2 action) `(:remote :receive))
         (= (nth action 3) :success)
         (= (nth action 4) :get-waypoint-paths))
    (let [paths (last action)]
      (handle `(:waypoint-paths :refresh ~paths) state))

    :else
    state))

(s/defn delegate :- State [action :- Action state :- State]
  (case (first action)
    :page
    (update state :page (partial p/handle (rest action)))

    :waypoint-paths
    (update state :waypoint-paths (partial wp/handle (rest action)))

    :remote
    (update state :remote
            (partial r/handle (a/forward address (a/tag :remote)) (rest action)))

    state))

(s/defn handle :- State [action :- Action state :- State]
  (->> state
       (delegate action)
       (eavesdrop action)))

(defn view [address state]
  [:div
   [:div.container
    [:div.row
     (navbar/view address (seq (get-in state [:remote :pending])))
     [:div
      (p/view (a/forward address (a/tag :page)) state)]]]])

(defn address [action]
  (pp/pprint action)
  (swap! debug update :actions conj action)
  (swap! state (partial handle action))
  (swap! debug update :state conj @state)
  (render @state))

(defn render [state]
  (q/render (sab/html (view address state))
            (.getElementById js/document "app")))

(defn init! []
  (let [initial-page (h/get-page)]
    (h/hook-browser
     (fn [page] (address `(:page :navigate ~page)))
     initial-page)
    (reset! state (init initial-page))
    (swap! debug update :state conj @state)
    (render @state)))
