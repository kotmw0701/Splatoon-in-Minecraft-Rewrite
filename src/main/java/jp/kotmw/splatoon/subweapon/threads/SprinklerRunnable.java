package jp.kotmw.splatoon.subweapon.threads;

import java.util.Random;

import jp.kotmw.splatoon.Main;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SprinklerRunnable extends BukkitRunnable {

	private Location l;

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
		Vector vec = new Vector(x*Main.xz,Main.y,z*Main.xz);
		ball.setVelocity(vec);
	}
}
