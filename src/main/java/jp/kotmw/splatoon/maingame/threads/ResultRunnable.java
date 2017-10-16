package jp.kotmw.splatoon.maingame.threads;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.Turf_War;

public class ResultRunnable extends BukkitRunnable {

	private Turf_War battle;
	private int tick = 26+10+10;
	private int i = 0;
	private int ii = 98;
	private double parcent;
	private static String base = "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
	private static String space = "            ";
 
	public ResultRunnable(Turf_War battle) {
		this.battle = battle;
		parcent =  ((double)battle.getTeam1Result() / (double)(battle.getTeam1Result()+battle.getTeam2Result()))*100;
	}

	@Override
	public void run() {
		ArenaData data = battle.getArena();
		if(tick >= 20) {
			for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
				MainGame.sendTitle(player, 0, 5, 0, " ", MeterText(data, i, ii));
			}
			i++;
			ii--;
		} else if(tick < 20 && tick >= 15){
			
			for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
				MainGame.sendTitle(player, 0, 5, 0, " ", MeterText2(data, (int)parcent-2, (int)parcent-1));
			}
		} else if(tick < 15 && tick >= 11) {
			battle.sendResult();
		} else if(tick < 0) {
			MainGame.end(data, false);
			cancel();
		}
		tick--;
	}

	private static String MeterText(ArenaData data, int i, int ii)
	{
		return data.getSplatColor(1).getChatColor()+base.substring(0, i)+ ChatColor.GRAY +base.substring(i + 1, ii)+ data.getSplatColor(2).getChatColor()+base.substring(ii + 1, 99)+ space;
	}

	private static String MeterText2(ArenaData data, int i, int ii)
	{
		return data.getSplatColor(1).getChatColor()+base.substring(0, i)+ data.getSplatColor(2).getChatColor()+base.substring(ii, 99)+ space;
	}

}
