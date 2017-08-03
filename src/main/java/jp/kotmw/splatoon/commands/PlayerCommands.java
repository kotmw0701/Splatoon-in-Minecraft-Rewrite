package jp.kotmw.splatoon.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.RankingPattern;
import jp.kotmw.splatoon.maingame.MainGame;

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
			else if("roomlist".equalsIgnoreCase(args[0])) {
				player.sendMessage(ChatColor.GREEN+"待機部屋一覧");
				for(String room : DataStore.getRoomList()) player.sendMessage("- "+room+ChatColor.GREEN+" | "+ChatColor.WHITE+DataStore.getRoomPlayersList(room).size()+" / 8");
			} else if("arenalist".equalsIgnoreCase(args[0])) {
				player.sendMessage(ChatColor.GREEN+"ステージ一覧");
				for(String room : DataStore.getArenaList()) player.sendMessage("- "+room+" "+DataStore.getArenaData(room).getGameStatus().getStats());
			}
		} else if(args.length == 2) {
			if("join".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasRoomData(args[1])) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"その待機部屋は存在しません");
					return false;
				}
				MainGame.join(player, DataStore.getRoomData(args[1]));
				return true;
			} else if("rank".equalsIgnoreCase(args[0])) {
				RankingPattern pattern = getPattern(args[1]);
				if(pattern == null) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"そのランキングは存在しません");
					return false;
				}
				int i = 1;
				player.sendMessage(ChatColor.GREEN.toString()+ChatColor.STRIKETHROUGH+"---------------"+ChatColor.WHITE+"[ Ranking ]"+ChatColor.GREEN.toString()+ChatColor.STRIKETHROUGH+"---------------");
				player.sendMessage(ChatColor.AQUA.toString()+ ChatColor.BOLD + pattern.getText() +ChatColor.WHITE+ " のランキングは以下の通りです");
				for(String ranking : DataStore.getRanking(pattern)) {
					if(i >= 10)
						break;
					player.sendMessage(ranking.replaceAll("\\.0", ""));
					i++;
				}
				player.sendMessage(ChatColor.GREEN.toString()+ChatColor.STRIKETHROUGH+"----------------------------------------");
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
	
	public RankingPattern getPattern(String pattern) {
		for(RankingPattern pattern2 : RankingPattern.values()) 
			if(pattern2.toString().equalsIgnoreCase(pattern))
				return pattern2;
		return null;
	}
}
