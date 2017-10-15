package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.gamedatas.ConfigData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.SignType;
import jp.kotmw.splatoon.gamedatas.RankData;
import jp.kotmw.splatoon.gamedatas.SignData;


public class OtherFiles extends PluginFiles {

	static String signfiledir = "Rooms"+File.separator+"Signs";

	public static void createLobby(Location l) {
		FileConfiguration file = new YamlConfiguration();
		file.set("Lobby.X", l.getX());
		file.set("Lobby.Y", l.getY());
		file.set("Lobby.Z", l.getZ());
		file.set("Lobby.Yaw", l.getYaw());
		file.set("Lobby.Pitch", l.getPitch());
		SettingFiles(file, new File(filepath+"Lobby.yml"));
	}

	public static void createConfig() {
		if(new File(filepath+"Config.yml").exists())
			return;
		FileConfiguration file = new YamlConfiguration();
		file.set("ConfigVersion", 4);
		file.set("TransfarCount", 10);
		file.set("Time.Turf_War", 180);
		file.set("Time.Splat_Zones", 300);
		file.set("FinishTeleportLobby", false);
		file.set("UseDatabase", false);
		file.set("CanPaintColors", Arrays.stream(DyeColor.values()).map(color -> color.toString()).collect(Collectors.toList()));
		file.set("CanSplitBlocks", Arrays.asList("IRON_FENCE", "IRON_TRAPDOOR"));
		SettingFiles(file, new File(filepath+"Config.yml"));
	}

	public static void createRankSettingFile() {
		if(new File(filepath+"RankFile.yml").exists())
			return;
		FileConfiguration file = new YamlConfiguration();
		file.set("Rank.Rank2", 700);
		file.set("Rank.Rank3", 1600);
		file.set("Rank.Rank4", 2600);
		file.set("Rank.Rank5", 3700);
		file.set("Rank.Rank6", 4800);
		file.set("Rank.Rank7", 6000);
		file.set("Rank.Rank8", 7200);
		file.set("Rank.Rank9", 8600);
		file.set("Rank.Rank10", 10000);
		file.set("Rank.Rank11", 11500);
		file.set("Rank.Rank12", 13100);
		file.set("Rank.Rank13", 14800);
		file.set("Rank.Rank14", 16600);
		file.set("Rank.Rank15", 18600);
		file.set("Rank.Rank16", 20600);
		file.set("Rank.Rank17", 22700);
		file.set("Rank.Rank18", 25000);
		file.set("Rank.Rank19", 27400);
		file.set("Rank.Rank20", 30000);
		for(int i = 1; i <= 30; i++) file.set("Rank.Rank"+(20+i), 24*i);
		SettingFiles(file, new File(filepath+"RankFile.yml"));
	}

	public static void saveSignLoc(String name, Location l, SignType type) {
		FileConfiguration file = new YamlConfiguration();
		file.set("Name", name);
		file.set("Type", type.toString());
		file.set("World", l.getWorld().getName());
		file.set("X", l.getBlockX());
		file.set("Y", l.getBlockY());
		file.set("Z", l.getBlockZ());
		String filename = SignFile(name, type);
		SettingFiles(file,DirFile(signfiledir, filename));
		SignData data = new SignData(filename, file);
		DataStore.addSignData(filename, data);
	}

	public static List<String> getSignFileList() {
		return getFileList(SignDir());
	}

	public static File SignDir() {
		return new File(filepath + signfiledir);
	}

	private static String SignFile(String name, SignType type) {
		int i = 1;
		String filename = type.getType()+"_"+name+"_"+i;
		while(getSignFileList().contains(filename))
			filename = type.getType()+"_"+name+"_"+(++i);
		return filename;
	}

	public static boolean removeSign(String filename) {
		return new File(SignDir()+File.separator+filename+".yml").delete();
	}

	public static boolean configUpdater(ConfigData data) {
		if(data.getConfigversion().equalsIgnoreCase("4"))
			return false;
		FileConfiguration file = new YamlConfiguration();
		file.set("ConfigVersion", 4);
		file.set("TransfarCount", data.getTransfarCount());
		file.set("Time.Turf_War", data.getTimeforTurfWar());
		file.set("Time.Splat_Zones", data.getTimeforSplatZones());
		file.set("FinishTeleportLobby", data.isFinishteleportlobby());
		file.set("CanPaintColors", data.getCanpaintcolors());
		file.set("CanSplitBlocks", data.getCanSplitBlocks());
		SettingFiles(file, new File(filepath+"Config.yml"));
		return true;
	}
	
	public static void AllSignReload() {
		for(String sign : getSignFileList()) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(signfiledir, sign));
			SignData data = new SignData(sign, file);
			DataStore.addSignData(sign, data);
		}
	}

	public static void ConfigReload() {
		ConfigData data = new ConfigData(YamlConfiguration.loadConfiguration(new File(filepath+"Config.yml")));
		if(configUpdater(data)) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(new File(filepath+"Config.yml"));
			data = new ConfigData(file);
		}
		DataStore.setConfig(data);
	}
	
	public static void RankFileReload() {
		FileConfiguration file = YamlConfiguration.loadConfiguration(new File(filepath+"RankFile.yml"));
		DataStore.setRank(new RankData(file));
	}

	public static void AllTemplateFileGenerator() {
		OtherFiles.createConfig();
		OtherFiles.createRankSettingFile();
		StageFiles.ArenaDir().mkdir();
		PlayerFiles.PlayersDir().mkdir();
		WaitRoomFiles.RoomDir().mkdir();
		WeaponFiles.WeaponDir().mkdir();
		WeaponFiles.createTemplateShooterFile();
		WeaponFiles.createTemplateRollerFile();
		WeaponFiles.createTemplateChargerFile();
		OtherFiles.SignDir().mkdir();
	}
}
