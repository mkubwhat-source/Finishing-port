import json
from PIL import Image
M=json.load(open('/tmp/claude-0/sg/signmap.json'))
def tf(k,x,y,w,h):
    return [(x,y),(w-1-x,y),(x,h-1-y),(w-1-x,h-1-y),(y,x),(h-1-y,x),(y,w-1-x),(h-1-y,w-1-x)][k]
T='/home/claude/work/bf-port/src/main/resources/assets/bountifulfares/textures/'
for wood in ['hoary','walnut']:
    for kind,src,dst in [('sign',T+'entity/signs/%s.png',T+'block/%s_sign.png'),('hanging',T+'entity/signs/hanging/%s.png',T+'block/%s_hanging_sign.png')]:
        O=Image.open(src%wood).convert('RGBA'); OP=O.load()
        assert O.size==(64,32), O.size
        N=Image.new('RGBA',(32,32),(0,0,0,0)); NP=N.load()
        for r,c in M[kind]:
            x0,y0,x1,y1=r;w=x1-x0;h=y1-y0
            k,sx,sy=c[0]
            for x in range(w):
                for y in range(h):
                    u,v=tf(k,x,y,w,h); NP[x0+x,y0+y]=OP[sx+u,sy+v]
        N.save(dst%wood); print('wrote',dst%wood)
