# Brewing guide — Forbidden Brews 0.7.2

Install the matching [release](../releases/0.7.2/) on client and server. See [compatibility](compatibility.md) for loader and Java requirements. Fabric also requires Fabric API.

## Stand and crop

Craft the Chaos Brewing Stand with this grid:

| | | |
|---|---|---|
| Amethyst Shard | Brewing Stand | Amethyst Shard |
| Empty | Netherite Ingot | Empty |
| Obsidian | Blaze Rod | Obsidian |

![Actual Minecraft crafting interface](../previews/chaos_stand_recipe.png)

![Chaos Brewing Stand in Minecraft](../previews/chaos_stand_in_game.gif)

Collect it with a Diamond or Netherite Pickaxe. **One Nether Wart + one Netherite Scrap → one Netherite Wart** is a shapeless recipe. Plant it on Soul Sand, not Soul Soil. It grows through four stages by random ticks, without water or light requirements. Bone Meal does not accelerate it. Immature plants return one Wart; mature plants yield 2–4, increased by Fortune.

## Interface

1. Click the empty result slot marked **Choose**. With an empty base or water, all sixteen basic potions appear without scrolling. Hover ingredient ghosts for required counts.
2. Insert a previous **drinkable** potion in the central base slot to reveal its upgrades and splash recipe. Fortune, Looting and Inversion have three levels. Splash bottles cannot be upgraded.
3. Add the base, one Netherite Wart and ingredients in the four side slots in any order. Unrelated ingredient types prevent brewing.
4. Add Blaze Powder: one powder supplies twenty brews. Brewing starts when the selected recipe matches and the output is free. Missing ingredients reset progress; saves preserve active brewing. Automatic ingredient matching also works for hoppers.
5. Collect the result from the lower central slot.

![English brewing interface](../previews/chaos_stand_ui.gif)

Hoppers insert Wart/ingredients from above, base/fuel from the sides and extract below. Breaking the stand drops its contents.

## Recipes

**Every row also consumes one Netherite Wart and one fuel charge.** Times assume 20 TPS. The splash row applies to all 22 drinkable items: 44 brewing recipes total.

| Result | Base | Ingredients | Time |
|---|---|---|---|
| Fortune I | Water Bottle | Rabbit's Foot ×1, Gold Ingot ×4, Emerald ×1 | 10 s |
| Fortune II | Fortune I | Rabbit's Foot ×2, Diamond ×1, Emerald Block ×1 | 15 s |
| Fortune III | Fortune II | Golden Apple ×1, Diamond Block ×1, Nether Star ×1 | 20 s |
| Looting I | Water Bottle | Ghast Tear ×1, Iron Ingot ×4, Blaze Rod ×1 | 12 s |
| Looting II | Looting I | Ghast Tear ×2, Diamond ×2, Ender Pearl ×2 | 17 s |
| Looting III | Looting II | Nether Star ×1, Diamond Block ×1, Netherite Scrap ×1 | 22 s |
| Homeward | Water Bottle | Compass ×1, Ender Pearl ×2, Amethyst Shard ×4 | 12 s |
| Random Teleport | Water Bottle | Chorus Fruit ×4, Ender Pearl ×2, Amethyst Shard ×8, Echo Shard ×1 | 16 s |
| Double Ore | Water Bottle | Diamond ×2, Raw Gold ×4, Amethyst Shard ×8, Netherite Scrap ×1 | 18 s |
| Hot Pick | Water Bottle | Magma Cream ×2, Blast Furnace ×1, Blaze Rod ×2, Raw Iron ×8 | 14 s |
| Inversion I | Water Bottle | Fermented Spider Eye ×2, Phantom Membrane ×1, Amethyst Shard ×6, Redstone Dust ×4 | 14 s |
| Inversion II | Inversion I | Fermented Spider Eye ×3, Glowstone Dust ×4, Phantom Membrane ×2, Amethyst Shard ×8 | 15 s |
| Inversion III | Inversion II | Fermented Spider Eye ×4, Glowstone Dust ×8, Ghast Tear ×1, Echo Shard ×1 | 20 s |
| Creeper Heart | Water Bottle | TNT ×1, Gunpowder ×4, Slimeball ×2, Blaze Powder ×2 | 18 s |
| Truce | Water Bottle | Golden Apple ×1, Honey Bottle ×2, Emerald ×4, Spore Blossom ×1 | 18 s |
| Swarm | Water Bottle | Rotten Flesh ×8, Bone ×8, Spider Eye ×4, Echo Shard ×1 | 20 s |
| Ore Seeker | Water Bottle | Spyglass ×1, Amethyst Shard ×8, Glowstone Dust ×4, Diamond ×1 | 16 s |
| Hunter | Water Bottle | Spectral Arrow ×8, Spider Eye ×2, Tripwire Hook ×2, Echo Shard ×1 | 18 s |
| Night Wings | Water Bottle | Phantom Membrane ×2, Feather ×8, Ghast Tear ×1, Echo Shard ×1 | 18 s |
| Juggernaut | Water Bottle | Netherite Ingot ×1, Iron Block ×2, Obsidian ×8, Magma Cream ×4 | 24 s |
| Shapeshifter | Water Bottle | Chorus Fruit ×8, Rabbit Hide ×4, Slimeball ×4, Echo Shard ×2 | 20 s |
| Gravity | Water Bottle | Shulker Shell ×2, Phantom Membrane ×4, Amethyst Shard ×8, Nether Star ×1 | 24 s |
| Any splash version | Matching drinkable potion | Gunpowder ×2 | 8 s |

![Base choices](../previews/chaos_recipe_picker.png)

![Splash recipe unlocked by the drinkable base](../previews/chaos_recipe_upgrades.png)

## Effects and controls

**Fortune/Looting:** add 1/2/3 enchantment levels to drops, stacking with tool/weapon enchantments, for 2/5/8 minutes. Fortune III + potion II gives Fortune V. Fortune is separate from vanilla Luck. Actual tools remain unchanged; Silk Touch keeps its normal priority.

**Double Ore/Hot Pick:** two minutes. Drops are calculated with Fortune, then smelted through world furnace recipes, then doubled. Silk Touch bypasses smelting/doubling. Placeable ore blocks cannot be repeatedly multiplied; unsmelted Ancient Debris remains one block. No additional furnace XP is granted.

**Ore Seeker:** all loaded ores in a 32-block sphere, full-bright actual textures with cyan outlines through walls, no count cap. Incremental scanning can take around two seconds; mined ores disappear immediately. Deepslate ores, Nether Quartz, Ancient Debris and compatible tagged mod ores are included. Extend `forbidden_brews:ores` with datapacks. **Hunter:** hostile mobs at 32 blocks, buttons, pressure plates and tripwires/hooks at 24. Both last two minutes, are visible only to the affected client and retain the full ore radius when combined.

**Homeward:** safe bed/anchor or forced spawn, falling back near world spawn. **Random Teleport:** current dimension, up to ±2048 blocks on each horizontal axis, at least 256 on one axis. Both check border, ground, headroom, liquids and hazards. Random Teleport tries twelve destinations asynchronously; failure leaves you in place. Death, logout or dimension change cancels a pending search. These effects teleport players only and reset velocity/fall distance.

**Inversion:** harmful; rotates world/held item 180° for 11/22/45 seconds by level. Higher levels last longer. Movement, HUD and menus stay usable; milk restores the view.

**Creeper Heart:** drinking creates a power-3 explosion with four points of separate base self-damage, modified by difficulty/armour. Splash creates one power-4 TNT-like impact explosion. Nearby entities and blocks follow normal explosion rules; low health can still be fatal.

**Truce:** two minutes. Clears targeting; hitting a mob permits retaliation from that particular mob. A splashed mob stops its own aggression until attacked. Environmental hazards, TNT and players still cause damage. **Swarm:** harmful, two minutes. Attempts a safe spawn every four seconds at 12–24 blocks, capped at sixteen nearby hostiles within 32 blocks. Daytime uses Creepers, Spiders and Husks; other dimensions have their own pools. Respects Peaceful, `doMobSpawning` and active chunks. Milk stops new attempts; existing mobs remain.

**Night Wings:** two-minute Bat form, 0.5×0.9 blocks. Double-tap Jump to toggle flight, hold Jump to rise, Sneak to descend. Landing ends flight; it can be restarted. Milk restores original permissions and protects the first landing.

**Juggernaut:** two-minute Brute form, 1.4×2.9 blocks, eye height 2.5. Requires headroom. Doubles maximum health and adds +3 attack damage while preserving other modifiers and current health percentage: 15/20 → 30/40; later 24/40 → 12/20. Reapplication/reloading cannot stack the bonus.

Breaking a block with a tool mines a 4×4 plane perpendicular to the main look direction through native harvesting with durability, loot, enchantments and protection events. Bedrock, unbreakable blocks and blocks harder than Obsidian are blocked even as the original target in Creative. Obsidian (hardness 50) is allowed; Reinforced Deepslate (55) is not. Eight-tick interval; no chain reaction.

**Shapeshifter:** two minutes; Pig, Cow, Sheep, Wolf, Fox, Rabbit, Chicken, Bee, Spider, Zombie, Skeleton, Creeper, Enderman, Slime or Bat. Chooses a form that fits, persists across saves, rerolls on a fresh drink. Bat grants flight; other mob powers are not granted. Form priority: Juggernaut → Night Wings → Shapeshifter. Identity, equipment and inventory remain intact.

**Gravity:** two minutes. Each separate Jump press toggles ascent/descent, including mid-air; holding Jump does not repeat. Vertical speed is capped at 0.65 blocks/tick; ascent stops at build height. Safe fall handling covers the first landing after removal. Bat flight, passengers and spectators keep their controls. Works with `allow-flight=false`; mobs do not receive jump-controlled movement.

All families have splash versions. Timed splash duration follows vanilla distance scaling. Player controls apply to players; affected mobs retain ordinary AI. Milk removes effects and restores health modifiers, dimensions and flight permissions.

## Verification

31 Forge GameTests cover all 44 recipes and native gameplay. Production packaging rejects test fixtures and validates all language keys/placeholders. The English client recording asserts ten simultaneous ores, exclusion beyond 32 blocks, removal after mining, Brute 40 HP/restoration to 20, sixteen native harvested blocks, bat flight, a random form and both gravity directions. NeoForge/Fabric have production build and server startup checks; their client recordings and GameTests have not been run.
