package jp.kotmw.splatoon.event;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore.ArenaStatusEnum;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaStatusChangeEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private ArenaData data;
	private ArenaStatusEnum status;

	public ArenaStatusChangeEvent(ArenaData data, ArenaStatusEnum status) {
		this.data = data;
		this.status = status;
	}

	public ArenaData getArena() {
		return data;
	}

	public ArenaStatusEnum getStatus() {
		return status;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
