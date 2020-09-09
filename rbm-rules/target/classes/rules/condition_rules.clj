(ns rules.condition-rules
  (:require [clara.rules :as r])
  (:import [com.rbm
            Subject
            Condition]))

;(r/defrule infer-condition
;  [Subject
;   (> bp 10)
;   (= ?subName subName)]
;  =>
;  (r/insert! (Condition. "Hypertention"  ?subName)))

(r/defquery get-conditions
  []
  [?con <- Condition])