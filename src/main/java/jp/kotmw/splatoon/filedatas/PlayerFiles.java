package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerStatusData;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerFiles extends PluginFiles{

	static String filedir = "Players";

	public static void createPlayerFile(String uuid, String name) {
		FileConfiguration file = new YamlConfiguration();
		file.set("Name", name);
		file.set("Rate.Win", 0);
		file.set("Rate.Lose", 0);
		file.set("Rate.FinalWin", false);
		file.set("Rate.WinStreak", 0);
		file.set("Rate.MaxWinStreak", 0);
		file.set("Status.Rank", 1);
		file.set("Status.Exp", 0);
		file.set("Status.TotalExp", 0);
		file.set("Status.Weapons", setWeapon());
		file.set("Friends", new ArrayList<String>());
		file.set("Invites", new ArrayList<String>());
		SettingFiles(file, DirFile(filedir, uuid));
	}

	/*public static void UpdatePlayerFile(ResultSet set) {
		FileConfiguration file = new YamlConfiguration();
		try {
			file.set("Name", set.getString("Name"));
			file.set("Rate.Win", set.getInt("Win"));
			file.set("Rate.Lose", set.getInt("Lose"));
			file.set("Rate.FinalWin", set.getBoolean("FinalWin"));
			file.set("Rate.WinStreak", set.getInt("WinStreak"));
			file.set("Rate.MaxWinStreak", set.getInt("MaxWinStreak"));
			file.set("Status.Rank", set.getInt("Rank"));
			file.set("Status.Exp", set.getInt("Exp"));
			file.set("Status.TotalExp", set.getInt("TotalExp"));
			file.set("Friends", new ArrayList<String>());
			file.set("Invites", new ArrayList<String>());
			SettingFiles(file, DirFile(filedir, set.getString("UUID")));
		} catch (SQLException e) {
			System.out.println("データベース接続に失敗したため、更新は行われません");
			e.printStackTrace();
		}
	}*/

	public static boolean AlreadyCreateFile(String uuid) {
		return DirFile(filedir, uuid).exists();
	}

	public static File PlayersDir() {
		return new File(filepath + filedir);
	}

	public static void loadPlayerData(String uuid, String name) {
		if(!PlayerFiles.AlreadyCreateFile(uuid)) {
			PlayerFiles.createPlayerFile(uuid, name);
		}
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
		PlayerStatusData data = new PlayerStatusData(uuid, file);
		DataStore.addStatusData(name, data);
	}

	/**
	 *
	 * @param uuid 武器を追加するプレイヤーのUUID
	 * @param weapon ブキ名
	 *
	 * @return その武器を持っていない場合はtrue<br>
	 * 持っている場合はfalseを返す
	 */
	public static boolean addWeapon(String uuid, String weapon) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
		List<String> weapons = file.getStringList("Status.Weapons");
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
	public static boolean hasHaveWeapon(String uuid, String weaponname) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
		for(String haveweapon : file.getStringList("Status.Weapons")) {
			if(haveweapon.equals(weaponname))
				return true;
		}
		return false;
	}

	private static List<String> setWeapon() {
		List<String> list = new ArrayList<String>();
		list.add("SplatShooter");
		return list;
	}
}
