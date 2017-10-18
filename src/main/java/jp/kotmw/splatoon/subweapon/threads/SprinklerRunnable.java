package jp.kotmw.splatoon.subweapon.threads;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SprinklerRunnable extends BukkitRunnable {

	private Location l;
	private double xz = 0.2, y = 0.2;

	public SprinklerRunnable(Location l) {
		this.l = l;
	}

	@Override
	public void run() {
		if(l.getBlock().getType() != Material.END_ROD)
			cancel();
		shoot();
	}

	private void shoot() {
		Location l = this.l.clone();
		Random random = new Random();
		double angle = random.nextInt(360*100)/100;
		double x = Math.cos(Math.toRadians(angle));
		double z = Math.sin(Math.toRadians(angle));
		Entity ball = l.getWorld().spawnEntity(l.add(0.5,1,0.5), EntityType.SNOWBALL);
		Vector vec = new Vector(x*xz,y,z*xz);
		ball.setVelocity(vec);
	}
}
