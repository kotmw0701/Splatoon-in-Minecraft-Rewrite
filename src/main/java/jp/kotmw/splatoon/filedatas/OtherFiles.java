package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.gamedatas.ConfigData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.SignType;
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
		file.set("ConfigVersion", Integer.toHexString(3));
		file.set("TransfarCount", 10);
		file.set("Time.Turf_War", 180);
		file.set("Time.Splat_Zones", 300);
		file.set("FinishTeleportLobby", false);
		file.set("UseDatabase", false);
		List<String> colors = new ArrayList<String>();
		for(DyeColor color : DyeColor.values())
			colors.add(color.toString());
		file.set("CanPaintColors", colors);
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
		while(getSignFileList().contains(filename)) {
			i++;
			filename = type.getType()+"_"+name+"_"+i;
		}
		return filename;
	}

	public static boolean removeSign(String filename) {
		return new File(SignDir()+File.separator+filename+".yml").delete();
	}

	public static void AllSignReload() {
		for(String sign : getSignFileList()) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(signfiledir, sign));
			SignData data = new SignData(sign, file);
			DataStore.addSignData(sign, data);
		}
	}

	public static void ConfigReload() {
		FileConfiguration file = YamlConfiguration.loadConfiguration(new File(filepath+"Config.yml"));
		DataStore.setConfig(new ConfigData(file));
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
