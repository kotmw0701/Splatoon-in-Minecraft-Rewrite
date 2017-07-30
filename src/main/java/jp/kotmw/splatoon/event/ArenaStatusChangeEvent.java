package jp.kotmw.splatoon.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;

public class ArenaStatusChangeEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private ArenaData data;
	private GameStatusEnum status;

	public ArenaStatusChangeEvent(ArenaData data, GameStatusEnum status) {
		this.data = data;
		this.status = status;
	}

	public ArenaData getArena() {
		return data;
	}

	public GameStatusEnum getStatus() {
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
