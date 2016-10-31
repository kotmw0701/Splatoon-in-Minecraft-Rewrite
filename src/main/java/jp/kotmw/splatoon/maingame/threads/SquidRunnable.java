package jp.kotmw.splatoon.maingame.threads;

import jp.kotmw.splatoon.SplatColor;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
		if(!SplatColor.isBelowBlockTeamColor(Bukkit.getPlayer(name)))
			return;
		Player player = Bukkit.getPlayer(name);
		float ink = player.getExp();
		if(ink <= 1.0) {
			player.setExp(ink+0.016f);
		}
	}
}
