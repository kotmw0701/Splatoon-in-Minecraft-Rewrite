package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scoreboard.Scoreboard;

import jp.kotmw.splatoon.event.ArenaStatusChangeEvent;
import jp.kotmw.splatoon.gamedatas.DataStore.GameStatusEnum;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.Turf_War;
import jp.kotmw.splatoon.maingame.threads.BattleRunnable;
import jp.kotmw.splatoon.manager.TeamCountManager;
import jp.kotmw.splatoon.util.Location;
import jp.kotmw.splatoon.util.SplatColor;

public class ArenaData {
	private String arena;
	private boolean status;
	private String world;
	private int totalpaintblock;
	private Location stagepos1, stagepos2;
	private Location areapos1, areapos2;
	private Map<Integer, List<Location>> posisions = new HashMap<>();
	private int teamscount;
	private int playerscount;
	private int winteam;
	private Map<Integer, SplatColor> teamcolor = new HashMap<>();
	private BattleRunnable runtask;
	private GameStatusEnum gameStatus;
	private Scoreboard scoreboard;
	private Turf_War battle;
	private List<BlockState> rollbackblocks = new ArrayList<BlockState>();
	private TeamCountManager team1_count, team2_count;
	private int totalareablock;
	private List<ArmorStand> areastands = new ArrayList<ArmorStand>();

	public ArenaData(String arena, FileConfiguration file) {
		this.arena = arena;
		this.status = file.getBoolean("Stage.Status");
		this.world = file.getString("Stage.World");
		this.totalpaintblock = file.getInt("Stage.TotalPaintBlock");
		String[] pos1 = file.getString("Stage.Pos1").split("/");
		String[] pos2 = file.getString("Stage.Pos2").split("/");
		this.stagepos1 = new Location(world,
				Integer.valueOf(pos1[0]),
				Integer.valueOf(pos1[1]),
				Integer.valueOf(pos1[2]));
		this.stagepos2 = new Location(world,
				Integer.valueOf(pos2[0]),
				Integer.valueOf(pos2[1]),
				Integer.valueOf(pos2[2]));
		if(file.contains("SplatZone.Pos1") && file.contains("SplatZone.Pos2")) {
			pos1 = file.getString("SplatZone.Pos1").split("/");
			pos2 = file.getString("SplatZone.Pos2").split("/");
			this.areapos1 = new Location(world,
					Integer.valueOf(pos1[0]),
					Integer.valueOf(pos1[1]),
					Integer.valueOf(pos1[2]));
			this.areapos2 = new Location(world,
					Integer.valueOf(pos2[0]),
					Integer.valueOf(pos2[1]),
					Integer.valueOf(pos2[2]));
		} else if(!file.contains("SplatZone.Pos1") || !file.contains("SplatZone.Pos2")) {
			this.areapos1 = new Location(null, 0, 0, 0);
			this.areapos2 = new Location(null, 0, 0, 0);
		}
		this.teamscount = getMaxTeam(file);
		this.playerscount = getMaxPlayer(file);
		for(int i = 1; i <= this.teamscount; i++) {
			List<Location> poss = new ArrayList<>();
			for(int ii = 1; ii <= this.playerscount; ii++) {
				String[] teamloc = file.getString("SpawnPos.Team"+i+".P"+ii+".Loc").split("/");
				String[] teamrotation = file.getString("SpawnPos.Team"+i+".P"+ii+".HeadRotation").split("/");
				double x = Double.valueOf(teamloc[0]);
				double y = Double.valueOf(teamloc[1]);
				double z = Double.valueOf(teamloc[2]);
				float yaw = Float.valueOf(teamrotation[0]);
				float pitch = Float.valueOf(teamrotation[1]);
				poss.add(new Location(world, x, y, z, yaw, pitch));
			}
			posisions.put(i, poss);
		}
		this.team1_count = new TeamCountManager();
		this.team2_count = new TeamCountManager();
	}

	public String getName() {return arena;}
	public boolean isStatus() {return status;}
	public String getWorld() {return world;}
	public int getTotalpaintblock() {return totalpaintblock;}
	public Location getStagePosition1() {return stagepos1;}
	public Location getStagePosition2() {return stagepos2;}
	public Location getAreaPosition1() {return areapos1;}
	public Location getAreaPosition2() {return areapos2;}

	/**
	 * チームスポーン地点を取得
	 *
	 * @param team チームの番号(最大値は getTeamsCount() で取得可能)
	 * @param num テレポート場所の数 (最大 getPlayersCount() で取得可能)
	 * @return 引数の番号の座標が帰ってくる
	 */
	public Location getTeamPlayerPosision(int team, int num) {
		if((team > teamscount || team < 0) || (num > playerscount || num < 0))
			return null;
		if(posisions.containsKey(team))
			return posisions.get(team).get(num-1);
		return null;
	}
	
	public int getMaximumTeamNum() {return teamscount;}
	
	public int getMinimumPlayerNum() {
		int minimun = 20;
		for(int i = 1; i <= teamscount; i++) {
			if(!posisions.containsKey(i))
				continue;
			if(minimun > posisions.get(i).size())
				minimun = posisions.get(i).size();
		}
		return minimun;
	}
	
	public int getMaximumPlayerNum() {
		int maximum = 4;
		for(int i = 1; i <= teamscount; i++) {
			if(!posisions.containsKey(i))
				continue;
			if(maximum < posisions.get(i).size())
				maximum = posisions.get(i).size();
		}
		return maximum;
	}
	
	public int getMaximumPlayerNum(int team) {
		return (posisions.containsKey(team) ? posisions.get(team).size() : 0);
	}
	
	public int getWinTeam() {return winteam;}
	
	public SplatColor getSplatColor(int team) {
		if(team > teamscount)
			return SplatColor.WHITE;
		return teamcolor.get(team);
	}
	
	public int getColorTeam(int colorID) {
		for(Entry<Integer, SplatColor> colors : teamcolor.entrySet())
			if(colors.getValue().getColorID() == colorID)
				return colors.getKey().intValue();
		return 0;
	}

	public BattleRunnable getTask() {return runtask;}

	public GameStatusEnum getGameStatus() {return gameStatus;}

	public Scoreboard getScoreboard() {return scoreboard;}

	public Turf_War getBattleClass() {return battle;}

	public List<BlockState> getRollbackblocks() {return rollbackblocks;}

	public TeamCountManager getTeam1_count() {return team1_count;}

	public TeamCountManager getTeam2_count() {return team2_count;}

	public int getTotalareablock() {return totalareablock;}

	public List<ArmorStand> getAreastands() {return areastands;}

	//***********************************************//

	public void setName(String arena) {this.arena = arena;}
	public void setStatus(boolean status) {this.status = status;}
	public void setWorld(String world) {this.world = world;}
	public void setTotalpaintblock(int totalpaintblock) {this.totalpaintblock = totalpaintblock;}

	public void setTeamPlayerPosision(Location loc, int team, int num) {
		List<Location> locations = (posisions.containsKey(team) ? posisions.get(team) : new ArrayList<>());
		locations.add(loc);
		posisions.put(team, locations);
	}
	
	public void setTeamWin(int winteam) {this.winteam = winteam;}

	@Deprecated
	public void setTeamColor(SplatColor color, int team) {
		teamcolor.put(team, color);
	}

	public void setTask(BattleRunnable task) {
		this.runtask = task;
	}

	public void setGameStatus(GameStatusEnum status) {
		ArenaStatusChangeEvent event = new ArenaStatusChangeEvent(this, status);
		Bukkit.getPluginManager().callEvent(event);
		this.gameStatus = status;
		GameSigns.UpdateStatusSign(arena);
	}

	public void setScoreBoard(Scoreboard scoreboard) {this.scoreboard = scoreboard;}

	public void setBattleClass(Turf_War battle) {this.battle = battle;}

	public void addRollBackBlock(BlockState block) {this.rollbackblocks.add(block);}

	public void setTotalareablock(int totalareablock) {this.totalareablock = totalareablock;}

	public void setAreastands(List<ArmorStand> areastands) {this.areastands = areastands;}

	public void updateTeamColor(){
		List<SplatColor> colors = new ArrayList<>(Arrays.asList(SplatColor.values()));
		colors.remove(SplatColor.WHITE);
		IntStream.rangeClosed(1, teamscount).forEach(team -> {
			Collections.shuffle(colors);
			teamcolor.put(team, colors.get(0));
			colors.remove(0);
		});
	}
	
	public void clearStatus() {
		updateTeamColor();
		this.team1_count = this.team2_count = new TeamCountManager();
		this.runtask = null;
		this.rollbackblocks.clear();
		this.setGameStatus(GameStatusEnum.ENABLE);
		this.areastands.clear();
	}
	
	@Override
	public String toString() {
		return "ArenaData [Name="+arena+
				" Status="+status+
				" World="+world+
				" TotalNum="+totalpaintblock+
				" GameStatus="+gameStatus+
				" TotalAreaNum="+totalareablock+
				"]";
	}
	
	private int getMaxPlayer(FileConfiguration file) {
		for(int i = 0; i <= 20; i++)
			if(!file.contains("SpawnPos.Team1.P"+(i+1)))
				return i;
		return 4;
	}
	
	private int getMaxTeam(FileConfiguration file) {
		for(int i = 0; i <= 8; i++)
			if(!file.contains("SpawnPos.Team"+(i+1)))
				return i;
		return 2;
	}
}
