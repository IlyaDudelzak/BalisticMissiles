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
