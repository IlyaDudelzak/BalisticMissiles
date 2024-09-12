package BalisticMissiles;

import BalisticMissiles.content.ModBlocks;
import BalisticMissiles.content.ModItems;
import BalisticMissiles.content.ModUnits;
import mindustry.mod.*;

public class BalisticMissilesMod extends Mod{
    public ModBlocks modBlocks;
    public ModItems modItems;
    public ModUnits modUnits;

    public BalisticMissilesMod(){
        modBlocks = new ModBlocks();
        modItems = new ModItems();
        modUnits = new ModUnits();
    }

    @Override
    public void loadContent(){
        ModBlocks.load();
        ModItems.load();
        ModUnits.load();
    }

}
