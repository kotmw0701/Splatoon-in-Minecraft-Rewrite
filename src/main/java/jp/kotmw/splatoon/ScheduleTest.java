package jp.kotmw.splatoon;

import org.bukkit.entity.Player;

import jp.kotmw.splatoon.maingame.MainGame;

public class ScheduleTest extends Thread {

	private int millisecond;
	private Player player;

	public ScheduleTest(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		while(millisecond <= 60000) {
			MainGame.sendAtionBar(player, convertTime(millisecond));
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			millisecond++;
		}
	}

	public String convertTime(int tick) {
		int millisecond = tick%1000;
		int second = (tick/1000)%60;
		int minute = ((tick/1000)/60)%60;
		String millisecondstr = String.valueOf(millisecond).length() < 3 ? "0"+String.valueOf(millisecond) : String.valueOf(millisecond);
		String secondstr = String.valueOf(second).length() < 2 ? "0"+String.valueOf(second) : String.valueOf(second);
		String minutestr = String.valueOf(minute).length() < 2 ? "0"+String.valueOf(minute) : String.valueOf(minute);
		return minutestr +"\\\'"+secondstr+"\\\""+millisecondstr;
	}
}
