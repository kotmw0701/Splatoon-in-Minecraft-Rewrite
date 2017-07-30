package jp.kotmw.splatoon.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;

public class SplatScoreBoard {

	public static void createScoreboard(ArenaData data) {
		Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = sb.registerNewObjective(data.getName(), "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(MainGame.Prefix);

		Team team1 = sb.registerNewTeam("SplatTeam1");
		team1.setPrefix(data.getSplatColor(1).getChatColor().toString());
		team1.setSuffix(ChatColor.RESET.toString());
		team1.setAllowFriendlyFire(false);
		team1.setCanSeeFriendlyInvisibles(false);
		team1.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);

		Team team2 = sb.registerNewTeam("SplatTeam2");
		team2.setPrefix(data.getSplatColor(2).getChatColor().toString());
		team2.setSuffix(ChatColor.RESET.toString());
		team2.setAllowFriendlyFire(false);
		team2.setCanSeeFriendlyInvisibles(false);
		team2.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);

		data.setScoreBoard(sb);
	}
	
	public static void resetScoreboard(ArenaData data) {
		Scoreboard scoreboard = data.getScoreboard();
		scoreboard.getTeam("SplatTeam1").setPrefix(data.getSplatColor(1).getChatColor().toString());
		scoreboard.getTeam("SplatTeam2").setPrefix(data.getSplatColor(2).getChatColor().toString());
		Objective objective = scoreboard.getObjective(data.getName());
		objective.getScore(conversionTime(1)).setScore(0);
		scoreboard.resetScores(conversionTime(1));
	}

	public static void setTeam(PlayerData data) {
		Scoreboard board = DataStore.getArenaData(data.getArena()).getScoreboard();
		Team team = board.getTeam("SplatTeam"+data.getTeamid());
		team.addEntry(data.getName());
	}

	public static void showBoard(PlayerData data) {
		Bukkit.getPlayer(data.getName()).setScoreboard(DataStore.getArenaData(data.getArena()).getScoreboard());
	}

	public static void hideBoard(PlayerData data) {
		Bukkit.getPlayer(data.getName()).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		Scoreboard board = DataStore.getArenaData(data.getArena()).getScoreboard();
		board.getTeam("SplatTeam"+data.getTeamid()).removeEntry(data.getName());
	}

	public static void DefaultScoreBoard(ArenaData data, BattleType type) {
		Scoreboard sb = data.getScoreboard();
		Objective obj = sb.getObjective(data.getName());
		List<String> board = new ArrayList<String>();
		board.add(ChatColor.GREEN+"-Time left-");
		board.add(conversionTime(MainGame.getTime(type)));
		board.add(ChatColor.RESET.toString());
		board.add("Stage : "+data.getName());
		if(type == BattleType.Splat_Zones) {
			board.add(ChatColor.RESET.toString()+ChatColor.RESET.toString());
			board.add(ChatColor.YELLOW+"-Count-");
			board.add(data.getSplatColor(1).getChatColor()+"Team1 : "+ChatColor.WHITE+100);
			board.add(data.getSplatColor(2).getChatColor()+"Team2 : "+ChatColor.WHITE+100);
		}
		int i = board.size() -1;
		for(String boardtxt : board) {
			Score score = obj.getScore(boardtxt);
			score.setScore(i);
			i--;
		}
	}

	private static String conversionTime(int tick) {
		int second = tick%60;
		int minut = (tick/60)%60;
		if(String.valueOf(second).length() == 2)
			return ChatColor.GOLD.toString()+minut+" : "+second;
		return ChatColor.GOLD.toString()+minut+" : 0"+second;
	}

	public static void changeTime(ArenaData data, int tick) {
		Scoreboard board = data.getScoreboard();
		Objective obj = board.getObjective(data.getName());
		if(tick%20 != 0)
			tick -= tick%20;
		int scorevalue = obj.getScore(ChatColor.GREEN+"-Time left-").getScore()-1;
		Score score = obj.getScore(conversionTime((tick/20)+1));
		score.setScore(0);
		board.resetScores(conversionTime((tick/20)+1));
		score = obj.getScore(conversionTime(tick/20));
		score.setScore(scorevalue);
	}

	public static void updateTeam1Count(ArenaData data) {
		Scoreboard board = data.getScoreboard();
		Objective obj = board.getObjective(data.getName());
		int team1value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-1;
		TeamCountManager manager = data.getTeam1_count();
		List<String> beforelist = getText(data, 1, manager, true);
		String aftertext = getText(data, 1, manager, false).get(0);
		for(String beforetext : beforelist) {
			obj.getScore(beforetext).setScore(0);
			board.resetScores(beforetext);
		}
		if(manager.getpenalty() > 0)
			aftertext = getText(data, 1, manager, false).get(1);
		obj.getScore(aftertext).setScore(team1value);
	}

	public static void updateTeam2Count(ArenaData data) {
		Scoreboard board = data.getScoreboard();
		Objective obj = board.getObjective(data.getName());
		int team2value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-2;
		TeamCountManager manager = data.getTeam2_count();
		List<String> beforelist = getText(data, 2, manager, true);
		String aftertext = getText(data, 2, manager, false).get(0);
		for(String beforetext : beforelist) {
			obj.getScore(beforetext).setScore(0);
			board.resetScores(beforetext);
		}
		if(manager.getpenalty() > 0)
			aftertext = getText(data, 2, manager, false).get(1);
		obj.getScore(aftertext).setScore(team2value);
	}

	public static void updatePenalty(ArenaData data, int team, int beforepenalty) {
		Scoreboard board = data.getScoreboard();
		Objective obj = board.getObjective(data.getName());
		int team1value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-1;
		TeamCountManager manager = team == 1 ? data.getTeam1_count() : data.getTeam2_count();
		if(manager.getpenalty() < 1)
			return;
		String beforetext = data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount())+" +"+beforepenalty;
		if(beforepenalty < 1)
			beforetext = getText(data, team, manager, false).get(0);
		String aftertext = getText(data, team, manager, false).get(1);
		Score team1 = obj.getScore(beforetext);
		team1.setScore(0);
		board.resetScores(beforetext);
		team1 = obj.getScore(aftertext);
		team1.setScore(team1value);
	}

	private static List<String> getText(ArenaData data, int team,TeamCountManager manager, boolean before) {
		int Subtraction = before ? 1 : 0;
		List<String> list = new ArrayList<String>();
		list.add(data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount()+Subtraction));
		list.add(data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount())+" +"+(manager.getpenalty()+Subtraction));
		return list;
	}
}
