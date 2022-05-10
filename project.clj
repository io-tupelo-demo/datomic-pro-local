(defproject io-tupelo-demo/datomic-basic "0.9.4"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [com.datomic/datomic-pro "1.0.6269"]
                 [org.clojure/clojure "1.10.0"]
                 [prismatic/schema "1.1.10"]
                 [tupelo "22.05.04"]
                 ]
  :resource-paths ["resources/"]

  :global-vars { *warn-on-reflection* false }

  :plugins  [ [lein-codox "0.10.3"] ]
  :update :daily ;  :always

  :target-path "target/%s"
  :clean-targets [ "target" ]

  :jvm-opts ["-Xms500m" "-Xmx2g"
            ]
)
