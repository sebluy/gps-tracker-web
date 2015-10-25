(ns gps-tracker.core-test
  (:require [clojure.test :as t]
            [schema.test :as st]
            [gps-tracker.db :as db]
            [clojure.java.jdbc :as jdbc]))

;;;; helpers

(def test-db-spec
  {:subprotocol "postgresql"
   :subname     "//localhost/gpstracker_test"
   :user        "dev"
   :password    "dev"})

;; todo: make sure tables are setup in test_db
;; use migration library or write your own

(defn with-test-db [f]
  (jdbc/with-db-transaction
    [transaction test-db-spec]
    (jdbc/db-set-rollback-only! transaction)
    (binding [db/*txn* transaction]
      (f))))

(t/use-fixtures :once st/validate-schemas)
(t/use-fixtures :each with-test-db)

;;;; tests

(t/deftest add-get-tracking-paths
  (let [date (java.util.Date. 1000)
        path {:id date
              :points [{:latitude 1.0 :longitude 2.0 :time date :accuracy 2.0}]}]
    (db/api-action [:add-tracking-path path])
    (t/is (= (db/api-action [:get-tracking-path date])))))

(t/run-tests)
