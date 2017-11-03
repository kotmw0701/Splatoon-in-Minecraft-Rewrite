package jp.kotmw.splatoon.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;

public class SplatBossBar {

	private Map<Integer, BossBar> teambossbar = new HashMap<>();
	private ArenaData data;
	
	public SplatBossBar(ArenaData data) {// ❙
		this.data = data;
		IntStream.rangeClosed(1, data.getMaximumTeamNum()).forEach(team -> {
			BossBar bar = Bukkit.createBossBar(updateisLifeBar_Allteam(team), BarColor.GREEN, BarStyle.SOLID);
			bar.setProgress(0.0);
			teambossbar.put(team, bar);
		});
	}
	
	/**
	 * 負荷が怖い場所その2
	 * 
	 */
	public void updateBar() {
		double total = data.getTotalTeamScore();
		teambossbar.entrySet().forEach(team -> {
			double teamscore = data.getTeamScore(team.getKey().intValue()),
					progress = (teamscore > 0.0 ? teamscore / total : 0.0);
			BossBar bar = team.getValue();
			bar.setProgress((progress > 1.0 ? 1.0 : progress));
			bar.setColor((progress > 0.5 ? (progress > 0.75 ? BarColor.BLUE : BarColor.GREEN) : (progress < 0.25 ? BarColor.RED : BarColor.YELLOW)));
		});
	}
	
	public void updateLifeBar() {
		teambossbar.entrySet().forEach(team -> {
			BossBar bar = team.getValue();
			bar.setTitle(updateisLifeBar_Allteam(team.getKey()));
		});
	}
	
	public void show(PlayerData data) {
		if(data.isAllView()) {
			teambossbar.values().forEach(bar -> bar.addPlayer(Bukkit.getPlayer(data.getName())));
			return;
		}
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
			bar.setColor(BarColor.GREEN);
			bar.setProgress(0.0);
		});
	}
	
	private String updateisLifeBar_Allteam(int myteam) {
		String text = "";
		for(int team = 1; team <= data.getMaximumTeamNum(); team++) {
			if(team == myteam)
				continue;
			text += (text.equalsIgnoreCase("") ? "" : " ")+updateisLifeBar(team);
		}
		return text;
	}
	
	private String updateisLifeBar(int team) {
		String players = "";
		List<PlayerData> playerDatas = DataStore.getArenaPlayersList(data.getName()).stream().filter(pd -> pd.getTeamid() == team).collect(Collectors.toList());
		for(int playerNum = 1; playerNum <= data.getMaximumPlayerNum(); playerNum++) {
			if(playerDatas.size() < playerNum) {
				players += ChatColor.DARK_GRAY + "❙";
				continue;
			}
			PlayerData playerData = playerDatas.get(playerNum-1);
			players += (playerData.isDead() ? ChatColor.DARK_GRAY : data.getSplatColor(team).getChatColor()) + "❙";
		}
		return players + ChatColor.RESET;
	}
	
	
	
	/*
	 * メモ
	 * 
	 * 概要 : 呼び出されたらチームの全員のPlayerData取って、isDeadがtrueだったらアイコンを暗転
	 * MAX人数居ない時の為にintのforで回してるけど、ここからどうやってPlayerData取り出そうかっていう
	 * 
	 * 提案
	 * ・リストのサイズを固定長にする
	 * ・別でリスト回して、そこからPlayerDataのポジションIDを取り出して比較してやる
	 * 
	 * リストのサイズ固定長にすると、add removeが使えなくなる
	 *   ↑
	 * DataStore#getArenaPlayersListの戻り値を固定長にするだけだから多分可変はしない・・・はず
	 * それだったらそこの戻り値を固定長にするって方面で問題は無さげ
	 * 
	 * ArenaPlayersListだとステージ全体のプレイヤー一覧取られるの忘れてた
	 *   ↑
	 * フィルター掛ければいいや
	 */
}
