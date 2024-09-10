package BalisticMissiles.content;

import arc.math.Interp;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.effect.WrapEffect;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.type.unit.MissileUnitType;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.draw.*;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.with;

public class ModBlocks {
    public static Block
            //crafting
            penisPress,
            //turrets
            penisTurret,
            BasicLauncher
    ;

    public static void load(){
        penisPress = new GenericCrafter("penis-press") {
            {
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.copper, 1500, Items.lead, 1500}));
                this.craftEffect = Fx.pulverizeMedium;
                this.outputItem = new ItemStack(ModItems.penis, 1);
                this.craftTime = 600.0F;
                this.size = 2;
                this.itemCapacity = 32;
                this.hasItems = true;
                this.consumeItem(Items.sporePod, 16);
            }
        };
        penisTurret = new ItemTurret("penis-turret"){{
            requirements(Category.turret, with(new Object[]{Items.copper, 1500, Items.lead, 1500}));
            ammo(
                    Items.copper,  new BasicBulletType(1f, 0){{
                        this.spawnUnit = new MissileUnitType("penis-missile") {
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
                    }}
            );

            shoot = new ShootBarrel();
            drawer = new DrawTurret(){{
                parts.add(new RegionPart("-barrel-"){{
                    progress = PartProgress.constant(0);
                    under = true;
                }});
            }};

            shootY = 3f;
            size = 2;
            reload = 24f;
            range = 11000;
            shootCone = 15f;
            ammoUseEffect = Fx.casing1;
            health = 250;
            inaccuracy = 2f;
            rotateSpeed = 10f;
            coolant = consumeCoolant(0.1f);
            researchCostMultiplier = 0.05f;

            limitRange();
        }};
        BasicLauncher = new Launcher("launcher") {
            {
                this.requirements(Category.turret, ItemStack.with(new Object[]{Items.copper, 4000}));
                this.size = 3;
                this.itemCapacity = 100;
                this.launchTime = 120.0F;
                this.alwaysUnlocked = true;
//                ammo(Items.surgeAlloy, 5);
            }
        };
    }
}
