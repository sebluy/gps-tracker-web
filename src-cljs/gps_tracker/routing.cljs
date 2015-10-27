(ns gps-tracker.routing
  (:require [bidi.bidi :as bidi]
            [clojure.set :as s]))

(def routes ["" {"tracking-paths" :tracking-paths
                 "tracking-path/" {[:path-id] :tracking-path}
                 "waypoint-paths" :waypoint-paths
                 "waypoint-path/" {"new" :new-waypoint-path
                                   [:path-id] :waypoint-path}}])

;; page {:id Keyword
;;       :params Any
;;       Other Any}

;; routable is a url safe version of a page

(defmulti page->routeable :id)

(defmethod page->routeable :waypoint-path [page]
  (update-in page [:params :path-id] #(.getTime %)))

(defmethod page->routeable :default [page] page)

(defmulti routeable->page :id)

(defmethod routeable->page :waypoint-path [routeable]
  (update-in routeable [:params :path-id] #(js/Date. (long %))))

(defmethod routeable->page :default [routeable] routeable)

(defn- route->routeable [route]
  (let [{:keys [handler route-params]} (bidi/match-route routes route)]
    {:id handler :params route-params}))

(defn- routeable->route [{:keys [id params]}]
  (apply bidi/path-for routes id (-> params seq flatten)))

(defn page->route [page] (-> page page->routeable routeable->route))
(defn route->page [route] (-> route route->routeable routeable->page))

(defn- route->href [route]
  (str "/#" route))

(defn page->href [page]
  (-> page
      page->route
      route->href))
