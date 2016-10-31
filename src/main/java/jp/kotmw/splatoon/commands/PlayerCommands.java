package jp.kotmw.splatoon.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.maingame.MainGame;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return false;
		Player player = (Player)sender;
		if(args.length < 1) {
			//コマンドリスト
		} else if(args.length == 1) {
			if("leave".equalsIgnoreCase(args[0]))
				MainGame.leave(player);
		} else if(args.length == 2) {
			if("join".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasRoomData(args[1])) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"その待機部屋は存在しません");
					return false;
				}
				MainGame.join(player, DataStore.getRoomData(args[1]));
				return true;
			}
		} else if(args.length == 3) {
			if("join".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasRoomData(args[1])) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"その待機部屋は存在しません");
					return false;
				}
				String target = args[2];
				Player targetPlayer = getPlayer(target);
				if(target.equalsIgnoreCase("@p"))
					targetPlayer = player;
				else if(target.equalsIgnoreCase("@r"))
					targetPlayer = getRandomPlayer();
				else if(target.equalsIgnoreCase("@a")) {
					player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"人数がはみ出た場合はそのプレイヤーは弾かれます");
					for(Player players : Bukkit.getOnlinePlayers()) {
						MainGame.join(players, DataStore.getRoomData(args[1]));
					}
					return true;
				}
				if(targetPlayer == null) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"指定したプレイヤーが存在しません");
					return false;
				}
				MainGame.join(targetPlayer, DataStore.getRoomData(args[1]));
				return false;
			} else if("setvec".equalsIgnoreCase(args[0])) {
				try {
				Main.xz = Double.valueOf(args[1]);
				Main.y = Double.valueOf(args[2]);
				Bukkit.broadcastMessage(MainGame.Prefix+"X: "+ChatColor.GREEN+Main.xz+ChatColor.WHITE+" Y: "+ChatColor.GREEN+Main.y+ChatColor.WHITE+" Z: "+ChatColor.GREEN+Main.xz);
				} catch (NumberFormatException e) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"数字入れてくださいな");
				}
			}
		}
		return false;
	}

	public static Player getPlayer(String name) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().equalsIgnoreCase(name))
				return player;
		}
		return null;
	}

	public static Player getRandomPlayer() {
		Random random = new Random();
		List<Player> list = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		return list.get(random.nextInt(list.size()));
	}
}
