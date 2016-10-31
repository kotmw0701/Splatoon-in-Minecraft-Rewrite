package jp.kotmw.splatoon.event;

import jp.kotmw.splatoon.gamedatas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerGameLeaveEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private PlayerData data;

	public PlayerGameLeaveEvent(PlayerData data) {
		this.data = data;
	}

	public PlayerData getPlayerData() {
		return data;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(data.getName());
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
