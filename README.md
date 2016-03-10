Run:
lein figwheel
cider-connect 7888
load env/dev/clj/gps_tracker/env.clj

Todo:

Switch to quiescent
- Rethink page structure (rails?)

Handle errors
- on remote errors revert to last known state
- on read, add fail callback

Fix figwheel repl ???

Cleanup project.clj

Start using public/private in namespaces with better discipline.

Extract "pattern" for ref and onComponentMount imperative stuff

Use lein heroku for uberjar deployment.

Don't allow a empty path to be uploaded.

Add authentication/authorization.

Bring libraries up to date.

Add tests.

Allow waypoints to be moved on waypoint creation map.

Fix % in bidi and convert dates in urls to iso time instead of long
