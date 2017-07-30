package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

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
import jp.kotmw.splatoon.manager.SplatColorManager;
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
	private List<Location> team1 = new ArrayList<>();
	private List<Location> team2 = new ArrayList<>();
	private int teamscount;
	private SplatColor team1color;
	private SplatColor team2color;
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
		for(int i = 1; i <= 8; i++) {
			if(!file.contains("SpawnPos.Team"+i)) {
				teamscount = i-1;
				break;
			}
			for(int ii = 1; ii <= 4; ii++) {
				String[] teamloc = file.getString("SpawnPos.Team"+i+".P"+ii+".Loc").split("/");
				String[] teamrotation = file.getString("SpawnPos.Team"+i+".P"+ii+".HeadRotation").split("/");
				double x = Double.valueOf(teamloc[0]);
				double y = Double.valueOf(teamloc[1]);
				double z = Double.valueOf(teamloc[2]);
				float yaw = Float.valueOf(teamrotation[0]);
				float pitch = Float.valueOf(teamrotation[1]);
				switch (i) {
				case 1:
					team1.add(new Location(world, x, y, z, yaw, pitch));
					break;
				case 2:
					team2.add(new Location(world, x, y, z, yaw, pitch));
					break;
				}
			}
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
	 * @param num 1～4の範囲で
	 * @return 引数の番号の座標が帰ってくる
	 */
	public Location getTeam1(int num) {return team1.get(num-1);}

	/**
	 * チームスポーン地点を取得
	 *
	 * @param num 1～4の範囲で
	 * @return 引数の番号の座標が帰ってくる
	 */
	public Location getTeam2(int num) {return team2.get(num-1);}
	
	public int getTeamsCount() {return teamscount;}
	
	public SplatColor getSplatColor(int team) {
		switch(team) {
		case 1:
			return team1color;
		case 2:
			return team2color;
		default:
			break;
		}
		return SplatColor.WHITE;
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

	public void setTeam1(Location loc, int num) {team1.set(num-1, loc);}
	public void setTeam2(Location loc, int num) {team2.set(num-1, loc);}

	public void setTeam1Color(SplatColor color) {
		this.team1color = color;
	}

	public void setTeam2Color(SplatColor color) {
		this.team2color = color;
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

	public void clearStatus() {
		SplatColorManager.SetColor(this);
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
				" Team1Color="+team1color+
				" Team2Color="+team2color+
				" GameStatus="+gameStatus+
				" TotalAreaNum="+totalareablock+
				"]";
	}
}
