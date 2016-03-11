(ns ^:figwheel-no-load gps-tracker.env
  (:require [gps-tracker.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/start
 {:load-warninged-code true
  :on-jsload #(core/render @core/state)
  :websocket-url "ws://localhost:3449/figwheel-ws"})

(core/init!)
