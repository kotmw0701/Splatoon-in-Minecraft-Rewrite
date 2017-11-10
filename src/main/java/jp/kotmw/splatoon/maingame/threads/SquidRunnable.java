package jp.kotmw.splatoon.maingame.threads;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.manager.SplatColorManager;

public class SquidRunnable extends BukkitRunnable {

	private String name;

	public SquidRunnable(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		PlayerData data = DataStore.getPlayerData(name);
		if(data.getInkCoolTime() > 0) {
			int cooltime = data.getInkCoolTime();
			cooltime--;
			data.setInkCoolTime(cooltime);
			return;
		}
		if(!data.isSquidMode())
			return;
		if((!SplatColorManager.isBelowBlockTeamColor(Bukkit.getPlayer(name), true)) && !data.isClimb())
			return;
		Player player = Bukkit.getPlayer(name);
		float ink = player.getExp();
		if(ink <= 0.983) {
			player.setExp(ink+0.016f);
		}
	}
}
