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
           {:latitude (double n) :longitude (double (- n)) :time (add-mins time (+ n i))}))})

;; generates n tracking paths in order of most recent first
(defn generate-tracking-paths [n]
  (let [base-date (Date.)]
    (into []
          (for [i (reverse (range 1 (+ n 1)))]
            (generate-tracking-path i base-date)))))

;;;; tests

(t/deftest add-get-tracking-paths
  (let [paths (generate-tracking-paths 4)]
    (doseq [path paths]
      (db/api-action [:add-tracking-path path]))
    (t/is (= paths (db/api-action [:get-tracking-paths])))))

(t/deftest delete-tracking-paths
  (let [paths (generate-tracking-paths 4)
        removed-id (-> paths (nth 2) :id)]
    (doseq [path paths]
      (db/api-action [:add-tracking-path path]))
    (db/api-action [:delete-tracking-path removed-id])
    (t/is (= (filterv #(not= (% :id) removed-id) paths)
             (db/api-action [:get-tracking-paths])))))
