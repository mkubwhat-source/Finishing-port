import zipfile, json, io
from PIL import Image
NEW=zipfile.ZipFile('/home/claude/work/bf-port/.gradle/loom-cache/minecraftMaven/net/minecraft/minecraft-merged-aab4b66517/26.3/minecraft-merged-aab4b66517-26.3.jar')
OLD=zipfile.ZipFile('/tmp/claude-0/client-1.21.1.jar')
def img(z,p): return Image.open(io.BytesIO(z.read(p))).convert('RGBA')
woods=['oak','spruce','birch','jungle','acacia','dark_oak','mangrove','cherry','bamboo','crimson','warped']
R=json.load(open('/tmp/claude-0/sg/rects.json'))
def tf(k,x,y,w,h):
    return [(x,y),(w-1-x,y),(x,h-1-y),(w-1-x,h-1-y),(y,x),(h-1-y,x),(y,w-1-x),(h-1-y,w-1-x)][k]
def prect(r):
    x0,y0,x1,y1=r[:4]
    a,b=sorted([x0,x1]); c,d=sorted([y0,y1])
    return (int(a*2),int(c*2),int(b*2),int(d*2))
result={}
for kind,newfmt,oldfmt in [('sign','assets/minecraft/textures/block/%s_sign.png','assets/minecraft/textures/entity/signs/%s.png'),
                           ('hanging','assets/minecraft/textures/block/%s_hanging_sign.png','assets/minecraft/textures/entity/signs/hanging/%s.png')]:
    rects=sorted(set(prect(r) for r in R[kind]))
    pairs=[]
    for w in woods:
        try: pairs.append((w,img(NEW,newfmt%w),img(OLD,oldfmt%w)))
        except KeyError as e: print('missing',w,kind)
    mapping=[]
    for r in rects:
        x0,y0,x1,y1=r;w=x1-x0;h=y1-y0
        if w==0 or h==0: continue
        cands=None
        for (wood,N,O) in pairs:
            NP=N.load();OP=O.load();ow,oh=O.size
            cs=set()
            for k in range(8):
                sw,sh=(w,h) if k<4 else (h,w)
                for sx in range(ow-sw+1):
                    for sy in range(oh-sh+1):
                        if cands is not None and (k,sx,sy) not in cands: continue
                        ok=True
                        for x in range(w):
                            for y in range(h):
                                u,v=tf(k,x,y,w,h)
                                if NP[x0+x,y0+y]!=OP[sx+u,sy+v]: ok=False;break
                            if not ok:break
                        if ok: cs.add((k,sx,sy))
            cands=cs
        print(kind,r,len(cands),sorted(cands)[:4])
        mapping.append((r,sorted(cands)))
    result[kind]=mapping
json.dump(result,open('/tmp/claude-0/sg/signmap.json','w'))
