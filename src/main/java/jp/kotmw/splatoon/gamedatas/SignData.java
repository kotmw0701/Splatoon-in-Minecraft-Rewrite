package jp.kotmw.splatoon.gamedatas;

import jp.kotmw.splatoon.gamedatas.DataStore.SignType;

import org.bukkit.configuration.file.FileConfiguration;

public class SignData {
	String filename;
	String name;
	String world;
	int x,y,z;
	SignType type;

	public SignData(String filename, FileConfiguration file) {
		this.filename = filename;
		this.name = file.getString("Name");
		this.world = file.getString("World");
		this.x = file.getInt("X");
		this.y = file.getInt("Y");
		this.z = file.getInt("Z");
		this.type = SignType.valueOf(file.getString("Type"));
	}

	public String getFilename() {return filename;}

	public String getName() {return name;}

	public String getWorld() {return world;}

	public int getX() {return x;}

	public int getY() {return y;}

	public int getZ() {return z;}

	public SignType getType() {return type;}

	public void setFilename(String filename) {this.filename = filename;}

	public void setName(String name) {this.name = name;}

	public void setWorld(String world) {this.world = world;}

	public void setX(int x) {this.x = x;}

	public void setY(int y) {this.y = y;}

	public void setZ(int z) {this.z = z;}

	public void setType(SignType type) {this.type = type;}


}
