(ns gps-tracker.routing
  (:require [bidi.bidi :as bidi])
  (:import [goog Uri]))

(def routes ["" {(bidi/alts "waypoint-paths/index" "") :waypoint-paths-index
                 "waypoint-paths/" {"new" :waypoint-paths-new
                                    "show/" {[:path-id] :waypoint-paths-show}}
                 "not-found" :not-found}])

;; todo: integrate schema here

;; routable is a url safe version of a page

(defmulti page->routeable
  "Converts a page into a representation that can be put in a url
  e.g. converts date parameters to an equivalent string format
  should be reversable by the corresponding routeable->page."
  :id)

(defmethod page->routeable :waypoint-paths-show [page]
  (update page :path-id #(.getTime %)))

(defmethod page->routeable :default [page] page)

(defmulti routeable->page
  "Reverses page->routeable"
  :id)

(defmethod routeable->page :waypoint-paths-show [routeable]
  (let [path-id (-> (routeable :path-id) (long) (js/Date.))]
    (if (js/isNaN path-id)
      {:id :not-found}
      (assoc routeable :path-id path-id))))

(defmethod routeable->page :default [routeable] routeable)

(defn- route->routeable [route]
  (let [{:keys [handler route-params]} (bidi/match-route routes route)]
    (assoc route-params :id (or handler :not-found))))

(defn- routeable->route [routeable]
  (let [id (routeable :id)
        params (dissoc routeable :id)]
    (apply bidi/path-for routes id (-> params seq flatten))))

(defn page->route [page] (-> page page->routeable routeable->route))
(defn route->page [route] (-> route route->routeable routeable->page))

(defn- route->href [route]
  (str "/#" (Uri. route)))

(defn page->href [page]
  (-> page
      page->route
      route->href))

(defn attrs [page on-click]
  {:href (page->href page)
   :onClick (fn [e]
              (on-click page)
              (.preventDefault e))})
