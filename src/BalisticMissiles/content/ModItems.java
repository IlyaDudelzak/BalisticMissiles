package BalisticMissiles.content;

import arc.graphics.*;
import mindustry.type.*;

public class ModItems {
    public static Item

    penis, bomb, nuke, hBomb

    ;

    public static void load(){
        penis = new MissileItem("penis", Color.valueOf("a26a1e"), 3){{
            this.hardness = 1;
            this.cost = 10f;
        }};
        bomb = new Explosive("bomb", Color.valueOf("aaaaaa")) {{
            this.cost = 2.5f;
            this.explosiveness = 1f;
        }};
        nuke = new Explosive("nuke", Color.valueOf("aaaaaa")) {{
            this.cost = 5f;
            this.explosivePower = 5;
        }};
        hBomb = new Explosive("h-bomb", Color.valueOf("aaaaaa")) {{
            this.cost = 10f;
            this.explosivePower = 25;
        }};
    }
}
