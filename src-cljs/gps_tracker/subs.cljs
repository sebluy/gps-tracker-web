(ns gps-tracker.subs
  (:require [sigsub.core :as sigsub :include-macros :true]
            [gps-tracker.remote :as remote]))

(defn path-id []
  (sigsub/with-signals
    [id [:page :route-params :id]]
    (fn []
      (js/parseInt @id))))

(defn path-ids []
  (sigsub/with-signals
    [remote-ids [:remote :path-ids]]
    (fn []
      (if @remote-ids
        @remote-ids
        (do (remote/get-path-ids)
            :pending)))))

(defn dispose-path-ids []
  (fn [] (remote/remove-path-ids)))

(defn waypoint-path-ids []
  (sigsub/with-signals
    [remote-ids [:remote :waypoint-path-ids]]
    (fn []
      (if @remote-ids
        @remote-ids
        (do (remote/get-waypoint-path-ids)
            :pending)))))

(defn dispose-waypoint-path-ids []
  (fn [] (remote/remove-waypoint-path-ids)))

(defn path [[id]]
  (sigsub/with-signals
    [path [:remote :path id]]
    (fn []
      (if @path
        @path
        (do (remote/get-path id)
            :pending)))))

(defn dispose-path [[id]]
  (fn [] (remote/remove-path id)))

(sigsub/register-signal-skeleton [:path-ids] path-ids dispose-path-ids)
(sigsub/register-signal-skeleton
  [:waypoint-path-ids]
  waypoint-path-ids
  dispose-waypoint-path-ids)
(sigsub/register-signal-skeleton [:path] path dispose-path)
(sigsub/register-signal-skeleton [:page :path-id] path-id)
