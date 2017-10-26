package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.maingame.Turf_War;
import jp.kotmw.splatoon.manager.SplatBossBar;
import jp.kotmw.splatoon.manager.SplatScoreBoard;

public class StageFiles extends PluginFiles {

	static String filedir = "Arenas";

	public static boolean createArena(String arena, World world ,Location loc1, Location loc2) {
		FileConfiguration file = new YamlConfiguration();
		if(DirFile(filedir, arena).exists())
			file = YamlConfiguration.loadConfiguration(DirFile(filedir, arena));
		int x1b = loc1.getBlockX(), y1b = loc1.getBlockY(), z1b = loc1.getBlockZ();
		int x2b = loc2.getBlockX(), y2b = loc2.getBlockY(), z2b = loc2.getBlockZ();
		int x1 = x1b, x2 = x2b;
		if(x1b < x2b) {
			x1 = x2b;
			x2 = x1b;
		}
		int y1 = y1b, y2 = y2b;
		if(y1b < y2b) {
			y1 = y2b;
			y2 = y1b;
		}
		int z1 = z1b, z2 = z2b;
		if(z1b < z2b) {
			z1 = z2b;
			z2 = z1b;
		}
		int paintblockscount = Turf_War.getTotalArea(world, x1, x2, y1, y2, z1, z2);
		if(paintblockscount < 1)
			return false;
		file.set("Stage.Status", false);
		file.set("Stage.World", world.getName());
		file.set("Stage.Pos1", x1+"/"+y1+"/"+z1);
		file.set("Stage.Pos2", x2+"/"+y2+"/"+z2);
		file.set("Stage.TotalPaintBlock", paintblockscount);
		SettingFiles(file, DirFile(filedir, arena));
		return true;
	}

	public static FileConfiguration replacePosition(String name, FileConfiguration file) {
		int x1b = Integer.valueOf(file.getString("Stage.Pos1").split("/")[0]),
				y1b = Integer.valueOf(file.getString("Stage.Pos1").split("/")[1]),
				z1b = Integer.valueOf(file.getString("Stage.Pos1").split("/")[2]);
		int x2b = Integer.valueOf(file.getString("Stage.Pos2").split("/")[0]),
				y2b = Integer.valueOf(file.getString("Stage.Pos2").split("/")[1]),
				z2b = Integer.valueOf(file.getString("Stage.Pos2").split("/")[2]);
		if(x1b >= x2b && y1b >= y2b && z1b >= z2b)
			return file;
		int x1 = x1b, x2 = x2b;
		if(x1b < x2b) {
			x1 = x2b;
			x2 = x1b;
		}
		int y1 = y1b, y2 = y2b;
		if(y1b < y2b) {
			y1 = y2b;
			y2 = y1b;
		}
		int z1 = z1b, z2 = z2b;
		if(z1b < z2b) {
			z1 = z2b;
			z2 = z1b;
		}
		file.set("Stage.Pos1", x1+"/"+y1+"/"+z1);
		file.set("Stage.Pos2", x2+"/"+y2+"/"+z2);
		SettingFiles(file, DirFile(filedir, name));
		return file;
	}

	public static boolean setSpawnPos(String arena, Location l, int team, int pos) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, arena));
		file.set("SpawnPos.Team"+team+".P"+pos+".Loc", l.getX()+"/"+l.getY()+"/"+l.getZ());
		file.set("SpawnPos.Team"+team+".P"+pos+".HeadRotation", l.getYaw()+"/"+l.getPitch());
		SettingFiles(file, DirFile(filedir, arena));
		return true;
	}

	public static boolean setArea(String arena, Location loc1, Location loc2) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, arena));
		int x1b = loc1.getBlockX(), y1b = loc1.getBlockY(), z1b = loc1.getBlockZ();
		int x2b = loc2.getBlockX(), y2b = loc2.getBlockY(), z2b = loc2.getBlockZ();
		int x1 = x1b, x2 = x2b;
		if(x1b < x2b) {
			x1 = x2b;
			x2 = x1b;
		}
		int y = y2b;
		if(y1b < y2b)
			y = y1b;
		int z1 = z1b, z2 = z2b;
		if(z1b < z2b) {
			z1 = z2b;
			z2 = z1b;
		}
		file.set("SplatZone.Pos1", x1+"/"+y+"/"+z1);
		file.set("SplatZone.Pos2", x2+"/"+y+"/"+z2);
		SettingFiles(file, DirFile(filedir, arena));
		return true;
	}
	
	public static ArenaData setArenaData(String arena) {
		return setArenaData(arena, false);
	}

	public static ArenaData setArenaData(String arena, boolean update) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, arena));
		ArenaData data = new ArenaData(arena, file);
		if(update) {
			int paintblockscount = Turf_War.getTotalArea(Bukkit.getWorld(data.getWorld()), 
					data.getStagePosition1().getBlockX(), 
					data.getStagePosition2().getBlockX(), 
					data.getStagePosition1().getBlockY(), 
					data.getStagePosition2().getBlockY(), 
					data.getStagePosition1().getBlockZ(), 
					data.getStagePosition2().getBlockZ());
			file.set("Stage.TotalPaintBlock", paintblockscount);
			SettingFiles(file, DirFile(filedir, arena));
			data.setTotalpaintblock(paintblockscount);
		}
		DataStore.addArenaData(arena, data);
		return data;
	}

	private static List<String> getArenaFileList() {
		return getFileList(new File(filepath + filedir));
	}

	/**
	 *
	 * @param arena
	 * @return 設定が終わってない、若しくはMapに入ってない場合、既に有効化されていればfalseを返す
	 */
	public static boolean isFinishSetup(ArenaData data) {
		if(!DataStore.hasArenaData(data.getName()))
			return false;
		if(data.getMinimumPlayerNum() != data.getMaximumPlayerNum())
			return false;
		for(int i = 1; i <= data.getMaximumPlayerNum(); i++) {
			jp.kotmw.splatoon.util.Location location = data.getTeamPlayerPosision(1, i);
			if(location == null)
				return false;
			double y = location.getY();
			if(y <= 0)
				return false;
		}
		return true;
	}

	public static boolean AlreadyCreate(String arena) {
		return DataStore.hasArenaData(arena);
	}

	public static boolean AlreadyCreateFile(String arena) {
		return DirFile(filedir, arena).exists();
	}

	public static boolean isEnable(String arena) {
		return DataStore.getArenaData(arena).isStatus();
	}

	public static void setEnable(String arena) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, arena));
		file.set("Stage.Status", true);
		SettingFiles(file, DirFile(filedir, arena));
	}

	public static File ArenaDir() {
		return new File(filepath + filedir);
	}

	public static void AllStageReload() {
		for(String arena : getArenaFileList()) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, arena));
			file = replacePosition(arena, file);
			ArenaData data = new ArenaData(arena, file);
			data.setGameStatus(GameStatusEnum.ENABLE);
			if(!data.isStatus())
				continue;
			data.updateTeamColor();
			data.setScoreBoard(new SplatScoreBoard(data));
			data.setBossBar(new SplatBossBar(data));
			DataStore.addArenaData(arena, data);
		}
	}
}
