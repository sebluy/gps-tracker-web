// Compiled by ClojureScript 0.0-2411
goog.provide('contour.mapview');
goog.require('cljs.core');
contour.mapview.map_opts = new cljs.core.PersistentArrayMap(null, 2, ["zoom",(8),"center",(new google.maps.LatLng(-34.397,150.644))], null);
contour.mapview.map_load = (function map_load(){

new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, [contour.mapview.elem,goog.dom.getElement("map-canvas")], null);



return (new google.maps.Map(contour.mapview.elem,contour.mapview.map_opts));
});
events.listen.call(null,window,"load",contour.mapview.map_load);
