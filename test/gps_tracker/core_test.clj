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

(defn generate-common-point [n]
  {:latitude (double n)
   :longitude (double (- n))})

(defmulti generate-point (fn [type _ _ _] type))

(defmethod generate-point :tracking [_ n i time]
  (-> (generate-common-point n)
      (assoc :time (add-mins time (+ n i)))))

(defmethod generate-point :waypoint [_ n _ _]
  (generate-common-point n))

(defn generate-path [type n time]
  {:id (add-mins time n)
   :points (into [] (for [i (range n)] (generate-point type n i time)))})

;; generates n tracking paths in order of most recent first
(defn generate-paths [type n]
  (let [base-date (Date.)]
    (into []
          (for [i (reverse (range 1 (+ n 1)))]
            (generate-path type i base-date)))))

;;;; tests

(defn test-add-get-paths [type]
  "Generate 4 paths, add them to db, then read them out
   and assert equality"
  (let [paths (generate-paths type 4)]
    (doseq [path paths]
      (db/api-action {:action :add-path
                      :path-type type
                      :path path}))
    (t/is (= paths (db/api-action {:action :get-paths
                                   :path-type type})))))

(defn test-delete-paths [type]
  "Generate 4 paths, add them to db, delete one of them, then read
   them out and assert they are the same minus one"
  (let [paths (generate-paths type 4)
        removed-id (-> paths (nth 2) :id)]
    (doseq [path paths]
      (db/api-action {:action :add-path
                      :path-type type
                      :path path}))
    (db/api-action {:action :delete-path
                    :path-type type
                    :path-id removed-id})
    (t/is (= (filterv #(not= (% :id) removed-id) paths)
             (db/api-action {:action :get-paths
                             :path-type type})))))

(t/deftest add-get-tracking-paths
  (test-add-get-paths :tracking))

(t/deftest add-get-waypoint-paths
  (test-add-get-paths :waypoint))

(t/deftest delete-tracking-paths
  (test-delete-paths :tracking))

(t/deftest delete-waypoint-paths
  (test-delete-paths :waypoint))

(t/run-tests)
