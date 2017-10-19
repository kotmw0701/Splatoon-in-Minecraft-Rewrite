package jp.kotmw.splatoon.maingame;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.event.ArenaStatusChangeEvent;
import jp.kotmw.splatoon.event.PlayerGameJoinEvent;
import jp.kotmw.splatoon.event.PlayerGameLeaveEvent;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.maingame.threads.RespawnRunnable;

public class Listeners implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlace(BlockPlaceEvent e) {
		if(DataStore.hasPlayerData(e.getPlayer().getName())) {
			e.setCancelled(true);
			return;
		}
		e.setCancelled(isIn(e.getPlayer(), e.getBlock()));
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBreak(BlockBreakEvent e) {
		if(DataStore.hasPlayerData(e.getPlayer().getName())) {
			e.setCancelled(true);
			return;
		}
		e.setCancelled(isIn(e.getPlayer(), e.getBlock()));
	}
	
	private boolean isIn(Player player, Block block) {
		for(ArenaData data : DataStore.getArenaList()) {
			if(!data.isStatus())
				continue;
			if(!data.getWorld().equalsIgnoreCase(block.getWorld().getName()))
				continue;
			if(((data.getStagePosition1().getBlockX() >= block.getX()) && (block.getX() >= data.getStagePosition2().getBlockX()))
					&& ((data.getStagePosition1().getBlockY() >= block.getY()) && (block.getY() >= data.getStagePosition2().getBlockY()))
					&& ((data.getStagePosition1().getBlockZ() >= block.getZ()) && (block.getZ() >= data.getStagePosition2().getBlockZ()))) {
				player.sendMessage(MainGame.Prefix+ChatColor.RED+"範囲内編集をするには、Editmodeを有効にしてください");
				player.sendMessage(MainGame.Prefix+"対象ステージ : "+data.getName());
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onHung(FoodLevelChangeEvent e) {
		if(!(e.getEntity() instanceof Player))
			return;
		if(DataStore.hasPlayerData(e.getEntity().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInvClick(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player))
			return;
		if(DataStore.hasPlayerData(e.getWhoClicked().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onDrop(PlayerDropItemEvent e) {
		if(DataStore.hasPlayerData(e.getPlayer().getName()))
			e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		Player player = e.getPlayer();
		if(!DataStore.getPlayerData(player.getName()).isMove()) {
			Location from = e.getFrom(), to = e.getTo();
			if(from.getX() != to.getX()
					|| from.getY() != to.getY()
					|| from.getZ() != to.getZ())
			e.setCancelled(true);
			return;
		}
		Location location = player.getLocation();
		if(location.getBlock().getType() == Material.WATER ||
				location.getBlock().getType() == Material.STATIONARY_WATER) {
			player.damage(20);
		}
		if(DataStore.getPlayerData(player.getName()).getArena() == null)
			return;
		ArenaData data = DataStore.getArenaData(DataStore.getPlayerData(player.getName()).getArena());
		if(location.getBlockY() <= (data.getStagePosition2().getBlockY()-1))
			player.damage(20);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(!(e.getEntity() instanceof Player))
			return;
		Player p = (Player)e.getEntity();
		if(!DataStore.hasPlayerData(p.getName()))
			return;
		if(DataStore.getPlayerData(p.getName()).isAllCancel()
				|| e.getCause() == DamageCause.FALL
				|| e.getCause() == DamageCause.ENTITY_ATTACK)
			e.setCancelled(true);
	}

	@EventHandler
	public void onSquidDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		if(entity.getType() != EntityType.SQUID)
			return;
		if(DataStore.hasPlayerData(entity.getCustomName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		MainGame.leave(e.getPlayer());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		if(!DataStore.hasPlayerData(e.getEntity().getName()))
			return;
		if(DataStore.getPlayerData(e.getEntity().getName()).getArena() == null)
			return;
		Player player = e.getEntity();
		e.setDeathMessage("");
		e.setKeepInventory(true);
		player.getInventory().setHeldItemSlot(8);
		player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
		player.setRemainingAir(player.getMaximumAir());
		player.setFoodLevel(20);
		player.setGameMode(GameMode.SPECTATOR);
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(data.isSquidMode()) {
			LivingEntity squid = data.getPlayerSquid();
			if(squid != null)
				squid.remove();
			data.setPlayerSquid(null);
			data.setSquidMode(false);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.removePotionEffect(PotionEffectType.SPEED);
		}
		data.setDead(true);
		new RespawnRunnable(5, player).runTaskTimer(Main.main, 0, 20);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onGameJoin(PlayerGameJoinEvent e) {
		if(!e.getRoom().isLimitBreak())
			if(e.getJoinPlayerDatas().size() == 8) {
				e.setCancelled(true, ChatColor.RED+"ルーム参加人数上限のため、参加できません");
				return;
			}
		e.getPlayer().sendMessage(MainGame.Prefix+ChatColor.GREEN.toString()+ChatColor.BOLD+e.getPlayer().getName()
					+ChatColor.YELLOW+" がゲームに参加しました "
					+ChatColor.WHITE+"[ "+ChatColor.GOLD+(e.getJoinPlayerDatas().size()+1)+(e.getRoom().isLimitBreak() ? "" : "/8")+ChatColor.WHITE+" ]");
		for(PlayerData data : e.getJoinPlayerDatas()) {
			MainGame.sendMessage(data, ChatColor.GREEN.toString()+ChatColor.BOLD+e.getPlayer().getName()
					+ChatColor.YELLOW+" がゲームに参加しました "
					+ChatColor.WHITE+"[ "+ChatColor.GOLD+(e.getJoinPlayerDatas().size()+1)+(e.getRoom().isLimitBreak() ? "" : "/8")+ChatColor.WHITE+" ]");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onGameLeave(PlayerGameLeaveEvent e) {
		PlayerData data = e.getPlayerData();
		if(data.getArena() == null) {
			for(PlayerData datas : DataStore.getRoomPlayersList(data.getRoom()))
				MainGame.sendMessage(datas, ChatColor.GREEN+data.getName()+ChatColor.BLUE+" が退室しました");
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onChangeStatus(ArenaStatusChangeEvent e) {
		if(e.getStatus() != GameStatusEnum.ENABLE)
			return;
		WaitRoomData roomdata = DataStore.getRoomData(DataStore.getMaxPriorityData());
		MainGame.start(roomdata, e.getArena());
	}
}
