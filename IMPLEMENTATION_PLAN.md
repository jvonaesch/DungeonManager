# DungeonManager Implementation Plan

This file turns `ROADMAP.md` into the first concrete build order for the project’s next milestone.

## Goal for this milestone
Build the first usable slice of the app:

- a session owner for the current party
- JSON save/load for the session
- a thin local backend boundary
- a simple creature UI with a sidebar, selection, creature creation, and feat addition

The Java domain model should remain the source of truth while the new layers wrap around it.

---

## Build order checklist

### 1) Create the session owner

Target area: `src/dungeonmanager/session/`

- [ ] Add a session object that stores the creature list
- [ ] Track the currently selected creature ID
- [ ] Add methods to create, select, rename, and delete creatures
- [ ] Add methods to update base stats and add/remove feats
- [ ] Expose read-only session snapshots for UI and persistence

Implementation notes:
- Use `dungeonmanager.creature.Creature` as the aggregate root for creature state.
- Keep stat recalculation inside `dungeonmanager.stats.ModifiableStatSet#reloadValues()`.
- Route feat application through `dungeonmanager.feature.FeatureSet#addFeature(String, Feature)` and `removeFeature(...)`.
- Avoid letting UI code modify internal maps directly.

### 2) Add JSON snapshot persistence

Target area: `src/dungeonmanager/session/` or a nearby persistence package

- [ ] Define a versioned JSON schema
- [ ] Persist creature IDs, names, types, base stat overrides, active feats, and custom stats
- [ ] Serialize simple values only; do not serialize live registry objects
- [ ] Rehydrate registry-backed references with `dungeonmanager.registry.Registries#get()` on load
- [ ] Save into `C:\Users\jonas\DungeonManagerLibrary\`

Implementation notes:
- Keep a schema version field from the start.
- Make load deterministic so round-tripping preserves the visible session state.
- Ensure custom stats created through `dungeonmanager.stats.StatModifier#setValue(String, int)` can be restored.

### 3) Expose a thin local API boundary

Target area: `src/dungeonmanager/api/` if added, or a similarly named package

- [ ] Define methods or endpoints for session summary, creature list, creature detail, creature update, feat add, save, and load
- [ ] Keep the API layer thin and delegate all real logic to the session object
- [ ] Keep UI code from touching the domain model directly
- [ ] Wire startup through the new session/API path instead of the current debug harness in `dungeonmanager.DungeonManagerApp#run()`

Implementation notes:
- Prefer DTO-style request and response objects.
- Keep this boundary simple enough that a future web UI can call it easily.

### 4) Build the first creature UI slice

Target area: `src/dungeonmanager/ui/` if added, or the eventual front-end project

- [ ] Render a left sidebar with the current creature list
- [ ] Highlight the selected creature
- [ ] Add a `+` button to create a default creature
- [ ] Prompt for the creature name and base stats when creating a creature
- [ ] Show the selected creature’s name, type, stats, and active feats
- [ ] Add an `Add feat` action in the main detail panel

Implementation notes:
- Use `dungeonmanager.creature.Creature#rename(String)` and `changeType(CreatureType)` for edits.
- Show stats from the creature’s stat set, not from duplicated UI state.
- Keep the create flow focused on a default creature type first, such as `IntegratedCreatureType.DEFAULT`.

### 5) Add regression checks

Target area: `src/test/`

- [ ] Verify session create/select/update behavior
- [ ] Verify save/load round-trips the creature list and selection
- [ ] Verify feat addition still updates stats through the existing modifier path
- [ ] Verify custom stat handling still works after persistence changes

Implementation notes:
- Keep tests small and focused on the first milestone.
- Prefer tests that confirm behavior through public methods rather than internal maps.

---

## Recommended file areas

- `src/dungeonmanager/DungeonManagerApp.java` — startup and app wiring
- `src/dungeonmanager/session/` — session state and persistence coordination
- `src/dungeonmanager/api/` — local backend boundary for the UI
- `src/dungeonmanager/ui/` — creature sidebar/detail UI entry points if kept in this repo
- `src/dungeonmanager/creature/`, `src/dungeonmanager/feature/`, `src/dungeonmanager/stats/`, `src/dungeonmanager/registry/` — keep core domain logic stable
- `src/test/` — session, persistence, and feat regression coverage

---

## Suggested implementation sequence for future AI guidance

1. Session owner and creature collection
2. Save/load snapshot format
3. Local API layer
4. Sidebar and detail view
5. Creature creation flow
6. Feat addition flow
7. Regression tests

---

## Milestone completion criteria

This milestone is done when:

- the app can hold multiple creatures in a session
- a creature can be selected from a list
- the `+` button creates a default creature and prompts for a name and base stats
- the main view shows the selected creature’s stats
- a feat can be added to the selected creature
- the state can be saved to and loaded from JSON locally

---

## Notes for future AI prompts

Use concrete symbols from the codebase in future guidance, such as:

- `dungeonmanager.DungeonManagerApp#initialize()`
- `dungeonmanager.DungeonManagerApp#run()`
- `dungeonmanager.creature.Creature#rename(String)`
- `dungeonmanager.creature.Creature#changeType(CreatureType)`
- `dungeonmanager.feature.FeatureSet#addFeature(String, Feature)`
- `dungeonmanager.feature.FeatureInstance#reload()`
- `dungeonmanager.stats.ModifiableStatSet#reloadValues()`
- `dungeonmanager.registry.Registries#get()`

Keep the plan aligned with the real project structure and avoid speculative changes that are not supported by current symbols.

