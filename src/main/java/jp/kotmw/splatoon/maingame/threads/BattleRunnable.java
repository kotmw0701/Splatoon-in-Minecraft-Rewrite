package jp.kotmw.splatoon.maingame.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.SplatZones;
import jp.kotmw.splatoon.manager.SplatScoreBoard;

public class BattleRunnable extends BukkitRunnable {

	private ArenaData data;
	private int tick;
	private int second;
	private BattleType type;
	private String main, sub;

	public BattleRunnable(ArenaData data, int second, BattleType type) {
		this.data = data;
		this.tick = (second+10)*20;
		this.second = second;
		this.type = type;
		this.main = "ナワバリバトル";
		this.sub = "たくさんナワバリを確保しろ！";
		switch(type) {
		case Splat_Zones:
			this.main = "ガチエリア";
			this.sub = "ガチエリアを確保して守りぬけ！";
			break;
		case Rain_Maker:
			break;
		default:
			break;
		}
	}

	/*
	 * スタート前のカウントダウン           10*20tick
	 * ゲーム自体の時間                     180*20tick(可変)
	 */

	@Override
	public void run() {
		if(tick > 0) {
			if(tick%20 == 0) {
				if(tick > (second+5)*20) {//転送後のTitle表示
					for(PlayerData data : DataStore.getArenaPlayersList(this.data.getName()))
						MainGame.sendTitle(data, 0, 2, 0, main, sub);
				} else if(tick <= (second+5)*20&&tick > second*20) {//カウントダウン
					int count = tick/20-second;
					for(PlayerData data : DataStore.getArenaPlayersList(this.data.getName())) {
						MainGame.sendTitle(data,
								0,
								2,
								0,
								ChatColor.WHITE+"["+count+"]   >>> Ready? <<<   ["+count+"]",
								this.data.getSplatColor(data.getTeamid()).getChatColor()+"[味方カラー]");
					}
				} else if(tick == second*20) {//戦闘開始
					for(PlayerData data : DataStore.getArenaPlayersList(this.data.getName())) {
						data.setMove(true);
						data.setAllCansel(false);
						MainGame.sendTitle(data, 0, 2, 0, ChatColor.WHITE.toString()+ChatColor.BOLD+"GO!", " ");
						if(data.getSquidTask() == null) {
							BukkitRunnable task = new SquidRunnable(data.getName());
							task.runTaskTimer(Main.main, 0, 1);
							data.setSquidTask(task);
						}
						if(data.getHealthTask() == null) {
							BukkitRunnable task = new DamageHealthRunnable(data.getName());
							task.runTaskTimer(Main.main, 0, 20);
							data.setHealthTask(task);
						}
					}
					/*if(type.equals(BattleType.Turf_War))
						data.setTotalpaintblock(data.getBattleClass().getTotalArea());*/
				}
				if(tick <= second*20) {
					SplatScoreBoard.changeTime(data, tick);
					if((tick/20)%60 == 0)
						MainGame.sendMessageforArena(data.getName(), ChatColor.YELLOW+"残り時間 "+ChatColor.AQUA.toString()+ChatColor.BOLD+(tick/20)/60+ChatColor.YELLOW+" 分");
					if(tick/20 <= 10) {
						for(PlayerData data : DataStore.getArenaPlayersList(this.data.getName()))
							MainGame.sendTitle(data, 0, 2, 0, ChatColor.GRAY.toString()+ChatColor.BOLD+tick/20, " ");
					}
				}
			}
			if(type == BattleType.Splat_Zones) {
				if(tick%10 == 0)
					((SplatZones)data.getBattleClass()).checkArea();
				int team1_count = data.getTeam1_count().getcount();
				int team2_count = data.getTeam2_count().getcount();
				if(team1_count == 0 || team2_count == 0) {
					ResetPlayerData();
					for(PlayerData data : DataStore.getArenaPlayersList(this.data.getName())) {
						MainGame.sendTitle(data, 0, 2, 0, ChatColor.GRAY.toString()+ChatColor.BOLD+"Finish!", " ");
						MainGame.sendMessage(data, ChatColor.RED+"開発途中のため、リザルトはスキップします");
					}
					for(ArmorStand stand : data.getAreastands())
						stand.remove();
					MainGame.end(data, true);
					this.cancel();
					return;
				}
			}
		} else {
			for(PlayerData data : DataStore.getArenaPlayersList(this.data.getName()))
				MainGame.sendTitle(data, 1, 2, 1, ChatColor.GREEN.toString()+"～現在集計中～", ChatColor.YELLOW+"しばらくお待ち下さい|･ω･)ﾉ");
			ResetPlayerData();
			data.setGameStatus(GameStatusEnum.RESULT);
			if(type == BattleType.Turf_War)
				data.getBattleClass().resultBattle();
			else if(type == BattleType.Splat_Zones) {
				for(ArmorStand stand : data.getAreastands())
					stand.remove();
				MainGame.end(data, false);
			}
			this.cancel();
		}
		tick--;
	}

	private void ResetPlayerData() {
		for(PlayerData pdata : DataStore.getArenaPlayersList(data.getName())) {
			pdata.setSquidMode(false);
			if(pdata.getPlayerSquid() != null)
				pdata.getPlayerSquid().remove();
			pdata.setPlayerSquid(null);
			pdata.setDead(false);
			pdata.setClimb(false);
			pdata.setAllCansel(true);
			if(pdata.getTask() != null)
				pdata.getTask().cancel();
			if(pdata.getSquidTask() != null)
				pdata.getSquidTask().cancel();
			if(pdata.getHealthTask() != null) {
				pdata.getHealthTask().cancel();
			}
			pdata.setTask(null);
			pdata.setSquidTask(null);
			SplatScoreBoard.hideBoard(pdata);
			Player player = Bukkit.getPlayer(pdata.getName());
			player.getInventory().clear();
			player.removePotionEffect(PotionEffectType.SPEED);
			player.removePotionEffect(PotionEffectType.INVISIBILITY);
			player.setGameMode(GameMode.SPECTATOR);
			player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
			player.setFoodLevel(20);
		}
	}
}
