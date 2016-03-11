Run:
lein figwheel
cider-connect 7888
load env/dev/clj/gps_tracker/env.clj

Todo:

- Clean up UI
- Reintegrate routing/history
- Rethink page structure (rails?)
- Reintegrate server coms
- Add tracking

Should address be global or local (and get updated i.e. map)

Add error popup

Use custom styling

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
