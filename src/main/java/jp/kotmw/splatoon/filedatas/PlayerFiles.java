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

	private static void createPlayerFile(String uuid, String name) {
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
		file.set("Status.TotalPaint", 0);
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
	
	private static List<String> getPlayerFileList() {
		return getFileList(new File(filepath + filedir));
	}
	
	public static void AllPlayerFileReload() {
		for(String uuid : getPlayerFileList()) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
			PlayerStatusData data = new PlayerStatusData(uuid, file);
			DataStore.addStatusData(data.getName(), data);
		}
	}
	
	public static void checkPlayerData(String uuid, String name) {
		if(DataStore.hasStatusData(name))
			return;
		PlayerFiles.createPlayerFile(uuid, name);
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
		PlayerStatusData data = new PlayerStatusData(uuid, file);
		DataStore.addStatusData(name, data);
	}

	private static List<String> setWeapon() {
		List<String> list = new ArrayList<String>();
		list.add("SplatShooter");
		return list;
	}
	
	protected void updateStatusFile(PlayerStatusData data) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, data.getUuid()));
		file.set("Rate.Win", data.getWin());
		file.set("Rate.Lose", data.getLose());
		file.set("Rate.FinalWin", data.isFinalwin());
		file.set("Rate.WinStreak", data.getWinstreak());
		file.set("Rate.MaxWinStreak", data.getMaxwinstreak());
		file.set("Status.Rank", data.getRank());
		file.set("Status.Exp", data.getExp());
		file.set("Status.TotalExp", data.getTotalexp());
		file.set("Status.TotalPaint", data.getTotalPaint());
		SettingFiles(file, DirFile(filedir, data.getUuid()));
	}
}
