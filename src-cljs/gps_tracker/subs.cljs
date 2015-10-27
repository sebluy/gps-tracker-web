(ns gps-tracker.subs
  (:require [sigsub.core :as sigsub :include-macros :true]
            [gps-tracker.remote :as remote]))

(defn waypoint-paths []
  (sigsub/with-signals
    [paths [:remote :waypoint-paths]]
    (fn []
      (if @paths
        @paths
        (do (remote/get-waypoint-paths)
            :pending)))))

(defn dispose-waypoint-paths []
  (fn [] (remote/remove-waypoint-paths)))

(defn waypoint-path [[id]]
  (sigsub/with-signals
    [paths [:waypoint-paths]]
    (fn []
      (if (= @paths :pending)
        :pending
        (->> @paths
             (filter (fn [path] (= (path :id) id)))
             first)))))

(sigsub/register-signal-skeleton [:waypoint-paths]
                                 waypoint-paths dispose-waypoint-paths)

(sigsub/register-signal-skeleton [:waypoint-path] waypoint-path)
