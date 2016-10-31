package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.List;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.PlayerStatusData;
import jp.kotmw.splatoon.gamedatas.WeaponData;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InvMenu implements Listener {

	private String selectinv = ChatColor.BOLD.toString()+ChatColor.BLACK+"武器のカテゴリを選択してください";
	private String selectweapon = ChatColor.BOLD.toString()+ChatColor.BLACK+"武器を選択してください";

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		Action action = e.getAction();
		if(action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK
				|| action == Action.PHYSICAL)
			return;
		if(item == null
				|| item.getType() == Material.AIR
				|| !item.getItemMeta().hasDisplayName()
				|| !item.getItemMeta().getDisplayName().equals(GameItems.weaponselector)
				|| !item.getItemMeta().hasLore())
			return;
		player.openInventory(openMenu());
	}

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if(!DataStore.hasPlayerData(e.getWhoClicked().getName()))
			return;
		PlayerData data = DataStore.getPlayerData(e.getWhoClicked().getName());
		e.setCancelled(true);
		if(e.getCurrentItem() == null
				|| e.getCurrentItem().getType() == Material.AIR
				|| !e.getCurrentItem().hasItemMeta())
			return;
		if(e.getInventory().getName().equalsIgnoreCase(selectinv)) {
			if(!isType(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())))
				return;
			WeaponType type = WeaponType.valueOf(ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()));
			e.getWhoClicked().openInventory(getWeaponSelector(type, data.getName()));
		} else if(e.getInventory().getName().equalsIgnoreCase(selectweapon)) {
			String weapon = e.getCurrentItem().getItemMeta().getDisplayName();
			if(!DataStore.hasWeaponData(weapon))
				return;
			data.setWeapon(weapon);
			e.getWhoClicked().closeInventory();
		}
	}

	public Inventory openMenu() {
		Inventory inv = Bukkit.createInventory(null, 9, selectinv);

		ItemStack shooter = new ItemStack(Material.WOOD_HOE);
		ItemMeta shootermeta = shooter.getItemMeta();
		shootermeta.setDisplayName(ChatColor.GREEN+"Shooter");
		shootermeta.setLore(getDescriptionLore(WeaponType.Shooter));
		shooter.setItemMeta(shootermeta);

		ItemStack roller = new ItemStack(Material.STICK);
		ItemMeta rollermeta = roller.getItemMeta();
		rollermeta.setDisplayName(ChatColor.BLUE+"Roller");
		rollermeta.setLore(getDescriptionLore(WeaponType.Roller));
		roller.setItemMeta(rollermeta);

		ItemStack charger = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta chargermeta = charger.getItemMeta();
		chargermeta.setDisplayName(ChatColor.YELLOW+"Charger");
		chargermeta.setLore(getDescriptionLore(WeaponType.Charger));
		charger.setItemMeta(chargermeta);

		inv.setItem(1, shooter);
		inv.setItem(4, roller);
		inv.setItem(7, charger);

		return inv;
	}

	public Inventory getWeaponSelector(WeaponType type, String player) {
		Inventory inv = Bukkit.createInventory(null, 9, selectweapon);
		int i = 0;
		for(WeaponData weapon : getPlayerWeapons(player)) {
			if(!weapon.getType().equals(type))
				continue;
			ItemStack item = new ItemStack(weapon.getItemtype());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(weapon.getName());
			List<String> lore = new ArrayList<String>();
			lore.add("サブウェポン: "+weapon.getSubWeapon());
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(i, item);
			i++;
		}
		return inv;
	}

	private List<String> getDescriptionLore(WeaponType type) {
		List<String> lore = new ArrayList<String>();
		switch(type) {
		case Shooter:
			lore.add(ChatColor.GREEN+"シューター系武器のセレクターを開きます");
			break;
		case Roller:
			lore.add(ChatColor.BLUE+"ローラー系武器のセレクターを開きます");
			break;
		case Charger:
			lore.add(ChatColor.YELLOW+"チャージャー系武器のセレクターを開きます");
			break;
		default:
			break;
		}
		return lore;
	}

	private List<WeaponData> getPlayerWeapons(String player) {
		PlayerStatusData data = DataStore.getStatusData(player);
		List<WeaponData> weapons = new ArrayList<WeaponData>();
		for(String weapon : data.getWeapons())
			weapons.add(DataStore.getWeapondata(weapon));
		return weapons;
	}

	public boolean isType(String typename) {
		for(WeaponType type : WeaponType.values()) {
			if(typename.equalsIgnoreCase(type.toString()))
				return true;
		}
		return false;
	}
}
