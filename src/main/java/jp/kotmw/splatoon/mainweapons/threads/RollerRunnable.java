package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class RollerRunnable extends BukkitRunnable {

	String name;

	public RollerRunnable(String name) {
		this.name = name;
	}

	@Override
	public void run() {
		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		PlayerData data = DataStore.getPlayerData(name);
		PotionEffect potion = new PotionEffect(PotionEffectType.SLOW,
				30*20,
				DataStore.getWeapondata(data.getWeapon()).getSlowLevel(),
				false,
				false);
		int tick = data.getTick();
		if(tick > 0) {
			if(!data.isPaint()) {
				data.setPaint(true);
				Bukkit.getPlayer(name).addPotionEffect(potion);
			}
			tick--;
			data.setTick(tick);
		} else {
			if(data.isPaint()) {
				data.setPaint(false);
				Bukkit.getPlayer(name).removePotionEffect(PotionEffectType.SLOW);
			}
		}
	}
}
