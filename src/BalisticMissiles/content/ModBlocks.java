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
                this.requirements(Category.crafting, ItemStack.with(new Object[]{Items.copper, 75, Items.lead, 30}));
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
            requirements(Category.turret, with(Items.copper, 35));
            ammo(
                    Items.copper,  new BasicBulletType(1f, 0){{
                        this.spawnUnit = ModUnits.penisMissile;
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
                this.requirements(Category.effect, ItemStack.with(new Object[]{Items.copper, 1}));
                this.size = 3;
                this.itemCapacity = 100;
                this.launchTime = 120.0F;
                this.alwaysUnlocked = true;
            }
        };
    }
}
