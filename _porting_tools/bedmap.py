from PIL import Image
import itertools, json
old=Image.open('/tmp/claude-0/old/assets/minecraft/textures/entity/bed/red.png').convert('RGBA')
OP=old.load()
V='/tmp/claude-0/vb/'
dest={
 'head_up':'red_bed_head_up','head_east':'red_bed_head_east','head_west':'red_bed_head_west',
 'head_north':'bed_head_north','down':'bed_down',
 'foot_up':'red_bed_foot_up','foot_east':'red_bed_foot_east','foot_west':'red_bed_foot_west','foot_south':'red_bed_foot_south'}
# rects (x0,y0,x1,y1) used per texture, from template_bed_head/foot uvs
legs=[(0,13,3,16),(3,13,6,16),(6,13,9,16),(7,13,10,16),(10,13,13,16),(13,13,16,16)]
rects={
 'head_up':[(0,0,16,16)],'down':[(0,0,16,16)],'foot_up':[(0,0,16,16)],
 'head_east':[(0,7,16,13)]+legs,'head_west':[(0,7,16,13)]+legs,'head_north':[(0,7,16,13)]+legs,
 'foot_east':[(0,7,16,13)]+legs,'foot_west':[(0,7,16,13)]+legs,'foot_south':[(0,7,16,13)]+legs}
def tf(k,x,y,w,h):
    # dihedral transforms mapping dest local (x,y) in w*h to source local coords (sw,sh dims)
    if k==0: return x,y
    if k==1: return w-1-x,y
    if k==2: return x,h-1-y
    if k==3: return w-1-x,h-1-y
    if k==4: return y,x
    if k==5: return h-1-y,x
    if k==6: return y,w-1-x
    if k==7: return h-1-y,w-1-x
res={}
for name,f in dest.items():
    D=Image.open(V+f+'.png').convert('RGBA').load()
    for r in rects[name]:
        x0,y0,x1,y1=r; w=x1-x0; h=y1-y0
        cands=[]
        for k in range(8):
            sw,sh=(w,h) if k<4 else (h,w)
            for sx in range(0,64-sw+1):
                for sy in range(0,64-sh+1):
                    ok=True
                    for x in range(w):
                        for y in range(h):
                            p=D[x0+x,y0+y]
                            if p[3]==0: continue
                            u,v=tf(k,x,y,w,h)
                            q=OP[sx+u,sy+v]
                            if q!=p: ok=False;break
                        if not ok:break
                    if ok: cands.append((k,sx,sy))
        res[(name,r)]=cands
        print(name,r,len(cands),cands[:6])
json.dump({f'{a}|{b}':c for (a,b),c in res.items()},open('/tmp/claude-0/bedmap.json','w'))
