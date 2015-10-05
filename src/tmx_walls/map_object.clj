(ns tmx-walls.map-object
  (:require [play-clj.core :refer [tiled-map-layer map-layer
                                   map-objects map-objects!]]
            [tmx-walls.utils :as u])
  (:import  [com.badlogic.gdx.maps.objects CircleMapObject
             EllipseMapObject PolygonMapObject PolylineMapObject
             RectangleMapObject TextureMapObject]))

(defrecord MapWall [color data name opacity properties shape visible?])

(defmulti shape-data class)

(defmethod shape-data CircleMapObject
  [object]
  (let [c (.getCircle object)]
    [:circle
     {:radius (. c radius)
      :x (. c x)
      :y (. c y)}]))

(defmethod shape-data EllipseMapObject
  [object]
  (let [e (.getEllipse object)]
    [:ellipse
     {:x (. e x)
      :y (. e y)
      :width  (. e width)
      :height (. e height)}]))

(defmethod shape-data PolygonMapObject
  [object]
  (let [p (.getPolygon object)]
    [:polygon
     {:x (.getX p)
      :y (.getY p)
      :vertices (.getTransformedVertices p)}]))

(defmethod shape-data PolylineMapObject
  [object]
  (let [p (.getPolyline object)]
    [:polyline
     {:x (.getOriginX p)
      :y (.getOriginY p)
      :vertices (.getTransformedVertices p)}]))

(defmethod shape-data RectangleMapObject
  [object]
  (let [r (.getRectangle object)]
    [:rectangle
     {:x (. r x)
      :y (. r y)
      :width  (. r width)
      :height (. r height)}]))

(defmethod shape-data TextureMapObject
  [object]
  [:texture-region
   (.getTextureRegion object)])

(defn- map->world
  "Converts all position related data from
  an object from map to world coordinates."
  [ppm data]
  (let [convert? #{:x :y :radius :width :height}
        step (fn [m k v]
               (if (convert? k)
                 (assoc m k (/ v ppm))
                 (assoc m k (u/vertices->vertices ppm v))))]
    (reduce-kv step {} data)))

(defn- parse-property
  "Parses val denpending on its key or its value."
  [key val]
  (let [fixture-property? #{:density :friction :restitution}]
    (cond (fixture-property? key) (Float/parseFloat val)
          (#{"true" "false"} val) (Boolean/parseBoolean val)
          :else val)))

(defn properties->map
  "Takes a MapProperties and returns a clojure
  map with <keyword,property>. It also convert
  numerical values."
  [properties]
  (let [keys     (iterator-seq (.getKeys properties))
        keywords (map keyword keys)
        step     (fn [k kw]
                   (let [p (.get properties k)
                         v (parse-property kw p)]
                     [kw v]))]
    (->> (map step keys keywords)
         (into {} ,,,))))

(defn object->map-wall
  "Takes a (libGDX) MapObject and returns a MapWall.
  <clear?> defines whether the properties in the MapObject
  object must be cleared after the block is created.
  <clear?> is true by default."
  ([object ppm]
   (object->map-wall object ppm true))
  ([object ppm clear?]
   (let [properties     (.getProperties object)
         properties-map (properties->map properties)
         [shape data]   (shape-data object)]
     (when clear?
       (.clear properties))
     (map->MapWall {:color      (.getColor object)
                    :name       (.getName object)
                    :opacity    (.getOpacity object)
                    :properties properties-map
                    :shape      shape
                    :data       (map->world ppm data)
                    :visible?   (.isVisible object)}))))
