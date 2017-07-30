package jp.kotmw.splatoon.specialweapon.threads;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.util.DetailsColor.DetailsColorType;
import jp.kotmw.splatoon.util.ParticleAPI.EnumParticle;
import jp.kotmw.splatoon.util.ParticleAPI.Particle;

public class BarrierRunnable extends BukkitRunnable {
	
	Player player;
	float pitch = 0;
	
	public BarrierRunnable(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		pitch += Math.PI/10;
		showParticleCircles(player.getLocation(), 1.5, pitch);
		if(pitch > Math.PI)
			cancel();
	}
	
	void showParticleCircles(Location center, double radius, float pitch) {
		for(double deg = 0.0; deg <= 360.0; deg+=5) {
			double x = radius*Math.cos(Math.toRadians(deg))*Math.sin(pitch);
			double y = radius*Math.cos(pitch)+1.5;
			double z = radius*Math.sin(Math.toRadians(deg))*Math.sin(pitch);
			center.add(x,y,z);
			new Particle(EnumParticle.REDSTONE, 
					center, 
					DetailsColorType.WoolColor_WHITE.getRed(), 
					DetailsColorType.WoolColor_WHITE.getGreen(), 
					DetailsColorType.WoolColor_WHITE.getBlue(), 
					1, 
					0).sendParticle(player);
			center.subtract(x, y, z);
		}
	}
}
