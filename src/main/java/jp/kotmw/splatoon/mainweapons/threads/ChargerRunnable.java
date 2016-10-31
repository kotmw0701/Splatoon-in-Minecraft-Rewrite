package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.SplatColor;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.Charger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ChargerRunnable extends BukkitRunnable {

	private String name;
	private int full;
	private String meter = "";
	private PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 3600*20, 1, false, false);

	public ChargerRunnable(String name, int full) {
		this.name = name;
		this.full = full;
		for(int i = 1; i <= full; i++) {
			meter = meter+"|";
		}
	}

	@Override
	public void run() {
		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		PlayerData data = DataStore.getPlayerData(name);
		int tick = data.getTick();
		int charge = data.getCharge();
		if(tick > 0) {
			tick--;
			data.setTick(tick);
			if(charge < full) {
				charge++;
				data.setCharge(charge);
			}
			sendCharge(data, charge);
			Bukkit.getPlayer(data.getName()).addPotionEffect(slow);
		} else if (tick == 0){
			data.setCharge(0);
			data.setTick(-1);
			MainGame.sendTitle(data, 0, 1, 0, " ", " ");
			Charger.launch(data, charge);
			Bukkit.getPlayer(data.getName()).removePotionEffect(PotionEffectType.SLOW);
		}
	}

	private void sendCharge(PlayerData data, int charge) {
		ChatColor inkcolor = SplatColor.conversionChatColor(DataStore.getArenaData(data.getArena()).getDyeColor(data.getTeamid()));
		String coloredmeter = inkcolor+meter.substring(0, charge)+ChatColor.GRAY+meter.substring(charge)+"  ";
		MainGame.sendTitle(data, 0, 10, 0, " ", coloredmeter);
	}
}
