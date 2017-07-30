package jp.kotmw.splatoon.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import jp.kotmw.splatoon.filedatas.OtherFiles;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.maingame.MainGame;

public class ConsoleCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(s instanceof Player) {
			((Player)s).sendMessage(MainGame.Prefix+ChatColor.RED+"コンソール用コマンドです");
			return false;
		}
		if(args.length < 1) {
			//コマンドリスト
		} else if(args.length == 1) {
			if("configreload".equalsIgnoreCase(args[0])) {
				OtherFiles.ConfigReload();
				Bukkit.getConsoleSender().sendMessage(MainGame.Prefix+"Config.ymlを再読み込みしました");
			}
		} else if(args.length == 3) {
			if("join".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasRoomData(args[1])) {
					s.sendMessage(MainGame.Prefix+"その待機部屋は存在しません");
					return false;
				}
				String target = args[2];
				Player targetPlayer = PlayerCommands.getPlayer(target);
				if(target.equalsIgnoreCase("@p")) {
					if(s instanceof ConsoleCommandSender)
						targetPlayer = getClosestPlayer(new Location(Bukkit.getWorlds().get(0), 0, 0, 0));
					else if(s instanceof BlockCommandSender)
						targetPlayer = getClosestPlayer(((BlockCommandSender)s).getBlock().getLocation());
				} else if(target.equalsIgnoreCase("@r"))
					targetPlayer = PlayerCommands.getRandomPlayer();
				else if(target.equalsIgnoreCase("@a")) {
					s.sendMessage(MainGame.Prefix+"人数がはみ出た場合はそのプレイヤーは弾かれます");
					for(Player players : Bukkit.getOnlinePlayers()) {
						MainGame.join(players, DataStore.getRoomData(args[1]));
					}
					return true;
				}
				if(targetPlayer == null) {
					s.sendMessage(MainGame.Prefix+ChatColor.RED+"指定したプレイヤーが存在しません");
					return false;
				}
				MainGame.join(targetPlayer, DataStore.getRoomData(args[1]));
				return false;
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private static String getSuffix(String filename) {
		if(filename == null)
			return null;
		int point = filename.lastIndexOf(".");
		if(point != -1) {
			return filename.substring(point+1);
		}
		return filename;
	}

	private Player getClosestPlayer(Location l) {
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
}
