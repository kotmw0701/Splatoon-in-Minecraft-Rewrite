package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;

public class WaitRoomData {

	String room;
	String world;
	double x,y,z,yaw,pitch;
	BattleType type;
	boolean limitbreak;
	List<String> list = new ArrayList<>();
	BukkitRunnable task;

	public WaitRoomData(String room, FileConfiguration file) {
		this.room = room;
		this.world = file.getString("Room.World");
		this.x = file.getDouble("Room.X");
		this.y = file.getDouble("Room.Y");
		this.z = file.getDouble("Room.Z");
		this.yaw = file.getDouble("Room.Yaw");
		this.pitch = file.getDouble("Room.Pitch");
		this.list = file.getStringList("Room.SelectList");
		this.type = BattleType.valueOf(file.getString("Room.BattleType", BattleType.Turf_War.toString()));
		this.limitbreak = file.getBoolean("Room.LimitBreak", false);
	}

	public String getName() {return room;}

	public String getWorld() {return world;}

	public double getX() {return x;}

	public double getY() {return y;}

	public double getZ() {return z;}

	public double getYaw() {return yaw;}

	public double getPitch() {return pitch;}

	public List<String> getSelectList() {return list;}

	public BattleType getBattleType() {return type;}
	
	public boolean isLimitBreak() {return limitbreak;}

	public BukkitRunnable getTask() {return task;}

	public void setName(String room) {this.room = room;}

	public void setWorld(String world) {this.world = world;}

	public void setX(double x) {this.x = x;}

	public void setY(double y) {this.y = y;}

	public void setZ(double z) {this.z = z;}

	public void setYaw(double yaw) {this.yaw = yaw;}

	public void setPitch(double pitch) {this.pitch = pitch;}

	public void setSelectList(List<String> list) {this.list = list;}

	public void setBattleType(BattleType type) {this.type = type;}

	public void setTask(BukkitRunnable task) {this.task = task;}
}
