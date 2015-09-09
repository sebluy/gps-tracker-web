(ns gps-tracker.routing
  (:require [bidi.bidi :as bidi]))

(def routes ["" {"paths"          :paths
                 "path/"          {[:id] :path}
                 "waypoint-paths" :waypoint-paths
                 "waypoint-path/" {"new" :new-waypoint-path
                                   ["show/" :id] :show-waypoint-path}}])

(defn- route->page [route]
  (bidi/match-route routes route))

(defn- page->route [page]
  (bidi/unmatch-pair routes {:handler (page :handler)
                             :params  (page :route-params)}))

(defn- route->href [route]
  (str "/#" route))

(defn page->href [page]
  (-> page
      page->route
      route->href))



