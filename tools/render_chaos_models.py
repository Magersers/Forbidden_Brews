"""Blender authoring/render/export of the same cube geometry used in Minecraft.
blender -b -t 8 --python tools/render_chaos_models.py -- --target steve_brute --render animation
"""
import argparse
import json
import math
import sys
import struct
from pathlib import Path
import bpy
from mathutils import Vector

ROOT=Path(__file__).resolve().parents[1]
p=argparse.ArgumentParser()
p.add_argument('--target',choices=['chaos_brewing_stand','steve_brute'],required=True)
p.add_argument('--render',choices=['still','animation','none'],default='still')
args=p.parse_args(sys.argv[sys.argv.index('--')+1:] if '--' in sys.argv else [])
data=json.loads((ROOT/'art/models'/f'{args.target}.model.json').read_text(encoding='utf-8'))
project=json.loads((ROOT/'art/blockbench'/f'{args.target}.bbmodel').read_text(encoding='utf-8'))
bpy.ops.object.select_all(action='SELECT');bpy.ops.object.delete(use_global=False)
s=bpy.context.scene
s.render.engine='BLENDER_EEVEE';s.render.resolution_x=640;s.render.resolution_y=640
s.render.resolution_percentage=100;s.render.image_settings.file_format='PNG'
s.render.image_settings.color_mode='RGBA';s.render.film_transparent=True
s.render.fps=12;s.frame_start=1;s.frame_end=48
s.world.use_nodes=True;s.world.node_tree.nodes['Background'].inputs['Strength'].default_value=.55
s.world.node_tree.nodes['Background'].inputs['Color'].default_value=(.21,.25,.35,1)
s.view_settings.view_transform='Standard';s.view_settings.look='Medium High Contrast' if False else 'None'
s.view_settings.exposure=0
try:s.eevee.taa_render_samples=32
except AttributeError:pass
texture_path=ROOT/data['texture']
if args.target=='chaos_brewing_stand':texture_path=ROOT/'art/models/chaos_brewing_stand.texture.png'
image=bpy.data.images.load(str(texture_path));image.pack()
materials={}
for kind in set(c['material'] for c in data['cubes']):
    mat=bpy.data.materials.new(kind);mat.use_nodes=True
    nodes=mat.node_tree.nodes;bsdf=nodes.get('Principled BSDF')
    tex=nodes.new('ShaderNodeTexImage');tex.image=image;tex.interpolation='Closest'
    mat.node_tree.links.new(tex.outputs['Color'],bsdf.inputs['Base Color'])
    bsdf.inputs['Roughness'].default_value=.85
    if kind in ['rune','crystal','pink','amber','lava']:
        mat.node_tree.links.new(tex.outputs['Color'],bsdf.inputs['Emission Color'])
        bsdf.inputs['Emission Strength'].default_value=.5
    materials[kind]=mat


def vec(v):return Vector((v[0]/16,v[2]/16,v[1]/16))


bones={}
for name,b in data['bones'].items():
    ob=bpy.data.objects.new(name,None);s.collection.objects.link(ob)
    if b['parent']:
        ob.parent=bones[b['parent']]
        ob.location=vec(b['origin'])-vec(data['bones'][b['parent']]['origin'])
    else:ob.location=vec(b['origin'])
    bones[name]=ob

for c in data['cubes']:
    origin=vec(data['bones'][c['bone']]['origin'])
    a,b=vec(c['from'])-origin,vec(c['to'])-origin
    x0,y0,z0=a;x1,y1,z1=b
    faceverts={
        'north':[(x0,y0,z0),(x1,y0,z0),(x1,y0,z1),(x0,y0,z1)],
        'south':[(x1,y1,z0),(x0,y1,z0),(x0,y1,z1),(x1,y1,z1)],
        'east':[(x1,y0,z0),(x1,y1,z0),(x1,y1,z1),(x1,y0,z1)],
        'west':[(x0,y1,z0),(x0,y0,z0),(x0,y0,z1),(x0,y1,z1)],
        'up':[(x0,y0,z1),(x1,y0,z1),(x1,y1,z1),(x0,y1,z1)],
        'down':[(x0,y1,z0),(x1,y1,z0),(x1,y0,z0),(x0,y0,z0)],
    }
    vertices=[];faces=[]
    for side,verts in faceverts.items():
        start=len(vertices);vertices+=verts;faces.append(tuple(range(start,start+4)))
    mesh=bpy.data.meshes.new(c['name']);mesh.from_pydata(vertices,[],faces);mesh.update()
    mesh.materials.append(materials[c['material']])
    uv=mesh.uv_layers.new(name='Minecraft UV')
    for poly,side in zip(mesh.polygons,faceverts):
        u0,v0,u1,v1=c['faces'][side]
        coords=[(u0,v1),(u1,v1),(u1,v0),(u0,v0)]
        for index,(u,v) in zip(poly.loop_indices,coords):uv.data[index].uv=(u/256,1-v/256)
    ob=bpy.data.objects.new(c['name'],mesh);s.collection.objects.link(ob);ob.parent=bones[c['bone']]


def insert(name,channel,time,values):
    ob=bones[name];frame=1+round(time*12)
    if channel=='rotation':
        ob.rotation_euler=tuple(math.radians(v) for v in [values[0],-values[2],values[1]])
        ob.keyframe_insert(data_path='rotation_euler',frame=frame)
    else:
        b=data['bones'][name];origin=vec(b['origin'])
        if b['parent']:origin-=vec(data['bones'][b['parent']]['origin'])
        ob.location=origin+vec(values)
        ob.keyframe_insert(data_path='location',frame=frame)


# Store all editable animation clips as actions. Preview: idle, then heavy smash.
for anim in project['animations']:
    for name,ob in bones.items():ob.animation_data_clear()
    for animator in anim['animators'].values():
        for key in animator['keyframes']:
            dp=key['data_points'][0];values=[float(dp[k]) for k in ['x','y','z']]
            insert(animator['name'],key['channel'],key['time'],values)
    for name,ob in bones.items():
        if ob.animation_data and ob.animation_data.action:
            action=ob.animation_data.action;action.name=args.target+'/'+anim['name']+'/'+name
            action.use_fake_user=True
            track=ob.animation_data.nla_tracks.new();track.name=anim['name']
            strip=track.strips.new(anim['name'],1,action);strip.name=anim['name']
            strip.mute=True

for name,ob in bones.items():
    if ob.animation_data:ob.animation_data.action=None
    ob.rotation_euler=(0,0,0)
    b=data['bones'][name];ob.location=vec(b['origin'])
    if b['parent']:ob.location-=vec(data['bones'][b['parent']]['origin'])

selected=project['animations'][0] if args.target=='chaos_brewing_stand' else project['animations'][2]
for animator in selected['animators'].values():
    for key in animator['keyframes']:
        dp=key['data_points'][0]
        insert(animator['name'],key['channel'],key['time'],[float(dp[k]) for k in ['x','y','z']])
if args.target=='steve_brute':
    # One strike, hold the recovery, second gentle breath: four second preview.
    for name in ['body','head','left_arm','right_arm']:
        ob=bones[name]
        for frame in [30,49]:
            ob.rotation_euler=(0,0,0);ob.keyframe_insert(data_path='rotation_euler',frame=frame)
    # Show the full silhouette from changing angles without rotating through the back.
    for frame,angle in [(1,-12),(12,0),(24,10),(36,0),(49,-12)]:
        bones['root'].rotation_euler.z=math.radians(angle)
        bones['root'].keyframe_insert(data_path='rotation_euler',frame=frame)
for action in bpy.data.actions:
    for layer in action.layers:
        for strip in layer.strips:
            for bag in strip.channelbags:
                for fc in bag.fcurves:
                    for key in fc.keyframe_points:key.interpolation='LINEAR'

clip='brewing' if args.target=='chaos_brewing_stand' else 'smash_preview'


def light(name,location,power,color,size):
    l=bpy.data.lights.new(name,'AREA');l.energy=power;l.color=color;l.size=size
    o=bpy.data.objects.new(name,l);s.collection.objects.link(o);o.location=location
    o.rotation_euler=(Vector((0,0,1))-o.location).to_track_quat('-Z','Y').to_euler()


if args.target=='chaos_brewing_stand':loc=(2.4,-3.5,2.4);target=(.5,.5,.8);scale=2.05
else:loc=(4,-7,3.1);target=(0,0,1.7);scale=5.2
cam=bpy.data.cameras.new('Camera');cam.type='ORTHO';cam.ortho_scale=scale
o=bpy.data.objects.new('Camera',cam);s.collection.objects.link(o);o.location=loc
o.rotation_euler=(Vector(target)-o.location).to_track_quat('-Z','Y').to_euler();s.camera=o
light('Warm front',(-3,-4,6),400,(1,.86,.73),4)
light('Cold edge',(4,2,5),550,(.6,.77,1),3)
light('Fill',(2,-4,2),170,(.8,.9,1),4)
s.frame_set(1)
out=ROOT/'art/blender';out.mkdir(parents=True,exist_ok=True)
bpy.ops.wm.save_as_mainfile(filepath=str(out/(args.target+'.blend')),compress=True)
bpy.ops.object.select_all(action='DESELECT')
for ob in s.objects:
    if ob.type in ['MESH','EMPTY']:ob.select_set(True)
glb_path=ROOT/'art/models'/(args.target+'.glb')
bpy.ops.export_scene.gltf(filepath=str(glb_path),export_format='GLB',
                         use_selection=True,export_animations=True,export_frame_range=True,
                         export_force_sampling=True,export_animation_mode='ACTIONS')
# Empty-node actions export individually in Blender. Merge their independent
# sampler/channel lists into one playable clip, preserving all buffer accessors.
raw=glb_path.read_bytes();json_length=struct.unpack_from('<I',raw,12)[0]
gltf=json.loads(raw[20:20+json_length]);tail=raw[20+json_length:]
merged={'name':clip,'samplers':[],'channels':[]}
for anim in gltf.get('animations',[]):
    offset=len(merged['samplers']);merged['samplers']+=anim['samplers']
    for channel in anim['channels']:
        merged['channels'].append(channel | {'sampler':channel['sampler']+offset})
assert merged['channels'],'No animation channels exported'
gltf['animations']=[merged]
packed=json.dumps(gltf,separators=(',',':')).encode('utf-8')
packed+=b' '*((-len(packed))%4)
glb_path.write_bytes(struct.pack('<4sII',b'glTF',2,20+len(packed)+len(tail))+
                     struct.pack('<II',len(packed),0x4E4F534A)+packed+tail)
folder=ROOT/'renders'/('checks' if args.render=='still' else 'frames/'+args.target)
folder.mkdir(parents=True,exist_ok=True)
if args.render=='still':
    s.render.filepath=str(folder/(args.target+'.png'));bpy.ops.render.render(write_still=True)
elif args.render=='animation':
    s.render.filepath=str(folder/'frame_');bpy.ops.render.render(animation=True)
print('MODEL_READY',args.target,flush=True)
