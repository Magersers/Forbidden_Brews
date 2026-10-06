"""Package genuine English Minecraft crafting and stand recordings.

Run the isolated Forge visual fixture with run/stand-capture.flag present.
The recipe result is verified by Minecraft's actual crafting menu.
"""
from pathlib import Path
from PIL import Image
import shutil

ROOT = Path(__file__).resolve().parents[1]
run = ROOT / 'platforms/forge-1.20.1/run'
out = ROOT / 'previews'
with Image.open(run / 'chaos-crafting-native.png') as shot:
    assert shot.size == (1280, 720)
    shot.crop((464, 194, 816, 524)).resize((704, 660), Image.Resampling.NEAREST).save(out / 'chaos_stand_recipe.png')
shutil.copyfile(run / 'chaos-crafting-native.png', out / 'chaos_stand_crafting_full.png')
frames = []
for path in sorted(run.glob('chaos-stand-native-*.png')):
    with Image.open(path) as shot:
        frames.append(shot.convert('RGB').resize((640, 360), Image.Resampling.NEAREST))
assert len(frames) >= 50
target = out / 'chaos_stand_in_game.gif'
sequence = frames[::2]
sequence[0].save(target, save_all=True, append_images=sequence[1:], duration=200, loop=0, optimize=True)
assert target.stat().st_size < 2 * 1024 * 1024
print(f'Native crafting screenshot and {len(frames)}-frame stand GIF: {target.stat().st_size} bytes')
