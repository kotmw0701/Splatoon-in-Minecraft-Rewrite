package jp.kotmw.splatoon.gamedatas;

import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class WeaponData {
	private String name;
	private String displayname;
	private Material itemtype;
	private WeaponType type;
	private int damage;
	private float cost;
	private String subweaponname;

	//Shooter
	private int Firespeed;
	private int radius;
	private int angle;
	//Roller
	private int InkSplash;
	private int InkSplashDamage;
	private int InkSplashAngle;
	private int SlowLevel;
	//Charger
	private int fullcharge;

	public WeaponData(String name, FileConfiguration file) {
		this.name = name;
		this.displayname = ChatColor.translateAlternateColorCodes('&', file.getString("ItemInfo.Name"));
		this.itemtype = getItemType(file.getString("ItemInfo.Type"));
		this.type = getWeaponType(file.getString("WeaponInfo.Type"));
		this.damage = file.getInt("WeaponInfo.Damage");
		this.cost = (float) (file.getDouble("WeaponInfo.InkCost")/100);
		this.subweaponname = file.getString("WeaponInfo.SubWeapon");
		this.Firespeed = file.getInt("WeaponInfo.FireSpeed");
		this.radius = file.getInt("WeaponInfo.Radius");
		this.angle = file.getInt("WeaponInfo.Angle");
		this.InkSplash = file.getInt("WeaponInfo.InkSplash");
		this.InkSplashDamage = file.getInt("WeaponInfo.InkSplashDamage");
		this.InkSplashAngle = file.getInt("WeaponInfo.InkSplashAngle");
		this.SlowLevel = file.getInt("WeaponInfo.SlowLevel");
		this.fullcharge = file.getInt("WeaponInfo.FullCharge");
	}

	public String getName() {return name;}

	public String getDisplayname() {return displayname;}

	public Material getItemtype() {return itemtype;}

	public WeaponType getType() {return type;}

	public int getDamage() {return damage;}

	public int getFirespeed() {return Firespeed;}

	public int getRadius() {return radius;}

	public int getAngle() {return angle;}

	public float getCost() {return cost;}

	public int getInkSplash() {return InkSplash;}

	public int getInkSplashDamage() {return InkSplashDamage;}

	public int getInkSplashAngle() {return InkSplashAngle;}

	public int getSlowLevel() {return SlowLevel;}

	public int getFullcharge() {return fullcharge;}

	public String getSubWeapon() {return subweaponname;}

	private Material getItemType(String item) {
		for(Material type : Material.values())
			if(type.toString().equalsIgnoreCase(item))
				return type;
		return null;
	}

	private WeaponType getWeaponType(String weapon) {
		for(WeaponType type : WeaponType.values())
			if(type.toString().equalsIgnoreCase(weapon))
				return type;
		return null;
	}
}
