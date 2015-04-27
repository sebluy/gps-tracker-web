function initializeMap() {
  var coordinates = parseCoordinates() ;
  var mapOptions = {
    center: coordinates[0],
    zoom: 16
  } ;
  var oldPoint = mapOptions.center ;
  var map = new google.maps.Map(document.getElementById('map-canvas'),
    mapOptions);
  var lastPoint = coordinates[0] ;
  for (var i = 1 ; i < coordinates.length ; i++) {
    var newPoint = coordinates[i] ;
    addPointMap(newPoint, lastPoint, map) ;
    lastPoint = newPoint ;
  }
}

function parseCoordinates() {
  return JSON.parse($('#coordinates').html()) ;
}

function addPointMap(newPoint, lastPoint, map) {
  var points = [lastPoint, newPoint] ;
  var path = new google.maps.Polyline({
      path: points,
      geodesic: true,
      strokeColor: '#FF0000',
      strokeOpacity: 1.0,
      strokeWeight: 2
  }) ;
  path.setMap(map) ;
}

