package jp.kotmw.splatoon.maingame.threads;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.SplatZones;
import jp.kotmw.splatoon.maingame.Turf_War;

public class TransferRunnable extends BukkitRunnable {

	private ArenaData data;
	private String beforeroom;
	private int second;
	private BattleType type;

	public TransferRunnable(ArenaData data, String beforeroom, int second, BattleType type) {
		this.data = data;
		this.beforeroom = beforeroom;
		this.second = second;
		this.type = type;
	}

	@Override
	public void run() {
		if(second > 0) {
			for(PlayerData data : DataStore.getRoomPlayersList(beforeroom))
				MainGame.sendTitle(data,
						0,
						5,
						0,
						ChatColor.GREEN+"ステージに転送します",
						ChatColor.BLUE.toString()+"---[  "+ChatColor.DARK_AQUA.toString()+ChatColor.BOLD+second+ChatColor.BLUE.toString()+"  ]---");
		} else {
			List<PlayerData> datalist = DataStore.getRoomPlayersList(beforeroom);
			Collections.shuffle(datalist);
			int team = 1, posisions = 1, players = 1;
			for(PlayerData playerdata : datalist) {
				if(players >= data.getTotalPlayerCount()) {
					MainGame.sendMessage(playerdata, ChatColor.RED+"転送先ステージの許容人数をオーバーしたため、転送ができませんでした");
					MainGame.sendMessage(playerdata, ChatColor.YELLOW+"このまま残ることも可能ですが、待機前の場所に戻る場合は"+ChatColor.WHITE+" /splat leave "+ChatColor.YELLOW+"コマンドを使用してください");
					continue;
					/*
					 * メモ
					 * 
					 * TODO 対象ステージの最大許容人数オーバー時どうするか
					 * 
					 * 取りあえずオーバーした人はロビーに戻るかこのまま残るか選択出来るようにする
					 * 
					 */
				}
				if(team > data.getMaximumTeamNum()) {
					team = 1;
					posisions++;
				}
				playerdata.setMove(false);
				playerdata.setArena(this.data.getName());
				playerdata.setTeamId(team);
				playerdata.setPosisionId(posisions);
				MainGame.setInv(playerdata);
				Player player = Bukkit.getPlayer(playerdata.getName());
				player.setGameMode(GameMode.ADVENTURE);
				player.setExp(0.99f);//1.12.2対応のため
				data.getScoreboard().DefaultScoreBoard(type);
				data.getScoreboard().setTeam(playerdata);
				data.getScoreboard().showBoard(playerdata);
				data.getBossBar().show(playerdata);
				MainGame.Teleport(playerdata, this.data.getTeamPlayerPosision(team, posisions).convertLocation());
				team++;
				players++;
			}
			switch(type) {
			case Turf_War:
				data.setBattleClass(new Turf_War(data));
				SplatZones.clearAreaStand(data);
				break;
			case Splat_Zones:
				data.setBattleClass(new SplatZones(data));
				((SplatZones)data.getBattleClass()).showZone();
				break;
			case Rain_Maker:
				break;
			}
			GameSigns.UpdateJoinSign(beforeroom);
			StageTransfar(data);
			this.cancel();
		}
		second--;
	}

	private void StageTransfar(ArenaData data) {
		BattleRunnable task = new BattleRunnable(data, MainGame.getTime(type), type);
		task.runTaskTimer(Main.main, 0, 1);
		data.setTask(task);
	}
}
