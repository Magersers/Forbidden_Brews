"""Original flat pixel artwork: individual flask silhouettes and moving substances."""
from PIL import Image, ImageDraw, ImageFilter
import math

S=32
TAU=math.tau
DESIGNS={
 'inversion': ('Песочные часы','Два слоя и капли, движущиеся навстречу',[(11,11),(21,11),(24,14),(23,17),(19,20),(23,23),(24,27),(21,29),(11,29),(8,27),(9,23),(13,20),(9,17),(8,14)]),
 'wild_teleport': ('Круглый дисковый флакон','Вращающийся вихрь с тёмным центром',[(12,11),(20,11),(20,13),(24,14),(26,18),(26,23),(23,27),(19,29),(13,29),(9,27),(6,23),(6,18),(8,14),(12,13)]),
 'homeward': ('Фонарь с плоскими стенками','Вязкий мёд, тёплые сгустки и угольки',[(12,11),(20,11),(23,14),(24,16),(24,28),(22,29),(10,29),(8,28),(8,16),(9,14)]),
 'swarm': ('Угловатая колба-саркофаг','Багровый дым и вспыхивающие глаза',[(12,11),(20,11),(23,14),(25,17),(24,26),(21,29),(11,29),(8,26),(7,17),(9,14)]),
 'bat': ('Удлинённая капля с крыльями','Чернильный туман и движущиеся силуэты',[(13,11),(19,11),(20,15),(23,20),(23,25),(20,29),(12,29),(9,25),(9,20),(12,15)]),
 'juggernaut': ('Широкая бронированная бутыль','Каменные обломки и раскалённые трещины',[(12,11),(20,11),(22,14),(26,15),(26,27),(24,29),(8,29),(6,27),(6,15),(10,14)]),
 'ore_double': ('Двухкамерный флакон','Растущие парные кристаллы',[(13,11),(19,11),(19,14),(23,14),(26,17),(26,26),(23,29),(19,29),(16,26),(13,29),(9,29),(6,26),(6,17),(9,14),(13,14)]),
 'ore_sight': ('Ромбовидная колба','Сканирующий луч и проявляющиеся крупицы руды',[(13,11),(19,11),(19,14),(23,17),(26,21),(23,25),(17,29),(15,29),(9,25),(6,21),(9,17),(13,14)]),
 'truce': ('Изогнутый травяной сосуд','Плавающие листья в спокойном зелёном геле',[(13,11),(19,11),(19,15),(22,18),(24,23),(23,27),(20,29),(11,29),(8,26),(8,22),(10,18),(13,15)]),
 'creeper': ('Прямоугольный сосуд под давлением','Кислотная пена и пульсирующие газовые пузыри',[(12,11),(20,11),(21,14),(24,14),(24,28),(8,28),(8,14),(11,14)]),
 'hot_pick': ('Колба-печь с широким основанием','Лава, чёрная корка и разрывы расплава',[(13,11),(19,11),(19,16),(21,19),(24,23),(25,28),(7,28),(8,23),(11,19),(13,16)]),
 'fortune': ('Сердцевидный флакон','Золотые хлопья и вспыхивающие звёзды',[(13,11),(19,11),(19,14),(22,13),(25,15),(26,19),(24,23),(20,27),(17,29),(15,29),(12,27),(8,23),(6,19),(7,15),(10,13),(13,14)]),
 'looting': ('Амфора с боковыми рукоятями','Рубиновые сгустки и белые осколки трофеев',[(12,11),(20,11),(20,14),(24,17),(24,23),(21,27),(20,29),(12,29),(11,27),(8,23),(8,17),(12,14)]),
 'shapeshifter': ('Асимметричная колба','Две несмешивающиеся массы, меняющие границу и облик',[(12,11),(20,11),(20,14),(24,15),(26,18),(25,23),(22,26),(21,29),(11,29),(10,26),(7,23),(8,19),(11,17),(12,14)]),
 'hunter': ('Колба-щит с прицелом','Сканирующая сетка проявляет силуэты мобов и ловушек',[(12,11),(20,11),(20,13),(25,15),(25,22),(23,26),(19,29),(13,29),(9,26),(7,22),(7,15),(12,13)]),
 'gravity': ('Парящая капсула с магнитными кольцами','Серебристая масса и капли перемещаются между дном и верхом',[(13,11),(19,11),(21,13),(22,17),(22,25),(20,28),(18,29),(14,29),(12,28),(10,25),(10,17),(11,13)]),
}

def rgb(h):return tuple(int(h[i:i+2],16) for i in (1,3,5))
def mix(a,b,t):return tuple(round(x*(1-t)+y*t) for x,y in zip(a,b))
def clamp(x):return max(0,min(1,x))
def opaque(c):return (*c,255)

def draw_substance(id_,c,t,inner):
 im=Image.new('RGBA',(S,S));px=im.load()
 pale=mix(c,(245,255,225),.7)
 for y in range(11,30):
  for x in range(5,27):
   if not inner.getpixel((x,y)):continue
   dx=x-16;dy=y-21;radius=math.hypot(dx,dy);ang=math.atan2(dy,dx)
   depth=.5+.35*(1-min(1,abs(dx)/12))
   base=mix((8,12,23),c,depth)
   # Every substance has its own spatial field and animation.
   if id_=='inversion':
    seam=20+round(math.sin(t)*2)
    base=mix((24,11,56),c,.85 if y>seam else .28)
    if abs(y-seam)<2:base=mix(c,(245,220,255),.7)
    if y in [14,28]:base=pale
   elif id_=='wild_teleport':
    spiral=math.sin(ang*3-radius*.9-t*2)
    base=mix((5,21,39),c,.18+.70*max(0,spiral))
    if spiral>.88:base=mix(c,(205,255,240),.75)
    if radius<2.4:base=(9,5,29)
   elif id_=='homeward':
    blobs=math.sin(x*.55+math.sin(t)*.8)+math.cos(y*.6+math.cos(t)*.7)
    base=mix((86,33,13),(255,184,58),clamp(.55+blobs*.18))
    if y>26:base=(164,78,20)
   elif id_=='swarm':
    smoke=math.sin(x*.7+t+math.sin(y*.7-t))+math.cos(y*.5-t*2)
    base=mix((19,5,22),(177,25,60),clamp(.15+smoke*.29))
   elif id_=='bat':
    mist=math.sin(x*.55+math.sin(t)+math.cos(y*.45-t))+math.sin(y*.8-t)
    base=mix((12,9,35),(125,101,218),clamp(.28+mist*.18))
   elif id_=='juggernaut':
    crack=abs(math.sin(x*.73+math.sin(t)*.4)+math.cos(y*.87))
    base=(57,42,43) if crack>.26 else mix((239,48,12),(255,199,67),.5+.5*math.sin(t*2+y))
    if (x//3+y//3)%2 and crack>.55:base=(31,30,37)
   elif id_=='ore_double':
    base=mix((8,32,41),(33,120,103),.22+.24*math.sin(y*.25+t))
   elif id_=='ore_sight':
    scan=21+round(6*math.sin(t))
    base=mix((8,26,55),(39,117,177),depth*.7)
    if abs(y-scan)<1.5:base=mix((45,179,231),(199,251,255),1-abs(y-scan)/2)
   elif id_=='truce':
    base=mix((15,51,39),(103,192,131),depth*.62+.05*math.sin(t+y*.2))
   elif id_=='creeper':
    cells=math.sin(x*1.25+math.sin(t)*.3)*math.cos(y*1.19+math.cos(t)*.3)
    base=mix((28,65,21),(157,219,34),.50+.28*cells)
    if y<17:base=(190,235,117)
   elif id_=='hot_pick':
    crust=math.sin(x*.65+math.cos(t))+math.cos(y*.74-math.sin(t))
    base=(42,27,24) if crust>.85 else mix((203,39,8),(255,211,54),clamp(.5-crust*.35))
   elif id_=='fortune':
    base=mix((72,45,12),(222,161,32),depth*.65+.07*math.sin(t))
    if y>26:base=(218,163,46)
   elif id_=='looting':
    clots=math.sin(x*.83+math.sin(t)*.5)+math.cos(y*.77-math.cos(t)*.4)
    base=mix((52,10,36),(185,31,91),clamp(.43+clots*.20))
   elif id_=='shapeshifter':
    boundary=16+math.sin(t+(y-15)*.5)*3
    c2=(35,218,181);c3=(190,73,236)
    base=mix((11,25,36),c2 if x<boundary else c3,depth*.85)
    if abs(x-boundary)<.7:base=(240,192,224)
   elif id_=='hunter':
    scan=21+round(6*math.sin(t))
    base=mix((35,16,30),(143,64,52),depth*.55)
    if (x-10)%4==0 or (y-14)%4==0:base=(100,50,48)
    if abs(y-scan)<1.5:base=mix((239,106,69),(255,235,165),1-abs(y-scan)/2)
   elif id_=='gravity':
    center=21+6*math.cos(t)
    weight=math.exp(-((y-center)/2.1)**2)
    base=mix((15,21,55),(181,209,248),weight*.94)
    if weight>.18 and (x+y)%3==0:base=mix(base,(108,155,225),.55)
    if abs(y-center)<.8:base=(224,243,255)
   px[x,y]=opaque(base)
 d=ImageDraw.Draw(im)
 if id_=='inversion':
  for k,x in enumerate([12,16,20]):
   y=20+round(math.sin(t+k*2)*6)
   d.rectangle((x,y,x+1,y+1),fill=opaque(pale))
 elif id_=='wild_teleport':
  for k in range(3):
   a=t*2+k*TAU/3;x=16+round(6*math.cos(a));y=21+round(6*math.sin(a))
   d.point([(x,y),(x+1,y)],fill=(230,255,241,255))
 elif id_=='homeward':
  for k in range(4):
   x=11+k*3;y=26-round(((t/TAU+k/4)%1)*10)
   d.rectangle((x,y,x+1,y+2),fill=(255,205,105,255))
 elif id_=='swarm':
  for k in range(3):
   x=11+k*4;y=18+k*3+round(math.sin(t+k))
   d.point([(x,y),(x+2,y)],fill=(255,91+round(55*math.sin(t+k)),85,255))
 elif id_=='bat':
  for k in range(2):
   x=13+k*5+round(math.cos(t+k));y=18+k*6+round(math.sin(t+k))
   d.line([(x-2,y-1),(x,y),(x+2,y-1)],fill=(22,12,46,255));d.point((x,y+1),fill=(22,12,46,255))
 elif id_=='juggernaut':
  for k,(x,y) in enumerate([(11,18),(19,23),(14,27)]):
   yy=y+round(math.sin(t+k));d.rectangle((x,yy,x+2,yy+1),fill=(101,84,76,255));d.point((x,yy),fill=(179,145,118,255))
 elif id_=='ore_double':
  for k,x in enumerate([11,21]):
   h=5+round(2*math.sin(t+k*math.pi))
   d.polygon([(x,27-h),(x-3,25),(x,28),(x+3,25)],fill=(35,169,133,255))
   d.polygon([(x,27-h),(x-2,25),(x,27)],fill=(127,249,202,255))
   d.line([(x,27-h),(x,27)],fill=(215,255,225,255))
 elif id_=='ore_sight':
  scan=21+round(6*math.sin(t))
  for x,y in [(11,20),(17,25),(21,19),(14,16)]:
   col=(203,249,255,255) if abs(y-scan)<3 else (55,119,148,255)
   d.rectangle((x,y,x+1,y+1),fill=col)
 elif id_=='truce':
  for k,(x,y) in enumerate([(12,22),(19,18),(19,26)]):
   dx=round(math.sin(t+k));d.polygon([(x+dx-2,y),(x+dx,y-2),(x+dx+2,y-2),(x+dx+1,y+1)],fill=(57,129,62,255))
   d.line([(x+dx-1,y),(x+dx+1,y-1)],fill=(181,235,145,255))
 elif id_=='creeper':
  for k,(x,y,r) in enumerate([(13,19,2),(20,24,3),(12,26,1),(20,17,1)]):
   yy=y+round(math.sin(t+k));rr=max(1,r+round(.7*math.sin(t+k)))
   d.ellipse((x-rr,yy-rr,x+rr,yy+rr),fill=(60,105,24,255),outline=(216,250,131,255));d.point((x-1,yy-rr+1),fill=(247,255,205,255))
 elif id_=='hot_pick':
  for k in range(3):
   x=12+k*4;y=27-round(((t/TAU+k/3)%1)*7)
   d.point([(x,y),(x+1,y)],fill=(255,238,124,255))
 elif id_=='fortune':
  for k,(x,y) in enumerate([(11,19),(21,19),(16,25),(10,25),(20,27)]):
   if math.sin(t+k*1.8)>.1:
    d.point((x,y),fill=(255,255,211,255))
    if math.sin(t+k*1.8)>.8:d.point([(x-1,y),(x+1,y),(x,y-1),(x,y+1)],fill=(255,220,110,255))
 elif id_=='looting':
  for k,(x,y) in enumerate([(12,18),(20,23),(13,26)]):
   yy=y+round(math.sin(t+k));d.line([(x,yy),(x+2,yy+1)],fill=(242,200,170,255));d.point([(x-1,yy),(x+3,yy+1)],fill=(255,222,191,255))
 elif id_=='shapeshifter':
  yy=20+round(math.sin(t));d.rectangle((11,yy,13,yy+1),fill=(226,254,211,255));d.rectangle((20,yy,22,yy+1),fill=(255,203,233,255))
  d.point((12 if math.sin(t)>0 else 11,yy),fill=(16,44,39,255));d.point((21 if math.cos(t)>0 else 20,yy),fill=(41,18,65,255))
 elif id_=='hunter':
  scan=21+round(6*math.sin(t))
  # Hostile eyes, a pressure plate, a button and a tripwire silhouette.
  for x,y,kind in [(11,17,'mob'),(19,22,'mob'),(11,25,'plate'),(20,16,'button'),(18,26,'wire')]:
   active=abs(y-scan)<3
   col=(255,238,170,255) if active else (157,71,58,255)
   if kind=='mob':
    d.rectangle((x,y,x+3,y+3),outline=col)
    d.point([(x+1,y+1),(x+3,y+1)],fill=(255,88,76,255) if active else col)
   elif kind=='plate':d.line([(x,y),(x+3,y)],fill=col);d.point((x+1,y-1),fill=col)
   elif kind=='button':d.rectangle((x,y,x+1,y+2),fill=col)
   else:d.line([(x-1,y),(x,y-1),(x+1,y),(x+2,y-1)],fill=col)
 elif id_=='gravity':
  for k,x in enumerate([13,17,19]):
   y=21+round(5*math.cos(t+k*.8))
   d.ellipse((x-1,y-1,x+1,y+1),fill=(133,183,244,255),outline=(229,245,255,255))
  d.line([(16,17),(16,24)],fill=(77,107,186,255))
  if math.cos(t)>0:d.polygon([(14,24),(16,27),(18,24)],fill=(191,223,255,255))
  else:d.polygon([(14,18),(16,15),(18,18)],fill=(191,223,255,255))
 # Surface and air pocket follow the local silhouette, without painting outside.
 for y in range(11,30):
  for x in range(5,27):
   if not inner.getpixel((x,y)):px[x,y]=(0,0,0,0);continue
   if id_=='gravity':continue
   surface=14+(round(math.sin(t+x*.4)) if id_ in ['inversion','swarm','hot_pick','creeper'] else 0)
   if y<surface:px[x,y]=(0,0,0,0)
   elif y==surface:px[x,y]=opaque(mix(c,(227,249,220),.35))
 return im

GOLD=(185,132,54,255);EDGE=(60,43,39,255);IVORY=(233,224,175,255)
def ornaments(id_,t,c,front=False):
 im=Image.new('RGBA',(S,S));d=ImageDraw.Draw(im);lit=opaque(mix(c,(242,255,217),.65))
 if not front:
  if id_=='bat':
   wing=[(10,17),(5,12),(2,11),(2,18),(4,17),(5,21),(7,19),(10,24)]
   for points in [wing,[(32-x,y) for x,y in wing]]:
    d.polygon(points,fill=(41,24,67,255));d.line(points[:3],fill=(166,123,199,255),width=1)
   d.line([(3,14),(6,17),(8,20)],fill=(90,55,119,255));d.line([(29,14),(26,17),(24,20)],fill=(90,55,119,255))
  elif id_=='wild_teleport':
   d.ellipse((3,12,29,29),outline=GOLD,width=1)
   for x,y in [(3,20),(29,20),(16,29)]:d.rectangle((x-1,y-1,x+1,y+1),fill=lit)
  elif id_=='juggernaut':
   for side in [-1,1]:
    pts=[(16+side*10,18),(16+side*12,14),(16+side*10,12),(16+side*9,15)]
    d.polygon(pts,fill=IVORY);d.line(pts[:3],fill=GOLD)
  elif id_=='looting':
   for x in [3,24]:d.rectangle((x,16,x+4,24),outline=GOLD,width=2)
  elif id_=='truce':
   d.line([(6,27),(5,21),(7,15),(11,12)],fill=(80,111,50,255),width=1)
   d.polygon([(5,21),(2,19),(2,16),(6,18)],fill=(99,166,72,255));d.line([(2,16),(5,20)],fill=(176,223,124,255))
   d.polygon([(7,15),(6,11),(9,10),(10,12)],fill=(99,166,72,255))
  elif id_=='ore_double':
   for x in [4,28]:
    d.polygon([(x,21),(x-2,25),(x,28),(x+2,25)],fill=(24,154,120,255));d.line([(x,22),(x-1,25),(x,27)],fill=lit)
  elif id_=='inversion':
   d.line([(5,16),(3,18),(3,24)],fill=GOLD,width=1);d.polygon([(1,23),(3,27),(5,23)],fill=lit)
   d.line([(27,24),(29,22),(29,16)],fill=GOLD,width=1);d.polygon([(27,17),(29,13),(30,17)],fill=lit)
  elif id_=='swarm':
   for x in [4,27]:
    d.line([(x,16),(x,25)],fill=IVORY,width=2);d.point([(x-1,16),(x+1,16),(x-1,25),(x+1,25)],fill=IVORY)
  elif id_=='fortune':
   d.line([(7,16),(4,18),(4,22)],fill=GOLD)
   d.rectangle((1,23,3,25),fill=(86,164,56,255));d.rectangle((4,20,6,22),fill=(86,164,56,255));d.rectangle((4,24,6,26),fill=(86,164,56,255));d.point((4,23),fill=(227,228,139,255))
  elif id_=='shapeshifter':
   d.line([(7,20),(3,18),(3,14)],fill=(140,89,171,255),width=2);d.line([(24,23),(28,25),(29,21)],fill=(74,173,157,255),width=2)
  elif id_=='hunter':
   d.rectangle((2,17,7,23),outline=GOLD,width=1)
   d.line([(1,20),(8,20)],fill=lit);d.line([(5,15),(5,25)],fill=lit)
   d.line([(23,14),(28,16),(29,22),(27,26)],fill=(154,112,71,255))
   d.line([(25,25),(29,25)],fill=IVORY);d.line([(25,27),(29,27)],fill=GOLD)
  elif id_=='gravity':
   for y in [16,27]:
    d.ellipse((6,y-2,26,y+2),outline=GOLD,width=1)
    d.rectangle((5,y-1,7,y+1),fill=lit);d.rectangle((25,y-1,27,y+1),fill=lit)
   d.polygon([(2,13),(4,10),(6,13)],fill=lit);d.line([(4,13),(4,19)],fill=GOLD)
   d.polygon([(26,25),(28,28),(30,25)],fill=lit);d.line([(28,19),(28,25)],fill=GOLD)
 else:
  if id_=='inversion':d.rectangle((12,19,20,21),outline=GOLD,width=1)
  elif id_=='homeward':
   d.line([(7,16),(16,10),(25,16)],fill=GOLD,width=2)
   d.rectangle((12,24,20,28),fill=(99,58,26,255),outline=GOLD);d.rectangle((15,25,17,27),fill=IVORY)
  elif id_=='swarm':
   d.rectangle((13,24,19,28),fill=(183,175,139,255));d.point([(14,25),(18,25),(16,27)],fill=(51,37,38,255))
  elif id_=='juggernaut':
   for x in [6,24]:d.rectangle((x,17,x+2,27),fill=(90,73,65,255));d.point([(x+1,18),(x+1,26)],fill=IVORY)
   d.line([(8,28),(24,28)],fill=GOLD,width=2)
  elif id_=='ore_double':d.line([(16,16),(16,26)],fill=(165,210,199,255))
  elif id_=='ore_sight':
   d.polygon([(24,17),(29,19),(30,22),(29,25),(24,25)],fill=GOLD)
   d.ellipse((26,20,29,23),fill=(36,69,104,255));d.point((28,21),fill=lit)
  elif id_=='creeper':
   for x in [6,24]:
    d.rectangle((x,15,x+1,28),fill=(46,69,39,255));d.point([(x,17),(x,25)],fill=GOLD)
   d.rectangle((12,24,20,29),fill=(53,80,26,255));d.rectangle((13,25,14,26),fill=(14,28,14,255));d.rectangle((18,25,19,26),fill=(14,28,14,255));d.point([(16,27),(15,28),(16,28),(17,28)],fill=(14,28,14,255))
  elif id_=='hot_pick':
   d.line([(7,28),(25,28)],fill=(80,60,45,255),width=2)
   d.line([(25,27),(28,20)],fill=(158,105,48,255),width=2);d.line([(24,20),(27,17),(29,19)],fill=(228,182,102,255),width=2)
  elif id_=='fortune':
   d.line([(11,12),(10,9),(14,11),(16,8),(18,11),(22,9),(21,12)],fill=GOLD,width=1);d.point((16,9),fill=lit)
  elif id_=='looting':
   d.line([(25,27),(29,17)],fill=(196,207,212,255),width=2);d.line([(25,23),(30,25)],fill=GOLD,width=1)
  elif id_=='shapeshifter':
   d.rectangle((23,12,29,17),fill=(69,46,89,255),outline=GOLD);d.point([(24,14),(28,14)],fill=lit);d.line([(26,13),(26,16)],fill=GOLD)
  elif id_=='hunter':
   d.line([(8,14),(12,12),(20,12),(24,14)],fill=GOLD,width=1)
   d.rectangle((11,27,21,29),fill=(57,37,37,255),outline=GOLD)
   d.point([(14,28),(18,28)],fill=lit)
  elif id_=='gravity':
   for y in [16,27]:
    d.line([(7,y),(11,y+2),(21,y+2),(25,y)],fill=GOLD,width=1)
    d.point([(10,y+1),(22,y+1)],fill=lit)
 return im

def render_frame(entry,variant,f,n=24):
 id_=entry['id'];c=rgb(entry['color']);t=TAU*(f%n)/n
 shape=Image.new('L',(S,S));ImageDraw.Draw(shape).polygon(DESIGNS[id_][2],fill=255)
 inner=shape.filter(ImageFilter.MinFilter(3))
 im=ornaments(id_,t,c)
 # One-pixel stepped glass contour with brighter left edges and dark right edges.
 glass=Image.new('RGBA',(S,S));p=glass.load()
 for y in range(S):
  for x in range(S):
   if shape.getpixel((x,y)):
    p[x,y]=(36,60,78,135) if inner.getpixel((x,y)) else ((185,216,237,255) if x<16 else (72,119,159,255))
 im.alpha_composite(glass)
 im.alpha_composite(draw_substance(id_,c,t,inner))
 d=ImageDraw.Draw(im)
 # Broken specular streaks leave the unique contents unobstructed.
 for y in range(17,25):
  xs=[x for x in range(S) if inner.getpixel((x,y))]
  if xs and y%4!=0:d.point((min(xs),y),fill=(205,234,246,235))
 if variant=='drink':
  d.rectangle((13,6,19,12),fill=(56,96,132,255));d.rectangle((14,6,17,12),fill=(177,211,233,180));d.rectangle((15,7,17,11),fill=(30,49,67,110))
  d.rectangle((12,4,20,6),fill=(138,69,41,255));d.rectangle((13,2,19,4),fill=(203,120,76,255));d.line([(13,2),(18,2)],fill=(242,167,106,255))
  d.line([(12,6),(20,6)],fill=(171,204,218,255))
 else:
  # Angled neck and sealed powder cap make each throwable unmistakable.
  d.polygon([(14,12),(12,10),(15,6),(19,8),(18,12)],fill=(157,196,220,255))
  d.line([(14,10),(16,7)],fill=(54,91,131,255),width=2)
  d.polygon([(14,6),(17,3),(22,6),(19,9)],fill=(149,82,47,255));d.line([(17,3),(21,5)],fill=(225,151,88,255),width=1)
  d.line([(21,5),(24,3),(26,4)],fill=(189,163,104,255),width=1)
  d.point((26,4),fill=(255,226,139,255) if f%6<3 else (239,118,34,255))
 im.alpha_composite(ornaments(id_,t,c,True))
 return im
