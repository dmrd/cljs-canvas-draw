
; Using Refs: https://presumably.de/reagent-mysteries-part-3-manipulating-the-dom.html
(ns app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            ["react" :as react]
            [clojure.string :as str]
            [goog.string :as gstring]
            [goog.string.format]
            ))

; TODO: Switch to 1 db
(defonce db_ref (r/atom {:canvas nil
                         :context nil}))

(defonce canvas_ref (r/atom nil))
(defonce context_ref (r/atom nil))

; Maybe these should just be def-ed?
(defonce isDrawing (r/atom false))
;(def setIsDrawing [] (fn ) setIsDrawing (r/atom false))

;(js/React.useState 0)

(defn startDrawing [event]
  (let
      [
       event (.-nativeEvent event)
       offsetX (.-offsetX event)
       offsetY (.-offsetY event)

       ;canvas (.-canvas @context_ref)
       canvas @context_ref
       ;current (.-current context)
        ]
    ;(.beginPath (.-canvas canvas))
    (.beginPath canvas)
    (.moveTo canvas offsetX offsetY)
    (reset! isDrawing true)
    ;(set! (.-beginPath))

    )
  )

(defn finishDrawing [event]
  (reset! isDrawing false)
  (let [canvas @context_ref
        ]
    (.closePath canvas)
    )
  )

; QUESTION: How to get single value from map passed in?
; QUESTION: How to effectively interop with js objects passed in?
(defn draw [event]
  (let
      [
       event (.-nativeEvent event)
       offsetX (.-offsetX event)
       offsetY (.-offsetY event)

       canvas @context_ref
       ;current (.-current canvas)
       ;event (js->clj event)
       ;n1 (get event "nativeEvent")
       ;nativeEvent (.- event)
       ]
    (if @isDrawing
      (do
        (.lineTo canvas offsetX offsetY)
        (.stroke canvas)
        )

      )
  ))

(defn canvas-pad []
  (do
  (react/useEffect
   (fn [] (let [canvas @canvas_ref] ;(.-current @ref)
               (set! (.. canvas -width) (* 2 (.-innerWidth js/window)))
               (set! (.. canvas -height) (* 2 (.-innerHeight js/window)))
               (set! (.. canvas -style -width) (gstring/format "%spx" (.-innerWidth js/window)))
               (set! (.. canvas -style -height) (gstring/format "%spx" (.-innerHeight js/window)))
               (reset! context_ref (.getContext canvas "2d"))
               (.scale @context_ref 2 2)
               (set! (.-lineCap @context_ref) "round")
               (set! (.-strokeStyle @context_ref) "black")
               (set! (.-lineWidth @context_ref) 5)
           ))
   #js []
  )
  [:div
   [:h1 "Canvas drawing"]
   [:div "Draw with your mouse."]
   [:canvas
    {
     :onMouseDown startDrawing
     :onMouseUp finishDrawing
     :onMouseMove draw
     :ref (fn [el] (reset! canvas_ref el) )

     }

    ]])
  )

(defn main []
  [canvas-pad]
)



(defn run []
  ;(rdom/render [canvas-pad]
  (let [div  (js/document.getElementById "root")]
  (def functional-compiler (reagent.core/create-compiler {:function-components true}))

  ;; Using the option
  (reagent.dom/render [main] div functional-compiler)
  (reagent.core/as-element [main] functional-compiler)
  ;; Setting compiler as the default
  (reagent.core/set-default-compiler! functional-compiler)
  ))


;; Exported methods
; On init
(defn ^:export init [] (run))

; On reload
(defn ^:dev/after-load start [] (run))

;; optional
;(defn ^:dev/before-load stop []
;  (js/console.log "stop"))a
