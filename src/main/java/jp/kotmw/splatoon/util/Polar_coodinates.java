package jp.kotmw.splatoon.util;

import org.bukkit.Location;
import org.bukkit.World;

public class Polar_coodinates implements Cloneable{
	private World world;
	private double radius; //r
	private double theta; //θ ※ラジアン
	private double phi; //φ ※ラジアン
	
	public Polar_coodinates(World world, double radius, double theta, double phi) {
		this.world = world;
		this.radius = radius;
		this.theta = theta;
		this.phi = phi;
	}
	
	public Polar_coodinates(Location bukkitlocation) {
		this.radius = bukkitlocation.distance(new Location(bukkitlocation.getWorld(), 0, 0, 0));
		double x = bukkitlocation.getX(), y = bukkitlocation.getY(), z = bukkitlocation.getZ();
		this.theta = Math.acos(z/Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2)));
		this.phi = Math.atan(y/x);
	}
	
	public Location convertLocation() {
		double x = radius*Math.sin(theta)*Math.cos(phi);
		double y = radius*Math.sin(theta)*Math.sin(phi);
		double z = radius*Math.cos(theta);
		return new Location(world, x, y, z);
	}
	
	public Location rotation_Xaxis(double newtheta) {
		Location loc = convertLocation();
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		return new Location(world, x, y*Math.cos(newtheta)-z*Math.sin(newtheta), y*Math.sin(newtheta)+z*Math.cos(newtheta));
	}

	public Location rotation_Yaxis(double newtheta) {
		Location loc = convertLocation();
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		return new Location(world, x*Math.cos(newtheta)+z*Math.sin(newtheta), y, (-x)*Math.sin(newtheta)+z*Math.cos(newtheta));
	}
	
	/**
	 * converLocationとなんも変わらないよ((
	 * ただ回転量が増えるだけ
	 * 
	 * @param newtheta 移動分の角度
	 */
	public Location rotation_Zaxis(double newtheta) {
		Location loc = convertLocation();
		double x = loc.getX(), y = loc.getY(), z = loc.getZ();
		return new Location(world, x*Math.cos(newtheta)-y*Math.sin(newtheta), x*Math.sin(newtheta)+y*Math.cos(newtheta), z);
	}
	
	public Polar_coodinates add(Polar_coodinates pc) {
		this.radius += pc.radius;
		this.theta += pc.theta;
		this.phi += pc.phi;
		return this;
	}
	
	public Polar_coodinates add(double radius, double theta, double phi) {
		this.radius += radius;
		this.theta += theta;
		this.phi += phi;
		return this;
	}
	
	public World getWorld() {return world;}
	
	public double getRadius() {return radius;}
	
	public double getTheta() {return theta;}
	
	public double getPhi() {return phi;}

	public void setWorld(World world) {this.world = world;}

	public void setRadius(double radius) {this.radius = radius;}

	public void setTheta(double theta) {this.theta = theta;}

	public void setPhi(double phi) {this.phi = phi;}
	
	@Override
	public Polar_coodinates clone() {
		try {
			return (Polar_coodinates) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}
}
