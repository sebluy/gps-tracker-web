(ns gps-tracker.core-test
  (:require [clojure.test :as t]
            [schema.test :as st]
            [gps-tracker.db :as db]
            [clojure.java.jdbc :as jdbc])
  (:import [java.util Date]))

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

(defn mins [n]
  (* n 60 1000))

(defn add-mins [date n]
  (-> date .getTime (+ (mins n)) Date.))

;; simple path generator
;; maybe replace with test.check or schema.generator
(defn generate-tracking-path [n time]
  {:id (add-mins time n)
   :points
   (into []
         (for [i (range n)]
           {:latitude n :longitude (- n) :time (add-mins time (+ n i))}))})

;;;; tests

(t/deftest add-get-tracking-paths
  (let [path (generate-tracking-path 4 (Date.))]
    (db/api-action [:add-tracking-path path])
    (t/is (= (db/api-action [:get-tracking-path (path :id)])))))

;; dependent on add tracking path working
;; path ids should be in order of most recent first
(t/deftest get-tracking-path-ids
  (let [date (Date.)
        paths (for [i (range 1 4)] (generate-tracking-path i date))]
    (doseq [path paths]
      (db/api-action [:add-tracking-path path]))
    (let [ids (db/api-action [:get-tracking-path-ids])]
      (t/is (= ids (reverse (map :id paths)))))))

;; dependent on add tracking path and get path ids working
(t/deftest remove-tracking-paths
  (let [date (Date.)
        paths (for [i (range 1 3)] (generate-tracking-path i date))]
    (doseq [path paths]
      (db/api-action [:add-tracking-path path]))
    (db/api-action [:delete-tracking-path (-> paths first :id)])
    (t/is (= (list (-> paths second :id)) (db/api-action [:get-tracking-path-ids])))))

(t/run-tests)
