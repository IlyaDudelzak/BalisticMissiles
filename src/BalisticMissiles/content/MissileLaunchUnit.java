package BalisticMissiles.content;

import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import java.util.Iterator;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.core.World;
import mindustry.entities.EntityGroup;
import mindustry.entities.Units;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.type.Sector;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.storage.CoreBlock;

public class MissileLaunchUnit implements Drawc, Entityc, LaunchPayloadc, Posc, Teamc, Timedc{
    protected transient boolean added;
    public transient int id = EntityGroup.nextId();
    public transient Interval in = new Interval();
    protected transient int index__all = -1;
    protected transient int index__draw = -1;
    public float lifetime;
    public Seq<ItemStack> stacks = new Seq();
    public Team team;
    public float time;
    public float x;
    public float y;
    public float tx;
    public float ty;
    public float ep;
    public Building target;

    protected MissileLaunchUnit(float tx, float ty, float ep) {
        this.team = Team.derelict;
        this.tx = tx;
        this.ty = ty;
        this.ep = ep;
    }

    protected MissileLaunchUnit(float ep) {
        this.team = Team.derelict;
        this.ep = ep;
    }
    public Seq<ItemStack> stacks() {
        return this.stacks;
    }

    public Interval in() {
        return this.in;
    }

    public float lifetime() {
        return this.lifetime;
    }

    public float time() {
        return this.time;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public int classId() {
        return 15;
    }

    public int id() {
        return this.id;
    }

    public String toString() {
        return "LaunchPayload#" + this.id;
    }

    public Team team() {
        return this.team;
    }

    public void id(int id) {
        this.id = id;
    }

    public void in(Interval in) {
        this.in = in;
    }

    public void lifetime(float lifetime) {
        this.lifetime = lifetime;
    }

    public void setIndex__all(int index) {
        this.index__all = index;
    }

    public void setIndex__draw(int index) {
        this.index__draw = index;
    }

    public void stacks(Seq<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public void team(Team team) {
        this.team = team;
    }

    public void time(float time) {
        this.time = time;
    }

    public void x(float x) {
        this.x = x;
    }

    public void y(float y) {
        this.y = y;
    }

    public void read(Reads read) {
        short REV = read.s();
        if (REV == 0) {
            this.lifetime = read.f();
            int stacks_LENGTH = read.i();
            this.stacks.clear();

            for(int INDEX = 0; INDEX < stacks_LENGTH; ++INDEX) {
                ItemStack stacks_ITEM = TypeIO.readItems(read);
                if (stacks_ITEM != null) {
                    this.stacks.add(stacks_ITEM);
                }
            }

            this.team = TypeIO.readTeam(read);
            this.time = read.f();
            this.x = read.f();
            this.y = read.f();
            this.afterRead();
        } else {
            throw new IllegalArgumentException("Unknown revision '" + REV + "' for entity type 'LaunchPayloadComp'");
        }
    }

    public void write(Writes write) {
        write.s(0);
        write.f(this.lifetime);
        write.i(this.stacks.size);

        for(int INDEX = 0; INDEX < this.stacks.size; ++INDEX) {
            TypeIO.writeItems(write, (ItemStack)this.stacks.get(INDEX));
        }

        TypeIO.writeTeam(write, this.team);
        write.f(this.time);
        write.f(this.x);
        write.f(this.y);
    }

    public <T extends Entityc> T self() {
        return (T) this;
    }

    public <T> T as() {
        return (T) this;
    }

    public Building buildOn() {
        return Vars.world.buildWorld(this.x, this.y);
    }

    public boolean cheating() {
        return this.team.rules().cheat;
    }

    public boolean inFogTo(Team viewer) {
        return this.team != viewer && !Vars.fogControl.isVisible(viewer, this.x, this.y);
    }

    public boolean isAdded() {
        return this.added;
    }

    public boolean isLocal() {
        boolean var10000;
        label26: {
            if (this instanceof Unitc) {
                Unitc u = (Unitc)this;
                if (u.controller() == Vars.player) {
                    break label26;
                }
            }

            var10000 = false;
            return var10000;
        }

        var10000 = true;
        return var10000;
    }

    public boolean isNull() {
        return false;
    }

    public boolean isRemote() {
        boolean var10000;
        if (this instanceof Unitc) {
            Unitc u = (Unitc)this;
            if (u.isPlayer() && !this.isLocal()) {
                var10000 = true;
                return var10000;
            }
        }

        var10000 = false;
        return var10000;
    }

    public boolean onSolid() {
        Tile tile = this.tileOn();
        return tile == null || tile.solid();
    }

    public boolean serialize() {
        return true;
    }

    public float clipSize() {
        return Float.MAX_VALUE;
    }

    public float cx() {
        return this.x + this.fin(Interp.pow2In) * (12.0F + Mathf.randomSeedRange((long)(this.id() + 3), 4.0F));
    }

    public float cy() {
        return this.y + this.fin(Interp.pow5In) * (100.0F + Mathf.randomSeedRange((long)(this.id() + 2), 30.0F));
    }

    public float fin() {
        return this.time / this.lifetime;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public int tileX() {
        return World.toTile(this.x);
    }

    public int tileY() {
        return World.toTile(this.y);
    }

    public Block blockOn() {
        Tile tile = this.tileOn();
        return tile == null ? Blocks.air : tile.block();
    }

    public Tile tileOn() {
        return Vars.world.tileWorld(this.x, this.y);
    }

    public Floor floorOn() {
        Tile tile = this.tileOn();
        return tile != null && tile.block() == Blocks.air ? tile.floor() : (Floor)Blocks.air;
    }

    public CoreBlock.CoreBuild closestCore() {
        return Vars.state.teams.closestCore(this.x, this.y, this.team);
    }

    public CoreBlock.CoreBuild closestEnemyCore() {
        return Vars.state.teams.closestEnemyCore(this.x, this.y, this.team);
    }

    public CoreBlock.CoreBuild core() {
        return this.team.core();
    }

    public static MissileLaunchUnit create(float tx, float ty, float ep) {
        return new MissileLaunchUnit(tx, ty, ep);
    }

    public static MissileLaunchUnit create(float ep) {
        return new MissileLaunchUnit(ep);
    }
    public void setTarget(){
        this.target = this.closestEnemyCore();
    }
    public void setTarget(float tx, float ty){
        this.tx = tx;
        this.ty = ty;
    }

    public void add() {
        if (!this.added) {
            this.index__all = Groups.all.addIndex(this);
            this.index__draw = Groups.draw.addIndex(this);
            this.added = true;
        }
    }

    public void afterRead() {
    }

    public void draw() {
        float alpha = this.fout(Interp.pow5Out);
        float scale = (1.0F - alpha) * 1.3F + 1.0F;
        float cx = this.cx();
        float cy = this.cy();
        float rotation = this.fin() * (130.0F + Mathf.randomSeedRange((long)this.id(), 50.0F));
        Draw.z(110.001F);
        Draw.color(Pal.engine);
        float rad = 0.2F + this.fslope();
        Fill.light(cx, cy, 10, 25.0F * (rad + scale - 1.0F), Tmp.c2.set(Pal.engine).a(alpha), Tmp.c1.set(Pal.engine).a(0.0F));
        Draw.alpha(alpha);

        for(int i = 0; i < 4; ++i) {
            Drawf.tri(cx, cy, 6.0F, 40.0F * (rad + scale - 1.0F), (float)i * 90.0F + rotation);
        }

        Draw.color();
        Draw.z(129.0F);
        Block var9 = this.blockOn();
        Object var10000;
        if (var9 instanceof LaunchPad) {
            LaunchPad p = (LaunchPad)var9;
            var10000 = p.podRegion;
        } else {
            var10000 = Core.atlas.find("launchpod");
        }

        TextureRegion region = (TextureRegion) var10000;
        scale *= ((TextureRegion)region).scl();
        float rw = (float)((TextureRegion)region).width * scale;
        float rh = (float)((TextureRegion)region).height * scale;
        Draw.alpha(alpha);
        Draw.rect((TextureRegion)region, cx, cy, rw, rh, rotation);
        Tmp.v1.trns(225.0F, this.fin(Interp.pow3In) * 250.0F);
        Draw.z(116.0F);
        Draw.color(0.0F, 0.0F, 0.0F, 0.22F * alpha);
        Draw.rect((TextureRegion)region, cx + Tmp.v1.x, cy + Tmp.v1.y, rw, rh, rotation);
        Draw.reset();
    }

    public void remove() {
        if (this.added) {
            Building target = this.closestEnemyCore();
            if(target != null) {
                Unit entity = UnitTypes.crawler.create(this.team);
                entity.set(target);
                entity.add();
                Log.info("Spawned new crawler");
            }
            else {

                Log.err("Nowhere to spawn crawler");
            }
            Groups.all.removeIndex(this, this.index__all);
            this.index__all = -1;
            Groups.draw.removeIndex(this, this.index__draw);
            this.index__draw = -1;
            this.added = false;
            Log.info("MissileLaunchUnit disappeared.");
            if (Vars.state.isCampaign()) {
                Sector destsec = Vars.state.rules.sector.info.getRealDestination();
                if (this.team() == Vars.state.rules.defaultTeam && destsec != null && (destsec != Vars.state.rules.sector || Vars.net.client())) {
                    ItemSeq dest = new ItemSeq();
                    Iterator var3 = this.stacks.iterator();

                    while(var3.hasNext()) {
                        ItemStack stack = (ItemStack)var3.next();
                        dest.add(stack);
                        Vars.state.rules.sector.info.handleItemExport(stack);
                        Events.fire(new EventType.LaunchItemEvent(stack));
                    }

                    if (!Vars.net.client()) {
                        destsec.addItems(dest);
                    }
                }
            }

        }
    }

    public void set(Position pos) {
        this.set(pos.getX(), pos.getY());
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void trns(Position pos) {
        this.trns(pos.getX(), pos.getY());
    }

    public void trns(float x, float y) {
        this.set(this.x + x, this.y + y);
    }

    public void update() {
        float r = 3.0F;
        if (this.in.get(4.0F - this.fin() * 2.0F)) {
            Fx.rocketSmoke.at(this.cx() + Mathf.range(r), this.cy() + Mathf.range(r), this.fin());
        }

        this.time = Math.min(this.time + Time.delta, this.lifetime);
        if (this.time >= this.lifetime) {
            this.remove();
        }

    }
}
