Run
-------------------------
lein figwheel
cider-connect 7888
load env/dev/clj/gps_tracker/env.clj

Todo
--------------------------

Refactor waypoint path/tracking path

Implement delete for tracking paths (with checkpoints)

Redirect to not found page server side

move from src and src-cljs to src/clj and src/cljs

Add error modal/popup

Use custom styling (learn from bootstrap)

Fix figwheel repl, do whatever github readme says

Cleanup project.clj

Start using public/private in namespaces with better discipline.

Use lein heroku for uberjar deployment.

Add authentication/authorization.

Bring libraries up to date.

Create migrations ns.

Allow waypoints to be moved on waypoint creation map.

Fix % in bidi and convert dates in urls to iso time instead of long

Maybe
--------------------

Move all remote related stuff to remote (checkpoints, waypoint paths, tracking paths)

Cleanup eavesdrop

Do delegations automagically

Only use list notation for namespace routing, maps else

Figure out some way to manage app state and pure, sync effects, async effects (helps with testablility)

Add tests for actions with side effects

Switch to rest API
