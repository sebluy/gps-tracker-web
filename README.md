Run:
lein figwheel
cider-connect 7888
load env/dev/clj/gps_tracker/env.clj

Todo:

Smart parens strict mode

Add tests

Add schema (actions, data)

- Reintegrate server coms
- Add tracking

Redirect to not found page server side

Add error modal/popup

Use custom styling (learn from bootstrap)

Handle errors
- on remote errors revert to last known state
- on read, add fail callback

Fix figwheel repl, do whatever github readme says

Cleanup project.clj

Start using public/private in namespaces with better discipline.

Use lein heroku for uberjar deployment.

Add authentication/authorization.

Bring libraries up to date.

Allow waypoints to be moved on waypoint creation map.

Fix % in bidi and convert dates in urls to iso time instead of long
