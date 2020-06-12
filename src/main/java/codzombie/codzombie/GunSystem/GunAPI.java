package codzombie.codzombie.GunSystem;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GunAPI {

    public static ArrayList<Material> IgnoreMaterial = new ArrayList<>();

    public static Random random = new Random();

    public static NumberFormat nf = NumberFormat.getInstance();

    public static void SetDefaultData(){
        nf.setMaximumFractionDigits(2);
        IgnoreMaterial.add(Material.GLASS);
        IgnoreMaterial.add(Material.BARRIER);
        IgnoreMaterial.add(Material.AIR);
    }

    //線追蹤尋找生物
    public static LineTraceFindEntityData LineTraceFindEntity(Location Start, Location End, double SceneRadius, boolean hasCollide) {
        Entity FinalEnt = null;
        double Distance = Start.distance(End); //兩點間的距離
        Location FindLocation = null;
        Vector AddVec = End.clone().subtract(Start).toVector().normalize(); //起點增加方向
        boolean isCompleteFindEnt = false;
        for (double i = 0; i < Distance; i += SceneRadius) {
            Location CurrentLocation = Start.clone().add(AddVec.clone().multiply(i)); //目前檢測的位置
            List<Entity> CurrentSceneEnt = (List<Entity>) Start.getWorld().getNearbyEntities(CurrentLocation, SceneRadius, SceneRadius, SceneRadius); //此地點檢測生物
            Material Type = CurrentLocation.getBlock().getType();
            if (hasCollide && !IgnoreMaterial.contains(Type)){
                FindLocation = CurrentLocation;
                break;
            }
            for (Entity SceneEnt : CurrentSceneEnt) { //目前檢測到的生物放入陣列
                if (!SceneEnt.isDead() && SceneEnt instanceof LivingEntity && !(SceneEnt instanceof Player) && !(SceneEnt instanceof ArmorStand)) {
                    FinalEnt = SceneEnt;
                    FindLocation = CurrentLocation;
                    isCompleteFindEnt = true;
                    break;
                }
            }

            if (isCompleteFindEnt){
                break;
            }
        }

        if (FinalEnt == null){
            FindLocation = End;
        }

        return new LineTraceFindEntityData(FinalEnt, FindLocation);
    }

    //線追蹤尋找遮擋物
    public static boolean LineTraceFindCover(Location Start, Location End){
        double Distance = Start.distance(End);
        Vector CheckDirection = End.subtract(Start).toVector().normalize();
        for (double d = 0; d < Distance; d += 1){
            final Location CheckLocation = Start.add(CheckDirection.multiply(d));
            if (CheckLocation.getBlock().getType() != Material.AIR){
                return true;
            }
        }
        return false;
    }

    //線追蹤尋找生物
    public static LineTraceDataNoCollide LineTraceFindEntity(Location Start, Location End, double SceneRadius) {
        ArrayList<Entity> AllSceneEntity = new ArrayList<>();
        ArrayList<Location> AllSceneLocation = new ArrayList<>();
        double Distance = Start.distance(End); //兩點間的距離
        Vector AddVec = Start.clone().subtract(End).toVector().normalize(); //起點增加方向
        for (int i = 0; i < Distance; i++) {
            Location CurrentLocation = Start.clone().subtract(AddVec.clone().multiply(i)); //目前檢測的位置
            List<Entity> CurrentSceneEnt = (List<Entity>) Start.getWorld().getNearbyEntities(CurrentLocation, SceneRadius, SceneRadius, SceneRadius); //此地點檢測生物
            for (Entity SceneEnt : CurrentSceneEnt) { //目前檢測到的生物放入陣列
                if (!AllSceneEntity.contains(SceneEnt)) {
                    if (SceneEnt instanceof Monster && !(SceneEnt instanceof ArmorStand)) {
                        AllSceneEntity.add(SceneEnt);
                        AllSceneLocation.add(CurrentLocation);
                    }
                }
            }
        }
        return new LineTraceDataNoCollide(AllSceneEntity, AllSceneLocation);
    }

    //線追蹤尋找位置
    public static ArrayList<Location> LineTraceFindLocationNoCollision(Location Start, Location End) {
        ArrayList<Location> AllLocation = new ArrayList<>(); //存放所有位置
        double Distance = Start.distance(End); //兩點間的距離
        Vector AddVec = Start.clone().subtract(End).toVector().normalize(); //起點增加方向
        for (int i = 0; i < Distance; i++) {
            Location CurrentLocation = Start.clone().subtract(AddVec.clone().multiply(i)); //目前檢測的位置
            AllLocation.add(CurrentLocation);
        }
        return AllLocation;
    }

    //線追蹤尋找位置
    public static ArrayList<Location> getLineAllLocation(Location Start, Location End) {
        ArrayList<Location> AllLocation = new ArrayList<>();
        double Distance = Start.distance(End); //兩點間的距離
        Vector AddVec = Start.clone().subtract(End).toVector().normalize(); //起點增加方向
        for (int i = 0; i < Distance; i++) {
            Location CurrentLocation = Start.clone().subtract(AddVec.clone().multiply(i)); //目前檢測的位置
            if (!AllLocation.contains(CurrentLocation)) {
                AllLocation.add(CurrentLocation);
            }
        }
        return AllLocation;
    }

    //取的圓形座標
    public static ArrayList<Location> getCircleLocation(Location Center, double Radius, int Amount){

        double PI = Math.PI * 2 / Amount;

        ArrayList<Location> AllLocation = new ArrayList<>();

        for (int i = 0; i < Amount; i++){
            double Angle = PI * i;
            double X = Center.getX() + Radius * Math.sin(Angle);
            double Z = Center.getZ() + Radius * Math.cos(Angle);
            AllLocation.add(new Location(Center.getWorld(), X, Center.getY(), Z));
        }

        return AllLocation;
    }

    //是否有爆頭
    public static boolean isHeadShot(Location ShotLocation, LivingEntity CheckEnt){

        final double SceneHeadShotRadius = 1.1;

        if (CheckEnt.getEyeLocation().distance(ShotLocation) < SceneHeadShotRadius){
            return true;
        }
        return false;
    }

}
