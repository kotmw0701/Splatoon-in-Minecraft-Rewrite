package jp.kotmw.splatoon.maingame;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.manager.SplatColorManager;

public class SquidMode implements Listener {

	PotionEffect invisible = new PotionEffect(PotionEffectType.INVISIBILITY, 3600*20, 1, false, false);
	PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 3600*20, 3, false, false);

	@EventHandler
	public void changeSquid(PlayerToggleSneakEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(DataStore.getPlayerData(e.getPlayer().getName()).getArena() == null)
			return;
		if(!e.isSneaking())
			return;
		Player player = e.getPlayer();
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(data.isDead() || data.isAllCancel() || data.isClimb())
			return;
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
		if(data.isSquidMode()) {
			LivingEntity squid = data.getPlayerSquid();
			if(squid != null)
				squid.remove();
			data.setPlayerSquid(null);
			data.setSquidMode(false);
			data.setClimb(false);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.getInventory().setHeldItemSlot(0);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.removePotionEffect(PotionEffectType.SPEED);
		} else {
			player.addPotionEffect(invisible);
			player.getInventory().setHeldItemSlot(3);
			if(SplatColorManager.isBelowBlockTeamColor(player, true)) {
				player.addPotionEffect(speed);
			} else {
				spawnSquid(player);
			}
			data.setSquidMode(true);
			if(SplatColorManager.isTargetBlockTeamColor(player)) {
				data.setClimb(true);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(!DataStore.getPlayerData(e.getPlayer().getName()).isSquidMode())
			return;
		Player player = e.getPlayer();
		PlayerData data = DataStore.getPlayerData(player.getName());
		LivingEntity squid = data.getPlayerSquid();
		if(squid != null)
			squid.teleport(player.getLocation());
		if(SplatColorManager.isTargetBlockTeamColor(player)) {
			data.setClimb(true);
			player.setAllowFlight(true);
			player.setFlying(true);
		} else if(!SplatColorManager.isTargetBlockTeamColor(player)) {
			data.setClimb(false);
			player.setAllowFlight(false);
			player.setFlying(false);
		}
		if(!SplatColorManager.isBelowBlockTeamColor(player, true) && !data.isClimb()) {
			spawnSquid(player);
			player.removePotionEffect(PotionEffectType.SPEED);
		} else if(SplatColorManager.isBelowBlockTeamColor(player, true) || data.isClimb()) {
			player.addPotionEffect(speed);
			if(squid != null) {
				squid.remove();
				data.setPlayerSquid(null);
			}
		}
		if(canSlipBlock_under(player.getLocation())) player.teleport(player.getLocation().add(0, -0.1, 0));
		/*Block block = canSlipBlock_front(e);
		if(block == null)
			return;
		new ParticleAPI.Particle(EnumParticle.REDSTONE, 
				block.getLocation().clone().add(0.5, 0, 0.5),
				0.1f, 
				0.1f, 
				0.1f, 
				1,
				0).sendParticle(player);*/
	}

	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(!DataStore.getPlayerData(e.getPlayer().getName()).isSquidMode())
			return;
		if(e.getNewSlot() < 3)
			e.setCancelled(true);
	}

	private void spawnSquid(Player player) {
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(data.getPlayerSquid() != null)
			return;
		LivingEntity squid = (LivingEntity)player.getWorld().spawnEntity(player.getLocation(), EntityType.SQUID);
		squid.setCustomName(player.getName());
		squid.setAI(false);
		data.setPlayerSquid(squid);
	}
	
	private boolean canSlipBlock_under(Location location) {
		Location loc = location.clone().add(0, -1, 0);
		return isSlipBlock(loc);
	}
	
	@SuppressWarnings("unused")
	private Block canSlipBlock_front(PlayerMoveEvent e) {
		Location before = e.getFrom(), after = e.getTo();
		double x = before.getX() - after.getX(), 
				z = before.getZ() - after.getZ();
		if(Math.abs(x) >= Math.abs(z)) {
			for(int i = 0; i<= 1; i++) {
				Location loc = after.clone().add((x >= 0 ? i : -i), 0, 0);
				if(isSlipBlock(loc))
					return loc.getBlock();
			}
		} else if(Math.abs(z) >= Math.abs(x)) {
			for(int i = 0; i<= 1; i++) {
				Location loc = after.clone().add(0, 0, (z >= 0 ? i : -i));
				if(isSlipBlock(loc))
					return loc.getBlock();
			}
		}
		return null;
	}
	
	public boolean isSlipBlock(Location location) {
		return DataStore.getConfig().getCanSplitBlocks().contains(location.getBlock().getType().toString());
	}
}
