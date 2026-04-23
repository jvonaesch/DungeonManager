
## Stat
- [ ] StatModifiers can rely on other Stats, not just constants (query requires a snapshot of creature's stats) 

## Flag
- [ ] FlagSet holds a typed boolean dataset about a creature (proficiencies)
- [ ] Implement ProficiencyProvider (analogous to StatModifier) that sets a flag while present

## Resource
- [ ] Resource: Holds a cap, sum of a stat value and a constant.
- [ ] Add ResourceSet and ResourceSetInstance: Features can register a resource that is managed by instances of a creature
- [ ] _(Consider making levels a resource, too - can be changed per creature instance)_

## Creature
- [x] Let creature own its own ID
- [ ] Replace CreatureType with CreatureBasis, also implemented by Creature (can be sourced by ID)
- [ ] Add CreatureInstance: A specific instance of a creature that holds its current resources
- [ ] Move Leave FeatureSet under Creature's control, let feature dependencies (active or not) and stat modifiers run per instance. 

## Content Packs / serialization
- [x] Implement loading stats from content packs
- [x] Switch all content pack loading to use java io paths instead of String paths
- [x] Make stat sets work with String ids instead of Stat instances
- [ ] Implement creature serialization and deserialization
- [ ] Enable loading packs from zip files
- [ ] Add per-pack **defaultStatSet** (default_stat_set.json) that is applied to all creatures while the pack is loaded (defaults are held by Session)

## Feature
- [ ] Implement feature types (add to serialization methods) to distinguish for later feature selections
- [ ] Implement FeatureSelectionSection, allowing to select features from a list of options (or of a given type) with a maximum number of selections

