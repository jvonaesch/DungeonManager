# DungeonManager PWA Roadmap

This roadmap turns the current Java domain model into a local backend for a future progressive web app. The first visible milestone is a simple creature manager UI:

- a left sidebar listing creatures
- a `+` button to create a default creature
- a prompt to enter the creature name and base stats
- a main panel that shows the selected creature's stats
- an action to add a feat to the selected creature

The plan is intentionally incremental so future AI guidance can follow it step by step without rewriting the whole project.

---

## 0. Current starting point

Verified entry points and symbols in the existing codebase:

- `dungeonmanager.DungeonManagerApp#initialize()` registers stats and commands
- `dungeonmanager.DungeonManagerApp#run()` currently runs ad-hoc tests instead of a real application loop
- `dungeonmanager.creature.Creature`
  - holds `DefaultedStatSet stats`
  - holds `FeatureSet feature`
  - supports `rename(String)` and `changeType(CreatureType)`
- `dungeonmanager.feature.FeatureSet#addFeature(String, Feature)` and `removeFeature(...)`
- `dungeonmanager.feature.FeatureInstance#reload()` loads feature sections and modifiers
- `dungeonmanager.stats.ModifiableStatSet#reloadValues()` recalculates derived stat values
- `dungeonmanager.registry.Registries#get()` is the shared registry entry point

Important constraint: keep the current registry-driven stat model intact while adding persistence and UI layers around it.

---

## 1. Define the product shape

### Goal
Make the intended user flow explicit before writing implementation code.

### Deliverables
- A minimal creature list view
- A selected creature detail view
- Create/edit flows for creature name and base stats
- Feature/feat addition flow for the selected creature
- Local save/load to JSON

### Acceptance criteria
- The app can represent multiple creatures in memory
- A creature can be selected from a list
- A new creature can be created with default stats
- Changes survive a save/load cycle

---

## 2. Stabilize the backend domain boundary

### Goal
Keep the Java domain model as the source of truth for creature state.

### Work items
1. Review `dungeonmanager.creature.Creature` as the main aggregate root for session data.
2. Keep stat recalculation inside `dungeonmanager.stats.ModifiableStatSet#reloadValues()`.
3. Keep feature application/removal routed through `dungeonmanager.feature.FeatureSet`.
4. Avoid direct UI access to internal maps like `ModifiableStatSet#base_values` or `FeatureSet#features`.

### Guidance for future AI
- Prefer small adapter classes over changing the core stat math.
- If UI needs data, expose read-only DTOs instead of raw mutable objects.
- Any new creature state should be owned by a session-level store, not `DungeonManagerApp`.

---

## 3. Introduce a session layer

### Goal
Add a single place that owns the active party/session state.

### Suggested direction
Use `src/dungeonmanager/session/` for the application state that sits above the domain model.

### Responsibilities
- Store the current list of creatures
- Track the selected creature ID
- Create a default creature
- Delete or rename creatures
- Save and load the whole session

### Suggested session operations
- `createCreature()`
- `selectCreature(id)`
- `updateCreatureName(id, name)`
- `updateBaseStat(id, statId, value)`
- `addFeatToCreature(id, featId)`
- `serialize()` / `deserialize()`

### Acceptance criteria
- One object owns the party/session state
- UI and persistence code do not need to know about registry internals

---

## 4. Define JSON persistence

### Goal
Persist the session locally so a table can be resumed later.

### Data to store
- Session metadata and schema version
- Creature list
- Creature IDs and names
- Creature type
- Base stat overrides
- Active features / feats
- Feature selections if any exist
- Custom stats that were created dynamically

### Important notes
- Serialize IDs, not live registry objects.
- Rehydrate stat and feature references through `Registries.get()` when loading.
- Preserve a schema version field from day one.
- Save custom stats explicitly so `StatModifier#setValue(String, int)`-created stats can be restored deterministically.

### Acceptance criteria
- Save file can be written to the library folder under `C:\Users\jonas\DungeonManagerLibrary\`
- Load reconstructs the same visible creature list and selected creature state

---

## 5. Add a local API boundary

### Goal
Let the future web UI talk to the Java backend cleanly.

### Recommended direction
Expose a small local HTTP API from the Java app instead of binding the UI directly to domain objects.

### Suggested endpoints
- `GET /session`
- `GET /creatures`
- `POST /creatures`
- `GET /creatures/{id}`
- `PATCH /creatures/{id}`
- `POST /creatures/{id}/features`
- `DELETE /creatures/{id}/features/{featureId}`
- `POST /save`
- `POST /load`

### Acceptance criteria
- The UI can list creatures without knowing the Java object graph
- The UI can create a creature and fetch the updated detail view

---

## 6. Build the first UI slice: sidebar + detail panel

### Goal
Reach the simple view described in the request.

### Sidebar behavior
- Show the list of creatures in the current session
- Highlight the selected creature
- Include a `+` button at the top or bottom

### `+` button flow
- Create a new creature with a default type, such as `IntegratedCreatureType.DEFAULT`
- Prompt for:
  - creature name
  - base stat values
- Save the creature immediately into the session
- Select the newly created creature

### Detail panel behavior
When a creature is selected:
- show its name and type
- show its stats from `Creature#stats`
- show its active feats/features
- provide an `Add feat` action

### Acceptance criteria
- A user can create a creature from the sidebar
- A user can click a creature and see its stats
- A user can add a feat and see the stat effect update

---

## 7. Implement feat/feature editing

### Goal
Support one visible gameplay action: adding a feat to a creature.

### Work items
- Use `dungeonmanager.feature.FeatureSet#addFeature(String, Feature)` for feature addition
- Use `dungeonmanager.feature.FeatureSet#removeFeature(...)` for cleanup
- Keep modifier recalculation through `ModifiableStatSet#addModifier(...)` / `removeModifier(...)`
- If a feature has selectable subsections, preserve `FeatureInstance#reload()` and `SelectionSection#loadToInstance(FeatureInstance)` behavior

### Acceptance criteria
- A selected creature can receive a feat from the UI
- Stat changes appear immediately in the detail panel

---

## 8. Make the app installable as a PWA

### Goal
Turn the web UI into an offline-capable shell.

### Work items
- Add a manifest
- Add a service worker
- Cache the static UI bundle
- Support offline startup against local saved state
- Keep the backend running locally during play sessions

### Acceptance criteria
- The UI can be installed from the browser
- The UI can reopen without losing local app assets

---

## 9. Defer or avoid until later

These should stay out of the first implementation pass unless they unblock the simple view:

- advanced command-line features in `dungeonmanager.command`
- deep feature tree editors
- combat encounter automation
- importing/exporting multiple campaigns
- syncing to cloud storage

---

## 10. Suggested AI execution order

When asking AI to continue implementation, use this sequence:

1. Create the session/state layer
2. Add JSON save/load
3. Expose a minimal local API
4. Build the sidebar + creature detail UI
5. Add creature creation flow
6. Add feat selection and application
7. Make the UI installable as a PWA

---

## 11. Definition of done for the first milestone

The first milestone is complete when all of the following are true:

- the app shows a sidebar of creatures
- the `+` button creates a default creature
- the user is prompted for a name and base stats
- selecting a creature shows its stats in the main view
- the main view includes an option to add a feat
- the state can be saved to and loaded from JSON locally

---

## 12. Notes for future AI prompts

Use concrete project symbols in prompts and comments, for example:

- `dungeonmanager.DungeonManagerApp#initialize()`
- `dungeonmanager.DungeonManagerApp#run()`
- `dungeonmanager.creature.Creature#rename(String)`
- `dungeonmanager.creature.Creature#changeType(CreatureType)`
- `dungeonmanager.feature.FeatureSet#addFeature(String, Feature)`
- `dungeonmanager.feature.FeatureInstance#reload()`
- `dungeonmanager.stats.ModifiableStatSet#reloadValues()`
- `dungeonmanager.registry.Registries#get()`

Keep the roadmap aligned with real behavior already present in the codebase, and avoid speculative changes that are not supported by current symbols.

