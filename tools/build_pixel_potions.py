"""Original flat pixel potion sprites with distinct silhouettes and substances.
No Blender models or mod behavior are generated. Requires Pillow.
python tools/build_pixel_potions.py
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont
import argparse, json

ROOT=Path(__file__).resolve().parents[1]
N=24
S=32
CATALOG=json.loads((ROOT/'art'/'catalog.json').read_text(encoding='utf-8'))

from potion_designs import render_frame, DESIGNS
ENGLISH=json.loads((ROOT/'common/src/main/resources/assets/forbidden_brews/lang/en_us.json').read_text(encoding='utf-8'))
def english_name(entry): return ENGLISH['effect.forbidden_brews.'+entry['id']]

def rgb(h): return tuple(int(h[i:i+2],16) for i in (1,3,5))

def frame(entry,variant,f,bases=None):
 return render_frame(entry,variant,f,N)

def font(size,bold=False):
 return ImageFont.truetype('C:/Windows/Fonts/'+('consolab.ttf' if bold else 'consola.ttf'),size)

def card_icon(im,size=160):
 return im.resize((size,size),Image.Resampling.NEAREST)

def gallery(frames,variant,f):
 rows=(len(CATALOG)+6)//7
 footer=134+rows*257
 W,H=1040,footer+322
 im=Image.new('RGB',(W,H),(12,16,25));d=ImageDraw.Draw(im)
 d.text((30,22),'FORBIDDEN BREWS',font=font(30,True),fill=(231,223,197))
 d.text((31,63),str(len(CATALOG))+' DISTINCT BOTTLES / '+('DRINKABLE' if variant=='drink' else 'SPLASH'),font=font(16),fill=(136,170,187))
 for i,entry in enumerate(CATALOG):
  x=24+(i%7)*144;y=106+(i//7)*257
  d.rectangle((x,y,x+134,y+237),fill=(23,30,42),outline=(54,64,79),width=2)
  icon=card_icon(frames[entry['id']][f],128)
  im.paste(icon,(x+3,y+28),icon)
  title=english_name(entry)
  words=title.split();lines=['']
  for word in words:
   if len(lines[-1]+word)>15:lines.append(word+' ')
   else:lines[-1]+=word+' '
  for j,line in enumerate(lines):d.text((x+8,y+176+j*18),line.strip(),font=font(13,True),fill=(219,223,226))
  d.rectangle((x+8,y+221,x+124,y+223),fill=rgb(entry['color']))
 d.text((30,footer),'LIVING LIQUIDS',font=font(25,True),fill=(219,231,231))
 d.text((31,footer+40),'Vortices / mist / crystals / lava / foam / leaves / gold',font=font(17),fill=(148,170,185))
 # Big examples make the actual liquid animation easy to judge.
 for j,id_ in enumerate(['wild_teleport','ore_double','creeper','hot_pick']):
  icon=card_icon(frames[id_][f],192);im.paste(icon,(40+j*250,footer+89),icon)
 return im

def save_gif(frames,path,duration=100):
 # Fixed palette prevents frame-to-frame color changes and keeps pixel edges sharp.
 palette_source=Image.new('RGB',(frames[0].width*3,frames[0].height))
 for k,idx in enumerate([0,N//3,2*N//3]):palette_source.paste(frames[idx].convert('RGB'),(k*frames[0].width,0))
 palette=palette_source.quantize(colors=256)
 quant=[im.convert('RGB').quantize(palette=palette,dither=Image.Dither.NONE) for im in frames]
 quant[0].save(path,save_all=True,append_images=quant[1:],duration=duration,loop=0,optimize=True,disposal=1)

def build(jar=None):
 bases=None
 previews=ROOT/'previews';previews.mkdir(exist_ok=True)
 art=ROOT/'art'/'icons';art.mkdir(exist_ok=True)
 textures=ROOT/'art'/'minecraft'/'assets'/'forbidden_brews'/'textures'/'item'
 textures.mkdir(parents=True,exist_ok=True)
 for variant in ['drink','splash']:
  frames={}
  for entry in CATALOG:
   id_=entry['id'];seq=[frame(entry,variant,f,bases) for f in range(N)];frames[id_]=seq
   folder=art/variant;folder.mkdir(exist_ok=True)
   seq[0].save(folder/(id_+'.png'))
   sheet=Image.new('RGBA',(S,S*N))
   for f,im in enumerate(seq):sheet.paste(im,(0,f*S))
   name=id_+'_'+variant
   sheet.save(textures/(name+'.png'))
   (textures/(name+'.png.mcmeta')).write_text(json.dumps({'animation':{'frametime':2,'interpolate':False}},indent=2)+'\n',encoding='utf-8')
   small=[]
   for im in seq:
    bg=Image.new('RGB',(192,192),(19,25,35));icon=card_icon(im,192);bg.paste(icon,(0,0),icon);small.append(bg)
   gifs=previews/'individual'/variant;gifs.mkdir(parents=True,exist_ok=True)
   save_gif(small,gifs/(id_+'.gif'))
  cards=[gallery(frames,variant,f) for f in range(N)]
  cards[0].save(previews/('potions_'+variant+'.png'))
  save_gif(cards,previews/('potions_'+variant+'.gif'))
  print('READY',variant,flush=True)
 # Focused side-by-side review of drinking and splash bottles.
 sample=[]
 for f in range(N):
  im=Image.new('RGB',(1000,410),(12,16,25));d=ImageDraw.Draw(im)
  d.text((25,15),'DISTINCT BOTTLES / UNIQUE LIQUIDS',font=font(22,True),fill=(224,227,223))
  for j,id_ in enumerate(['inversion','wild_teleport','bat','ore_double','hot_pick']):
   entry=next(e for e in CATALOG if e['id']==id_)
   for k,variant in enumerate(['drink','splash']):
    icon=card_icon(frame(entry,variant,f,bases),160);im.paste(icon,(j*200+20,45+k*170),icon)
   d.text((j*200+18,383),english_name(entry),font=font(13,True),fill=(181,201,205))
  sample.append(im)
 save_gif(sample,previews/'style_sample.gif');sample[0].save(previews/'style_sample.png')
 additions=[]
 for f in range(N):
  im=Image.new('RGB',(900,530),(12,16,25));d=ImageDraw.Draw(im)
  d.text((24,16),'TELEPORT / HUNTER / GRAVITY',font=font(24,True),fill=(231,223,197))
  for j,id_ in enumerate(['wild_teleport','hunter','gravity']):
   entry=next(e for e in CATALOG if e['id']==id_)
   d.text((28+j*295,55),english_name(entry),font=font(19,True),fill=rgb(entry['color']))
   for k,variant in enumerate(['drink','splash']):
    icon=card_icon(frame(entry,variant,f),192);im.paste(icon,(51+j*295,85+k*206),icon)
  additions.append(im)
 save_gif(additions,previews/'new_potions.gif');additions[0].save(previews/'new_potions.png')
 for entry in CATALOG:
  entry['bottle_shape'],entry['substance_structure'],_=DESIGNS[entry['id']]
  entry['name_en']=english_name(entry)
 (ROOT/'art'/'catalog.json').write_bytes((json.dumps(CATALOG,ensure_ascii=False,indent=2)+'\n').replace('\n','\r\n').encode('utf-8'))
 print('2D_ASSETS_READY',flush=True)

if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--vanilla-jar',help='Legacy option; no longer required')
 build(p.parse_args().vanilla_jar)
