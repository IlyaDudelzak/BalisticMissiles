package BalisticMissiles.content;

import arc.graphics.Color;
import mindustry.type.Item;

public class MissileItem extends Item{
    public int val;
    public MissileItem(String name, Color color, int val){
        super(name, color);
        this.val = val;
    }
}
