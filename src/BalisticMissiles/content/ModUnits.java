package BalisticMissiles.content;

import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.ExplosionBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.FlarePart;
import mindustry.gen.Unit;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.unit.MissileUnitType;

public class ModUnits {
    public static UnitType

    penisMissile

    ;

    public static void load(){
        penisMissile = new MissileUnitType("penis-missile") {
            {
                this.trailColor = this.engineColor = Pal.techBlue;
                this.engineSize = 1.75F;
                this.engineLayer = 110.0F;
                this.speed = 3.7F;
                this.maxRange = 6.0F;
                this.lifetime = 900.0F;
                this.outlineColor = Pal.darkOutline;
                this.health = 550.0F;
                this.lowAltitude = true;
                this.parts.add(new FlarePart() {
                    {
                        this.progress = PartProgress.life.slope().curve(Interp.pow2In);
                        this.radius = 0.0F;
                        this.radiusTo = 35.0F;
                        this.stroke = 3.0F;
                        this.rotation = 45.0F;
                        this.y = -5.0F;
                        this.followRotation = true;
                    }
                });
                this.weapons.add(new Weapon() {
                    {
                        this.shootCone = 360.0F;
                        this.mirror = false;
                        this.reload = 1.0F;
                        this.shootOnDeath = true;
                        this.bullet = new ExplosionBulletType(5000.0F, 120.0F) {
                            {
                                this.shootEffect = new MultiEffect(new Effect[]{Fx.massiveExplosion, new WrapEffect(Fx.dynamicSpikes, Pal.techBlue, 24.0F), new WaveEffect() {
                                    {
                                        this.colorFrom = this.colorTo = Pal.techBlue;
                                        this.sizeTo = 120.0F;
                                        this.lifetime = 30.0F;
                                        this.strokeFrom = 4.0F;
                                    }
                                }});
                            }
                        };
                    }
                });
            }
        };
    }
}
