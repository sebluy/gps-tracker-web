Run:
lein figwheel
cider-connect 7888
load env/dev/clj/gps_tracker/env.clj

Todo:

Add schema (actions, data)

Add tests

- Reintegrate server coms
- Add tracking

Redirect to not found page server side

move from src and src-cljs to src/clj and src/cljs

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
