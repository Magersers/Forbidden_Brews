"""Original textured cube models; no downloaded meshes or Minecraft textures.
Generates editable Blockbench projects, vanilla block JSON, and a Java entity layer.
Run with Python/Pillow before render_chaos_models.py in Blender.
"""
from pathlib import Path
import base64
import json
import math
import random
import uuid
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
ART = ROOT / 'art'
ASSETS = ART / 'minecraft/assets/forbidden_brews'
SIZE = 256
COLORS = {
    'netherite': '#39343f', 'obsidian': '#292039', 'gold': '#b18a45',
    'rune': '#56efda', 'crystal': '#a16be8', 'pink': '#e467b5',
    'amber': '#f3b445', 'glass': '#aeced7', 'cork': '#a9653c',
    'skin': '#ad795c', 'shirt': '#258e94', 'pants': '#4b4592',
    'hair': '#3d2920', 'stone': '#50535c', 'boot': '#353344',
    'lava': '#ff973b', 'belt': '#534133',
}


def uid(s):
    return str(uuid.uuid5(uuid.NAMESPACE_URL, 'forbidden_brews/' + s))


def save(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')


class Model:
    def __init__(self, name):
        self.name = name
        self.cubes = []
        self.bones = {'root': {'origin': [0, 0, 0], 'parent': None}}
        self.texture = Image.new('RGBA', (SIZE, SIZE), (0, 0, 0, 0))
        self.x = self.y = self.row = 0

    def bone(self, name, origin, parent='root'):
        self.bones[name] = {'origin': origin, 'parent': parent}

    def cube(self, name, a, b, material, bone='root', detail=None):
        w, h, d = [max(1, math.ceil(b[i] - a[i])) for i in range(3)]
        width, height = 2 * (w + d), h + d
        if self.x + width + 1 > SIZE:
            self.x = 0
            self.y += self.row + 1
            self.row = 0
        assert self.y + height <= SIZE, 'Texture atlas overflow'
        u, v = self.x, self.y
        self.x += width + 1
        self.row = max(self.row, height)
        faces = {
            'east': [u, v+d, u+d, v+d+h],
            'north': [u+d, v+d, u+d+w, v+d+h],
            'west': [u+d+w, v+d, u+2*d+w, v+d+h],
            'south': [u+2*d+w, v+d, u+2*(d+w), v+d+h],
            'up': [u+d, v, u+d+w, v+d],
            'down': [u+d+w, v+d, u+d+2*w, v],
        }
        rgb = tuple(bytes.fromhex(COLORS[material][1:]))
        rng = random.Random(name)
        for rect in faces.values():
            x0, y0, x1, y1 = rect
            for yy in range(min(y0,y1), max(y0,y1)):
                for xx in range(x0,x1):
                    shade = rng.choice([-12, -6, 0, 0, 0, 6, 10])
                    self.texture.putpixel((xx, yy), tuple(max(0,min(255,c+shade)) for c in rgb)+(255,))
        dr = ImageDraw.Draw(self.texture)
        nx, ny, _, _ = faces['north']
        if detail == 'face':
            dr.rectangle((nx,ny,nx+w-1,ny+2),fill=COLORS['hair'])
            for ex in [2, w-4]:
                dr.rectangle((nx+ex,ny+5,nx+ex+2,ny+5),fill='#dbd2c2')
                dr.point((nx+ex+1,ny+5),fill='#7165f5')
                dr.line((nx+ex,ny+4,nx+ex+2,ny+4),fill='#3d2920')
            dr.rectangle((nx+4,ny+7,nx+7,ny+8),fill='#825239')
            dr.rectangle((nx+2,ny+9,nx+w-3,ny+h-1),fill='#513326')
            dr.rectangle((nx+4,ny+9,nx+7,ny+9),fill='#b1785c')
            dr.line((nx+9,ny+2,nx+9,ny+4),fill='#ef9a43')
        elif detail == 'shirt':
            dr.rectangle((nx+w//2-2,ny,nx+w//2+2,ny+2),fill=COLORS['skin'])
            dr.line((nx+w//2,ny+3,nx+w//2,ny+h-3),fill='#196770')
            for x in range(1,w-2,4):
                dr.rectangle((nx+x,ny+h-3,nx+x+1,ny+h-1),fill=COLORS['skin'])
            dr.line((nx+2,ny+6,nx+6,ny+6),fill='#46a5a4')
            dr.rectangle((nx+1,ny+7,nx+3,ny+9),fill=COLORS['skin'])
        elif detail == 'cracks':
            for side, rect in faces.items():
                x0,y0,x1,y1=rect
                y0,y1=sorted((y0,y1))
                cx=(x0+x1)//2
                for y in range(y0+1,y1-1):
                    cx=max(x0,min(x1-1,cx+rng.choice([-1,0,0,1])))
                    dr.point((cx,y),fill='#ff9c36')
                    if y%3==0 and cx+1<x1: dr.point((cx+1,y),fill='#ffd785')
        elif detail == 'pants':
            dr.line((nx+1,ny,nx+1,ny+h-1),fill='#6e63b2')
            dr.rectangle((nx+2,ny+h-3,nx+w-3,ny+h-2),fill='#393270')
        elif detail == 'knuckles':
            for x in range(1,w-1,3):
                dr.line((nx+x,ny+2,nx+x,ny+h-3),fill='#80533f')
        c={'name':name,'from':list(a),'to':list(b),'material':material,
           'bone':bone,'uv':[u,v],'faces':faces,'uuid':uid(self.name+'/'+name)}
        self.cubes.append(c)

    def write(self, category, animations):
        texture_path = ASSETS / f'textures/{category}/{self.name}.png'
        texture_path.parent.mkdir(parents=True,exist_ok=True)
        self.texture.save(texture_path)
        tex = {'name':self.name+'.png','id':'0','uuid':uid(self.name+'/texture'),
               'width':SIZE,'height':SIZE,'uv_width':SIZE,'uv_height':SIZE,
               'source':'data:image/png;base64,'+base64.b64encode(texture_path.read_bytes()).decode()}
        elements=[]
        for c in self.cubes:
            elements.append({k:c[k] for k in ['name','from','to','uuid']} | {
                'type':'cube','box_uv':True,'uv_offset':c['uv'],
                'origin':self.bones[c['bone']]['origin'],
                'faces':{side:{'uv':uv,'texture':0} for side,uv in c['faces'].items()}})
        def group(name):
            return {'name':name,'uuid':uid(self.name+'/bone/'+name),
                    'origin':self.bones[name]['origin'],'rotation':[0,0,0],
                    'children':[c['uuid'] for c in self.cubes if c['bone']==name]+
                    [group(b) for b in self.bones if self.bones[b]['parent']==name]}
        save(ART/'blockbench'/f'{self.name}.bbmodel',{
            'meta':{'format_version':'4.10','model_format':'free','box_uv':True},
            'name':self.name,'model_identifier':'forbidden_brews:'+self.name,
            'resolution':{'width':SIZE,'height':SIZE},'elements':elements,
            'outliner':[group('root')],'textures':[tex],'animations':animations,
        })
        save(ART/'models'/f'{self.name}.model.json',{
            'name':self.name,'texture_size':SIZE,'texture':str(texture_path.relative_to(ROOT)).replace('\\','/'),
            'bones':self.bones,'cubes':self.cubes,
        })


def animation(model, name, length, loop, tracks):
    animators={}
    for bone, channels in tracks.items():
        keys=[]
        for channel, frames in channels.items():
            for time, values in frames:
                keys.append({'channel':channel,'time':time,'interpolation':'linear',
                             'data_points':[dict(zip(['x','y','z'],map(str,values)))],
                             'uuid':uid(model.name+'/'+name+'/'+bone+'/'+channel+'/'+str(time))})
        animators[uid(model.name+'/bone/'+bone)]={'name':bone,'type':'bone','keyframes':keys}
    return {'uuid':uid(model.name+'/'+name),'name':name,'loop':loop,'length':length,
            'snapping':24,'animators':animators}


def make_stand():
    m=Model('chaos_brewing_stand')
    m.bone('crystal',[8,19,8]);m.bone('halo',[8,19,8])
    m.cube('netherite_foundation',[0,0,0],[16,2,16],'netherite')
    m.cube('gilded_border',[1,2,1],[15,3,15],'gold')
    m.cube('obsidian_slab',[2,3,2],[14,4,14],'obsidian')
    for x in [0,13]:
        for z in [0,13]:
            m.cube(f'corner_{x}_{z}',[x,0,z],[x+3,3,z+3],'netherite')
            m.cube(f'corner_rune_{x}_{z}',[x+1,3,z+1],[x+2,3.5,z+2],'rune')
    for i,x in enumerate([3,7.5,12]):
        m.cube('front_rune_'+str(i),[x,.5,-.05],[x+1,1.5,.05],'rune')
    m.cube('main_pillar',[7,4,7],[9,16,9],'netherite')
    for y in [6,10,14]:m.cube('pillar_band_'+str(y),[6.5,y,6.5],[9.5,y+1,9.5],'gold')
    m.cube('left_arm',[3,9,7],[8,10,8],'gold')
    m.cube('right_arm',[8,9,7],[13,10,8],'gold')
    m.cube('rear_arm',[7.5,9,8],[8.5,10,13],'gold')
    for i,(x,z,mat) in enumerate([(3,6,'rune'),(13,6,'pink'),(8,13,'amber')]):
        m.cube('cup_'+str(i),[x-2,9,z-2],[x+2,10,z+2],'gold')
        m.cube('cup_liquid_'+str(i),[x-1.5,10,z-1.5],[x+1.5,10.25,z+1.5],mat)
        for dx in [-1.75,1.75]:
            m.cube(f'holder_{i}_{dx}',[x+dx-.25,10,z-1.75],[x+dx+.25,13,z+1.75],'netherite')
        m.bone('vial_'+str(i),[x,11,z])
        m.cube('vial_liquid_'+str(i),[x-1,11,z-1],[x+1,14,z+1],mat,'vial_'+str(i))
        for dx in [-1,1]:
            for dz in [-1,1]:
                m.cube(f'glass_{i}_{dx}_{dz}',[x+dx-.2,11,z+dz-.2],[x+dx+.2,14.5,z+dz+.2],'glass','vial_'+str(i))
        m.cube('neck_'+str(i),[x-.5,14.5,z-.5],[x+.5,15.5,z+.5],'glass','vial_'+str(i))
        m.cube('stopper_'+str(i),[x-.75,15.5,z-.75],[x+.75,16,z+.75],'cork','vial_'+str(i))
    for i,(y,w) in enumerate([(15.5,1),(16.5,2),(17.5,3),(18.5,4),(19.5,3),(20.5,2),(21.5,1)]):
        m.cube('crystal_step_'+str(i),[8-w/2,y,8-w/2],[8+w/2,y+1,8+w/2],'crystal','crystal')
    m.cube('crystal_core',[7.5,18,5.95],[8.5,19.5,6.05],'rune','crystal')
    for x in [3,12.5]:
        m.cube('ritual_column_'+str(x),[x,4,12],[x+.5,22,12.5],'netherite')
        m.cube('column_light_'+str(x),[x-.5,22,11.5],[x+1,24,13],'rune')
    for i,(a,b) in enumerate([([3,19,3],[13,19.5,3.5]),([3,19,12.5],[13,19.5,13]),([3,19,3.5],[3.5,19.5,12.5]),([12.5,19,3.5],[13,19.5,12.5])]):
        m.cube('halo_rail_'+str(i),a,b,'gold','halo')
    tracks={'crystal':{'rotation':[(0,[0,0,0]),(4,[0,360,0])],
                       'position':[(0,[0,0,0]),(1,[0,.5,0]),(2,[0,0,0]),(3,[0,-.5,0]),(4,[0,0,0])]},
            'halo':{'rotation':[(0,[0,0,0]),(4,[0,-360,0])]}}
    for i in range(3):tracks['vial_'+str(i)]={'position':[(0,[0,0,0]),(1,[0,.3,0]),(2,[0,0,0]),(3,[0,-.3,0]),(4,[0,0,0])]}
    m.write('block',[animation(m,'brewing',4,'loop',tracks)])
    native={'parent':'minecraft:block/block','ambientocclusion':False,'textures':{'0':'forbidden_brews:block/'+m.name,'particle':'forbidden_brews:block/chaos_stone'},
            'elements':[{'from':c['from'],'to':c['to'],'faces':{
                face:{'texture':'#0','uv':[round(v*16/SIZE,5) for v in uv]} for face,uv in c['faces'].items()}} for c in m.cubes]}
    save(ASSETS/'models/block/chaos_brewing_stand.json',native)
    save(ASSETS/'models/item/chaos_brewing_stand.json',{'parent':'forbidden_brews:block/chaos_brewing_stand',
        'display':{'gui':{'rotation':[30,225,0],'translation':[0,-2,0],'scale':[.55,.55,.55]},
                   'ground':{'translation':[0,3,0],'scale':[.35,.35,.35]},
                   'fixed':{'rotation':[0,180,0],'scale':[.6,.6,.6]}}})
    save(ASSETS/'blockstates/chaos_brewing_stand.json',{'variants':{'':{'model':'forbidden_brews:block/chaos_brewing_stand'}}})
    particle=Image.new('RGB',(16,16));rng=random.Random('chaos_stone')
    for y in range(16):
        for x in range(16):
            shade=rng.choice([-8,-4,0,4,8]);particle.putpixel((x,y),(57+shade,52+shade,63+shade))
    particle.save(ASSETS/'textures/block/chaos_stone.png')
    # Animated magic is supported by vanilla's texture atlas. Geometry animation
    # in the Blender/Blockbench preview is an art reference for a future renderer.
    sheet=Image.new('RGBA',(SIZE,SIZE*24))
    magical=[c for c in m.cubes if c['material'] in ['rune','crystal','pink','amber']]
    for frame in range(24):
        im=m.texture.copy()
        for c in magical:
            for rect in c['faces'].values():
                x0,y0,x1,y1=rect;y0,y1=sorted((y0,y1))
                for y in range(y0,y1):
                    for x in range(x0,x1):
                        rgb=im.getpixel((x,y))[:3]
                        pulse=1+.12*math.sin(math.tau*frame/24+(y-y0)*1.2)
                        if (x+y-frame//2)%7==0:pulse+=.22
                        im.putpixel((x,y),tuple(min(255,round(a*pulse)) for a in rgb)+(255,))
        sheet.paste(im,(0,SIZE*frame))
    target=ASSETS/'textures/block/chaos_brewing_stand.png'
    m.texture.save(ART/'models/chaos_brewing_stand.texture.png')
    sheet.save(target)
    save(Path(str(target)+'.mcmeta'),{'animation':{'frametime':2,'width':SIZE,'height':SIZE,'interpolate':False}})
    return m


def make_brute():
    m=Model('steve_brute')
    for name,origin in [('body',[0,15,0]),('head',[0,34,0]),('left_arm',[-12,31,0]),('right_arm',[12,31,0]),('left_leg',[-5,14,0]),('right_leg',[5,14,0])]:
        m.bone(name,origin,'body' if name in ['head','left_arm','right_arm'] else 'root')
    m.cube('torn_shirt',[-10,15,-5],[10,34,5],'shirt','body','shirt')
    m.cube('neck',[-4,32,-4],[4,36,4],'skin','head')
    m.cube('steve_head',[-6,34,-6],[6,46,6],'skin','head','face')
    m.cube('hair_cap',[-6.2,43,-6.2],[6.2,46.2,6.2],'hair','head')
    m.cube('hair_back',[-6.1,38,5.9],[6.1,43,6.3],'hair','head')
    m.cube('left_brow',[-5,40.7,-6.4],[-1.5,41.5,-5.9],'hair','head')
    m.cube('right_brow',[1.5,40.7,-6.4],[5,41.5,-5.9],'hair','head')
    m.cube('belt',[-10.2,14,-5.2],[10.2,16,5.2],'belt','body')
    m.cube('belt_buckle',[-1.5,14,-5.5],[1.5,16,-5.2],'gold','body')
    for side,sign in [('left',-1),('right',1)]:
        arm=side+'_arm';leg=side+'_leg'
        x=sign*13
        m.cube(side+'_upper_arm',[x-4.5,20,-4.5],[x+4.5,33,4.5],'skin',arm)
        m.cube(side+'_sleeve',[x-4.7,28,-4.7],[x+4.7,33.2,4.7],'shirt',arm)
        m.cube(side+'_forearm',[x-5,9,-5],[x+5,22,5],'skin',arm)
        m.cube(side+'_fist',[x-6,5,-7],[x+6,15,5],'skin',arm,'knuckles')
        m.cube(side+'_stone_bracer',[x-5.5,15,-5.5],[x+5.5,22,5.5],'stone',arm,'cracks')
        m.cube(side+'_shoulder',[x-5.5,31,-5.5],[x+5.5,35,5.5],'stone',arm,'cracks')
        outer=x+sign*5
        m.cube(side+'_shoulder_shard',[outer-1.5,34,-1.5],[outer+1.5,38,1.5],'stone',arm,'cracks')
        for k in range(3):
            m.cube(side+'_knuckle_'+str(k),[x-4.5+k*3,9,-8],[x-2+k*3,12,-6.8],'stone',arm,'cracks')
        lx=sign*5
        m.cube(side+'_trousers',[lx-4.5,1,-4.5],[lx+4.5,15,4.5],'pants',leg,'pants')
        m.cube(side+'_boot',[lx-4.7,0,-5.5],[lx+4.7,4,4.7],'boot',leg)
        m.cube(side+'_knee',[lx-3,6,-5],[lx+3,10,-4.3],'stone',leg,'cracks')
    idle={'body':{'position':[(0,[0,0,0]),(1,[0,.3,0]),(2,[0,0,0])]},
          'left_arm':{'rotation':[(0,[0,0,-2]),(1,[0,0,-4]),(2,[0,0,-2])]},
          'right_arm':{'rotation':[(0,[0,0,2]),(1,[0,0,4]),(2,[0,0,2])]}}
    walk={}
    for name,sign in [('left_arm',1),('right_arm',-1),('left_leg',-1),('right_leg',1)]:
        walk[name]={'rotation':[(0,[sign*24,0,0]),(.5,[-sign*24,0,0]),(1,[sign*24,0,0])]}
    walk['body']={'position':[(0,[0,0,0]),(.25,[0,.6,0]),(.5,[0,0,0]),(.75,[0,.6,0]),(1,[0,0,0])]}
    smash={
        'body':{'rotation':[(0,[0,0,0]),(.5,[-15,0,0]),(.85,[28,0,0]),(1.15,[12,0,0]),(1.8,[0,0,0])]},
        'left_arm':{'rotation':[(0,[0,0,-3]),(.5,[-145,0,-12]),(.72,[-160,0,-12]),(.85,[-50,0,-4]),(1.15,[-20,0,-3]),(1.8,[0,0,-3])]},
        'right_arm':{'rotation':[(0,[0,0,3]),(.5,[-145,0,12]),(.72,[-160,0,12]),(.85,[-50,0,4]),(1.15,[-20,0,3]),(1.8,[0,0,3])]},
        'head':{'rotation':[(0,[0,0,0]),(.5,[-15,0,0]),(.85,[10,0,0]),(1.8,[0,0,0])]}}
    m.write('entity',[animation(m,'idle',2,'loop',idle),animation(m,'walk',1,'loop',walk),animation(m,'smash',1.8,'once',smash)])
    # Native Mojang-mapped model layer, with 1 texel per model unit. Runtime
    # registration and player transformation are separate from this art asset.
    lines=['package io.github.magersers.forbiddenbrews.client;',
           'import net.minecraft.client.model.geom.ModelPart;',
           'import net.minecraft.client.model.geom.PartPose;',
           'import net.minecraft.client.model.geom.builders.*;',
           'public final class BruteModelLayer {',
           '    private BruteModelLayer() {}',
           '    public static LayerDefinition create() {',
           '        MeshDefinition mesh = new MeshDefinition();',
           '        PartDefinition root = mesh.getRoot();']
    for bone,b in m.bones.items():
        if bone=='root': continue
        p=m.bones[b['parent']]['origin']; o=b['origin']
        # Entity model Y points down, origin at ground 24; our model Y points up.
        offs=[o[0]-p[0],-(o[1]-p[1]),o[2]-p[2]]
        if b['parent']=='root':offs[1]+=24
        cubes=[c for c in m.cubes if c['bone']==bone]
        builder='CubeListBuilder.create()'
        for c in cubes:
            a,cend=c['from'],c['to'];dim=[cend[i]-a[i] for i in range(3)]
            coord=[a[0]-o[0],o[1]-cend[1],a[2]-o[2]]
            builder+=f'\n            .texOffs({c["uv"][0]}, {c["uv"][1]}).addBox('+', '.join(f'{v:.3f}F' for v in coord+dim)+')'
        lines.append(f'        PartDefinition {bone} = {b["parent"]}.addOrReplaceChild("{bone}", {builder}, PartPose.offset('+', '.join(f'{v:.3f}F' for v in offs)+'));')
    lines+=['        return LayerDefinition.create(mesh, 256, 256);','    }','}']
    p=ART/'java/BruteModelLayer.java';p.parent.mkdir(parents=True,exist_ok=True);p.write_text('\n'.join(lines)+'\n',encoding='utf-8')
    return m


if __name__=='__main__':
    for model in [make_stand(),make_brute()]:
        print(model.name, len(model.cubes), 'cubes;',len(model.bones),'bones')
