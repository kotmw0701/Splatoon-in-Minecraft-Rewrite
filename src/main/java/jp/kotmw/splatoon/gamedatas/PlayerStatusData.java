package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.filedatas.PlayerFiles;

public class PlayerStatusData extends PlayerFiles {

	private String uuid;
	private String name;
	private int win;
	private int lose;
	private boolean finalwin;
	private int winstreak;
	private int maxwinstreak;
	private int rank;
	private double exp;
	private double totalexp;
	private int totalpaint;
	private List<String> weapons = new ArrayList<String>();

	public PlayerStatusData(String uuid, FileConfiguration file) {
		this.uuid = uuid;
		this.name = file.getString("Name");
		this.win = file.getInt("Rate.Win");
		this.lose = file.getInt("Rate.Lose");
		this.finalwin = file.getBoolean("Rate.FinalWin");
		this.winstreak = file.getInt("Rate.WinStreak");
		this.maxwinstreak = file.getInt("Rate.MaxWinStreak");
		this.rank = file.getInt("Status.Rank");
		this.exp = file.getDouble("Status.Exp");
		this.totalexp = file.getDouble("Status.TotalExp");
		this.totalpaint = file.getInt("Status.TotalPaint");
		this.weapons = file.getStringList("Status.Weapons");
	}

	public String getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
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

	public double getExp() {
		return exp;
	}

	public double getTotalexp() {
		return totalexp;
	}
	
	public int getTotalPaint() {
		return totalpaint;
	}

	public List<String> getWeapons() {
		return weapons;
	}
	
	public void updateWinnerScore() {
		win++;
		if(finalwin) {
			winstreak++;
			if(winstreak > maxwinstreak)
				maxwinstreak = winstreak;
		}
		finalwin = true;
	}
	
	public void updateLoserScore() {
		lose++;
		finalwin = false;
		winstreak = 0;
	}
	
	public boolean updateScoreExp() {
		PlayerData data = DataStore.getPlayerData(name);
		double score = data.getScore()+exp;
		if(rank == 20)//一時的にランク20以上は設定しない
			return false;
		totalexp += data.getScore();
		boolean rankup = false;
		while(score >= DataStore.getRankData().getNextRankExp(rank)) {
			rankup = true;
			score -= DataStore.getRankData().getNextRankExp(rank);
			rank++;
		}
		exp = score;
		updateStatusFile(this);
		return rankup;
	}
	
	/**
	 *
	 * @param uuid 武器を追加するプレイヤーのUUID
	 * @param weapon ブキ名
	 *
	 * @return その武器を持っていない場合はtrue<br>
	 * 持っている場合はfalseを返す
	 */
	public boolean addWeapon(String weapon) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
		weapons = file.getStringList("Status.Weapons");
		if(weapons.contains(weapon))
			return false;
		weapons.add(weapon);
		setData(DirFile(filedir, uuid), "Status.Weapons", weapons);
		return true;
	}

	/**
	 *
	 * @param uuid 対象プレイヤーのハイフン抜きのUUID
	 * @param weaponname 調べる武器名
	 * @return 既に持っていればtrueが返される
	 */
	public boolean hasHaveWeapon(String weaponname) {
		return weapons.contains(weaponname);
	}
}
