(ns tst.tupelo-datomic.basic
  (:use tupelo.core
        tupelo.test)
  (:require
    [datomic.api :as d]
    [tupelo.profile :as prof]
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

; uses "list form" of query syntax
(dotest
  (d/create-database datomic-uri) ; create the DB
  (try
    (let [conn (d/connect datomic-uri)] ; create & save a connection to the db
      (is= (type conn) datomic.peer.LocalConnection)
      (let ; let-spy-pretty
        [s1 (d/transact conn [{:db/doc "Hello World"}])
         r1 (d/transact conn movie-schema)
         r2 (d/transact conn first-movies)
         r3 (d/q '[:find ?movie-title
                   :where [_ :movie/title ?movie-title]]
              (d/db conn))]
        (is-set= (keys @s1) [:db-before :db-after :tx-data :tempids])
        (is-set= r3 [["Commando"]
                     ["The Goonies"]
                     ["Repo Man"]]))
      (let [r-1985 (d/q '[:find ?movie-title
                          :where
                          [?e :movie/title ?movie-title]
                          [?e :movie/release-year 1985]]
                     (d/db conn))]
        (is-set= r-1985 [["Commando"]
                         ["The Goonies"]]))
      (let [r-1985 (d/q '[:find ?year ?movie-title ?genre
                          :where
                          [?e :movie/title ?movie-title]
                          [?e :movie/release-year ?year]
                          [?e :movie/genre ?genre]
                          [?e :movie/release-year 1985]
                          ]
                     (d/db conn))]
        (is-set= r-1985
          [[1985 "The Goonies" "action/adventure"]
           [1985 "Commando" "action/adventure"]])))
    (finally
      (d/delete-database datomic-uri))))

; same as last query above but uses "map form" of query syntax
(dotest
  (d/create-database datomic-uri) ; create the DB
  (try
    (let [conn (d/connect datomic-uri)] ; create & save a connection to the db
      (let ; let-spy-pretty
        [s1     (d/transact conn [{:db/doc "Hello World"}])
         r1     (d/transact conn movie-schema)
         r2     (d/transact conn first-movies)
         r-1985 (d/q '{:find  [?year ?movie-title ?genre]
                       :in    [$] ; <= optional, this is implied for db-only queries
                       :where [[?e :movie/title ?movie-title]
                               [?e :movie/release-year ?year]
                               [?e :movie/genre ?genre]
                               [?e :movie/release-year 1985]]}
                  (d/db conn))]
        (is-set= r-1985
          [[1985 "The Goonies" "action/adventure"]
           [1985 "Commando" "action/adventure"]])))
    (finally
      (d/delete-database datomic-uri))))

