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
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;

public class SplatScoreBoard {

	private Scoreboard scoreboard;
	private ArenaData data;
	
	public SplatScoreBoard(ArenaData data) {
		this.data = data;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = scoreboard.registerNewObjective(data.getName(), "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(MainGame.Prefix);
		for(int teamnum = 1; teamnum <= data.getMaximumTeamNum(); teamnum++) {
			Team team = scoreboard.registerNewTeam("SplatTeam"+teamnum);
			team.setPrefix(data.getSplatColor(teamnum).getChatColor().toString());
			team.setSuffix(ChatColor.RESET.toString());
			team.setAllowFriendlyFire(false);
			team.setCanSeeFriendlyInvisibles(false);
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		}
	}
	
	public void resetScoreboard() {
		for(int teamnum = 1; teamnum <= data.getMaximumTeamNum(); teamnum++) {
			scoreboard.getTeam("SplatTeam"+teamnum).setPrefix(data.getSplatColor(teamnum).getChatColor().toString());
		}
		Objective objective = scoreboard.getObjective(data.getName());
		objective.getScore(conversionTime(1)).setScore(0);
		scoreboard.resetScores(conversionTime(1));
	}

	public void DefaultScoreBoard(BattleType type) {
		Objective obj = scoreboard.getObjective(data.getName());
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

	private String conversionTime(int tick) {
		int second = tick%60;
		int minut = (tick/60)%60;
		if(String.valueOf(second).length() == 2)
			return ChatColor.GOLD.toString()+minut+" : "+second;
		return ChatColor.GOLD.toString()+minut+" : 0"+second;
	}

	public void changeTime(int tick) {
		Objective obj = scoreboard.getObjective(data.getName());
		if(tick%20 != 0)
			tick -= tick%20;
		int scorevalue = obj.getScore(ChatColor.GREEN+"-Time left-").getScore()-1;
		Score score = obj.getScore(conversionTime((tick/20)+1));
		score.setScore(0);
		scoreboard.resetScores(conversionTime((tick/20)+1));
		score = obj.getScore(conversionTime(tick/20));
		score.setScore(scorevalue);
	}

	public void updateTeam1Count() {
		Objective obj = scoreboard.getObjective(data.getName());
		int team1value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-1;
		TeamCountManager manager = data.getTeam1_count();
		List<String> beforelist = getText(1, manager, true);
		String aftertext = getText(1, manager, false).get(0);
		for(String beforetext : beforelist) {
			obj.getScore(beforetext).setScore(0);
			scoreboard.resetScores(beforetext);
		}
		if(manager.getpenalty() > 0)
			aftertext = getText(1, manager, false).get(1);
		obj.getScore(aftertext).setScore(team1value);
	}

	public void updateTeam2Count() {
		Objective obj = scoreboard.getObjective(data.getName());
		int team2value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-2;
		TeamCountManager manager = data.getTeam2_count();
		List<String> beforelist = getText(2, manager, true);
		String aftertext = getText(2, manager, false).get(0);
		for(String beforetext : beforelist) {
			obj.getScore(beforetext).setScore(0);
			scoreboard.resetScores(beforetext);
		}
		if(manager.getpenalty() > 0)
			aftertext = getText(2, manager, false).get(1);
		obj.getScore(aftertext).setScore(team2value);
	}

	public void updatePenalty(int team, int beforepenalty) {
		Objective obj = scoreboard.getObjective(data.getName());
		int team1value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-1;
		TeamCountManager manager = team == 1 ? data.getTeam1_count() : data.getTeam2_count();
		if(manager.getpenalty() < 1)
			return;
		String beforetext = data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount())+" +"+beforepenalty;
		if(beforepenalty < 1)
			beforetext = getText(team, manager, false).get(0);
		String aftertext = getText(team, manager, false).get(1);
		Score team1 = obj.getScore(beforetext);
		team1.setScore(0);
		scoreboard.resetScores(beforetext);
		team1 = obj.getScore(aftertext);
		team1.setScore(team1value);
	}
	
	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	private List<String> getText(int team,TeamCountManager manager, boolean before) {
		int Subtraction = before ? 1 : 0;
		List<String> list = new ArrayList<String>();
		list.add(data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount()+Subtraction));
		list.add(data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount())+" +"+(manager.getpenalty()+Subtraction));
		return list;
	}
	
	public void setTeam(PlayerData data) {
		Team team = scoreboard.getTeam("SplatTeam"+data.getTeamid());
		team.addEntry(data.getName());
	}

	public void showBoard(PlayerData data) {
		Bukkit.getPlayer(data.getName()).setScoreboard(scoreboard);
	}

	public void hideBoard(PlayerData data) {
		Bukkit.getPlayer(data.getName()).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		scoreboard.getTeam("SplatTeam"+data.getTeamid()).removeEntry(data.getName());
	}
}
