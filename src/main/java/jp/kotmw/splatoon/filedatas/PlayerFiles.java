package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerStatusData;

public class PlayerFiles extends PluginFiles{

	protected static String filedir = "Players";

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

	private static List<String> setWeapon() {
		List<String> list = new ArrayList<String>();
		list.add("SplatShooter");
		return list;
	}
	
	public enum PlayerFile_Num {
		WIN("Rate.Win"), 
		LOSE("Rate.Lose"), 
		RANK("Status.Rank"), 
		EXP("Status.Exp");
		
		private final String pass;
		
		private PlayerFile_Num(String pass) {
			this.pass = pass;
		}
		
		public String getPass() {
			return pass;
		}
	}
}
