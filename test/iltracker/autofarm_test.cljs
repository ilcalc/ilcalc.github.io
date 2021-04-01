(ns iltracker.autofarm-test
  (:require [iltracker.autofarm :as a]
            [cljs.test :refer [deftest is]]))

(deftest deposit-transaction?-test
  (is (a/deposit-transaction? {:input "0xe2bbb158deadbeef"}))
  (is (a/deposit-transaction? {:input "0xe2bbb158"}))
  (is (a/deposit-transaction? {:input "0xe2BBB158"}))
  (is (not (a/deposit-transaction? {:input ""})))
  (is (not (a/deposit-transaction? {:input "0x"})))
  (is (not (a/deposit-transaction? {:input "whataver"}))))

(deftest tx->pool-id-test
  (is (= 7 (a/tx->pool-id {:input (str "0xe2bbb158"
                                       "0000000000000000000000000000000000000000000000000000000000000007"
                                       "000000000000000000000000000000000000000000000000f47dc089d99bf5b5")}))))
