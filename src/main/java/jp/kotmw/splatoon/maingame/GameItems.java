package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.List;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.gamedatas.WeaponData;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GameItems {

	public static String weaponselector = ChatColor.GREEN
			+ ChatColor.BOLD.toString()
			+ "WeaponSelector"
			+ ChatColor.RESET.toString() +ChatColor.GRAY
			+ " [Right Click]";

	public static ItemStack getSelectItem() {
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(weaponselector);
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD + "右クリックで武器選択の表示");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getWeaponItem(WeaponData data) {
		ItemStack item = new ItemStack(data.getItemtype());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(data.getDisplayname());
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GOLD+"SplatoonPluginItem");
		lore.add("----Status-----");
		lore.add("WeaponType: "+data.getType().toString());
		lore.add("Ink Cost: "+data.getCost());
		lore.add("Damage: "+data.getDamage());
		if(data.getType() == WeaponType.Shooter)
			lore.add("Fire speed: "+data.getFirespeed());
		else if(data.getType() == WeaponType.Roller)
			lore.add("SlowLevel: "+data.getSlowLevel());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack getSubWeaponItem(WeaponData data) {
		SubWeaponData subweapon = DataStore.getSubWeaponData(data.getSubWeapon());
		ItemStack item = new ItemStack(subweapon.getItemtype());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(subweapon.getType().toString());
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GOLD+"SplatoonPluginItem");
		lore.add("-----Status-----");
		lore.add("Bomb");
		lore.add("Bomb!");
		lore.add("Bomb!!");
		lore.add("Bomb!!!");
		lore.add("Bomb!!!!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
