(ns gps-tracker.routing
  (:require [bidi.bidi :as bidi]
            [clojure.set :as s])
  (:import [goog Uri]))

(def routes ["" {"waypoint-paths" :waypoint-paths
                 "waypoint-path/" {"new" :new-waypoint-path
                                   [:path-id] :waypoint-path}}])

;; todo: integrate schema here

;; routable is a url safe version of a page

(defmulti page->routeable
  "Converts a page into a representation that can be put in a url
  e.g. converts date parameters to an equivalent string format
  should be reversable by the corresponding routeable->page."
  :id)

(defmethod page->routeable :waypoint-path [page]
  (update-in page [:params :path-id] #(.getTime %)))

(defmethod page->routeable :default [page] page)

(defmulti routeable->page
  "Reverses page->routeable"
  :id)

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
  (str "/#" (Uri. route)))

(defn page->href [page]
  (-> page
      page->route
      route->href))
