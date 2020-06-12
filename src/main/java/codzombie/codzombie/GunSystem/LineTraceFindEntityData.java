package codzombie.codzombie.GunSystem;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class LineTraceFindEntityData {

    public Entity FindEnt;

    public Location FindEntLocation;

    public LineTraceFindEntityData(Entity Ent, Location FindLocation){
        FindEnt = Ent;
        this.FindEntLocation = FindLocation;
    }

}
