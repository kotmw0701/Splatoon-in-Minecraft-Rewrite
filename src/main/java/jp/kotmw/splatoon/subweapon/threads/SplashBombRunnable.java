package jp.kotmw.splatoon.subweapon.threads;

import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;

public class SplashBombRunnable extends BukkitRunnable {
	private PlayerData data;
	private TNTPrimed bomb;

	public SplashBombRunnable(PlayerData data, TNTPrimed tnt) {
		this.data = data;
		this.bomb = tnt;
	}

	@Override
	public void run() {
		int tick = bomb.getFuseTicks();
		if(bomb.isOnGround() && tick > 20) {
			bomb.setFuseTicks(1*20);
		}
		if(tick == 0) {
			SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(data.getWeapon()).getSubWeapon());
			Paint.SpherePaint(bomb.getLocation(), 4, data);
			MainGame.SphereDamager(data, bomb.getLocation(), subweapon, 4, false);
			this.cancel();
		}
	}
}
