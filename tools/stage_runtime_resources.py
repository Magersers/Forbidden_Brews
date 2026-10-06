"""Shared assets plus version-specific datapack directory/schema names."""
import json
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]

def write(path,data):
    path.parent.mkdir(parents=True,exist_ok=True)
    path.write_text(json.dumps(data,ensure_ascii=False,indent=2)+'\n',encoding='utf-8')

for version,modern in [('1.20.1',False),('1.21.1',True)]:
    res=ROOT/'versions'/version/'src/main/resources'
    data=res/'data'
    recipe={'type':'minecraft:crafting_shaped','category':'misc','pattern':['ABA',' N ','ORO'],
            'key':{c:{'item':'minecraft:'+item} for c,item in {
                'A':'amethyst_shard','B':'brewing_stand','N':'netherite_ingot',
                'O':'obsidian','R':'blaze_rod'}.items()},
            'result':{('id' if modern else 'item'):'forbidden_brews:chaos_brewing_stand','count':1}}
    write(data/'forbidden_brews'/('recipe' if modern else 'recipes')/'chaos_brewing_stand.json',recipe)
    loot={'type':'minecraft:block','pools':[{'rolls':1,'entries':[{
        'type':'minecraft:item','name':'forbidden_brews:chaos_brewing_stand'}],
        'conditions':[{'condition':'minecraft:survives_explosion'}]}]}
    write(data/'forbidden_brews'/('loot_table' if modern else 'loot_tables')/'blocks/chaos_brewing_stand.json',loot)
    for tag in ['mineable/pickaxe','needs_diamond_tool']:
        write(data/'minecraft/tags'/('block' if modern else 'blocks')/(tag+'.json'),{
            'replace':False,'values':['forbidden_brews:chaos_brewing_stand']})
    advancement={'parent':'minecraft:recipes/root','criteria':{
        'has_netherite':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{
            'items':(['minecraft:netherite_ingot'] if modern else ['minecraft:netherite_ingot'])}]}},
        'has_the_recipe':{'trigger':'minecraft:recipe_unlocked','conditions':{'recipe':'forbidden_brews:chaos_brewing_stand'}}},
        'requirements':[['has_netherite','has_the_recipe']],
        'rewards':{'recipes':['forbidden_brews:chaos_brewing_stand']}}
    write(data/'forbidden_brews'/('advancement' if modern else 'advancements')/'recipes/chaos_brewing_stand.json',advancement)
    write(res/'pack.mcmeta',{'pack':{'pack_format':34 if modern else 15,'description':'Forbidden Brews models and recipes'}})

for locale,name in [('ru_ru','Стойка хаоса'),('en_us','Chaos Brewing Stand')]:
    write(ROOT/'common/src/main/resources/assets/forbidden_brews/lang'/(locale+'.json'),{
        'block.forbidden_brews.chaos_brewing_stand':name})

for target,modloader,loadermin,loadername,mc,minimum in [
    ('forge-1.20.1','javafml','47','forge','1.20.1','47.4.0'),
    ('neoforge-1.21.1','javafml','4','neoforge','1.21.1','21.1.252')]:
    toml=f"""modLoader="{modloader}"
loaderVersion="[{loadermin},)"
license="All Rights Reserved"
[[mods]]
modId="forbidden_brews"
version="0.1.0"
displayName="Forbidden Brews"
authors="Magersers"
description='''Chaos Brewing Stand and original animated potion art. Transformation effects are in development.'''
[[dependencies.forbidden_brews]]
modId="{loadername}"
{'type="required"' if loadername=='neoforge' else 'mandatory=true'}
versionRange="[{minimum},)"
ordering="NONE"
side="BOTH"
[[dependencies.forbidden_brews]]
modId="minecraft"
{'type="required"' if loadername=='neoforge' else 'mandatory=true'}
versionRange="[{mc}]"
ordering="NONE"
side="BOTH"
"""
    p=ROOT/'platforms'/target/'src/main/resources/META-INF'/('neoforge.mods.toml' if loadername=='neoforge' else 'mods.toml')
    p.parent.mkdir(parents=True,exist_ok=True);p.write_text(toml,encoding='utf-8')

write(ROOT/'platforms/fabric-1.21.1/src/main/resources/fabric.mod.json',{
    'schemaVersion':1,'id':'forbidden_brews','version':'0.1.0','name':'Forbidden Brews',
    'description':'Chaos Brewing Stand and original animated potion art. Transformation effects are in development.',
    'authors':['Magersers'],'license':'All Rights Reserved','environment':'*',
    'accessWidener':'forbidden_brews.accesswidener',
    'entrypoints':{'main':['io.github.magersers.forbiddenbrews.ForbiddenBrews']},
    'depends':{'fabricloader':'>=0.16.14','fabric-api':'>=0.116.17','minecraft':'1.21.1','java':'>=21'}})
print('Resources staged for Forge 1.20.1, NeoForge/Fabric 1.21.1')
