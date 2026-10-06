"""Package only ordinary loader JARs; reject accidental test/dev artifacts."""
import hashlib
import json
import shutil
import zipfile
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
VERSION='0.2.0'
out=ROOT/'releases'/VERSION;out.mkdir(parents=True,exist_ok=True)
manifest=[]
for target in ['forge-1.20.1','neoforge-1.21.1','fabric-1.21.1']:
    name='forbidden-brews-'+target+'-'+VERSION+'.jar'
    source=ROOT/'platforms'/target/'build/libs'/name
    with zipfile.ZipFile(source) as archive:
        paths=archive.namelist()
        assert not any('ChaosStandTests' in p or 'VisualScenario' in p or 'VisualClientMixin' in p or '/structures/empty.nbt' in p or 'visual.mixins.json' in p for p in paths), 'Test artifacts in production JAR'
        assert 'assets/forbidden_brews/models/block/chaos_brewing_stand.json' in paths
        assert 'assets/forbidden_brews/textures/entity/steve_brute.png' in paths
        recipe_path='data/forbidden_brews/'+('recipes' if target=='forge-1.20.1' else 'recipe')+'/chaos_brewing_stand.json'
        recipe=json.loads(archive.read(recipe_path))
        assert recipe['key']['N']['item']=='minecraft:netherite_ingot'
        wart_recipe=recipe_path.replace('chaos_brewing_stand.json','netherite_wart.json')
        assert json.loads(archive.read(wart_recipe))['result']['count']==1
        for resource in ['textures/gui/chaos_stand.png','blockstates/netherite_wart.json','models/item/looting_3_splash.json']:
            assert 'assets/forbidden_brews/'+resource in paths
        assert 'forbidden_brews.mixins.json' in paths
        if target!='neoforge-1.21.1':
            assert 'forbidden_brews.refmap.json' in paths
        assert any(p.endswith('/ChaosMenu.class') for p in paths)
        assert any(p.endswith('/client/ChaosScreen.class') for p in paths)
        assert ('META-INF/mods.toml' if target.startswith('forge') else 'META-INF/neoforge.mods.toml' if target.startswith('neoforge') else 'fabric.mod.json') in paths
    dest=out/name;shutil.copyfile(source,dest)
    sha=hashlib.sha256(dest.read_bytes()).hexdigest()
    manifest.append({'file':name,'bytes':dest.stat().st_size,'sha256':sha,'target':target})
    print(name,dest.stat().st_size,sha)
(out/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
