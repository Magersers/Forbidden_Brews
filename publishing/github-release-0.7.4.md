# Forbidden Brews 0.7.4

Random Teleport now searches nearby safe terrain and keeps its drinkable potion until teleportation succeeds. This release also includes the Brute combat, expanded Shapeshifter, reversed-gravity rendering and brewing-interface improvements made since the last published release, 0.7.2. The withdrawn 0.7.3 build is superseded.

## Random Teleport hotfix

- Search neighbouring columns throughout each prepared chunk instead of rejecting a destination after checking one column. Solid leaf canopies can provide a safe landing.
- Keep remote chunks loaded during the asynchronous search, retry failed requests, and select destinations within the world border. The search is bounded to 24 attempts and a time limit.
- Consume a drink only after a safe destination is ready. Failed or cancelled searches preserve the original potion without creating extra empty bottles.
- Repeated drinking cannot create overlapping searches. Transferring the pending bottle outside the player's inventory cancels that drink; creative mode retains its potion.
- Splash versions use the improved destination search. A thrown splash bottle is still consumed when thrown.
- Loader metadata now takes its version from the Gradle project, with a packaging check that rejects mismatched version numbers.

## Brute, transformations and interface

- An active Juggernaut no longer resets in cramped mines when combined with Ore Seeker. Initial transformation still needs enough headroom; milk and effect expiry restore the original form.
- Charged Brute melee attacks damage nearby mobs within three blocks of the primary target for 60% of the main hit's damage, with strong knockback. Walls, allies and owned tameable pets are excluded. Double maximum health, +3 attack damage, 4×4 harvesting and protected-block restrictions remain.
- Shapeshifter discovers registered mobs, including compatible modded mobs and bosses such as the Ender Dragon. Large forms require open space. Reapplying the effect changes form while preserving a longer existing duration; saved forms use registry keys and migrate older saves.
- Reversed gravity turns the player model and world/hand view upside down. A second jump or effect removal restores orientation; HUD and menus remain readable.
- Missing Chaos Stand ingredients appear pale and translucent. Inserted resources are fully bright; insufficient totals have a copper outline.

![Actual Minecraft ingredient hints and inserted resources](https://raw.githubusercontent.com/Magersers/Forbidden_Brews/main/previews/chaos_ingredients_0.7.3.gif)

## Downloads and installation

| Minecraft / loader | Requirements |
|---|---|
| Forge 1.20.1 | Forge 47.4.0+, Java 17 |
| NeoForge 1.21.1 | NeoForge 21.1.252+, Java 21 |
| Fabric 1.21.1 | Fabric Loader 0.16.14+, Fabric API 0.116.17+1.21.1 or a compatible newer 1.21.1 release, Java 21 |

Download the matching attached JAR and replace the old Forbidden Brews JAR on **both client and server**. Install only one loader variant. SHA-256 checksums are attached in `manifest.json`.

[CurseForge project](https://www.curseforge.com/minecraft/mc-mods/forbidden-brews) · [Every recipe and controls](https://github.com/Magersers/Forbidden_Brews/blob/v0.7.4/docs/brewing.md)

## Chaos Brewing Stand recipe

Use a crafting table with **2 Amethyst Shards, 1 Brewing Stand, 1 Netherite Ingot, 2 Obsidian and 1 Blaze Rod** to make one Chaos Brewing Stand.

| | | |
|---|---|---|
| Amethyst Shard | Brewing Stand | Amethyst Shard |
| Empty | Netherite Ingot | Empty |
| Obsidian | Blaze Rod | Obsidian |

![Stand recipe in Minecraft's actual crafting interface](https://raw.githubusercontent.com/Magersers/Forbidden_Brews/main/previews/chaos_stand_recipe.png)

![Chaos Brewing Stand recorded in Minecraft](https://raw.githubusercontent.com/Magersers/Forbidden_Brews/main/previews/chaos_stand_in_game.gif)

![Actual English brewing interface](https://raw.githubusercontent.com/Magersers/Forbidden_Brews/main/previews/chaos_stand_ui.gif)

## Validation and credits

All **43 required Forge GameTests** passed, including remote terrain loading, unsafe terrain without potion loss, small world borders, offhand/creative drinking through Minecraft's native use events, cancellation and transfer protection. All three production builds and packaging checks passed. Runtime GameTests were run on Forge; NeoForge/Fabric were compiled and packaged. The separate 0.7.4 visual client attempt timed out at login, so no new Random Teleport client recording is claimed. The brewing and ingredient media above are actual earlier Minecraft captures.

Languages: English (US/UK), Russian, German, Spanish, French, Brazilian Portuguese, Simplified Chinese, Japanese, Korean and Ukrainian. Translations are AI-assisted and have not been reviewed by native speakers.

Concept and direction: Magersers. Implementation, procedural art code, localization and documentation were substantially created with AI assistance. The repository logo is AI-generated; gameplay media is recorded in Minecraft. All Rights Reserved.
