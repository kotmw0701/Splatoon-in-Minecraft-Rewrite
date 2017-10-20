package jp.kotmw.splatoon.maingame.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.manager.SplatColorManager;

public class DamageHealthRunnable extends BukkitRunnable {

	private String name;
	
	public DamageHealthRunnable(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		Player player = Bukkit.getPlayer(name);
		if(SplatColorManager.isBelowBlockTeamColor(player, true)) {
			if(player.getHealth() <= 18.0) player.setHealth(player.getHealth()+4.0);
		}
		else if(SplatColorManager.isBelowBlockTeamColor(player, false)) {
			player.damage(10.0);
		}
	}
}
