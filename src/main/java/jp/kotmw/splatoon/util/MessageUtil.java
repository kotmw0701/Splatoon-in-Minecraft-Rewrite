package jp.kotmw.splatoon.util;

import org.bukkit.Bukkit;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;

public class MessageUtil extends Title {

	public static void sendMessage(PlayerData data, String msg) {
		Bukkit.getPlayer(data.getName()).sendMessage(MainGame.Prefix+msg);
	}

	public static void sendTitle(PlayerData data, int fadein, int stay, int fadeout, String main, String sub) {
		sendTitle(Bukkit.getPlayer(data.getName()), fadein, stay, fadeout, main, sub);
	}

	public static void sendActionBar(PlayerData data, String msg) {
		sendActionBar(Bukkit.getPlayer(data.getName()), msg);
	}

	public static void sendMessageforArena(String arena, String msg) {
		for(PlayerData data : DataStore.getArenaPlayersList(arena))
			sendMessage(data, msg);
	}

	public static void sendMessageforRoom(String room, String msg) {
		for(PlayerData data : DataStore.getRoomPlayersList(room))
			sendMessage(data, msg);
	}

	public static void sendMessageforTeam(String arena, String msg, int team) {
		for(PlayerData player : DataStore.getArenaPlayersList(arena))
			if(player.getTeamid() == team)
				sendMessage(player, msg);
	}

	public static void sendTitleforTeam(ArenaData data, int team, int fadein, int stay, int fadeout, String main, String sub) {
		for(PlayerData player : DataStore.getArenaPlayersList(data.getName()))
			if(player.getTeamid() == team)
				sendTitle(player, fadein, stay, fadeout, main, sub);
	}

	public static void sendActionBarforTeam(ArenaData data, String msg, int team) {
		for(PlayerData player : DataStore.getArenaPlayersList(data.getName()))
			if(player.getTeamid() == team)
				sendActionBar(player, msg);
	}
}
