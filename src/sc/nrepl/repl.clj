(ns sc.nrepl.repl
  (:require [sc.api]
            [sc.nrepl.impl :as i]))

(defn in-ep
  "'In Execution Point' - Sets the current Execution Point Id to `ep-id`.
  Subsequent commands sent to the REPL via 'eval' or 'load-file'
  will be transformed so that each form `expr` is wrapped with (sc.api/letsc epid expr)."
  [ep-id]
  (when (some? ep-id)
    ;; NOTE will throw the EP does not exist, acting as a check against mistyped EP ids. (Val, 25 Jan 2019)
    (sc.api/ep-info ep-id))
  (reset! i/current-ep-id ep-id))

(defn exit
  "Exists the context of the current Execution Point, if any. Idempotent."
  []
  (in-ep nil))

