(defproject gps-tracker "0.1.0-SNAPSHOT"

  :description "The web interface for a GPS tracker application"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.28"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [sebluy/sigsub "0.1.1"]
                 [bidi "1.20.3"]
                 [cljs-ajax "0.3.14"]
                 [environ "1.0.0"]
                 [hiccup "1.0.5"]
                 [reagent "0.5.0"]
                 [compojure "1.4.0"]
                 [cljsjs/google-maps "3.18-1"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-middleware-format "0.5.0"]
                 [ring/ring-devel "1.4.0"]
                 [yesql "0.4.2"]
                 [org.postgresql/postgresql "9.3-1103-jdbc41"]]

  :min-lein-version "2.0.0"
  :uberjar-name "gps-tracker.jar"
  :jvm-opts ["-server"]

  :main gps-tracker.core

  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.7"]
            [lein-figwheel "0.3.7"]
            [lein-cljsbuild "1.0.6"]]

  :clean-targets ^{:protect false} ["resources/public/js"]

  :cljsbuild
  {:builds {:app {:source-paths ["src-cljs"]
                  :compiler     {:output-to     "resources/public/js/app.js"
                                 :main          "gps-tracker.env"
                                 :output-dir    "resources/public/js/out"
                                 :asset-path    "js/out"
                                 :optimizations :none
                                 :pretty-print  true}}}}

  :profiles
  {:uberjar {:omit-source  true
             :source-paths ["env/prod/clj"]
             :env          {:production true}
             :hooks        [leiningen.cljsbuild]
             :cljsbuild    {:jar true
                            :builds
                                 {:app
                                  {:source-paths ["env/prod/cljs"]
                                   :compiler     {:optimizations :advanced
                                                  :elide-asserts :true
                                                  :pretty-print  false}}}}

             :aot          :all}
   :dev     {:source-paths ["env/dev/clj"]
             :cljsbuild    {:builds
                            {:app
                             {:source-paths ["env/dev/cljs"] :compiler {:source-map true}}}}

             :figwheel     {:http-server-root "public"
                            :server-port      3449
                            :nrepl-port       7888
                            :css-dirs         ["resources/public/css"]}

             :repl-options {:init-ns gps-tracker.env}
             :env          {:dev true}}})
