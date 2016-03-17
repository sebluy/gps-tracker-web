(ns gps-tracker.tracking-paths
  (:require [schema.core :as s]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker-common.schema :as cs]))

(s/defschema Action
  (s/either
   (sh/list :refresh (sh/singleton [cs/TrackingPath]))
   (sh/list :delete (sh/singleton cs/PathID))))

(s/defn handle :- [cs/TrackingPath] [action :- Action paths :- [cs/TrackingPath]]
  (case (first action)
    :delete
    (let [id (last action)]
      (filterv (fn [path] (not= id (path :id))) paths))

    :refresh
    (let [paths (last action)]
      paths)

    paths))
