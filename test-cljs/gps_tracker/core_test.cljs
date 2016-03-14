(ns gps-tracker.core-test
  (:require [cljs.test :as t]
            [gps-tracker.core :as c]
            [schema.test :as st]))

(t/deftest check-schemas
  (let [s0 (c/init {:id :waypoint-paths-index})

        s1 (->> s0
                (c/handle `(:page :waypoint-paths-index :new))
                (c/handle `(:page :waypoint-paths-new :add-point
                                 {:latitude 1.0 :longitude 2.0}))
                (c/handle `(:page :waypoint-paths-new :add-point
                                 {:latitude 3.0 :longitude 4.0}))
                (c/handle `(:page :waypoint-paths-new :add-point
                                 {:latitude 5.0 :longitude 6.0})))
        path (get-in s1 [:page :path])
        _ (t/is (= (path :points) [{:latitude 1.0 :longitude 2.0}
                                   {:latitude 3.0 :longitude 4.0}
                                   {:latitude 5.0 :longitude 6.0}]))

        s2 (c/handle `(:page :waypoint-paths-new :create ~path) s1)
        _ (t/is (= (get-in s2 [:page :id] :waypoint-paths-index)))
        _ (t/is (= (get-in s2 [:waypoint-paths 0] path)))

        path-id (get-in s2 [:waypoint-paths 0 :id])
        _ (println s2)
        s3 (->> s2
                (c/handle `(:page :waypoint-paths-index :show ~path-id))
                (c/handle `(:page :waypoint-paths-show :delete ~path-id)))
        _ (t/is (= (count (s3 :waypoint-paths)) 0))
        _ (t/is (= (get-in s3 [:page :id] :waypoint-paths-index)))]))

(t/use-fixtures :once st/validate-schemas)

(t/run-tests)
