# Stand and Brute models

Only these two assets use 3D geometry; potion bottles remain animated flat pixel items.

The Chaos Brewing Stand has a Netherite base, three bottle holders, a golden ring and an Amethyst core. Its runtime Minecraft block/item model uses animated glowing textures. The artistic rotating preview is a model presentation, not rotating block geometry in-game.

![Chaos Brewing Stand model preview](../previews/chaos_brewing_stand.gif)

The Brute is a block-shaped mutated Steve, about 2.9 blocks tall, with large fists, a torn turquoise shirt, purple trousers, stone growths and orange cracks. Its model is wired into the Juggernaut transformation, including collision dimensions, eye height and first-person hands. Blockbench includes idle/walk/smash actions; Blender includes editable actions and a smash animation.

![Brute model preview](../previews/steve_brute.gif)

Editable sources: [Blockbench](../art/blockbench/), [Blender](../art/blender/), [GLB](../art/models/). Minecraft assets live under `art/minecraft/assets/forbidden_brews/`. Reproduce pixel items with `tools/build_pixel_potions.py` and runtime GUI/crop resources with `tools/build_runtime_art.py`.

See the [brewing guide](brewing.md) for recipes, transformation mechanics and verified gameplay. These assets and their generation code were substantially created with AI assistance.
