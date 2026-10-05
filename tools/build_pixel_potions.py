"""Flat potion sprites using the ordinary Minecraft bottle from a local client JAR.
No Blender models or mod behavior are generated. Requires Pillow.
python tools/build_pixel_potions.py --vanilla-jar /path/to/minecraft-client.jar
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont, ImageFilter
import argparse, json, math, colorsys, zipfile, io

ROOT=Path(__file__).resolve().parents[1]
N=24
S=32
TAU=math.tau
CATALOG=json.loads((ROOT/'art'/'catalog.json').read_text(encoding='utf-8'))

def load_bottles(jar):
 with zipfile.ZipFile(jar) as z:
  return {name:Image.open(io.BytesIO(z.read('assets/minecraft/textures/item/'+name+'.png'))).convert('RGBA').resize((S,S),Image.Resampling.NEAREST) for name in ['potion','splash_potion']}

def rgb(h): return tuple(int(h[i:i+2],16) for i in (1,3,5))
def mix(a,b,t): return tuple(round(x*(1-t)+y*t) for x,y in zip(a,b))

def decorations(id_,t,c):
 im=Image.new('RGBA',(S,S));d=ImageDraw.Draw(im)
 gold=(204,154,62,255);dark=(87,64,38,255);lit=(*mix(c,(255,255,220),.5),255)
 # Keep the vanilla bottle dominant: adornments use only a handful of pixels.
 if id_=='bat':
  wing=[(6,18),(3,16),(2,17),(2,21),(3,20),(4,22),(6,21)]
  d.polygon(wing,fill=(43,31,65,255));d.point((3,17),fill=lit)
  d.polygon([(32-x,y) for x,y in wing],fill=(43,31,65,255));d.point((29,17),fill=lit)
 elif id_=='juggernaut':
  for x in [8,24]:
   d.line([(x,18),(x-1 if x==8 else x+1,15),(x,13)],fill=gold,width=1)
 elif id_=='ore_double':
  for x in [5,26]:
   d.polygon([(x,22),(x-2,25),(x,28),(x+2,25)],fill=(*mix(c,(0,20,35),.45),255))
   d.line([(x,23),(x-1,25),(x,26)],fill=lit,width=1)
 else:
  # A small hanging charm, attached to the neck by a two-pixel cord.
  d.line([(21,14),(24,16),(26,20)],fill=dark,width=1)
  if id_=='creeper':
   d.rectangle((25,20,29,25),fill=(69,99,33,255))
   d.point([(26,21),(28,21),(27,23),(26,24),(28,24)],fill=(12,28,16,255))
   d.point((25,20),fill=lit)
  elif id_=='hot_pick':
   d.line([(26,24),(28,21)],fill=gold)
   d.line([(25,21),(27,20),(29,21)],fill=(255,188,68,255))
  elif id_=='homeward':
   d.line([(24,22),(27,19),(30,22)],fill=gold)
   d.rectangle((25,22,29,25),fill=(155,89,40,255));d.rectangle((27,23,27,25),fill=lit)
  elif id_=='fortune':
   d.point([(26,21),(28,21),(26,23),(28,23),(27,22),(27,24)],fill=(108,197,95,255));d.point((27,25),fill=gold)
  elif id_=='looting':
   d.line([(26,25),(29,20)],fill=(201,211,218,255));d.line([(25,23),(28,25)],fill=gold)
  elif id_=='truce':
   d.polygon([(26,25),(25,23),(26,21),(29,20),(28,23)],fill=(79,172,90,255));d.line([(26,25),(28,21)],fill=lit)
  elif id_=='ore_sight':
   d.polygon([(24,22),(27,20),(30,22),(27,24)],fill=gold);d.point((27,22),fill=lit)
  elif id_=='inversion':
   d.line([(25,24),(25,20),(24,21)],fill=lit);d.line([(28,20),(28,24),(29,23)],fill=lit)
  elif id_=='wild_teleport':
   d.rectangle((25,20,29,24),outline=(58,55,103,255));d.line([(26,20),(29,21),(28,24),(25,23)],fill=lit)
  elif id_=='swarm':
   d.rectangle((25,20,29,24),fill=(99,42,54,255));d.point([(26,21),(28,21)],fill=lit);d.point((27,24),fill=gold)
  elif id_=='shapeshifter':
   d.rectangle((25,20,29,24),fill=(77,48,114,255));d.line([(27,20),(27,24)],fill=gold);d.point([(26,21),(28,22)],fill=lit)
 return im

def frame(entry,variant,f,bases):
 c=rgb(entry['color']);id_=entry['id'];t=TAU*f/N
 im=decorations(id_,t,c)
 # Round belly matches the vanilla bottle; 2x resolution allows living liquid.
 fluid=Image.new('RGBA',(S,S));px=fluid.load()
 for y in range(16,28):
  lo,hi=(12,21) if y in [16,27] else (10,23)
  for x in range(lo,hi+1):
   wave=math.sin((x-10)*.54+t)*.5+math.sin((y-16)*.85-t)*.5
   edge=min(x-lo,hi-x)/max(1,(hi-lo)/2)
   depth=.38+.32*edge+.15*math.cos(t+(y-16)*.4)
   base=mix((10,12,29),c,max(.12,min(1,depth)))
   band=.5+.5*math.sin((y-16)*1.06-t*2+math.sin((x-10)*.45+t))
   if band>.77:base=mix(base,mix(c,(225,255,247),.45),(band-.77)/.23*.65)
   surface=17+round(math.sin(t+(x-10)*.55))
   if y<surface:continue
   if y==surface:base=mix(c,(225,255,245),.35+.20*math.sin(t+x*.3))
   px[x,y]=(*base,255)
 # Swirling luminous threads, visible through the unmodified glass highlights.
 d=ImageDraw.Draw(fluid)
 for k in range(2):
  yy=20+k*4+round(math.sin(t+k*1.6))
  for x in range(12,22):
   y=yy+round(math.sin((x-12)*.62+t+k))
   if 18<=y<=26 and px[x,y][3]:d.point((x,y),fill=(*mix(c,(240,255,242),.36),255))
 # Six bubbles rise in a loop; one-pixel glints pulse inside the liquid.
 for k in range(3):
  phase=(f/N+k/3)%1;xx=[13,18,21][k];yy=26-round(phase*8)
  if .07<phase<.90:
   d.point((xx,yy),fill=(*mix(c,(247,255,246),.74),255))
   if k==1:d.point((xx+1,yy+1),fill=(*mix(c,(247,255,246),.2),255))
 # Clip to belly, leaving bottle geometry and all vanilla glass pixels intact.
 mask=Image.new('L',(S,S));md=ImageDraw.Draw(mask)
 md.rectangle((12,16,21,27),fill=255);md.rectangle((10,18,23,25),fill=255)
 fluid.putalpha(mask.point(lambda a:a))
 # Restore transparent upper air pocket after applying the geometric mask.
 for x in range(10,24):
  surface=17+round(math.sin(t+(x-10)*.55))
  for y in range(16,surface):fluid.putpixel((x,y),(0,0,0,0))
 im.alpha_composite(fluid)
 im.alpha_composite(bases['potion' if variant=='drink' else 'splash_potion'])
 # Tiny neck tie; do not alter the familiar cork or overall vanilla silhouette.
 if id_ not in ['bat','juggernaut','ore_double']:
  ImageDraw.Draw(im).point((21,14),fill=(*mix(c,(245,227,166),.3),255))
 return im

def font(size,bold=False):
 return ImageFont.truetype('C:/Windows/Fonts/'+('consolab.ttf' if bold else 'consola.ttf'),size)

def card_icon(im,size=160):
 return im.resize((size,size),Image.Resampling.NEAREST)

def gallery(frames,variant,f):
 W,H=1040,970
 im=Image.new('RGB',(W,H),(12,16,25));d=ImageDraw.Draw(im)
 d.text((30,22),'FORBIDDEN BREWS',font=font(30,True),fill=(231,223,197))
 d.text((31,63),'ОБЫЧНЫЕ ФЛАКОНЫ / '+('ПИТЬЕВЫЕ' if variant=='drink' else 'ВЗРЫВНЫЕ'),font=font(16),fill=(136,170,187))
 for i,entry in enumerate(CATALOG):
  x=24+(i%7)*144;y=106+(i//7)*257
  d.rectangle((x,y,x+134,y+237),fill=(23,30,42),outline=(54,64,79),width=2)
  icon=card_icon(frames[entry['id']][f],128)
  im.paste(icon,(x+3,y+28),icon)
  title=entry['name_ru']
  words=title.split();lines=['']
  for word in words:
   if len(lines[-1]+word)>15:lines.append(word+' ')
   else:lines[-1]+=word+' '
  for j,line in enumerate(lines):d.text((x+8,y+176+j*18),line.strip(),font=font(13,True),fill=(219,223,226))
  d.rectangle((x+8,y+221,x+124,y+223),fill=rgb(entry['color']))
 d.text((30,648),'ЖИВАЯ ЖИДКОСТЬ',font=font(25,True),fill=(219,231,231))
 d.text((31,688),'Переливы, светящиеся потоки и поднимающиеся пузырьки.',font=font(17),fill=(148,170,185))
 # Big examples make the actual liquid animation easy to judge.
 for j,id_ in enumerate(['inversion','ore_double','creeper','hot_pick']):
  icon=card_icon(frames[id_][f],192);im.paste(icon,(40+j*250,737),icon)
 return im

def save_gif(frames,path,duration=100):
 # Fixed palette prevents frame-to-frame color changes and keeps pixel edges sharp.
 palette_source=Image.new('RGB',(frames[0].width*3,frames[0].height))
 for k,idx in enumerate([0,N//3,2*N//3]):palette_source.paste(frames[idx].convert('RGB'),(k*frames[0].width,0))
 palette=palette_source.quantize(colors=256)
 quant=[im.convert('RGB').quantize(palette=palette,dither=Image.Dither.NONE) for im in frames]
 quant[0].save(path,save_all=True,append_images=quant[1:],duration=duration,loop=0,optimize=True,disposal=1)

def build(jar):
 bases=load_bottles(jar)
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
  im=Image.new('RGB',(800,340),(12,16,25));d=ImageDraw.Draw(im)
  d.text((25,15),'ОБЫЧНЫЕ ФЛАКОНЫ + ЖИВАЯ ЖИЖА',font=font(22,True),fill=(224,227,223))
  for j,id_ in enumerate(['inversion','bat','creeper','hot_pick']):
   entry=next(e for e in CATALOG if e['id']==id_)
   for k,variant in enumerate(['drink','splash']):
    icon=card_icon(frame(entry,variant,f,bases),144);im.paste(icon,(j*200+28,50+k*136),icon)
  sample.append(im)
 save_gif(sample,previews/'style_sample.gif');sample[0].save(previews/'style_sample.png')
 print('2D_ASSETS_READY',flush=True)

if __name__=='__main__':
 p=argparse.ArgumentParser();p.add_argument('--vanilla-jar',required=True)
 build(p.parse_args().vanilla_jar)
