package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class PlayerStatusData {

	private String uuid;
	private int win;
	private int lose;
	private boolean finalwin;
	private int winstreak;
	private int maxwinstreak;
	private int rank;
	private int exp;
	private int totalexp;
	private List<String> weapons = new ArrayList<String>();

	/*public PlayerStatusData(ResultSet set) {
		try {
			this.uuid = set.getString("UUID");
			this.win = set.getInt("win");
			this.lose = set.getInt("lose");
			this.finalwin = set.getBoolean("finalwin");
			this.winstreak = set.getInt("winstreak");
			this.maxwinstreak = set.getInt("maxwinstreak");
			this.rank = set.getInt("rank");
			this.exp = set.getInt("exp");
			this.totalexp = set.getInt("totalexp");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}*/

	public PlayerStatusData(String uuid, FileConfiguration file) {
		this.uuid = uuid;
		this.win = file.getInt("Rate.Win");
		this.lose = file.getInt("Rate.Lose");
		this.finalwin = file.getBoolean("Rate.FinalWin");
		this.winstreak = file.getInt("Rate.WinStreak");
		this.maxwinstreak = file.getInt("Rate.MaxWinStreak");
		this.rank = file.getInt("Status.Rank");
		this.exp = file.getInt("Status.Exp");
		this.totalexp = file.getInt("Status.TotalExp");
		this.weapons = file.getStringList("Status.Weapons");
	}

	public String getUuid() {
		return uuid;
	}

	public int getWin() {
		return win;
	}

	public int getLose() {
		return lose;
	}

	public boolean isFinalwin() {
		return finalwin;
	}

	public int getWinstreak() {
		return winstreak;
	}

	public int getMaxwinstreak() {
		return maxwinstreak;
	}

	public int getRank() {
		return rank;
	}

	public int getExp() {
		return exp;
	}

	public int getTotalexp() {
		return totalexp;
	}

	public List<String> getWeapons() {
		return weapons;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public void setFinalwin(boolean finalwin) {
		this.finalwin = finalwin;
	}

	public void setWinstreak(int winstreak) {
		this.winstreak = winstreak;
	}

	public void setMaxwinstreak(int maxwinstreak) {
		this.maxwinstreak = maxwinstreak;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public void setTotalexp(int totalexp) {
		this.totalexp = totalexp;
	}
}
