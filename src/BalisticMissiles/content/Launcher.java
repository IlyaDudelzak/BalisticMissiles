package BalisticMissiles.content;

import arc.Core;
import arc.Events;
import arc.Graphics;
import arc.audio.Sound;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.ObjectMap;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Scaling;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.game.EventType;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.Sector;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.campaign.LaunchPad;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Iterator;

public class Launcher extends Block {
    public float launchTime = 1.0F;
    public Sound launchSound;
    public TextureRegion lightRegion;
    public TextureRegion podRegion;
    public Color lightColor;
    public ObjectMap<Item, Integer> ammoTypes = new OrderedMap();

    public Launcher(String name) {
        super(name);
        this.launchSound = Sounds.none;
        this.lightColor = Color.valueOf("eab678");
        this.solid = true;
        this.update = true;
        this.hasItems = true;
        this.itemCapacity = 1;
        this.configurable = true;

        this.flags = EnumSet.of(new BlockFlag[]{BlockFlag.launchPad});
    }
    public void ammo(Object... objects) {
        this.ammoTypes = OrderedMap.of(objects);
    }
    public void setStats() {
        super.setStats();
        this.stats.add(Stat.launchTime, this.launchTime / 60.0F, StatUnit.seconds);
    }

    public void setBars() {
        super.setBars();
        this.addBar("progress", (LaunchPadBuild build) -> {
            return new Bar(() -> {
                return Core.bundle.get("bar.launchcooldown");
            }, () -> {
                return Pal.ammo;
            }, () -> {
                float clamp = Mathf.clamp(build.launchCounter / this.launchTime);
                return clamp;
            });
        });
    }

    public boolean outputsItems() {
        return false;
    }

    public class LaunchPadBuild extends Building {
        public float launchCounter;

        public LaunchPadBuild() {
        }
        public int acceptStack(Item item, int amount, Teamc source) {
            Integer type = (Integer) Launcher.this.ammoTypes.get(item);
//            return type == null ? 0 : Math.min(Launcher.this.itemCapacity - this.items.get(item), amount);
            return 10;
        }

        public Graphics.Cursor getCursor() {
            return (Graphics.Cursor)(Vars.state.isCampaign() && !Vars.net.client() ? super.getCursor() : Graphics.Cursor.SystemCursor.arrow);
        }

        public boolean shouldConsume() {
            return this.launchCounter < Launcher.this.launchTime;
        }

        public double sense(LAccess sensor) {
            return sensor == LAccess.progress ? (double)Mathf.clamp(this.launchCounter / Launcher.this.launchTime) : super.sense(sensor);
        }

        public void draw() {
            super.draw();
            if (Launcher.this.lightRegion != null) {
                if (Launcher.this.lightRegion.found()) {
                    Draw.color(Launcher.this.lightColor);
                    float progress = this.launchCounter / Launcher.this.launchTime;
                    int steps = 3;
                    float step = 1.0F;

                    for(int i = 0; i < 4; ++i) {
                        for(int j = 0; j < steps; ++j) {
                            float alpha = Mathf.curve(progress, (float)j / (float)steps, ((float)j + 1.0F) / (float)steps);
                            float offset = -((float)j - 1.0F) * step;
                            Draw.color(Pal.metalGrayDark, Launcher.this.lightColor, alpha);
                            Draw.rect(Launcher.this.lightRegion, this.x + (float) Geometry.d8edge(i).x * offset, this.y + (float)Geometry.d8edge(i).y * offset, (float)(i * 90));
                        }
                    }

                    Draw.reset();
                }

                Draw.rect(Launcher.this.podRegion, this.x, this.y);
                Draw.reset();
            }
        }

        public void updateTile() {
            if ((this.launchCounter += this.edelta()) >= Launcher.this.launchTime) {
                Launcher.this.launchSound.at(this.x, this.y);
                MissileLaunchUnit entity = MissileLaunchUnit.create(
                        100,
                        100,
                        5,
                        podRegion
                );
                entity.set(this);
                entity.lifetime(120.0F);
                entity.team(this.team);
                entity.setTarget();
                entity.add();
                Fx.launchPod.at(this);
                Effect.shake(3.0F, 3.0F, this);
                this.launchCounter = 0.0F;
            }
        }

        public void display(Table table) {
            super.display(table);
            if (Vars.state.isCampaign() && !Vars.net.client() && this.team == Vars.player.team()) {
                table.row();
                table.label(() -> {
                    Sector dest = Vars.state.rules.sector == null ? null : Vars.state.rules.sector.info.getRealDestination();
                    return Core.bundle.format("launch.destination", new Object[]{dest != null && dest.hasBase() ? "[accent]" + dest.name() : Core.bundle.get("sectors.nonelaunch")});
                }).pad(4.0F).wrap().width(200.0F).left();
            }
        }

        public void buildConfiguration(Table table) {
            if (Vars.state.isCampaign() && !Vars.net.client()) {
                table.button(Icon.upOpen, Styles.cleari, () -> {
                    Vars.ui.planet.showSelect(Vars.state.rules.sector, (other) -> {
                        if (Vars.state.isCampaign() && other.planet == Vars.state.rules.sector.planet) {
                            Vars.state.rules.sector.info.destination = other;
                        }

                    });
                    this.deselect();
                }).size(40.0F);
            } else {
                this.deselect();
            }
        }

        public byte version() {
            return 1;
        }

        public void write(Writes write) {
            super.write(write);
            write.f(this.launchCounter);
        }

        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) {
                this.launchCounter = read.f();
            }

        }
    }

    abstract static class LaunchPayloadComp implements Drawc, Timedc, Teamc {
        float x;
        float y;
        transient Interval in = new Interval();

        LaunchPayloadComp() {
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
            TextureRegion var10000;
            if (var9 instanceof Launcher) {
                Launcher p = (Launcher)var9;
                var10000 = p.podRegion;
            } else {
                var10000 = Core.atlas.find("launcher");
            }

            TextureRegion region = var10000;
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

        float cx() {
            return this.x + this.fin(Interp.pow2In) * (12.0F + Mathf.randomSeedRange((long)(this.id() + 3), 4.0F));
        }

        float cy() {
            return this.y + this.fin(Interp.pow5In) * (100.0F + Mathf.randomSeedRange((long)(this.id() + 2), 30.0F));
        }

        public void update() {
            float r = 3.0F;
            if (this.in.get(4.0F - this.fin() * 2.0F)) {
                Fx.rocketSmoke.at(this.cx() + Mathf.range(r), this.cy() + Mathf.range(r), this.fin());
            }

        }
    }
}
