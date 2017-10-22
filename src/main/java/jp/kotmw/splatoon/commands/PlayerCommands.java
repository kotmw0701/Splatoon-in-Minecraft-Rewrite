package jp.kotmw.splatoon.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.RankingPattern;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.maingame.MainGame;

public class PlayerCommands extends CommandLib {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg2, String[] args) {
		if(!(sender instanceof Player))
			return false;
		Player player = getPlayer(sender);
		if(args.length == 0) {
			sendMsg(MainGame.Prefix);
			sendMsgs("-----Player Command List-----"
			,"/splat join <room> [player]"
			,"/splat leave"
			,"/splat roomlist"
			,"/splat arenalist"
			,"/splat rank <win/lose/rank/rate/totalpaint/maxwinstreak>"
			,"------------------------------");
			return true;
		} else if(args.length == 1) {
			if("leave".equalsIgnoreCase(args[0]))
				MainGame.leave(player);
			else if("roomlist".equalsIgnoreCase(args[0])) {
				sendPMsg(ChatColor.GREEN+"待機部屋一覧");
				for(WaitRoomData room : DataStore.getRoomList()) 
					sendMsg("- "+room.getName()+ChatColor.GREEN+" | "
				+ChatColor.WHITE+
				DataStore.getRoomPlayersList(room.getName()).size()+" / "+(room.isLimitBreak() ? "∞ "+ChatColor.RED.toString()+ChatColor.BOLD.toString()+ChatColor.UNDERLINE.toString()+ChatColor.ITALIC+"LIMIT BREAKING ROOM" : "8"));
			} else if("arenalist".equalsIgnoreCase(args[0])) {
				sendPMsg(ChatColor.GREEN+"ステージ一覧");
				for(ArenaData room : DataStore.getArenaList()) sendMsg("- "+room.getName()+" "+room.getGameStatus().getStats());
			}
		} else if(args.length == 2) {
			if("join".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasRoomData(args[1])) {
					sendPMsg(ChatColor.RED+"その待機部屋は存在しません");
					return false;
				}
				MainGame.join(player, DataStore.getRoomData(args[1]));
				return true;
			} else if("rank".equalsIgnoreCase(args[0])) {
				RankingPattern pattern = getPattern(args[1]);
				if(pattern == null) {
					sendPMsg(ChatColor.RED+"そのランキングは存在しません");
					return false;
				}
				int i = 1;
				sendMsg(ChatColor.GREEN.toString()+ChatColor.STRIKETHROUGH+"---------------"+ChatColor.WHITE+"[ Ranking ]"+ChatColor.GREEN.toString()+ChatColor.STRIKETHROUGH+"---------------");
				sendMsg(ChatColor.AQUA.toString()+ ChatColor.BOLD + pattern.getText() +ChatColor.WHITE+ " のランキングは以下の通りです");
				for(String ranking : DataStore.getRanking(pattern)) {
					if(i >= 10)
						break;
					sendMsg(ranking);
					i++;
				}
				sendMsg(ChatColor.GREEN.toString()+ChatColor.STRIKETHROUGH+"----------------------------------------");
			} else if("spectate".equalsIgnoreCase(args[0])) {
				
			}
		} else if(args.length == 3) {
			if("join".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasRoomData(args[1])) {
					sendPMsg(ChatColor.RED+"その待機部屋は存在しません");
					return false;
				}
				String target = args[2];
				Player targetPlayer = getPlayer(target);
				if(target.equalsIgnoreCase("@p"))
					targetPlayer = player;
				else if(target.equalsIgnoreCase("@r"))
					targetPlayer = getRandomPlayer();
				else if(target.equalsIgnoreCase("@a")) {
					sendPMsg(ChatColor.GREEN+"人数がはみ出た場合はそのプレイヤーは弾かれます");
					for(Player players : Bukkit.getOnlinePlayers()) {
						MainGame.join(players, DataStore.getRoomData(args[1]));
					}
					return true;
				}
				if(targetPlayer == null) {
					sendPMsg(ChatColor.RED+"指定したプレイヤーが存在しません");
					return false;
				}
				MainGame.join(targetPlayer, DataStore.getRoomData(args[1]));
				return false;
			}
		}
		return false;
	}
	
	public RankingPattern getPattern(String pattern) {
		for(RankingPattern pattern2 : RankingPattern.values()) 
			if(pattern2.toString().equalsIgnoreCase(pattern))
				return pattern2;
		return null;
	}
}
