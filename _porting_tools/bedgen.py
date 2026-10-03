from PIL import Image
import json, sys
def tf(k,x,y,w,h):
    return [(x,y),(w-1-x,y),(x,h-1-y),(w-1-x,h-1-y),(y,x),(h-1-y,x),(y,w-1-x),(h-1-y,w-1-x)][k]
m=json.load(open('/tmp/claude-0/bedmap.json'))
S=(0,7,16,13); L=lambda a:(a,13,a+3,16)
used={'head_up':[(0,0,16,16)],'down':[(0,0,16,16)],'foot_up':[(0,0,16,16)],
 'head_east':[S,L(7),L(10),L(13)],'head_west':[S,L(0),L(3),L(6)],'head_north':[S,L(0),L(3),L(10),L(13)],
 'foot_east':[S,L(0),L(3),L(6)],'foot_west':[S,L(7),L(10),L(13)],'foot_south':[S,L(0),L(3),L(10),L(13)]}
def gen(src):
    P=Image.open(src).convert('RGBA').load()
    outs={}
    for name,rs in used.items():
        im=Image.new('RGBA',(16,16),(0,0,0,0)); O=im.load()
        for r in rs:
            c=m[f'{name}|{r}']
            k,sx,sy=c[0]
            x0,y0,x1,y1=r;w=x1-x0;h=y1-y0
            for x in range(w):
                for y in range(h):
                    u,v=tf(k,x,y,w,h); O[x0+x,y0+y]=P[sx+u,sy+v]
        outs[name]=im
    return outs
# self-check against vanilla
dest={'head_up':'red_bed_head_up','head_east':'red_bed_head_east','head_west':'red_bed_head_west','head_north':'bed_head_north','down':'bed_down','foot_up':'red_bed_foot_up','foot_east':'red_bed_foot_east','foot_west':'red_bed_foot_west','foot_south':'red_bed_foot_south'}
red=gen('/tmp/claude-0/old/assets/minecraft/textures/entity/bed/red.png')
bad=0
for n,f in dest.items():
    V=Image.open('/tmp/claude-0/vb/'+f+'.png').convert('RGBA').load(); G=red[n].load()
    for x in range(16):
        for y in range(16):
            a=V[x,y]; b=G[x,y]
            if a[3]==0 and b[3]==0: continue
            if a!=b: bad+=1
    print(n,'mismatch so far',bad)
if bad==0:
    out='/home/claude/work/bf-port/src/main/resources/assets/bountifulfares/textures/block/'
    coir=gen('/home/claude/work/bf-port/src/main/resources/assets/bountifulfares/textures/entity/bed/coir_bed.png')
    for n,im in coir.items():
        im.save(out+'coir_bed_'+n+'.png'); print('wrote',n)
