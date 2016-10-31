package jp.kotmw.splatoon.commands;

import java.util.ArrayList;
import java.util.List;

import jp.kotmw.splatoon.SplatColor;
import jp.kotmw.splatoon.filedatas.OtherFiles;
import jp.kotmw.splatoon.filedatas.PlayerFiles;
import jp.kotmw.splatoon.filedatas.StageFiles;
import jp.kotmw.splatoon.filedatas.WaitRoomFiles;
import jp.kotmw.splatoon.filedatas.WeaponFiles;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.ArenaStatusEnum;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.SplatScoreBoard;
import jp.kotmw.splatoon.mainweapons.Paint;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class SettingCommands implements CommandExecutor{

	List<ArmorStand> stands = new ArrayList<ArmorStand>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player))
			return false;
		Player player = (Player)sender;
		if(args.length < 1) {
			player.sendMessage(MainGame.Prefix);
			player.sendMessage("-----Setting Command List-----");
			player.sendMessage("/splatsetting setlobby");
			player.sendMessage("/splatsetting <room> setroom");
			player.sendMessage("/splatsetting <arena> setarena");
			player.sendMessage("/splatsetting <arena> setarea");
			player.sendMessage("/splatsetting <arena> finish");
			player.sendMessage("/splatsetting <arena> setspawn <1/2> <1/2/3/4>");
			player.sendMessage("/splatsetting <arena> editmode");
			player.sendMessage("------------------------------");
		} else if(args.length == 1) {
			if("setlobby".equalsIgnoreCase(args[0])) {
				OtherFiles.createLobby(player.getLocation());
				player.sendMessage(MainGame.Prefix+ChatColor.GREEN + "ロビーを設定しました");
			} else if("configreload".equalsIgnoreCase(args[0])) {
				OtherFiles.ConfigReload();
				player.sendMessage(MainGame.Prefix+"Config.ymlを再読み込みしました");
			} else if("start".equalsIgnoreCase(args[0])) {
				if(DataStore.hasPlayerData(player.getName())) {
					PlayerData playerdata = DataStore.getPlayerData(player.getName());
					MainGame.start(DataStore.getRoomData(playerdata.getRoom()));
				}
			}
		} else if(args.length == 2) {
			if("rollback".equalsIgnoreCase(args[0])) {
				if(!DataStore.hasArenaData(args[1]))
					return false;
				ArenaData data = DataStore.getArenaData(args[1]);
				Paint.RollBack(data);
			}
			String name = args[0];
			if("setarena".equalsIgnoreCase(args[1])) {
				if(StageFiles.AlreadyCreate(name)) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED + "そのステージは既に存在します");
					player.sendMessage(MainGame.Prefix+ChatColor.GREEN + "ステージ範囲の再設定をしたい場合は "
					+ChatColor.YELLOW+"/splatsetting "+name+" editmode"+ChatColor.GREEN+"のコマンドを使用してステージを無効化してからsetarenaのコマンドを再実行してください");
					return false;
				}
				WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
				Selection selection = worldEdit.getSelection(player);
				if(selection == null)
					return false;
				if(!StageFiles.createArena(name, selection.getWorld(), selection.getMinimumPoint(), selection.getMaximumPoint())) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"着色可能ブロックを1つ以上設置してください");
					return false;
				}
				player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"ステージの範囲設定が完了しました");
				player.sendMessage(MainGame.Prefix+ChatColor.YELLOW+"以下のコマンドで設定を終えてから、finishコマンドを実行してください");
				player.sendMessage(MainGame.Prefix+ChatColor.YELLOW+"/splatsetting "+name+" setspawn <1/2> <1/2/3/4>");
				player.sendMessage(MainGame.Prefix+ChatColor.YELLOW+"/splatsetting "+name+" setarea");
				return true;
			} else if("setarea".equalsIgnoreCase(args[1])) {
				if(!StageFiles.AlreadyCreateFile(name)) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"そのステージは存在しません");
					return false;
				}
				if(StageFiles.AlreadyCreate(name) && DataStore.getArenaData(name).isStatus()) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED + "そのステージは既に有効化されています");
					player.sendMessage(MainGame.Prefix+ChatColor.GREEN + "エリア範囲の再設定をしたい場合は "
					+ChatColor.YELLOW+"/splatsetting "+name+" editmode"+ChatColor.GREEN+"のコマンドを使用してステージを無効化してからsetarenaのコマンドを再実行してください");
					return false;
				}
				WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
				Selection selection = worldEdit.getSelection(player);
				if(selection == null)
					return false;
				StageFiles.setArea(name, selection.getMinimumPoint(), selection.getMaximumPoint());
				player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"エリア範囲を設定しました");
			} else if("finish".equalsIgnoreCase(args[1])) {
				if(DataStore.hasArenaData(name))
					if(DataStore.getArenaData(name).isStatus()) {
						player.sendMessage(MainGame.Prefix+ChatColor.RED+"既に有効化済みです");
						return false;
					}
				ArenaData data = StageFiles.setArenaData(name);
				if(!StageFiles.isFinishSetup(data)) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"そのステージはセットアップが完了していません");
					DataStore.removeArenaData(name);
					return false;
				}
				data.setStatus(true);
				data.setGameStatus(ArenaStatusEnum.ENABLE);
				StageFiles.setEnable(name);
				SplatColor.SetColor(data);
				SplatScoreBoard.updateScoreboard(data);
				player.sendMessage(MainGame.Prefix+ChatColor.GREEN + "設定完了を確認し、使用可能になりました！");
				return true;
			} else if("setroom".equalsIgnoreCase(args[1])) {
				if(DataStore.hasRoomData(name)) {
					player.sendMessage(MainGame.Prefix + ChatColor.GREEN + "既に作成されていた待機部屋の座標と置き換えました");
					WaitRoomData data = DataStore.getRoomData(name);
					player.sendMessage(MainGame.Prefix + ChatColor.RED + "以前に設定されていたデータはこちらです");
					player.sendMessage("X: "+data.getX());
					player.sendMessage("Y: "+data.getY());
					player.sendMessage("Z: "+data.getZ());
					player.sendMessage("Yaw: "+data.getYaw());
					player.sendMessage("Pitch: "+data.getPitch());
					player.sendMessage("BattleType: "+data.getBattleType().toString());
				} else {
					player.sendMessage(MainGame.Prefix + ChatColor.YELLOW + name + ChatColor.GREEN + " という待機部屋を作成しました");
				}
				WaitRoomFiles.creareWaitRoom(name, player.getLocation(), BattleType.Turf_War);
			} else if("editmode".equalsIgnoreCase(args[1])) {
				if(!DataStore.hasArenaData(name)) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"セットアップが完了しているステージでのみ使用可能です");
				}
				ArenaData data = DataStore.getArenaData(name);
				if(!data.isStatus()) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"再有効化の場合は /splatsetting "+name+" finishのコマンドを実行してください");
				}
				data.setStatus(false);
				data.setGameStatus(ArenaStatusEnum.DISABLE);
				player.sendMessage(MainGame.Prefix+ChatColor.GREEN+"ステージを無効化し、編集モードに切り替わりました");
			}
		} else if(args.length == 3) {
			String name = args[0];
			if("addweapon".equalsIgnoreCase(args[1])) {
				String weaponname = args[2];
				Player target = getPlayer(name);
				if(target == null) {
					player.sendMessage(MainGame.Prefix + ChatColor.RED+"そのプレイヤーは存在しません");
					return false;
				}
				if(!WeaponFiles.exists(weaponname)) {
					player.sendMessage(MainGame.Prefix + ChatColor.RED+"そのブキは存在しません");
					return false;
				}
				if(PlayerFiles.hasHaveWeapon(player.getUniqueId().toString().replaceAll("-", ""), weaponname)) {
					player.sendMessage(MainGame.Prefix + ChatColor.RED+"対象のプレイヤーはその武器を既に持っています");
					return false;
				}
				PlayerFiles.addWeapon(player.getUniqueId().toString().replaceAll("-", ""), weaponname);
				player.sendMessage(MainGame.Prefix + ChatColor.YELLOW+target.getName()+ChatColor.WHITE+" に "+ChatColor.GREEN+weaponname+ChatColor.WHITE+" を追加しました");
			}
			if("setroom".equalsIgnoreCase(args[1])) {
				BattleType type = getType(args[2]);
				if(type == null) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED+"そのバトルタイプはありません");
					return false;
				}
				if(DataStore.hasRoomData(name)) {
					player.sendMessage(MainGame.Prefix + ChatColor.GREEN + "既に作成されていた待機部屋の座標と置き換えました");
					WaitRoomData data = DataStore.getRoomData(name);
					player.sendMessage(MainGame.Prefix + ChatColor.RED + "以前に設定されていたデータはこちらです");
					player.sendMessage("X: "+data.getX());
					player.sendMessage("Y: "+data.getY());
					player.sendMessage("Z: "+data.getZ());
					player.sendMessage("Yaw: "+data.getYaw());
					player.sendMessage("Pitch: "+data.getPitch());
					player.sendMessage("BattleType: "+data.getBattleType().toString());
				} else {
					player.sendMessage(MainGame.Prefix + ChatColor.YELLOW + name + ChatColor.GREEN + " という待機部屋を作成しました");
				}
				WaitRoomFiles.creareWaitRoom(name, player.getLocation(), type);
			}
		} else if(args.length == 4) {
			String name = args[0];
			if("setspawn".equalsIgnoreCase(args[1])) {
				if(!StageFiles.AlreadyCreateFile(name)) {
					player.sendMessage(MainGame.Prefix + ChatColor.RED + "そのステージは存在しません");
					return false;
				}
				if(StageFiles.AlreadyCreate(name) && DataStore.getArenaData(name).isStatus()) {
					player.sendMessage(MainGame.Prefix+ChatColor.RED + "そのステージは既に有効化されています");
					player.sendMessage(MainGame.Prefix+ChatColor.GREEN + "スポーン地点の再設定をしたい場合は "
					+ChatColor.YELLOW+"/settingfiles "+name+" editmode"+ChatColor.GREEN+"のコマンドを使用してステージを無効化してからsetarenaのコマンドを再実行してください");
					return false;
				}
				if(!NumberUtils.isNumber(args[2])
						|| !NumberUtils.isNumber(args[3])) {
					player.sendMessage(MainGame.Prefix+ ChatColor.RED + "両方とも数値を入れてください");
					return false;
				}
				int team = Integer.valueOf(args[2]), pos = Integer.valueOf(args[3]);
				if(team == 0 && team >= 3) {
					player.sendMessage(MainGame.Prefix+ ChatColor.RED + "1か2にしてください");
					return false;
				}
				if(pos == 0 && pos >= 5) {
					player.sendMessage(MainGame.Prefix+ ChatColor.RED + "1～4の範囲にしてください");
					return false;
				}
				StageFiles.setSpawnPos(name, player.getLocation(), team, pos);
				player.sendMessage(MainGame.Prefix + ChatColor.GREEN + "チーム"+team+"、"+pos+"人目のスポーン地点を設定");
				return true;
			}
		} /*else if(args.length == 9) {
			if("setdata".equalsIgnoreCase(args[0])) {
				String uuid = player.getUniqueId().toString().replaceAll("-", "");
				int win = Integer.valueOf(args[1]);
				int lose = Integer.valueOf(args[2]);
				int winstreak = Integer.valueOf(args[3]);
				int maxwinstreak = Integer.valueOf(args[4]);
				boolean finalwin = Boolean.valueOf(args[5]);
				int rank = Integer.valueOf(args[6]);
				int exp = Integer.valueOf(args[7]);
				int totalexp = Integer.valueOf(args[8]);
				DataBaseTest.setPlayerData( player.getName(), uuid, win, lose, winstreak, maxwinstreak, finalwin, rank, exp, totalexp);
				player.sendMessage("データ設定");
			}
		}*/
		return false;
	}

	private Player getPlayer(String name) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			if(player.getName().equals(name)) {
				return player;
			}
		}
		return null;
	}

	public BattleType getType(String type) {
		BattleType bt = null;
		for(BattleType types : BattleType.values())
			if(types.toString().equalsIgnoreCase(type))
				bt = types;
		return bt;
	}
}
