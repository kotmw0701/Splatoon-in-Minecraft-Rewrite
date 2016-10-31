package jp.kotmw.splatoon.subweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BombType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.Paint;
import jp.kotmw.splatoon.subweapon.threads.SplashBombRunnable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Bomb implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName())
				|| DataStore.getPlayerData(e.getPlayer().getName()).getArena() == null)
			return;
		Action action = e.getAction();
		if(action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK
				|| action == Action.PHYSICAL)
			return;
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();
		PlayerData player = DataStore.getPlayerData(p.getName());
		SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(player.getWeapon()).getSubWeapon());
		if(player.isAllCancel()
				|| item == null
				|| item.getType() != subweapon.getItemtype()
				|| !item.hasItemMeta()
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(subweapon.getName()))
			return;
		if(p.getExp() < subweapon.getCost()) {
			MainGame.sendTitle(player, 0, 5, 0, " ", ChatColor.RED+"インクがありません!");
			return;
		}
		launch(p, subweapon);
		player.setInkCoolTime(subweapon.getCooltime());
		e.setCancelled(true);
	}

	private void launch(Player player, SubWeaponData data) {
		float ink = player.getExp();
		player.setExp((float) (ink-data.getCost()));
		switch(data.getType()) {
		case QuickBomb:
			player.launchProjectile(ThrownExpBottle.class, player.getLocation().getDirection());
			break;
		case SplashBomb:
			TNTPrimed tntprimed = (TNTPrimed) player.getLocation().getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.PRIMED_TNT);
			tntprimed.setFuseTicks(120*20);
			tntprimed.setYield(0);
			tntprimed.setVelocity(player.getLocation().getDirection());
			new SplashBombRunnable(DataStore.getPlayerData(player.getName()), tntprimed).runTaskTimer(Main.main, 0, 1);
			break;
		case SuckerBomb:
			player.launchProjectile(ThrownExpBottle.class, player.getLocation().getDirection());
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if(e.getEntity().getShooter() instanceof Player) {
			if(!DataStore.hasPlayerData(((Player)e.getEntity().getShooter()).getName()))
				return;
			Player player = (Player) e.getEntity().getShooter();
			if(player.getInventory().getItemInMainHand().getType().equals(getLaunchItem(e.getEntity())))
				e.setCancelled(true);
		}
	}

	@EventHandler
	public void onExplodeExpBottle(ExpBottleEvent e) {
		if(!(e.getEntity() instanceof ThrownExpBottle)
				|| !(e.getEntity().getShooter() instanceof Player))
			return;
		Player player = (Player) e.getEntity().getShooter();
		if(!DataStore.hasPlayerData(player.getName())
				|| DataStore.getPlayerData(player.getName()).getArena() == null)
			return;
		e.setExperience(0);
		PlayerData data = DataStore.getPlayerData(player.getName());
		SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(data.getWeapon()).getSubWeapon());
		if(subweapon.getType() == BombType.QuickBomb) {
			Paint.SpherePaint(e.getEntity().getLocation(), 4, data);
			MainGame.SphereDamager(data, e.getEntity().getLocation(), subweapon.getDamage(), 4);
			return;
		} else if(subweapon.getType() == BombType.SuckerBomb) {
			ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(e.getEntity().getLocation().add(0,-1,0), EntityType.ARMOR_STAND);
			TNTPrimed tntprimed = (TNTPrimed) player.getLocation().getWorld().spawnEntity(e.getEntity().getLocation().add(0, 1, 0), EntityType.PRIMED_TNT);
			tntprimed.setFuseTicks(2*20);
			tntprimed.setYield(0);
			stand.setCustomName(player.getName());
			stand.setPassenger(tntprimed);
			stand.setGravity(false);
			stand.setVisible(false);
			stand.setSmall(true);
		}
	}

	@EventHandler
	public void onExplode(ExplosionPrimeEvent e) {
		Entity entity = e.getEntity();
		Entity stand = entity.getVehicle();
		if(entity.getVehicle() == null)
			return;
		String name = stand.getCustomName();
		if(name == null)
			return;
		stand.remove();
		if(!DataStore.hasPlayerData(name)
				|| DataStore.getPlayerData(name).getArena() == null)
			return;
		PlayerData data = DataStore.getPlayerData(name);
		SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(data.getWeapon()).getSubWeapon());
		Paint.SpherePaint(e.getEntity().getLocation(), 4, data);
		MainGame.SphereDamager(data, e.getEntity().getLocation(), subweapon.getDamage(), 4);
	}

	private static Material getLaunchItem(Projectile projectile) {
		switch(projectile.getType()) {
		case ARROW:
			return Material.ARROW;
		case THROWN_EXP_BOTTLE:
			return Material.EXP_BOTTLE;
		case ENDER_PEARL:
			return Material.ENDER_PEARL;
		case ENDER_SIGNAL:
			return Material.EYE_OF_ENDER;
		case SPLASH_POTION:
			return Material.SPLASH_POTION;
		case FISHING_HOOK:
			return Material.FISHING_ROD;
		case SNOWBALL:
			return Material.SNOW_BALL;
		case EGG:
			return Material.EGG;
		default:
			break;
		}
		return null;
	}
}
