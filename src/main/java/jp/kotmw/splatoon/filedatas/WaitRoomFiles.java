package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;

public class WaitRoomFiles extends PluginFiles {

	static String filedir = "Rooms";

	public static boolean creareWaitRoom(String room, Location l, BattleType type) {
		FileConfiguration file = new YamlConfiguration();
		file.set("Room.World", l.getWorld().getName());
		file.set("Room.X", l.getX());
		file.set("Room.Y", l.getY());
		file.set("Room.Z", l.getZ());
		file.set("Room.Yaw", l.getYaw());
		file.set("Room.Pitch", l.getPitch());
		file.set("Room.SelectList", DataStore.getArenaList());
		file.set("Room.BattleType", type.toString());
		DataStore.addRoomData(room, new WaitRoomData(room, file));
		SettingFiles(file, DirFile(filedir, room));
		return true;
	}

	public static boolean AlreadyCreateFile(String room) {
		return DirFile(filedir, room).exists();
	}

	public static List<String> getRoomList() {
		return getFileList(new File(filepath + filedir));
	}

	public static File RoomDir() {
		return new File(filepath + filedir);
	}

	public static void AllRoomReload() {
		for(String room : getRoomList()) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, room));
			WaitRoomData data = new WaitRoomData(room, file);
			DataStore.addRoomData(room, data);
		}
	}
	
	public static boolean removeRoomFile(String room) {
		return DirFile(filedir, room).delete();
	}
	
	public static void editSelectList(WaitRoomData room, String arena, boolean add) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, room.getName()));
		List<String> arenas = room.getSelectList();
		if(add) arenas.add(arena);
		else arenas.remove(arena);
		room.setSelectList(arenas);
		file.set("Room.SelectList", arenas);
		SettingFiles(file, DirFile(filedir, room.getName()));
	}
}
