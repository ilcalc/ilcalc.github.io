(ns il.calc-test
  (:require [il.calc :as calc]
            [cljs.test :refer [deftest is]]))

(deftest hodl-value-test
  (is (= 3.75 (calc/hodl-value 400 150 50)))
  (is (= 4.715 (calc/hodl-value 200 543 50)))
  (is (= 1.5 (calc/hodl-value 100 0 50)))
  (is (= 1.15 (calc/hodl-value 100 0 15))))

(deftest pool-value-test
  (is (= 3.5355 (calc/pool-value 400 150 50)))
  (is (= 1.4142 (calc/pool-value 100 0 50)))
  (is (= 1 (calc/pool-value 0 0 50)))
  (is (= 1.88 (calc/pool-value 88 88 50))))

(deftest il-test
  (is (= 5.72 (calc/il 400 150 50)))
  (is (= 100.0 (calc/il -100 0 50)))
  (is (= 0 (calc/il 0 0 50)))
  (is (= 0 (calc/il 100 100 50))))
