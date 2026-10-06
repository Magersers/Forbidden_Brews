"""Package real Forge captures; no simulated gameplay or fabricated UI."""
from pathlib import Path
import shutil
from PIL import Image

ROOT=Path(__file__).resolve().parents[1]
RUN=ROOT/'platforms/forge-1.20.1/run'
OUT=ROOT/'previews'
names={0:'chaos_ghosts',1:'chaos_inserted',3:'brute_area',4:'shapeshifter_dragon',5:'gravity_model',6:'gravity_camera',7:'gravity_restored'}
for frame,name in names.items():
    shutil.copyfile(RUN/f'073-native-{frame}.png',OUT/f'{name}_0.7.3.png')
frames=[Image.open(RUN/f'073-native-{i}.png').convert('RGB').crop((386,106,894,614)) for i in (0,1)]
frames[0].save(OUT/'chaos_ingredients_0.7.3.gif',save_all=True,append_images=frames[1:],duration=1800,loop=0,optimize=True)
print('Packaged native 0.7.3 captures and ingredient comparison GIF')
