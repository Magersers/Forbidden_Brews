"""Compose actual Forge visual-test captures into repository previews."""
from pathlib import Path
from PIL import Image
import shutil

ROOT=Path(__file__).resolve().parents[1]
source=ROOT/'platforms/forge-1.20.1/run'
output=ROOT/'previews'
paths=sorted(source.glob('chaos-ui-*.png'))
assert len(paths)>=200, 'Run the complete visual test first'
frames=[]
for path in paths[2:]:
    with Image.open(path) as image:
        # GUI scale 2, 1280x720 framebuffer. Keep native crisp pixels.
        assert image.size==(1280,720)
        frames.append(image.crop((384,104,896,652)).convert('RGB'))
frames[0].save(output/'chaos_stand_ui.gif',save_all=True,append_images=frames[1:],duration=100,loop=0,optimize=False)
frames[95].save(output/'chaos_stand_ui.png')
for kind in ['base','upgrades']:
    with Image.open(source/f'chaos-picker-{kind}.png') as image:
        image.crop((384,104,896,652)).save(output/('chaos_recipe_picker.png' if kind=='base' else 'chaos_recipe_upgrades.png'))
shutil.copyfile(source/'chaos-world.png',output/'chaos_stand_in_game.png')
print(f'Captured {len(frames)} game frames; wrote GUI GIF, PNG and world screenshot')
fx=sorted(source.glob('chaos-fx-*.png'))
if fx:
    assert len(fx)>=560, 'Incomplete potion effect demo'
    effect_frames=[]
    for path in fx:
        with Image.open(path) as shot:effect_frames.append(shot.convert('RGB').resize((640,360),Image.Resampling.NEAREST))
    effect_frames[0].save(output/'remaining_potions_0.7.0_in_game.gif',save_all=True,append_images=effect_frames[1:],duration=100,loop=0,optimize=True)
    effect_frames[0].save(output/'ore_xray_0.7.0_in_game.gif',save_all=True,append_images=effect_frames[1:90],duration=100,loop=0,optimize=True)
    print(f'Wrote {len(effect_frames)} frames of actual textured ore X-ray, bat flight, brute mining, random morph and gravity')

for shot,kind in [('xray-diamond','xray_diamond'),('xray-gold','xray_gold'),('morph-bat','bat'),('morph-brute','brute'),('morph-smash','brute_smash'),('morph-random','shapeshifter'),('gravity-up','gravity_up'),('gravity-down','gravity_down'),('remaining-milk','remaining_milk')]:
    shutil.copyfile(source/f'{shot}.png',output/f'{kind}_in_game.png')
