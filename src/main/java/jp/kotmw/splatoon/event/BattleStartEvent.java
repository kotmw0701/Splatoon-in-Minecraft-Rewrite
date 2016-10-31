package jp.kotmw.splatoon.event;

import java.util.List;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BattleStartEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private ArenaData arenadata;
	private WaitRoomData roomdata;

	public BattleStartEvent(WaitRoomData roomdata, ArenaData arenadata) {
		this.arenadata = arenadata;
		this.roomdata = roomdata;
	}

	public List<PlayerData> getPlayers() {
		return DataStore.getArenaPlayersList(arenadata.getName());
	}

	public ArenaData getArenaData() {
		return this.arenadata;
	}

	public WaitRoomData getWaitRoomData() {
		return this.roomdata;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
