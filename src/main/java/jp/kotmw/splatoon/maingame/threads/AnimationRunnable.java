package jp.kotmw.splatoon.maingame.threads;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.maingame.MainGame;

public class AnimationRunnable extends BukkitRunnable {

	WaitRoomData data;
	int i = 1;

	public AnimationRunnable(WaitRoomData data) {
		this.data = data;
	}

	@Override
	public void run() {
		if(i <= 50) {
			for(PlayerData data : DataStore.getRoomPlayersList(this.data.getName()))
				MainGame.sendActionBar(data, "    "+ChatColor.DARK_GREEN.toString()+ChatColor.BOLD+getTextLine(i));
			i++;
		} else {
			i = 1;
		}
	}

	public static String getTextLine(int i) {
		String text = "定員に達するまで今しばらくお待ちください";
		String blank = "                                        ";
		if(i <= 20)
			return blank.substring(i*2, blank.length())+text.substring(0, i);
		else if(i > 20 && i <= 30)
			return text;
		else if(i > 30)
			return text.substring(i-30, text.length())+blank.substring(0, (i-30)*2);
		return text;
	}

	/*
	 * @memo
	 *
	 * 定員に達するまで今しばらくお待ちください
	 *
	 * 40
	 *
	 */

}
