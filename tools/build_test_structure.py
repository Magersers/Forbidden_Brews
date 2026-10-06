"""Small empty GameTest template in vanilla compressed NBT format."""
import gzip
import struct
from pathlib import Path

def string(s):
    b=s.encode();return struct.pack('>H',len(b))+b

def tag(t,name,payload):return bytes([t])+string(name)+payload

root=b'\x0a\x00\x00'
root+=tag(3,'DataVersion',struct.pack('>i',3465))
root+=tag(9,'size',b'\x03'+struct.pack('>i',3)+struct.pack('>iii',4,4,4))
root+=tag(9,'palette',b'\x0a'+struct.pack('>i',1)+tag(8,'Name',string('minecraft:air'))+b'\x00')
root+=tag(9,'blocks',b'\x0a'+struct.pack('>i',0))
root+=tag(9,'entities',b'\x0a'+struct.pack('>i',0))+b'\x00'
out=Path(__file__).resolve().parents[1]/'integration/forge/src/main/resources/data/forbidden_brews/structures/empty.nbt'
out.parent.mkdir(parents=True,exist_ok=True)
out.write_bytes(gzip.compress(root,mtime=0))
print('GameTest template ready')
