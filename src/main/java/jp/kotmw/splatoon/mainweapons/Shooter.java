package jp.kotmw.splatoon.mainweapons;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.threads.ShooterRunnable;
import jp.kotmw.splatoon.manager.Paint;

public class Shooter implements Listener {

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
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Shooter)
			return;
		if(data.isAllCancel()
				|| item == null
				|| item.getType() != DataStore.getWeapondata(data.getWeapon()).getItemtype()
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon()))
			return;
		WeaponData weapondata = DataStore.getWeapondata(data.getWeapon());
		if(player.getExp() < weapondata.getCost()) {
			MainGame.sendTitle(data, 0, 5, 0, " ", ChatColor.RED+"インクがありません!");
			return;
		}
		int tick = 1;
		if(weapondata.getFirespeed() < 5)
			tick=tick+(5-weapondata.getFirespeed());
		if(data.getTask() == null) {
			BukkitRunnable task = new ShooterRunnable(player.getName());
			task.runTaskTimer(Main.main, 0, weapondata.getFirespeed());
			data.setTask(task);
		}
		data.setTick(tick);
	}

	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if(!(e.getEntity() instanceof Snowball)
				|| !(e.getEntity().getShooter() instanceof Player))
			return;
		Player player = (Player) e.getEntity().getShooter();
		if(!DataStore.hasPlayerData(player.getName()))
			return;
		if(DataStore.getPlayerData(player.getName()).getArena() == null)
			return;
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Shooter)
			return;
		Paint.SpherePaint(e.getEntity().getLocation(), DataStore.getWeapondata(data.getWeapon()).getRadius(), data);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getDamager() instanceof Snowball && DataStore.hasPlayerData(e.getEntity().getName())) {
			Snowball ball = (Snowball) e.getDamager();
			if(!(ball.getShooter() instanceof Player))
				return;
			if(!(e.getEntity() instanceof Player))
				return;
			Player player = (Player) e.getEntity(), shooter = (Player) ball.getShooter();
			if(!DataStore.hasPlayerData(shooter.getName())
					|| player.getName() == shooter.getName()
					|| DataStore.getPlayerData(player.getName()).getTeamid() == DataStore.getPlayerData(shooter.getName()).getTeamid())
				return;
			WeaponData data = DataStore.getWeapondata(DataStore.getPlayerData(shooter.getName()).getWeapon());
			if(data.getType() != WeaponType.Shooter)
				return;
			e.setDamage(data.getDamage());
		}
	}

	@EventHandler
	public void onArmorstanddamage(EntityDamageByEntityEvent e) {
		if(e.getEntity().getType() != EntityType.ARMOR_STAND || !(e.getDamager() instanceof Snowball))
			return;
		Snowball ball = (Snowball) e.getDamager();
		if(!(ball.getShooter() instanceof Player) || !DataStore.hasPlayerData(((Player)ball.getShooter()).getName()))
			return;
		e.setCancelled(true);
	}

	public static void shoot(PlayerData data) {
		Player player = Bukkit.getPlayer(data.getName());
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
		player.setExp((float) (player.getExp()-weapon.getCost()));
		Paint.SpherePaint(player.getLocation(), DataStore.getWeapondata(data.getWeapon()).getRadius(), data);
		Random random = new Random();
		int angle = weapon.getAngle()*100;
		double x = Math.toRadians((random.nextInt(angle)/100)-((weapon.getAngle()-1)/2));
		double z = Math.toRadians((random.nextInt(angle)/100)-((weapon.getAngle()-1)/2));
		Vector direction = player.getLocation().getDirection().clone();
		MainGame.sync(() -> {
			Snowball snowball = player.launchProjectile(Snowball.class);
			Vector vec = new Vector(x,0,z), vec2 = new Vector(direction.getX()*0.75, direction.getY()*0.75, direction.getZ()*0.75);
			vec2.add(vec);
			snowball.setVelocity(vec2);
		});
	}

	/*
	 * シューター仕様のまとめ
	 * ・tick指定で連射速度を指定可能
	 * ・着弾地点&発射地点の着色の半径も指定可能
	 *
	 * [発射の角度の乱数関係のまとめ]
	 *   テストとして12°の角度の範囲内での乱数とする
	 *   正面が半分になるようにするとなると、12を半分に割った6°:6°で左右に分ける(分けないと片方だけ飛んでいくって感じになる)
	 *   -6～6の範囲内を乱数で取る
	 *   それぞれの角度をそれぞれのベクトルに変換し、XとZのベクトルに乗算(加算？)する
	 *
	 */
}
