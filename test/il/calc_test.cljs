(ns il.calc-test
  (:require [il.calc :as calc]
            [cljs.test :refer [deftest is]]))

(deftest hodl-value-test
  (is (= 3.75 (calc/hodl-value 400 150 50)))
  (is (= 4.715 (calc/hodl-value 200 543 50)))
  (is (= 1.5 (calc/hodl-value 100 0 50)))
  (is (= 1.15 (calc/hodl-value 100 0 15))))

(deftest pool-value-test
  (is (= 3.535533905932738 (calc/pool-value 400 150 50))))

(deftest il-test
  (is (= 5.719095841793653 (calc/il 400 150 50))))
