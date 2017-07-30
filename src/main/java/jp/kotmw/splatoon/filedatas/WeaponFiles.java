package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BombType;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.gamedatas.WeaponData;

public class WeaponFiles extends PluginFiles {

	static String filedir = "Weapons";

	public static void createTemplateShooterFile() {
		if(DirFile(filedir, "SplatShooter").exists())
			return;
		FileConfiguration file = new YamlConfiguration();
		file.set("ItemInfo.Name", "SplatShooter");
		file.set("ItemInfo.Type", Material.WOOD_HOE.toString());
		file.set("WeaponInfo.Type", WeaponType.Shooter.toString());
		file.set("WeaponInfo.Damage", 2);
		file.set("WeaponInfo.FireSpeed", 2);
		file.set("WeaponInfo.Radius", 2);
		file.set("WeaponInfo.InkCost", 0.9);
		file.set("WeaponInfo.Angle", 12);
		file.set("WeaponInfo.SubWeapon", "QuickBomb");
		SettingFiles(file, DirFile(filedir, "SplatShooter"));
	}

	public static void createTemplateRollerFile() {
		if(DirFile(filedir, "SplatRoller").exists())
			return;
		FileConfiguration file = new YamlConfiguration();
		file.set("ItemInfo.Name", "SplatRoller");
		file.set("ItemInfo.Type", Material.STICK.toString());
		file.set("WeaponInfo.Type", WeaponType.Roller.toString());
		file.set("WeaponInfo.Damage", 20);
		file.set("WeaponInfo.InkSplash", 12);
		file.set("WeaponInfo.InkSplashDamage", 10);
		file.set("WeaponInfo.InkSplashAngle", 45);
		file.set("WeaponInfo.SlowLevel", 0);
		file.set("WeaponInfo.InkCost", 0.1);
		file.set("WeaponInfo.SubWeapon", "SuckerBomb");
		SettingFiles(file, DirFile(filedir, "SplatRoller"));
	}

	public static void createTemplateChargerFile() {
		if(DirFile(filedir, "SplatCharger").exists())
			return;
		FileConfiguration file = new YamlConfiguration();
		file.set("ItemInfo.Name", "SplatCharger");
		file.set("ItemInfo.Type", Material.DIAMOND_HOE.toString());
		file.set("WeaponInfo.Type", WeaponType.Charger.toString());
		file.set("WeaponInfo.Damage", 20);
		file.set("WeaponInfo.InkCost", 18);
		file.set("WeaponInfo.FullCharge", 20);
		file.set("WeaponInfo.SubWeapon", "SplashBomb");
		SettingFiles(file, DirFile(filedir, "SplatCharger"));
	}

	public static List<String> getWeaponList() {
		return getFileList(new File(filepath + filedir));
	}

	public static File WeaponDir() {
		return new File(filepath + filedir);
	}

	public static boolean exists(String weaponname) {
		for(String weapon : getWeaponList())
			if(weapon.equals(weaponname))
				return true;
		return false;
	}

	public static void AllWeaponReload() {
		for(String weapon : getWeaponList()) {
			FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, weapon));
			WeaponData data = new WeaponData(weapon, file);
			DataStore.addWeaponData(weapon, data);
		}
	}

	public static void AllSubWeaponReload() {
		SubWeaponData quickbomb = new SubWeaponData("QuickBomb", Material.SLIME_BALL, BombType.QuickBomb, 12, 5, 60, 40, 6);//35, 25  (直撃60?めんｄ(ry))
		SubWeaponData splashbomb = new SubWeaponData("SplashBomb", Material.TNT, BombType.SplashBomb, 36, 6, 0, 70, 20);//180, 30
		SubWeaponData suckerbomb = new SubWeaponData("SuckerBomb", Material.BREWING_STAND_ITEM, BombType.SuckerBomb, 36, 6, 0, 70, 20);//180, 30
		DataStore.addSubWeaponData(quickbomb.getName(), quickbomb);
		DataStore.addSubWeaponData(splashbomb.getName(), splashbomb);
		DataStore.addSubWeaponData(suckerbomb.getName(), suckerbomb);
	}
}
