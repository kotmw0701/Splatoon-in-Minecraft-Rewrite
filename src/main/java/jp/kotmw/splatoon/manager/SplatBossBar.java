package jp.kotmw.splatoon.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.PlayerData;

public class SplatBossBar {

	private Map<Integer, BossBar> teambossbar = new HashMap<>();
	private ArenaData data;
	
	public SplatBossBar(ArenaData data) {
		IntStream.rangeClosed(1, data.getMaximumTeamNum()).forEach(team -> {
			BossBar bar = Bukkit.createBossBar("Team"+team, BarColor.YELLOW, BarStyle.SOLID);
			bar.setProgress(0.5);
			teambossbar.put(team, bar);
		});
		this.data = data;
	}
	
	/**
	 * 負荷が怖い場所その2
	 * 塗られるたびIntStreamが動くから
	 * 
	 */
	public void updateBar() {
		double total = data.getTotalTeamScore();
		teambossbar.entrySet().forEach(team -> {
			double teamscore = data.getTeamScore(team.getKey().intValue()),
					progress = (teamscore > 0.0 ? teamscore / total : 0.0);
			BossBar bar = team.getValue();
			bar.setProgress((progress > 1.0 ? 1.0 : progress));
			bar.setColor((progress > 0.5 ? BarColor.GREEN : (progress < 0.3 ? BarColor.RED : BarColor.YELLOW)));
		});
	}
	
	public void show(PlayerData data) {
		teambossbar.get(data.getTeamid()).addPlayer(Bukkit.getPlayer(data.getName()));
	}
	
	public void hide(int team) {
		teambossbar.get(team).removeAll();
	}
	
	public void removeAllPlayer() {
		IntStream.rangeClosed(1, data.getMaximumTeamNum()).forEach(team -> teambossbar.get(team).removeAll());
	}
	
	public void resetBossBar() {
		teambossbar.values().forEach(bar -> {
			bar.setColor(BarColor.YELLOW);
			bar.setProgress(0.5);
		});
	}
}
