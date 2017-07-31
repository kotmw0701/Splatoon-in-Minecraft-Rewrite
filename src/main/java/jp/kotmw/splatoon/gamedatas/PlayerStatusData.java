package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.filedatas.PlayerFiles;

public class PlayerStatusData extends PlayerFiles {

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
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
		for(String haveweapon : file.getStringList("Status.Weapons")) {
			if(haveweapon.equals(weaponname))
				return true;
		}
		return false;
	}
}
