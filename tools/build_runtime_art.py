"""Code-native pixel GUI and original Netherite Wart sprites, no external art."""
from pathlib import Path
import math
import random
import json
from PIL import Image,ImageDraw

ROOT=Path(__file__).resolve().parents[1]
ASSETS=ROOT/'art/minecraft/assets/forbidden_brews'

def save_json(path,value):
    path.parent.mkdir(parents=True,exist_ok=True);path.write_text(json.dumps(value,indent=2)+'\n',encoding='utf-8')

im=Image.new('RGBA',(256,256),'#142030');d=ImageDraw.Draw(im)
for inset,color in [(0,'#080d15'),(1,'#9c793e'),(3,'#d5ae65'),(4,'#384457'),(6,'#1a283a')]:
    d.rectangle((inset,inset,255-inset,255-inset),outline=color,width=1)
d.rectangle((8,7,247,26),fill='#22283b');d.line((10,27,245,27),fill='#775b39')
d.rectangle((10,30,245,155),fill='#101b29',outline='#2d3f55')
d.rectangle((40,166,216,248),fill='#101a28',outline='#776244')
# The central vessel frame is a stepped alchemical ring.
for ring,col in [(36,'#473752'),(32,'#a17e42'),(29,'#283c4d')]:
    pts=[(128-ring//2,79-ring),(128+ring//2,79-ring),(128+ring,79-ring//2),
         (128+ring,79+ring//2),(128+ring//2,79+ring),(128-ring//2,79+ring),
         (128-ring,79+ring//2),(128-ring,79-ring//2)]
    d.line(pts+[pts[0]],fill=col,width=1)
for sx,sy in [(42,60),(42,96),(214,60),(214,96)]:
    d.line([(sx,sy),(sx,79),(88 if sx<128 else 168,79)],fill='#5e4932',width=3)
    d.line([(sx,sy),(sx,79),(88 if sx<128 else 168,79)],fill='#b69b56',width=1)
d.line([(128,41),(128,50)],fill='#987342',width=3)
d.line([(128,112),(128,129)],fill='#987342',width=3)
d.rectangle((82,114,174,120),fill='#07121b',outline='#375868')
slots=[(120,70),(120,32),(34,52),(34,88),(206,52),(206,88),(206,128),(120,128)]
for index,(x,y) in enumerate(slots):
    d.rectangle((x-3,y-3,x+18,y+18),fill='#091421',outline='#d7b373' if index==7 else '#6c8192')
    d.line([(x-2,y+17),(x+17,y+17),(x+17,y-2)],fill='#293b50')
for row in range(4):
    for col in range(9):
        x=48+col*18;y=170+row*18+(4 if row==3 else 0)
        d.rectangle((x-1,y-1,x+16,y+16),fill='#111b29',outline='#465567')
        d.line((x,y+15,x+15,y+15),fill='#23344a')
for x in [13,238]:
    for y in [33,153,252]:
        d.rectangle((x-2,y-2,x+2,y+2),fill='#b18b48');d.point((x,y),fill='#71ead4')
gui=ASSETS/'textures/gui/chaos_stand.png';gui.parent.mkdir(parents=True,exist_ok=True);im.save(gui)

def wart(stage,frame,item=False):
    out=Image.new('RGBA',(32,32));p=ImageDraw.Draw(out)
    pods=[(16,25,4)] if stage==0 else [(10,23,4),(21,22,4)] if stage==1 else [(8,22,4),(16,15,5),(24,23,4)] if stage==2 else [(7,24,5),(13,16,5),(22,13,5),(25,25,5),(16,26,5)]
    if item:pods=[(11,19,6),(21,15,6),(19,25,5)]
    for x,y,r in pods:
        p.line([(16,30),(x,y)],fill='#735660',width=2)
        shape=[(x-r,y-r//2),(x-r+2,y-r),(x+r-2,y-r),(x+r,y-r//2),
               (x+r,y+r//2),(x+r-2,y+r),(x-r+2,y+r),(x-r,y+r//2)]
        p.polygon(shape,fill='#403c49',outline='#171b26')
        p.line([(x-r+2,y-r),(x+r-2,y-r),(x+r,y-r//2)],fill='#77707b',width=1)
        p.line([(x,y-r+1),(x-1,y),(x+1,y+2),(x,y+r-1)],fill='#e5903e',width=1)
        p.point((x+1,y),fill='#ffcf74')
        for k in range(2):
            yy=y-r+1+((frame//3+k*3)%(2*r-1))
            if 0<=yy<32:p.point((x,yy),fill='#ffe7b0')
    return out

for stage in range(4):
    path=ASSETS/f'textures/block/netherite_wart_stage_{stage}.png';path.parent.mkdir(parents=True,exist_ok=True)
    sheet=Image.new('RGBA',(32,32*24))
    for frame in range(24):sheet.paste(wart(stage,frame),(0,32*frame))
    sheet.save(path);save_json(Path(str(path)+'.mcmeta'),{'animation':{'frametime':2}})
    save_json(ASSETS/f'models/block/netherite_wart_stage_{stage}.json',{
        'parent':'minecraft:block/crop','render_type':'minecraft:cutout','textures':{'crop':f'forbidden_brews:block/netherite_wart_stage_{stage}'}})
save_json(ASSETS/'blockstates/netherite_wart.json',{'variants':{f'age={age}':{'model':f'forbidden_brews:block/netherite_wart_stage_{age}'} for age in range(4)}})
item=ASSETS/'textures/item/netherite_wart.png';sheet=Image.new('RGBA',(32,32*24))
for frame in range(24):sheet.paste(wart(3,frame,True),(0,32*frame))
sheet.save(item);save_json(Path(str(item)+'.mcmeta'),{'animation':{'frametime':2}})
save_json(ASSETS/'models/item/netherite_wart.json',{'parent':'minecraft:item/generated','textures':{'layer0':'forbidden_brews:item/netherite_wart'}})
for family in ['fortune','looting','homeward','wild_teleport','ore_double','hot_pick','inversion','creeper']:
    for level in range(1,4 if family in ['fortune','looting'] else 2):
        for variant in ['drink','splash']:
            save_json(ASSETS/f'models/item/{family}_{level}_{variant}.json',{'parent':'minecraft:item/generated',
                'textures':{'layer0':f'forbidden_brews:item/{family}_{variant}'}})
for name,color in [('fortune','#efcc61'),('looting','#ee55ab'),('homeward','#f3b653'),('wild_teleport','#ae79ff'),('ore_double','#68dfdb'),('hot_pick','#ff8a38'),('inversion','#dd70e8'),('creeper','#75ed58')]:
    icon=Image.new('RGBA',(18,18));p=ImageDraw.Draw(icon)
    if name=='fortune':
        p.line([(4,14),(12,6)],fill='#b28243',width=2)
        p.line([(5,4),(11,4),(14,7),(14,9)],fill=color,width=3)
        p.point((3,9),fill='#fff1b4');p.point((10,14),fill='#fff1b4')
    elif name=='looting':
        p.line([(4,13),(13,4)],fill=color,width=3);p.line([(3,10),(7,14)],fill='#f1e7d1',width=2)
    elif name=='wild_teleport':
        p.ellipse((3,2,14,15),outline=color,width=2);p.line([(8,5),(11,8),(8,11),(6,9)],fill='#e9caff',width=2)
    elif name=='ore_double':
        for x in [2,9]:p.polygon([(x,8),(x+3,3),(x+6,8),(x+3,14)],fill=color,outline='#286f95')
        p.line((4,5,5,7),fill='#d4fff6');p.line((11,5,12,7),fill='#d4fff6')
    elif name=='inversion':
        p.line([(4,13),(4,4),(2,6),(4,4),(6,6)],fill=color,width=2)
        p.line([(13,4),(13,13),(11,11),(13,13),(15,11)],fill='#f0b7ff',width=2)
    elif name=='creeper':
        p.rectangle((3,2,14,15),fill=color);p.rectangle((5,5,7,7),fill='#1d3823');p.rectangle((10,5,12,7),fill='#1d3823')
        p.rectangle((7,8,10,12),fill='#1d3823');p.rectangle((6,10,11,13),fill='#1d3823');p.point((8,13),fill=color)
    elif name=='hot_pick':
        p.line([(4,14),(12,6)],fill='#c77a36',width=2);p.line([(4,4),(11,4),(14,7),(14,9)],fill=color,width=3)
        p.polygon([(2,8),(1,14),(4,16),(6,13),(5,9),(4,12)],fill='#ffc76c')
    else:
        p.line([(3,8),(9,3),(15,8)],fill=color,width=2);p.rectangle((5,8,13,14),outline=color,width=2)
    path=ASSETS/f'textures/mob_effect/{name}.png';path.parent.mkdir(parents=True,exist_ok=True);icon.save(path)
preview=ROOT/'previews/netherite_wart.gif';frames=[wart(3,i,True).resize((256,256),Image.Resampling.NEAREST) for i in range(24)]
frames[0].save(preview,save_all=True,append_images=frames[1:],duration=100,loop=0,disposal=2)
print('GUI, 4 growth stages, 24 potion models and Netherite Wart ready')
