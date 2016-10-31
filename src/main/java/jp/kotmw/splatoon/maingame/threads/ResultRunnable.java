package jp.kotmw.splatoon.maingame.threads;

import jp.kotmw.splatoon.SplatColor;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.Turf_War;
import jp.kotmw.splatoon.mainweapons.Paint;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class ResultRunnable extends BukkitRunnable {

	private Turf_War battle;
	private int tick = 26+10+10;
	private int i = 0;
	private int ii = 98;

	public ResultRunnable(Turf_War battle) {
		this.battle = battle;
	}

	@Override
	public void run() {
		ArenaData data = DataStore.getArenaData(battle.arena);
		if(tick >= 20) {
			for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
				MainGame.sendTitle(player, 0, 5, 0, " ", MeterText(data, i, ii));
			}
			i++;
			ii--;
		} else if(tick < 20 && tick >= 15){
			double parcent =  ((double)battle.result_team1 / (double)(battle.result_team1+battle.result_team2))*100;
			System.out.println(parcent);
			for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
				MainGame.sendTitle(player, 0, 5, 0, " ", MeterText2(data, (int)parcent-2, (int)parcent-1));
			}
		} else if(tick < 15 && tick >= 11) {
			battle.sendResult();
			Paint.RollBack(data);
		} else if(tick < 0) {
			MainGame.end(data, DataStore.getArenaPlayersList(data.getName()));
			cancel();
		}
		tick--;
	}

	private static String MeterText(ArenaData data, int i, int ii)
	{
		String base = "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
		String space = "            ";

		return SplatColor.conversionChatColor(data.getDyeColor(1))+base.substring(0, i)+ ChatColor.GRAY +base.substring(i + 1, ii)+ SplatColor.conversionChatColor(data.getDyeColor(2))+base.substring(ii + 1, 99)+ space;
	}

	private static String MeterText2(ArenaData data, int i, int ii)
	{
		String base = "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
		String space = "            ";
		return SplatColor.conversionChatColor(data.getDyeColor(1))+base.substring(0, i)+ SplatColor.conversionChatColor(data.getDyeColor(2))+base.substring(ii, 99)+ space;
	}

}
