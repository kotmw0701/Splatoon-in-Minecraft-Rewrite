package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

public class DataStore {
	private static Map<String, ArenaData> arenadata = new HashMap<>();
	private static Map<String, WaitRoomData> roomdata = new HashMap<>();
	private static Map<String, PlayerData> playerdata = new HashMap<>();
	private static Map<String, WeaponData> weapondata = new HashMap<>();
	private static Map<String, SubWeaponData> subweapondata = new HashMap<>();
	private static Map<String, SignData> signdata = new HashMap<>();
	private static Map<String, PlayerStatusData> statusdata = new HashMap<>();
	private static List<String> prioritylist = new ArrayList<>();
	private static ConfigData config;
	private static RankData rankData;

	public static boolean hasArenaData(String arena) {
		return arenadata.containsKey(arena);
	}

	public static boolean hasRoomData(String room) {
		return roomdata.containsKey(room);
	}

	public static boolean hasPlayerData(String name) {
		return playerdata.containsKey(name);
	}

	public static boolean hasWeaponData(String weapon) {
		return weapondata.containsKey(weapon);
	}

	public static boolean hasSubWeaponData(String subweapon) {
		return subweapondata.containsKey(subweapon);
	}

	public static boolean hasSignData(String filename) {
		return signdata.containsKey(filename);
	}

	public static boolean hasStatusData(String name) {
		return statusdata.containsKey(name);
	}

	public static ArenaData getArenaData(String arena) {
		return arenadata.get(arena);
	}

	public static WaitRoomData getRoomData(String room) {
		return roomdata.get(room);
	}

	public static PlayerData getPlayerData(String name) {
		return playerdata.get(name);
	}

	public static WeaponData getWeapondata(String weapon) {
		return weapondata.get(weapon);
	}

	public static SubWeaponData getSubWeaponData(String subweapon) {
		return subweapondata.get(subweapon);
	}

	public static PlayerStatusData getStatusData(String name) {
		return statusdata.get(name);
	}

	public static List<String> getArenaList() {
		return new ArrayList<String>(arenadata.keySet());
	}

	public static List<String> getRoomList() {
		return new ArrayList<String>(roomdata.keySet());
	}

	public static List<SignData> getSignDataList() {
		return new ArrayList<SignData>(signdata.values());
	}

	public static List<WeaponData> getWeaponList() {
		return new ArrayList<WeaponData>(weapondata.values());
	}

	public static void addArenaData(String arena, ArenaData data) {
		arenadata.put(arena, data);
	}

	public static void addRoomData(String room, WaitRoomData data) {
		roomdata.put(room, data);
	}

	public static void addPlayerData(String player, PlayerData data) {
		playerdata.put(player, data);
	}

	public static void addWeaponData(String weapon, WeaponData data) {
		weapondata.put(weapon, data);
	}

	public static void addSubWeaponData(String subweapon, SubWeaponData data) {
		subweapondata.put(subweapon, data);
	}

	public static void addSignData(String filename, SignData data) {
		signdata.put(filename, data);
	}

	public static void addStatusData(String name, PlayerStatusData data) {
		statusdata.put(name, data);
	}

	public static ArenaData removeArenaData(String arena) {
		return arenadata.remove(arena);
	}

	public static WaitRoomData removeRoomData(String room) {
		return roomdata.remove(room);
	}

	public static PlayerData removePlayerData(String player) {
		return playerdata.remove(player);
	}

	public static PlayerStatusData removeStatusData(String name) {
		return statusdata.remove(name);
	}

	public static SignData removeSignData(String filename) {
		return signdata.remove(filename);
	}

	public static void addPriority(String room) {
		prioritylist.add(room);
	}

	public static String getMaxPriorityData() {
		if(prioritylist.size() < 1)
			return null;
		return prioritylist.remove(0);
	}

	public static void setConfig(ConfigData data) {
		config = data;
	}

	public static ConfigData getConfig() {
		return config;
	}
	
	public static void setRank(RankData data) {
		rankData = data;
	}
	
	public static RankData getRankData() {
		return rankData;
	}

	public static List<PlayerData> getArenaPlayersList(String arena) {
		List<PlayerData> list = new ArrayList<>();
		for(String player : playerdata.keySet()) {
			PlayerData data = playerdata.get(player);
			if(data.getArena() != null
					&& data.getArena().equalsIgnoreCase(arena))
				list.add(data);
		}
		return list;
	}

	public static List<PlayerData> getRoomPlayersList(String room) {
		List<PlayerData> list = new ArrayList<>();
		for(String player : playerdata.keySet()) {
			PlayerData data = playerdata.get(player);
			if(data.getRoom() != null
					&& data.getRoom().equalsIgnoreCase(room))
				if(data.getArena() == null)
					list.add(data);
		}
		return list;
	}
	
	public static List<String> getRanking(RankingPattern pattern) {
		List<String> list = new ArrayList<>();
		Map<String, Double> rank = new HashMap<>();
		for(Entry<String, PlayerStatusData> players : statusdata.entrySet()) rank.put(players.getKey(), players.getValue().getParam(pattern));
		rank.entrySet().stream()
		.sorted(Collections.reverseOrder(Entry.comparingByValue()))
		.forEach(map -> list.add(ChatColor.AQUA+map.getKey()+ChatColor.GREEN+" : "+ChatColor.WHITE+map.getValue()));
		return list;
	}

	public static void datasAllClear() {
		arenadata.clear();
		playerdata.clear();
		roomdata.clear();
		weapondata.clear();
		signdata.clear();
		prioritylist.clear();
		statusdata.clear();
	}

	public enum BattleType {
		Turf_War(ChatColor.GREEN.toString() + ChatColor.BOLD + "Turf War"),
		Splat_Zones(ChatColor.RED.toString() + ChatColor.BOLD + "Splat Zones"),
		Rain_Maker(ChatColor.BLUE.toString() + ChatColor.BOLD + "Rain Maker");

		private final String name;

		private BattleType(final String name) {
			this.name = name;
		}

		public String getType() {
			return name;
		}
	}

	public enum GameStatusEnum {
		ENABLE(ChatColor.GREEN + "ENABLED"),
		DISABLE(ChatColor.DARK_RED + "DISABLED"),
		INGAME(ChatColor.RED + "INGAME"),
		RESULT(ChatColor.LIGHT_PURPLE + "RESULT NOW");

		private final String stats;

		private GameStatusEnum(final String stats) {
			this.stats = stats;
		}

		public String getStats() {
			return stats;
		}
	}

	public enum SignType {
		JOIN("Join"),
		STATUS("Stauts"),
		SHOP("Shop");

		private final String type;

		private SignType(final String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}
	}
	
	public enum RankingPattern {
		WIN("勝数"), 
		LOSE("敗数"), 
		RANK("ランク"), 
		TOTALPAINT("総塗り面積"), 
		MAXWINSTREAK("最大連勝数"), 
		RATE("勝率");
		
		private final String text;
		
		private RankingPattern(final String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
	}

	public enum WeaponType {
		Shooter, Roller, Charger
	}

	public enum BombType {
		QuickBomb, SplashBomb, SuckerBomb
	}
}
