package BalisticMissiles.content;


import arc.graphics.Color;
import mindustry.type.Item;

public class Explosive extends Item {
    public float explosivePower;

    public Explosive(String name, Color color){
        super(name, color);
        explosivePower = 1f;
    }
}
