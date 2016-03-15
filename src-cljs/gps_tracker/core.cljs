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
                    :remote s/Bool})

(s/defschema Action (s/either
                     (sh/action :page p/Action)
                     (sh/action :waypoint-paths wp/Action)))


(s/defn init :- State [page :- p/PageID]
  {:page (p/init page)
   :waypoint-paths []
   :remote false})

(s/defn intercept :- State [action :- Action state :- State]
  (cond
    (= (take 3 action) '(:page :waypoint-paths-new :create))
    (let [path (last action)]
      (if (wp/valid? path)
        (->> state
             (handle `(:remote :send :create-waypoint-path ~path))
             (handle `(:page :navigate {:id :waypoint-paths-index}))
             (handle `(:waypoint-paths :create ~path)))
        (do (println "Path cannot be empty.")
            state)))

    (= (take 3 action) '(:page :waypoint-paths-show :delete))
    (let [path-id (last action)]
      (->> state
           (handle `(:remote :send :delete-waypoint-path ~path-id))
           (handle `(:page :navigate {:id :waypoint-paths-index}))
           (handle `(:waypoint-paths :delete ~path-id))))

    (= action `(:page :waypoint-paths-index :refresh))
    (handle `(:remote :send :get-waypoint-paths) state)

    (= (take 3 action) `(:remote :receive :get-waypoint-paths))
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
       (intercept action)))

(defn view [address state]
  [:div
   [:div.container
    [:div.row
     (navbar/view address (state :remote))
     [:div
      (p/view (a/forward address (a/tag :page)) state)]]]])

(defn address [action]
  (swap! debug update :actions conj action)
  (swap! state (partial handle action))
  (swap! debug update :state conj @state)
  (render @state))

(defn debug-pr [val]
  (println val)
  val)

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
