package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.event.BattleStartEvent;
import jp.kotmw.splatoon.event.PlayerGameJoinEvent;
import jp.kotmw.splatoon.event.PlayerGameLeaveEvent;
import jp.kotmw.splatoon.filedatas.PlayerFiles;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.PlayerStatusData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.maingame.threads.AnimationRunnable;
import jp.kotmw.splatoon.maingame.threads.TransferRunnable;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MessageUtil;

public class MainGame extends MessageUtil {

	public static String Prefix = "[ "+ChatColor.GREEN+"Splatoon"+ChatColor.WHITE+" ] ";

	public static void join(Player player, WaitRoomData data) {
		if(DataStore.hasPlayerData(player.getName()))
			return;
		PlayerGameJoinEvent event = new PlayerGameJoinEvent(player, data);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled()) {
			String reason = event.getCancelreason();
			if(reason == null)
				reason = "参加を拒否されました";
			player.sendMessage(MainGame.Prefix+reason);
			return;
		}
		PlayerFiles.checkPlayerData(player.getUniqueId().toString(), player.getName());
		if(data.getTask() == null) {
			BukkitRunnable task = new AnimationRunnable(data);
			task.runTaskTimer(Main.main, 0, 5);
			data.setTask(task);
		}
		PlayerData playerdata = new PlayerData(player.getName());
		playerdata.setRoom(data.getName());
		playerdata.setRollBackLocation(player.getLocation());
		playerdata.setAllCansel(true);
		List<ItemStack> items = new ArrayList<>();
		for(int i = 0 ; i <= 40 ; i++) {
			ItemStack item = player.getInventory().getItem(i);
			if(item == null)
				item = new ItemStack(Material.AIR);
			items.add(item);
		}
		playerdata.setRollBackItems(items);
		player.teleport(new Location(Bukkit.getWorld(data.getWorld()),
				data.getX(),
				data.getY(),
				data.getZ(),
				(float)data.getYaw(),
				(float)data.getPitch()));
		player.getInventory().clear();
		player.getInventory().setItem(0, GameItems.getSelectItem());
		for(PotionEffect effect : player.getActivePotionEffects())
			player.removePotionEffect(effect.getType());
		DataStore.addPlayerData(player.getName(), playerdata);
		GameSigns.UpdateJoinSign(data.getName());
		if(DataStore.getRoomPlayersList(data.getName()).size() < 8)
			return;
		start(data);
	}

	public static PlayerData leave(Player player) {
		if(!DataStore.hasPlayerData(player.getName()))
			return null;
		PlayerGameLeaveEvent event = new PlayerGameLeaveEvent(DataStore.getPlayerData(player.getName()));
		Bukkit.getPluginManager().callEvent(event);
		PlayerData data = DataStore.removePlayerData(player.getName());
		if(data.getArena() == null) {
			if(DataStore.getRoomPlayersList(data.getRoom()).size() < 1)
				if(DataStore.getRoomData(data.getRoom()).getTask() != null) {
					DataStore.getRoomData(data.getRoom()).getTask().cancel();
					DataStore.getRoomData(data.getRoom()).setTask(null);
				}
		}
		if(data.getTask() != null) {
			data.getTask().cancel();
			data.setTask(null);
		}
		player.getInventory().clear();
		sendTitle(data, 0, 1, 0, " ", " ");
		sendActionBar(data, " ");
		int i = 0;
		for(ItemStack item : data.getRollbackItems()) {
			player.getInventory().setItem(i, item);
			i++;
		}
		player.teleport(data.getRollBackLocation());
		GameSigns.UpdateJoinSign(data.getRoom());
		return data;
	}

	public static void start(WaitRoomData roomdata) {
		ArenaData arenadata = getRandomArena(roomdata);
		if(arenadata == null) {
			DataStore.addPriority(roomdata.getName());
			for(PlayerData data : DataStore.getRoomPlayersList(roomdata.getName())) {
				sendMessage(data, ChatColor.GREEN+"全てのステージが使用中のため、転送がキャンセルされました");
				sendMessage(data, ChatColor.GREEN+"ステージが1つでも空いたら"+ChatColor.GOLD.toString()+ChatColor.BOLD+"この待機部屋が最優先で"+ChatColor.GREEN+"転送されるので今しばらくお待ちください");
			}
			return;
		}
		roomdata.getTask().cancel();
		roomdata.setTask(null);
		for(PlayerData data : DataStore.getRoomPlayersList(roomdata.getName()))
			sendActionBar(data, " ");
		arenadata.setGameStatus(GameStatusEnum.INGAME);
		BattleStartEvent event = new BattleStartEvent(roomdata, arenadata);
		Bukkit.getPluginManager().callEvent(event);
		new TransferRunnable(arenadata, roomdata.getName(), DataStore.getConfig().getTransfarCount(), roomdata.getBattleType()).runTaskTimer(Main.main, 0, 20);
	}

	public static void start(WaitRoomData roomdata, ArenaData arenadata) {
		if(roomdata == null || arenadata == null)
			return;
		roomdata.getTask().cancel();
		roomdata.setTask(null);
		for(PlayerData data : DataStore.getRoomPlayersList(roomdata.getName())) {
			sendActionBar(data, " ");
			sendMessage(data, ChatColor.GOLD+"大変長らくお待たせいたしました、ただいまより転送いたします");
		}
		arenadata.setGameStatus(GameStatusEnum.INGAME);
		BattleStartEvent event = new BattleStartEvent(roomdata, arenadata);
		Bukkit.getPluginManager().callEvent(event);
		GameSigns.UpdateJoinSign(roomdata.getName());
		new TransferRunnable(arenadata, roomdata.getName(), DataStore.getConfig().getTransfarCount(), roomdata.getBattleType()).runTaskTimer(Main.main, 0, 20);
	}

	public static void setInv(PlayerData data) {
		Player player = Bukkit.getPlayer(data.getName());
		player.getInventory().clear();
		if(data.getWeapon() == null)
			data.setWeapon(DataStore.getStatusData(data.getName()).getWeapons().get(0));
		player.getInventory().setItem(0, GameItems.getWeaponItem(DataStore.getWeapondata(data.getWeapon())));
		player.getInventory().setItem(1, GameItems.getSubWeaponItem(DataStore.getWeapondata(data.getWeapon())));
	}

	/**
	 *
	 * @param data 対象の待機部屋のデータ
	 *
	 * @return ランダムで選択されたステージ、すべてのステージが使用不可能な場合はnullを返す
	 *
	 */
	public static ArenaData getRandomArena(WaitRoomData data) {
		List<String> arenas = data.getSelectList();
		Collections.shuffle(arenas);
		for(String arena : arenas) {
			if(!DataStore.hasArenaData(arena))
				continue;
			ArenaData arenadata = DataStore.getArenaData(arena);
			if(data.getBattleType() == BattleType.Splat_Zones) {
					if(arenadata.getAreaPosition1().getX() == 0
							&& arenadata.getAreaPosition1().getY() == 0
							&& arenadata.getAreaPosition1().getZ() == 0)
						if((arenadata.getAreaPosition1().getX() == arenadata.getAreaPosition2().getX())
								&&(arenadata.getAreaPosition1().getY() == arenadata.getAreaPosition2().getY())
								&&(arenadata.getAreaPosition1().getZ() == arenadata.getAreaPosition2().getZ()))
						continue;
			}
			if(!data.isLimitBreak())
				if((arenadata.getMaximumTeamNum() > 2) || (arenadata.getMaximumPlayerNum() > 4))
					continue;
			if(arenadata.getGameStatus() == GameStatusEnum.ENABLE)
				return arenadata;
		}
		return null;
	}

	public static void end(ArenaData data, boolean tf) {
		Paint.RollBack(data);
		data.clearStatus();
		data.getScoreboard().resetScoreboard();
		for(PlayerData datalist : DataStore.getArenaPlayersList(data.getName())) {
			Player player = Bukkit.getPlayer(datalist.getName());
			if(!tf) {
				PlayerStatusData statusData = datalist.getPlayerStatus();
				if(data.getWinTeam() == datalist.getTeamid()) {
					statusData.updateWinnerScore();
				} else {
					statusData.updateLoserScore();
				}
				if(statusData.updateScoreExp()) {
					player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1);
					player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"ランクが上がりました！");
				}
			}
			player.getInventory().clear();
			player.setGameMode(Bukkit.getDefaultGameMode());
			for(PotionEffect potion : player.getActivePotionEffects())
				player.removePotionEffect(potion.getType());
			sendTitle(datalist, 0, 1, 0, " ", " ");
			sendActionBar(datalist, " ");
			int i = 0;
			for(ItemStack item : datalist.getRollbackItems()) {
				player.getInventory().setItem(i, item);
				i++;
			}
			player.teleport(datalist.getRollBackLocation());
			DataStore.removePlayerData(datalist.getName());
		}
	}

	public static void Teleport(PlayerData data, Location loc) {
		Bukkit.getPlayer(data.getName()).teleport(loc);
	}


	/*public static void Damager(PlayerData data, Block block, int damage) {
		if(block == null)
			return;
		Damager(data, block.getX(), block.getY(), block.getZ(), 20);
	}*/
	
	public static void Damager(PlayerData data, Location location, int damage) {
		if(location == null)
			return;
		Damager(data, location.getBlockX(), location.getBlockY(), location.getBlockZ(), 20);
	}

	public static void Damager(PlayerData player, int x, int y, int z, int damage) {
		for(PlayerData data : DataStore.getArenaPlayersList(player.getArena())) {
			if(data.getName() == player.getName())
				continue;
			if(player.getTeamid() == data.getTeamid())
				continue;
			Player target = Bukkit.getPlayer(data.getName());
			Location loc = target.getLocation();
			int target_x = loc.getBlockX(),
					target_y = loc.getBlockY(),
					target_z = loc.getBlockZ();
			if(x == target_x && y == target_y && z == target_z) {
				target.damage(damage);
			}
		}
	}

	/*public static void SphereDamager(PlayerData player, Location center, SubWeaponData subWeaponData, double radius) {
		for(PlayerData data : DataStore.getArenaPlayersList(player.getArena())) {
			if(data.getName() == player.getName())
				continue;
			if(player.getTeamid() == data.getTeamid())
				continue;
			Player target = Bukkit.getPlayer(data.getName());
			double distance = center.distance(target.getLocation());
			if(radius > distance) target.damage(subWeaponData.getMaxDamage());
		}
	}*/
	
	public static void SphereDamager(PlayerData player, Location center, SubWeaponData subWeaponData, double radius, boolean crit) {
		for(PlayerData data : DataStore.getArenaPlayersList(player.getArena())) {
			if(data.getName() == player.getName())
				continue;
			if(player.getTeamid() == data.getTeamid())
				continue;
			Player target = Bukkit.getPlayer(data.getName());
			double distance = center.distance(target.getLocation());
			if(radius > distance) {
				if(crit && 0.5 > distance) {
					target.damage(subWeaponData.getCriticalDamage());
					continue;
				}
				//距離減衰式を入れる
				target.damage(subWeaponData.getMaxDamage());
			}
		}
	}
	

	/*DataStore.getArenaPlayersList(player.getArena()).stream()
	.filter(data -> data.getName() != player.getName())
	.filter(data -> player.getTeamid() == data.getTeamid())
	.filter(data -> (0.5 > center.distance(Bukkit.getPlayer(data.getName()).getLocation())))
	.forEach(data -> Bukkit.getPlayer(data.getName()).damage(critical));*/

	public static int getTime(BattleType type) {
		switch(type) {
		case Turf_War:
			return DataStore.getConfig().getTimeforTurfWar();
		case Splat_Zones:
			return DataStore.getConfig().getTimeforSplatZones();
		case Rain_Maker:
			break;
		}
		return 180;
	}
	
	public static void sync(Runnable runnable) {
		Bukkit.getScheduler().runTask(Main.main, runnable);
	}
}
