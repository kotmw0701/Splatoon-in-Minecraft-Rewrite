package jp.kotmw.splatoon.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;

import jp.kotmw.splatoon.maingame.MainGame;

public abstract class CommandLib implements CommandExecutor {
	
	protected Player player;
	
	protected Player getPlayer(String name) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name))
				return player;
		}
		return null;
	}

	protected Player getRandomPlayer() {
		Random random = new Random();
		List<Player> list = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		return list.get(random.nextInt(list.size()));
	}
	
	protected Player getClosestPlayer(Location l) {
		double closest = Double.MAX_VALUE;
		Player closestp = null;
		for(Player i : Bukkit.getOnlinePlayers()){
			double dist = i.getLocation().distance(l);
			if (closest == Double.MAX_VALUE || dist < closest){
				closest = dist;
				closestp = i;
			}
		}
		return closestp;
	}
	

	protected void sendPMsgs(String... texts) {
		Arrays.asList(texts).forEach(text -> sendPMsg(text));
	}
	
	protected void sendPMsg(String text) {
		player.sendMessage(MainGame.Prefix + text);
	}
	
	protected void sendMsgs(String... texts) {
		Arrays.asList(texts).forEach(text -> sendMsg(text));
	}
	
	protected void sendMsg(String text) {
		player.sendMessage(text);
	}
}
