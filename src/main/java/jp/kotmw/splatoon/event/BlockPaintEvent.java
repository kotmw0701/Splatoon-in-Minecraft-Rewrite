package jp.kotmw.splatoon.event;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.util.SplatColor;

public class BlockPaintEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private ArenaData arena;
	private PlayerData player;
	private Block block;

	public BlockPaintEvent(Block block, PlayerData player, ArenaData arena) {
		this.block = block;
		this.player = player;
		this.arena = arena;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(player.getName());
	}

	public PlayerData getPlayerData() {
		return player;
	}

	public Block getBlock() {
		return block;
	}

	public Location getLocation() {
		return block.getLocation();
	}

	public ArenaData getArenaData() {
		return arena;
	}

	public SplatColor getColor() {
		return arena.getSplatColor(player.getTeamid());
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
