(ns gps-tracker.core-test
  (:require [clojure.test :as t]
             [gps-tracker.db :as db]))

(def test-db-spec
  {:subprotocol "postgresql"
   :subname     "//localhost/gpstracker_test"
   :user        "dev"
   :password    "dev"})

(defmacro using-test-db [& body]
  `(with-redefs [db/db-spec test-db-spec]
     (jdbc/with-db-transaction [transaction url]
       (jdbc/db-set-rollback-only! transaction)
       ~@body)))

(s/defrecord TrackingPoint
    [latitude :- s/Num
     longitude :- s/Num
     time :- Date
     speed :- (s/Maybe s/Num)
     accuracy :- (s/Maybe s/Num)])

;;;; Paths

(s/defrecord TrackingPath
    [id :- Date
     points :- [TrackingPoint]])

(t/deftest "add/get tracking paths"
  (using-test-db
   (let [date (java.util.Date. 1000)
         path {:id date
               :points
               [{:latitude 1.0 :longitude 2.0 :time date :speed 1.4 :accuracy nil}]}]
     (api-action
      [[:add-tracking-path path]])
     (is (= (api-action [[:get-tracking-path date]]))))))
