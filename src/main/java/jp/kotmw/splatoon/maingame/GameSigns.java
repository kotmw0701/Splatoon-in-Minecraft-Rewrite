package jp.kotmw.splatoon.maingame;

import jp.kotmw.splatoon.filedatas.OtherFiles;
import jp.kotmw.splatoon.filedatas.PlayerFiles;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.SignType;
import jp.kotmw.splatoon.gamedatas.SignData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.gamedatas.WeaponData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class GameSigns implements Listener {

	String joinsign = ChatColor.DARK_GRAY+"["+ChatColor.DARK_GREEN+"Splatoon"+ChatColor.DARK_GRAY+"]";
	String statussign = ChatColor.DARK_GRAY+"["+ChatColor.DARK_BLUE+"SplatoonStatus"+ChatColor.DARK_GRAY+"]";
	String weaponsign = ChatColor.DARK_GRAY+"["+ChatColor.DARK_RED+"SplatWeaponShop"+ChatColor.DARK_GRAY+"]";

	@EventHandler
	public void onClickSign(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK
				&& e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		Block block = e.getClickedBlock();
		if(block.getType() != Material.SIGN
				&& block.getType() != Material.SIGN_POST
				&& block.getType() != Material.WALL_SIGN)
			return;
		if(e.getPlayer().isSneaking()
				&& e.getAction() == Action.LEFT_CLICK_BLOCK) {
			return;
		}
		Sign sign = (Sign)block.getState();
		Player player = e.getPlayer();
		if(sign.getLine(0).equalsIgnoreCase(joinsign)) {
			if(DataStore.hasRoomData(sign.getLine(1))) {
				WaitRoomData data = DataStore.getRoomData(sign.getLine(1));
				MainGame.join(player, data);
			}
		} else if(sign.getLine(0).equalsIgnoreCase(statussign)) {

		} else if(sign.getLine(0).equalsIgnoreCase(weaponsign)) {
			boolean nohave = PlayerFiles.addWeapon(player.getUniqueId().toString().replaceAll("-", ""), sign.getLine(1));
			if(nohave)
				player.sendMessage(MainGame.Prefix+ChatColor.GOLD.toString()+ChatColor.BOLD+sign.getLine(1)+ChatColor.GREEN+"を購入しました");
			else
				player.sendMessage(MainGame.Prefix+ChatColor.RED+"その武器は既に持っています");
			return;
		}
	}

	@EventHandler
	public void breakSign(BlockBreakEvent e) {
		Block block = e.getBlock();
		if(block.getType() != Material.SIGN
				&& block.getType() != Material.SIGN_POST
				&& block.getType() != Material.WALL_SIGN)
			return;
		Sign sign = (Sign)block.getState();
		if(!sign.getLine(0).equalsIgnoreCase(joinsign)
				&& !sign.getLine(0).equalsIgnoreCase(statussign)
				&& !sign.getLine(0).equalsIgnoreCase(weaponsign))
			return;
		if(!e.getPlayer().isSneaking()) {
			e.setCancelled(true);
			return;
		}
		SignData signdata = getSignData(sign.getLocation(), sign.getLine(1));
		DataStore.removeSignData(signdata.getFilename());
		OtherFiles.removeSign(signdata.getFilename());
		Player player = e.getPlayer();
		player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"看板を消去しました");
		player.sendMessage(ChatColor.GREEN+"看板のタイプ: "+signdata.getType().getType());
	}

	@EventHandler
	public void createSign(SignChangeEvent e) {
		String top = e.getLine(0);
		if(top.equalsIgnoreCase("[Splatoon]")
				|| top.equalsIgnoreCase("splat")
				|| top.equalsIgnoreCase("splatoon")) {
			Player player = e.getPlayer();
			String pattern = e.getLine(1);
			String name = e.getLine(2);
			SignType type = SignType.JOIN;
			if(pattern.equalsIgnoreCase("join")) {
				if(name == null || !DataStore.hasRoomData(name)) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED + "その待機部屋は存在しません");
					e.setCancelled(true);
					e.getBlock().breakNaturally();
					return;
				}
				WaitRoomData waitroomdata = DataStore.getRoomData(name);
				e.setLine(0, joinsign);
				e.setLine(1, name);
				e.setLine(2, DataStore.getRoomPlayersList(name).size()+" / 8");
				e.setLine(3, waitroomdata.getBattleType().getType());
				player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"参加用看板設置が完了しました");
			} else if(pattern.equalsIgnoreCase("status")) {
				if(name == null || !DataStore.hasArenaData(name)) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED + "そのステージは存在しません");
					e.setCancelled(true);
					e.getBlock().breakNaturally();
					return;
				}
				type = SignType.STATUS;
				ArenaData arenadata = DataStore.getArenaData(name);
				e.setLine(0, statussign);
				e.setLine(1, arenadata.getName());
				e.setLine(2, arenadata.getGameStatus().getStats());
				player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"ステータス看板の設置が完了しました");
			} else if(pattern.equalsIgnoreCase("shop")) {
				type = SignType.SHOP;
				if(name == null || !DataStore.hasWeaponData(name)) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED + "その武器は存在しません");
					e.setCancelled(true);
					e.getBlock().breakNaturally();
					return;
				}
				WeaponData data = DataStore.getWeapondata(name);
				e.setLine(0, weaponsign);
				e.setLine(1, data.getName());
				e.setLine(2, ""+0);
			}
			OtherFiles.saveSignLoc(name, e.getBlock().getLocation(), type);
		}
	}

	public static void UpdateJoinSign(String room) {
		for(SignData data : DataStore.getSignDataList()) {
			if(data.getType() == SignType.JOIN
					&& data.getName().equalsIgnoreCase(room)) {
				Location l = new Location(Bukkit.getWorld(data.getWorld()), data.getX(), data.getY(), data.getZ());
				Sign sign = (Sign)l.getBlock().getState();
				sign.setLine(2, DataStore.getRoomPlayersList(room).size()+" / 8");
				sign.update();
			}
		}
	}

	public static void UpdateStatusSign(String arena) {
		for(SignData data : DataStore.getSignDataList()) {
			if(data.getType() == SignType.STATUS
					&& data.getName().equalsIgnoreCase(arena)) {
				Location l = new Location(Bukkit.getWorld(data.getWorld()), data.getX(), data.getY(), data.getZ());
				Sign sign = (Sign)l.getBlock().getState();
				sign.setLine(2, DataStore.getArenaData(arena).getGameStatus().getStats());
				sign.update();
			}
		}
	}

	/**
	 *
	 * @param loc 消す看板がある対象の座標
	 * @param name 看板の上から2行目にあるString(部屋名とかステージ名、武器名)
	 * @return その看板が保存されたデータがあった場合はSignDataクラスを返す、データが存在し無かった場合はnull
	 */
	public static SignData getSignData(Location loc, String name) {
		for(SignData data : DataStore.getSignDataList()) {
			if(loc.getWorld().getName().equalsIgnoreCase(data.getWorld())
					&& loc.getX() == data.getX()
					&& loc.getY() == data.getY()
					&& loc.getZ() == data.getZ()
					&& data.getName().equalsIgnoreCase(name))
				return data;
		}
		return null;
	}
}
