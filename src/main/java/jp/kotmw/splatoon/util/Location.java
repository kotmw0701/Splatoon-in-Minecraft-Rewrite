package jp.kotmw.splatoon.util;

import org.bukkit.Bukkit;

public class Location {

	private String world;
	private double x,y,z;
	private float yaw,pitch;

	public Location(String world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = 0.0f;
		this.pitch = 0.0f;
	}

	public Location(String world, double x, double y, double z, float yaw, float pitch) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public String getWorld() {return world;}

	public double getX() {return x;}

	public double getY() {return y;}

	public double getZ() {return z;}
	
	public int getBlockX() {return (int) x;}

	public int getBlockY() {return (int) y;}

	public int getBlockZ() {return (int) z;}

	public float getYaw() {return yaw;}

	public float getPitch() {return pitch;}

	public void setWorld(String world) {this.world = world;}

	public void setX(double x) {this.x = x;}

	public void setY(double y) {this.y = y;}

	public void setZ(double z) {this.z = z;}

	public void setYaw(float yaw) {this.yaw = yaw;}

	public void setPitch(float pitch) {this.pitch = pitch;}

	public org.bukkit.Location convertLocation() {
		return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
	}
}
