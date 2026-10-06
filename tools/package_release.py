"""Package only ordinary loader JARs; reject accidental test/dev artifacts."""
import hashlib
import json
import shutil
import zipfile
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
out=ROOT/'releases/0.1.0';out.mkdir(parents=True,exist_ok=True)
manifest=[]
for target in ['forge-1.20.1','neoforge-1.21.1','fabric-1.21.1']:
    name='forbidden-brews-'+target+'-0.1.0.jar'
    source=ROOT/'platforms'/target/'build/libs'/name
    with zipfile.ZipFile(source) as archive:
        paths=archive.namelist()
        assert not any('ChaosStandTests' in p or '/structures/empty.nbt' in p for p in paths), 'Test artifacts in production JAR'
        assert 'assets/forbidden_brews/models/block/chaos_brewing_stand.json' in paths
        assert 'assets/forbidden_brews/textures/entity/steve_brute.png' in paths
        recipe_path='data/forbidden_brews/'+('recipes' if target=='forge-1.20.1' else 'recipe')+'/chaos_brewing_stand.json'
        recipe=json.loads(archive.read(recipe_path))
        assert recipe['key']['N']['item']=='minecraft:netherite_ingot'
        assert ('META-INF/mods.toml' if target.startswith('forge') else 'META-INF/neoforge.mods.toml' if target.startswith('neoforge') else 'fabric.mod.json') in paths
    dest=out/name;shutil.copyfile(source,dest)
    sha=hashlib.sha256(dest.read_bytes()).hexdigest()
    manifest.append({'file':name,'bytes':dest.stat().st_size,'sha256':sha,'target':target})
    print(name,dest.stat().st_size,sha)
(out/'manifest.json').write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
