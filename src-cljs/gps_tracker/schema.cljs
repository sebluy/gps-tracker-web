(ns gps-tracker.schema
  (:require [gps-tracker-common.schema :as ct]
            [schema.core :as s]))

(s/validate ct/GetPaths {:action :get-paths
                         :path-type :tracking})
