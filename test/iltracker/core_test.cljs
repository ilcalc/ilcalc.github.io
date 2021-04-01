(ns iltracker.core-test
  (:require [iltracker.core :as ilt]
            [cljs.test :refer [deftest is]]))

(deftest add-coin-ids-test
  (is (= [{:tokens ["AaA" "BBB"] :coin-ids [3 5]}]
         (ilt/add-coin-ids [{:tokens ["AaA" "BBB"]}]
                           [{:symbol "xxx"
                             :id 1
                             :platforms {:binance-smart-chain true}}
                            {:symbol "aaa"
                             :id 3
                             :platforms {:binance-smart-chain true}}
                            {:symbol "yyy"
                             :id 2
                             :platforms {:binance-smart-chain true}}
                            {:symbol "bbb"
                             :id 5
                             :platforms {:binance-smart-chain true}}]))))
