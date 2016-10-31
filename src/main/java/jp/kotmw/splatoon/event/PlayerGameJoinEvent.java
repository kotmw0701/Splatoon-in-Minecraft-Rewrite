package jp.kotmw.splatoon.event;

import java.util.List;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerGameJoinEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	private String cancelreason;
	private Player player;
	private WaitRoomData room;

	public PlayerGameJoinEvent(Player player, WaitRoomData room) {
		this.player = player;
		this.room = room;
	}

	public Player getPlayer() {
		return player;
	}

	public WaitRoomData getRoom() {
		return room;
	}

	public List<PlayerData> getJoinPlayerDatas() {
		return DataStore.getRoomPlayersList(room.getName());
	}

	public String getCancelreason() {
		return cancelreason;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel, String reason) {
		this.cancelled = cancel;
		this.cancelreason = reason;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
