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
            [gps-tracker.tracking-paths :as tp]
            [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.address :as a]
            [gps-tracker.pages.core :as p]))

(declare handle)
(declare address)
(declare render)

(defonce state (atom nil))
;(defonce debug (atom {:state '() :actions '()}))

;(-> @state :tracking-paths)
;(-> @debug :state #_(nth 0) :checkpoints)
;(->> @debug :actions (take 2))
;(swap! debug assoc :actions '())
;(-> @debug)

(s/defschema State {:page p/Page
                    :waypoint-paths [cs/WaypointPath]
                    :tracking-paths [cs/TrackingPath]
                    :checkpoints {s/Int [cs/WaypointPath]}
                    :remote r/Remote})

(s/defschema Action (s/either
                     (sh/list :page p/Action)
                     (sh/list :waypoint-paths wp/Action)
                     (sh/list :tracking-paths tp/Action)
                     (sh/list :remote r/Action)))

(s/defn init :- State [page :- p/PageID]
  {:page (p/init page)
   :waypoint-paths []
   :tracking-paths []
   :checkpoints {}
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

    (= action `(:page :tracking-paths-index :refresh))
    (let [remote-id (get-in state [:remote :next-id])]
      (handle `(:remote :send ~remote-id :get-tracking-paths) state))

    (and (= (take 2 action) `(:remote :send))
         (#{:delete-waypoint-path :create-waypoint-path} (nth action 3)))
    (let [remote-id (nth action 2)]
      (assoc-in state [:checkpoints remote-id] (state :waypoint-paths)))

    (and (= (take 2 action) `(:remote :receive))
         (= (nth action 3) :success)
         (= (nth action 4) :get-waypoint-paths))
    (let [paths (last action)]
      (handle `(:waypoint-paths :refresh ~paths) state))

    (and (= (take 2 action) `(:remote :receive))
         (= (nth action 3) :success)
         (= (nth action 4) :get-tracking-paths))
    (let [paths (last action)]
      (handle `(:tracking-paths :refresh ~paths) state))

    (and (= (take 2 action) `(:remote :receive))
         (= (nth action 3) :error)
         (#{:delete-waypoint-path :create-waypoint-path} (nth action 4)))
    (let [remote-id (nth action 2)
          checkpoint (get-in state [:checkpoints remote-id])]
      (println "Restoring waypoint paths")
      (-> state
          (->> (handle `(:waypoint-paths :refresh ~checkpoint)))
          (update :checkpoints dissoc remote-id)))

    (and (= (take 2 action) `(:remote :receive))
         (= (nth action 3) :success)
         (#{:delete-waypoint-path :create-waypoint-path} (nth action 4)))
    (let [remote-id (nth action 2)]
      (update state :checkpoints dissoc remote-id))

    :else
    state))

(s/defn delegate :- State [action :- Action state :- State]
  (case (first action)
    :page
    (update state :page (partial p/handle (rest action)))

    :waypoint-paths
    (update state :waypoint-paths (partial wp/handle (rest action)))

    :tracking-paths
    (update state :tracking-paths (partial tp/handle (rest action)))

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
  #_(pp/pprint action)
  #_(swap! debug update :actions conj action)
  (swap! state (partial handle action))
  #_(swap! debug update :state conj @state)
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
