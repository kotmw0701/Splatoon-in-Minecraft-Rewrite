package jp.kotmw.splatoon.maingame.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.util.Title;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RespawnRunnable extends BukkitRunnable {

	private int second;
	private Player player;

	public RespawnRunnable(int second, Player player) {
		this.second = second;
		this.player = player;
	}

	@Override
	public void run() {
		if(second > 0) {
			if(!DataStore.getPlayerData(player.getName()).isDead()) {
				this.cancel();
				return;
			}
			Title.sendAtionBar(player,
					ChatColor.DARK_GREEN.toString()+ChatColor.BOLD+"復活まで "+ChatColor.WHITE+" [ "+ChatColor.DARK_AQUA+
					ChatColor.BOLD+second+ChatColor.WHITE+" ]");
		} else {
			PlayerData data = DataStore.getPlayerData(player.getName());
			Location loc = data.getTeamid() == 1
					? DataStore.getArenaData(data.getArena()).getTeam1(1).convertLocation() : DataStore.getArenaData(data.getArena()).getTeam2(1).convertLocation();
			player.teleport(loc);
			player.setGameMode(GameMode.ADVENTURE);
			player.setVelocity(new Vector());
			player.setExp(1.0f);
			data.setDead(false);
			Title.sendAtionBar(player, " ");
			this.cancel();
		}
		second--;
	}

}
