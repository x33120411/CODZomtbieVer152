package codzombie.codzombie.GunSystem;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

public class LineTraceDataNoCollide {

    public ArrayList<Entity> SceneEnt;

    public ArrayList<Location> SceneLocation;

    public LineTraceDataNoCollide(ArrayList<Entity> Ent, ArrayList<Location> SceneLocation){
        SceneEnt = Ent;
        this.SceneLocation = SceneLocation;
    }

}
