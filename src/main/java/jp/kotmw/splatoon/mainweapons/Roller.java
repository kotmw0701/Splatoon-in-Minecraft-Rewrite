package jp.kotmw.splatoon.mainweapons;

import java.util.Random;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.threads.RollerRunnable;
import jp.kotmw.splatoon.manager.Paint;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Roller implements Listener {

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
		if(DataStore.getWeapondata(player.getWeapon()).getType() != WeaponType.Roller)
			return;
		if(player.isAllCancel()
				|| item == null
				|| item.getType() != DataStore.getWeapondata(player.getWeapon()).getItemtype()
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(player.getWeapon()))
			return;
		WeaponData weapon = DataStore.getWeapondata(player.getWeapon());
		if(p.getExp() < weapon.getCost()) {
			MainGame.sendTitle(player, 0, 5, 0, " ", ChatColor.RED+"インクがありません!");
			return;
		}
		if(player.getTask() == null) {
			BukkitRunnable task = new RollerRunnable(p.getName());
			task.runTaskTimer(Main.main, 0, 1);
			player.setTask(task);
		}
		player.setTick(5);
		p.setExp((float) (p.getExp()-weapon.getCost()));
		if(!player.isPaint()) {
			player.setPaint(true);
			//p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 5));
			RollerSplash(p, 12);
			Location loc = p.getLocation().clone();
			RollPaint(player,
					PlayerDirectionID_Eight(loc.getYaw()),
					loc.getWorld(),
					loc.getBlockX(),
					loc.getBlockY(),
					loc.getBlockZ());
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(!DataStore.hasPlayerData(p.getName()))
			return;
		if(DataStore.getPlayerData(e.getPlayer().getName()).getArena() == null)
			return;
		PlayerData player = DataStore.getPlayerData(p.getName());
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		WeaponData weapon = DataStore.getWeapondata(player.getWeapon());
		if(weapon.getType() != WeaponType.Roller)
			return;
		if(player.isAllCancel()
				|| item == null
				|| item.getType() != weapon.getItemtype()
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(player.getWeapon()))
			return;
		if(!player.isPaint())
			return;
		if(p.getExp() < weapon.getCost()) {
			MainGame.sendTitle(player, 0, 5, 0, " ", ChatColor.RED+"インクがありません!");
			return;
		}
		Location loc = p.getLocation().clone();
		p.setExp((float) (p.getExp()-weapon.getCost()));
		RollPaint(player,
				PlayerDirectionID_Eight(loc.getYaw()),
				loc.getWorld(),
				loc.getBlockX(),
				loc.getBlockY(),
				loc.getBlockZ());
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
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Roller)
			return;
		Paint.SpherePaint(e.getEntity().getLocation(), 1.4, data);
	}

	private void RollPaint(PlayerData data, int DirectionID, World world, int Xo, int Yo, int Zo) {
		if(DirectionID == 0)
			Zo += 2;
		else if(DirectionID == 1)
			Zo += 3;
		else if(DirectionID == 2)
			Xo -= 2;
		else if(DirectionID == 3)
			Xo -= 3;
		else if(DirectionID == 4)
			Zo -= 2;
		else if(DirectionID == 5)
			Zo -= 3;
		else if(DirectionID == 6)
			Xo += 2;
		else if(DirectionID == 7)
			Xo += 3;
		int x1, x2, y1, y2, z1, z2;
		int damage = DataStore.getWeapondata(data.getWeapon()).getDamage();
		y1 = Yo - 1; y2 = Yo;
		if(DirectionID == 0 || DirectionID == 4) {
			x1 = Xo - 2; x2 = Xo + 2;
			for (int xPoint = x1; xPoint <= x2; xPoint++) {
				for (int yPoint = y1; yPoint <= y2; yPoint++) {
					Block block = world.getBlockAt(xPoint, yPoint, Zo);
					Paint.PaintWool(data, block);
					MainGame.Damager(data, xPoint, yPoint, Zo, damage);
				}
			}
			return;
		} else if(DirectionID == 2 || DirectionID == 6) {
			z1 = Zo - 2; z2 = Zo + 2;
			for (int zPoint = z1; zPoint <= z2; zPoint++) {
				for (int yPoint = y1; yPoint <= y2; yPoint++) {
					Block block = world.getBlockAt(Xo, yPoint, zPoint);
					Paint.PaintWool(data, block);
					MainGame.Damager(data, Xo, yPoint, zPoint, damage);
				}
			}
			return;
		} else if(DirectionID == 1
				|| DirectionID == 3
				|| DirectionID == 5
				|| DirectionID == 7) {
			for(int i = 0; i<=3 ; i++) {
				for (int yPoint = y1; yPoint <= y2; yPoint++) {
					int x = Xo, z = Zo;
					Block block = null;
					Block block_ = null;
					if(DirectionID == 1) {
						x-=i; z-=i;
						block = world.getBlockAt(x, yPoint, z);
						if(i<3)
							block_ = world.getBlockAt(x, yPoint, z-1);
					} else if(DirectionID == 3) {
						x+=i; z-=i;
						block = world.getBlockAt(x, yPoint, z);
						if(i<3)
							block_ = world.getBlockAt(x+1, yPoint, z);
					} else if(DirectionID == 5) {
						x+=i; z+=i;
						block = world.getBlockAt(x, yPoint, z);
						if(i<3)
							block_ = world.getBlockAt(x, yPoint, z+1);
					} else if(DirectionID == 7) {
						x-=i; z+=i;
						block = world.getBlockAt(x, yPoint, z);
						if(i<3)
							block_ = world.getBlockAt(x-1, yPoint, z);
					}
					Paint.PaintWool(data, block);
					Paint.PaintWool(data, block_);
					MainGame.Damager(data, x, yPoint, z, damage);
					MainGame.Damager(data, block_, damage);
				}
			}
		}
	}

	public static void RollerSplash(Player player, int count) {
		for(int i = 1; i<=count; i++) {
			Vector direction = player.getLocation().getDirection().clone();
			Snowball ball = player.launchProjectile(Snowball.class);
			Random random = new Random();
			int angle = 45*100;
			double x = Math.toRadians((random.nextInt(angle)/100)-((45-1)/2));
			double z = Math.toRadians((random.nextInt(angle)/100)-((45-1)/2));
			direction.add(new Vector(x,0,z));
			ball.setVelocity(direction);
			ball.setShooter(player);
		}
	}

	public static int PlayerDirectionID_Eight(Float dir) {
		int id = -1;
		// 16 = 22.5
		// 8 = 45
		if(((dir >= 0.0&&dir <= 22.55)||(dir >= 337.56&&dir <= 360.0))
				||((dir <= 0.0&&dir >= -22.55)||(dir <= -337.56&&dir >= -360.0)))
			id = 0;
		else if((dir >= 22.56&&dir <= 67.55)
				||(dir <= -292.56&&dir >= -337.55))
			id = 1;
		else if((dir >= 67.56&&dir <= 112.55)
				||(dir <= -247.56&&dir >= -292.55))
			id = 2;
		else if((dir >= 112.56&&dir <= 157.55)
				||(dir <= -202.56&&dir >= -247.55))
			id = 3;
		else if((dir >= 157.56&&dir <= 202.55)
				||(dir <= -157.56&&dir >= -202.55))
			id = 4;
		else if((dir >= 202.56&&dir <= 247.55)
				||(dir <= -112.56&&dir >= -157.55))
			id = 5;
		else if((dir >= 247.56&&dir <= 292.55)
				||(dir <= -67.56&&dir >= -112.55))
			id = 6;
		else if((dir >= 292.56&&dir <= 337.55)
				||(dir <= -22.56&&dir >= -67.55))
			id = 7;
		return id;
	}

	public static int PlayerDirectionID_Four(Float dir)
	{
		int Direction = 5;
		if(((dir >= 0.0&&dir <= 44.5)||(dir >= 314.6&&dir <= 360.0))
				||((dir <= -0.0&&dir >= -44.5)||(dir <= -314.6&&dir >= -360.0)))//0
			Direction = 0;
		else if ((dir >= 44.6&&dir <= 134.5)
				||(dir <= -224.6&&dir >= -314.5))//1
			Direction = 1;
		else if ((dir >= 134.6&&dir <= 224.5)
				||(dir <= -134.6&&dir >= -224.5))//2
			Direction = 2;
		else if ((dir >= 224.6&&dir <= 314.5)
				||(dir <= -44.6&&dir >= -134.5))//3
			Direction = 3;
		return Direction;
	}
}
