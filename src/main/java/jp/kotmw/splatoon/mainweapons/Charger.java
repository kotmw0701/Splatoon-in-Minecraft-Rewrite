package jp.kotmw.splatoon.mainweapons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.threads.ChargerRunnable;

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
			MainGame.sendActionBar(player, ChatColor.RED+"インクがありません!");
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
}
