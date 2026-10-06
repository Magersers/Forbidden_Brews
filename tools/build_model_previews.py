"""Compose Blender frames into repository previews; original 3D art, not AI edits."""
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ROOT=Path(__file__).resolve().parents[1]
OUT=ROOT/'previews';OUT.mkdir(exist_ok=True)
font_path='C:/Windows/Fonts/seguisb.ttf'
font=ImageFont.truetype(font_path,26)
small=ImageFont.truetype('C:/Windows/Fonts/segoeui.ttf',17)
specs=[('chaos_brewing_stand','СТОЙКА ХАОСА','Незерит · три флакона · аметистовый сердечник'),
       ('steve_brute','ГРОМИЛА','Форма Стива после зелья разрушения')]
stills=[]
for name,title,caption in specs:
    frames=[]
    for src in sorted((ROOT/'renders/frames'/name).glob('frame_*.png')):
        im=Image.new('RGB',(540,640),'#111b2a')
        dr=ImageDraw.Draw(im)
        dr.text((270,20),title,font=font,fill='#ecf4f8',anchor='mt')
        dr.text((270,56),'FORBIDDEN BREWS',font=small,fill='#7acdc4',anchor='mt')
        model=Image.open(src).convert('RGBA').resize((530,530),Image.Resampling.LANCZOS)
        im.paste(model,(5,80),model)
        dr.text((270,615),caption,font=small,fill='#b9cbd9',anchor='mm')
        frames.append(im)
    assert len(frames)==48,(name,len(frames))
    durations=[80,80,90]*16 # Exactly four seconds at twelve frames per second.
    palette_source=Image.new('RGB',(96*8,96*6))
    for i,im in enumerate(frames):palette_source.paste(im.resize((96,96)),((i%8)*96,(i//8)*96))
    palette=palette_source.quantize(colors=128,method=Image.Quantize.MEDIANCUT)
    quantized=[im.quantize(palette=palette,dither=Image.Dither.NONE) for im in frames]
    quantized[0].save(OUT/(name+'.gif'),save_all=True,append_images=quantized[1:],duration=durations,loop=0,disposal=1,optimize=True)
    frames[0].save(OUT/(name+'.png'))
    stills.append(frames[0])
    print(name,(OUT/(name+'.gif')).stat().st_size,'bytes')
overview=Image.new('RGB',(1080,640))
for i,im in enumerate(stills):overview.paste(im,(540*i,0))
overview.save(OUT/'chaos_models.png')

# An original recipe illustration; the JSON recipe is authoritative.
recipe=Image.new('RGB',(820,470),'#111b2a');d=ImageDraw.Draw(recipe)
d.text((35,24),'РЕЦЕПТ СТОЙКИ ХАОСА',font=font,fill='#edf5ff')
icons={}
for symbol in ['A','B','N','O','R']:
    icon=Image.new('RGBA',(32,32));p=ImageDraw.Draw(icon)
    if symbol=='A':
        p.polygon([(8,24),(5,12),(10,7),(13,12),(17,3),(23,10),(21,23),(15,28)],fill='#8760d2',outline='#c3a4ff')
        p.line([(17,5),(14,23)],fill='#efdaff',width=2)
    elif symbol=='B':
        p.rectangle((14,5,17,24),fill='#e9c666')
        p.rectangle((5,25,26,28),fill='#858491')
        p.line([(7,19),(24,19)],fill='#bda368',width=3)
        for x in [6,23]:p.rectangle((x,16,x+3,21),fill='#628eac')
    elif symbol=='N':
        p.polygon([(5,14),(12,8),(27,12),(27,19),(21,24),(5,20)],fill='#474351',outline='#221f2b')
        p.polygon([(5,14),(12,8),(27,12),(21,18)],fill='#797380')
        p.line([(7,14),(20,18)],fill='#a19b9f',width=2)
    elif symbol=='O':
        p.polygon([(4,10),(16,4),(28,10),(28,23),(16,29),(4,23)],fill='#312443',outline='#65557a')
        p.line([(16,16),(28,10)],fill='#6b5484',width=2)
        p.line([(16,16),(16,28)],fill='#21192c',width=2)
    else:
        p.line([(7,25),(25,7)],fill='#a46225',width=6)
        p.line([(6,24),(24,6)],fill='#e0ad37',width=4)
        p.line([(6,22),(22,6)],fill='#ffe076',width=2)
    icons[symbol]=icon.resize((72,72),Image.Resampling.NEAREST)
for row,line in enumerate(['ABA',' N ','ORO']):
    for col,symbol in enumerate(line):
        x,y=35+col*92,100+row*92
        d.rectangle((x,y,x+84,y+84),fill='#263448',outline='#526174',width=3)
        if symbol!=' ':recipe.paste(icons[symbol],(x+6,y+6),icons[symbol])
legend=['2 × осколок аметиста','1 × варочная стойка','1 × слиток незерита','2 × обсидиан','1 × стержень ифрита']
for i,line in enumerate(legend):d.text((370,104+i*42),line,font=font,fill='#d4e1ec')
d.text((35,409),'Результат: 1 стойка хаоса',font=font,fill='#77e9d2')
recipe.save(OUT/'chaos_stand_recipe.png')
