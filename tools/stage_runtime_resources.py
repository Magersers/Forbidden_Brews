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
    write(data/'forbidden_brews'/('recipe' if modern else 'recipes')/'netherite_wart.json',{
        'type':'minecraft:crafting_shapeless','category':'misc',
        'ingredients':[{'item':'minecraft:nether_wart'},{'item':'minecraft:netherite_scrap'}],
        'result':{('id' if modern else 'item'):'forbidden_brews:netherite_wart','count':1}})
    loot={'type':'minecraft:block','pools':[{'rolls':1,'entries':[{
        'type':'minecraft:item','name':'forbidden_brews:chaos_brewing_stand'}],
        'conditions':[{'condition':'minecraft:survives_explosion'}]}]}
    write(data/'forbidden_brews'/('loot_table' if modern else 'loot_tables')/'blocks/chaos_brewing_stand.json',loot)
    mature={'condition':'minecraft:block_state_property','block':'forbidden_brews:netherite_wart','properties':{'age':'3'}}
    write(data/'forbidden_brews'/('loot_table' if modern else 'loot_tables')/'blocks/netherite_wart.json',{
        'type':'minecraft:block','pools':[{'rolls':1,'entries':[{'type':'minecraft:item','name':'forbidden_brews:netherite_wart',
        'functions':[{'function':'minecraft:set_count','count':{'type':'minecraft:uniform','min':2,'max':4},'conditions':[mature]},
        {'function':'minecraft:apply_bonus','enchantment':'minecraft:fortune','formula':'minecraft:uniform_bonus_count',
         'parameters':{'bonusMultiplier':1},'conditions':[mature]}]}],
        'conditions':[{'condition':'minecraft:survives_explosion'}]}]})
    for tag in ['mineable/pickaxe','needs_diamond_tool']:
        write(data/'minecraft/tags'/('block' if modern else 'blocks')/(tag+'.json'),{
            'replace':False,'values':['forbidden_brews:chaos_brewing_stand']})
    write(data/'forbidden_brews/tags'/('block' if modern else 'blocks')/'ores.json',{
        'replace':False,'values':['#minecraft:'+name+'_ores' for name in ['coal','iron','copper','gold','redstone','lapis','diamond','emerald']]+[
            'minecraft:nether_quartz_ore','minecraft:ancient_debris',
            {'id':'#forge:ores','required':False},{'id':'#c:ores','required':False}]})
    advancement={'parent' :'minecraft:recipes/root','criteria':{
        'has_netherite':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{
            'items':(['minecraft:netherite_ingot'] if modern else ['minecraft:netherite_ingot'])}]}},
        'has_the_recipe':{'trigger':'minecraft:recipe_unlocked','conditions':{'recipe':'forbidden_brews:chaos_brewing_stand'}}},
        'requirements':[['has_netherite','has_the_recipe']],
        'rewards':{'recipes':['forbidden_brews:chaos_brewing_stand']}}
    write(data/'forbidden_brews'/('advancement' if modern else 'advancements')/'recipes/chaos_brewing_stand.json',advancement)
    write(data/'forbidden_brews'/('advancement' if modern else 'advancements')/'recipes/netherite_wart.json',{
        'parent':'minecraft:recipes/root','criteria':{
            'has_scrap':{'trigger':'minecraft:inventory_changed','conditions':{'items':[{'items':['minecraft:netherite_scrap']}]}},
            'has_recipe':{'trigger':'minecraft:recipe_unlocked','conditions':{'recipe':'forbidden_brews:netherite_wart'}}},
        'requirements':[['has_scrap','has_recipe']],
        'rewards':{'recipes':['forbidden_brews:netherite_wart']}})
    write(res/'pack.mcmeta',{'pack':{'pack_format':34 if modern else 15,'description':'Forbidden Brews models and recipes'}})
    write(res/'forbidden_brews.mixins.json',{'required':True,'minVersion':'0.8',
        'package':'io.github.magersers.forbiddenbrews.mixin','compatibilityLevel':'JAVA_21' if modern else 'JAVA_17',
        'refmap':'forbidden_brews.refmap.json','mixins':['LootingMixin','FortuneMixin','OreDropsMixin','CreeperPotionMixin'],'client':['client.InversionMixin'],'injectors':{'defaultRequire':1}})

for locale,name in [('ru_ru','Стойка хаоса'),('en_us','Chaos Brewing Stand')]:
    ru=locale=='ru_ru'
    lang={'block.forbidden_brews.chaos_brewing_stand':name,
        'block.forbidden_brews.netherite_wart':'Незеритовый нарост' if ru else 'Netherite Wart',
        'effect.forbidden_brews.fortune':'Удача' if ru else 'Fortune',
        'effect.forbidden_brews.looting':'Добыча' if ru else 'Looting',
        'effect.forbidden_brews.homeward':'Возвращение домой' if ru else 'Homeward',
        'message.forbidden_brews.home_unsafe':'Рядом с точкой возрождения нет безопасного места' if ru else 'No safe landing near your spawn',
        'effect.forbidden_brews.wild_teleport':'Случайная телепортация' if ru else 'Random Teleport',
        'effect.forbidden_brews.ore_double':'Двойная руда' if ru else 'Double Ore',
        'effect.forbidden_brews.inversion':'Переворот' if ru else 'Inversion',
        'effect.forbidden_brews.creeper':'Сердце крипера' if ru else 'Creeper Heart',
        'effect.forbidden_brews.hot_pick':'Горячая кирка' if ru else 'Hot Pick',
        'message.forbidden_brews.teleport_search':'Ищем безопасную точку телепортации...' if ru else 'Searching for a safe teleport destination...',
        'message.forbidden_brews.teleport_unsafe':'Не удалось найти безопасную точку. Телепортация отменена' if ru else 'No safe destination found. Teleport cancelled',
        'gui.forbidden_brews.wart':'Нарост' if ru else 'Wart',
        'gui.forbidden_brews.components':'Компоненты' if ru else 'Ingredients',
        'gui.forbidden_brews.base':'Основа' if ru else 'Base',
        'gui.forbidden_brews.brewing':'Варка...' if ru else 'Brewing...',
        'gui.forbidden_brews.result':'Результат' if ru else 'Result',
        'gui.forbidden_brews.fuel':'Топливо' if ru else 'Fuel',
        'gui.forbidden_brews.choose':'Выбрать' if ru else 'Choose',
        'gui.forbidden_brews.picker':'Выберите зелье' if ru else 'Choose a potion',
        'gui.forbidden_brews.no_recipes':'Нужна вода или питьевое зелье' if ru else 'Insert water or a drinkable potion',
        'gui.forbidden_brews.ready':'Готово' if ru else 'Ready'}
    for family,base in [('fortune','Удача' if ru else 'Fortune'),('looting','Добыча' if ru else 'Looting'),('homeward','Домой' if ru else 'Homeward'),('wild_teleport','Случайная телепортация' if ru else 'Random Teleport'),('ore_double','Двойная руда' if ru else 'Double Ore'),('hot_pick','Горячая кирка' if ru else 'Hot Pick'),('inversion','Переворот' if ru else 'Inversion'),('creeper','Сердце крипера' if ru else 'Creeper Heart')]:
        for level in range(1,4 if family in ['fortune','looting','inversion'] else 2):
            for splash in [False,True]:
                key=f'{family}_{level}_{"splash" if splash else "drink"}'
                label=('Взрывное зелье ' if splash else 'Зелье ') if ru else ('Splash Potion of ' if splash else 'Potion of ')
                lang['item.forbidden_brews.'+key]=label+base+(' '+['I','II','III'][level-1] if family in ['fortune','looting','inversion'] else '')
    write(ROOT/'common/src/main/resources/assets/forbidden_brews/lang'/(locale+'.json'),lang)

for target,modloader,loadermin,loadername,mc,minimum in [
    ('forge-1.20.1','javafml','47','forge','1.20.1','47.4.0'),
    ('neoforge-1.21.1','javafml','4','neoforge','1.21.1','21.1.252')]:
    toml=f"""modLoader="{modloader}"
loaderVersion="[{loadermin},)"
license="All Rights Reserved"
[[mods]]
modId="forbidden_brews"
version="0.4.2"
displayName="Forbidden Brews"
authors="Magersers"
description='''Chaos brewing, eight potion families and renewable Netherite Wart.'''
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
"""+('[[mixins]]\nconfig="forbidden_brews.mixins.json"\n' if loadername=='neoforge' else '')
    p=ROOT/'platforms'/target/'src/main/resources/META-INF'/('neoforge.mods.toml' if loadername=='neoforge' else 'mods.toml')
    p.parent.mkdir(parents=True,exist_ok=True);p.write_text(toml,encoding='utf-8')

write(ROOT/'platforms/fabric-1.21.1/src/main/resources/fabric.mod.json',{
    'schemaVersion':1,'id':'forbidden_brews','version':'0.4.2','name':'Forbidden Brews',
    'description':'Chaos brewing, eight potion families and renewable Netherite Wart.',
    'authors':['Magersers'],'license':'All Rights Reserved','environment':'*',
    'accessWidener':'forbidden_brews.accesswidener',
    'entrypoints':{'main':['io.github.magersers.forbiddenbrews.ForbiddenBrews'],
        'client':['io.github.magersers.forbiddenbrews.client.ForbiddenBrewsClient']},
    'mixins':['forbidden_brews.mixins.json'],
    'depends':{'fabricloader':'>=0.16.14','fabric-api':'>=0.116.17','minecraft':'1.21.1','java':'>=21'}})
print('Resources staged for Forge 1.20.1, NeoForge/Fabric 1.21.1')
