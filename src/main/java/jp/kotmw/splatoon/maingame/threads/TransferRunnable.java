package jp.kotmw.splatoon.maingame.threads;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.SplatScoreBoard;
import jp.kotmw.splatoon.maingame.SplatZones;
import jp.kotmw.splatoon.maingame.Turf_War;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

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
			MainGame.setRandomTeam(DataStore.getRoomPlayersList(beforeroom));
			int team1 = 1, team2 = 1;
			for(PlayerData data : DataStore.getRoomPlayersList(beforeroom)) {
				data.setMove(false);
				data.setArena(this.data.getName());
				MainGame.setInv(data);
				Player player = Bukkit.getPlayer(data.getName());
				player.setGameMode(GameMode.ADVENTURE);
				player.setExp(1.0f);
				SplatScoreBoard.DefaultScoreBoard(this.data, type);
				SplatScoreBoard.setTeam(data);
				SplatScoreBoard.showBoard(data);
				if(data.getTeamid() == 1) {
					MainGame.Teleport(data, this.data.getTeam1(team1).convertLocation());
					team1++;
				} else if(data.getTeamid() == 2) {
					MainGame.Teleport(data, this.data.getTeam2(team2).convertLocation());
					team2++;
				}
			}
			switch(type) {
			case Turf_War:
				data.setBattleClass(new Turf_War(data.getName()));
				SplatZones.clearAreaStand(data);
				break;
			case Splat_Zones:
				data.setBattleClass(new SplatZones(data.getName()));
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
