(ns tst.tupelo-datomic.basic
  (:use tupelo.core tupelo.test)
  (:require
    [datomic.api        :as d]
  ))

(def datomic-uri "datomic:mem://hello") ; the URI for our test db

(def movie-schema [{:db/ident       :movie/title
                    :db/valueType   :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc         "The title of the movie"}

                   {:db/ident       :movie/genre
                    :db/valueType   :db.type/string
                    :db/cardinality :db.cardinality/one
                    :db/doc         "The genre of the movie"}

                   {:db/ident       :movie/release-year
                    :db/valueType   :db.type/long
                    :db/cardinality :db.cardinality/one
                    :db/doc         "The year the movie was released in theaters"}])

(def first-movies [{:movie/title        "The Goonies"
                    :movie/genre        "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title        "Commando"
                    :movie/genre        "action/adventure"
                    :movie/release-year 1985}
                   {:movie/title        "Repo Man"
                    :movie/genre        "punk dystopia"
                    :movie/release-year 1984}])

(def all-titles-q '[:find ?movie-title
                    :where [_ :movie/title ?movie-title]])

(defn exercise-db [conn]
  (nl) (println "exercise-db - enter")
  (nl) (spyx conn)
  (let-spy-pretty
    [s1 (d/transact conn [{:db/doc "Hello World"}])
     r1 (d/transact conn movie-schema)
     r2 (d/transact conn first-movies)
     r3 (d/q all-titles-q (d/db conn))]
    (println "exercise-db - exit")))

(dotest
  (d/create-database datomic-uri) ; create the DB
  (try
    (let [conn (d/connect datomic-uri)] ; create & save a connection to the db
      (exercise-db conn))
    (finally
      (d/delete-database datomic-uri))))

