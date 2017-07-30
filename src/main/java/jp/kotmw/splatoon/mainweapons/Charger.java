package jp.kotmw.splatoon.mainweapons;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.threads.ChargerRunnable;
import jp.kotmw.splatoon.manager.Paint;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

public class Charger implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(DataStore.getPlayerData(e.getPlayer().getName()).getArena() == null)
			return;
		Action action = e.getAction();
		if(action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK
				|| action == Action.PHYSICAL)
			return;
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();
		PlayerData player = DataStore.getPlayerData(p.getName());
		if(DataStore.getWeapondata(player.getWeapon()).getType() != WeaponType.Charger)
			return;
		if(player.isAllCancel()
				|| item == null
				|| item.getType() == Material.AIR
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(player.getWeapon()))
			return;
		WeaponData weapon = DataStore.getWeapondata(player.getWeapon());
		if(p.getExp() < weapon.getCost() && player.getCharge() <= 0) {
			MainGame.sendTitle(player, 0, 5, 0, " ", ChatColor.RED+"インクがありません!");
			return;
		}
		if(player.getCharge() <= 0)
			p.setExp((float) (p.getExp()-weapon.getCost()));
		if(player.getTask() == null) {
			BukkitRunnable task = new ChargerRunnable(p.getName(), weapon.getFullcharge());
			task.runTaskTimer(Main.main, 0, 1);
			player.setTask(task);
		}
		player.setTick(5);
	}

	public static void launch(PlayerData data, int charge) {
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
		Paint.SpherePaint(Bukkit.getPlayer(data.getName()).getLocation(), 1.2, data);
		//int full = weapon.getFullcharge();
		int shootlength = 35;
		ArenaData arena = DataStore.getArenaData(data.getArena());
		BlockIterator seeblock = new BlockIterator(Bukkit.getPlayer(data.getName()), shootlength);
		while(seeblock.hasNext()) {
			Block block = seeblock.next();
			Location loc = block.getLocation().clone();
			while(loc.getBlock().getType() == Material.AIR) {
				if(loc.getBlockY() <=arena.getStagePosition2().getY())
					break;
				loc.add(0,-1,0);
			}
			Paint.SpherePaint(loc, 1.5, data);
			MainGame.Damager(data, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), weapon.getDamage());
		}
	}
}
