"""Compose actual Forge visual-test captures into repository previews."""
from pathlib import Path
from PIL import Image
import shutil

ROOT=Path(__file__).resolve().parents[1]
source=ROOT/'platforms/forge-1.20.1/run'
output=ROOT/'previews'
paths=sorted(source.glob('chaos-ui-*.png'))
assert len(paths)>=150, 'Run the complete visual test first'
frames=[]
for path in paths[2:]:
    with Image.open(path) as image:
        # GUI scale 2, 1280x720 framebuffer. Keep native crisp pixels.
        assert image.size==(1280,720)
        frames.append(image.crop((384,104,896,616)).convert('RGB'))
frames[0].save(output/'chaos_stand_ui.gif',save_all=True,append_images=frames[1:],duration=100,loop=0,optimize=False)
frames[30].save(output/'chaos_stand_ui.png')
shutil.copyfile(source/'chaos-world.png',output/'chaos_stand_in_game.png')
print(f'Captured {len(frames)} game frames; wrote GUI GIF, PNG and world screenshot')
