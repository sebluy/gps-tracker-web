(ns ^:figwheel-no-load gps-tracker.env
  (:require [gps-tracker.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/start
 {:load-warninged-code true
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback core/mount-components})

(core/init!)
