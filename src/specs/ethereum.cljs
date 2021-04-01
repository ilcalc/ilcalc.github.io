(ns specs.ethereum
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(s/def ::address (s/and string?
                        #(str/starts-with? "0x" %)
                        #(= 42 (count %))))

(s/def ::timestamp pos-int?)
