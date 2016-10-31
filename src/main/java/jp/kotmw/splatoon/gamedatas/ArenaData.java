package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

import jp.kotmw.splatoon.SplatColor;
import jp.kotmw.splatoon.event.ArenaStatusChangeEvent;
import jp.kotmw.splatoon.gamedatas.DataStore.ArenaStatusEnum;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.Turf_War;
import jp.kotmw.splatoon.maingame.threads.BattleRunnable;
import jp.kotmw.splatoon.manager.TeamCountManager;
import jp.kotmw.splatoon.util.Location;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.scoreboard.Scoreboard;

public class ArenaData {
	private String arena;
	private boolean status;
	private String world;
	private int totalpaintblock;
	private Location stagepos1, stagepos2;
	private Location areapos1, areapos2;
	private List<Location> team1 = new ArrayList<>();
	private List<Location> team2 = new ArrayList<>();
	private DyeColor team1color;
	private DyeColor team2color;
	private BattleRunnable runtask;
	private ArenaStatusEnum gameStatus;
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
		this.stagepos1 = new Location(world,
				Integer.valueOf(file.getString("Stage.Pos1").split("/")[0]),
				Integer.valueOf(file.getString("Stage.Pos1").split("/")[1]),
				Integer.valueOf(file.getString("Stage.Pos1").split("/")[2]));
		this.stagepos2 = new Location(world,
				Integer.valueOf(file.getString("Stage.Pos2").split("/")[0]),
				Integer.valueOf(file.getString("Stage.Pos2").split("/")[1]),
				Integer.valueOf(file.getString("Stage.Pos2").split("/")[2]));
		if(file.contains("SplatZone.Pos1") && file.contains("SplatZone.Pos2")) {
			this.areapos1 = new Location(world,
					Integer.valueOf(file.getString("SplatZone.Pos1").split("/")[0]),
					Integer.valueOf(file.getString("SplatZone.Pos1").split("/")[1]),
					Integer.valueOf(file.getString("SplatZone.Pos1").split("/")[2]));
			this.areapos2 = new Location(world,
					Integer.valueOf(file.getString("SplatZone.Pos2").split("/")[0]),
					Integer.valueOf(file.getString("SplatZone.Pos2").split("/")[1]),
					Integer.valueOf(file.getString("SplatZone.Pos2").split("/")[2]));
		} else if(!file.contains("SplatZone.Pos1") || !file.contains("SplatZone.Pos2")) {
			this.areapos1 = new Location(null, 0, 0, 0);
			this.areapos2 = new Location(null, 0, 0, 0);
		}
		for(int i = 1; i <= 2; i++) {
			for(int ii = 1; ii <= 4; ii++) {
				double x = Double.valueOf(file.getString("SpawnPos.Team"+i+".P"+ii+".Loc").split("/")[0]);
				double y = Double.valueOf(file.getString("SpawnPos.Team"+i+".P"+ii+".Loc").split("/")[1]);
				double z = Double.valueOf(file.getString("SpawnPos.Team"+i+".P"+ii+".Loc").split("/")[2]);
				float yaw = Float.valueOf(file.getString("SpawnPos.Team"+i+".P"+ii+".HeadRotation").split("/")[0]);
				float pitch = Float.valueOf(file.getString("SpawnPos.Team"+i+".P"+ii+".HeadRotation").split("/")[1]);
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
	public DyeColor getDyeColor(int team) {
		switch(team) {
		case 1:
			return team1color;
		case 2:
			return team2color;
		default:
			break;
		}
		return DyeColor.WHITE;
	}

	public BattleRunnable getTask() {return runtask;}

	public ArenaStatusEnum getGameStatus() {return gameStatus;}

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

	public void setTeam1Color(DyeColor color) {
		this.team1color = color;
	}

	public void setTeam2Color(DyeColor color) {
		this.team2color = color;
	}

	public void setTask(BattleRunnable task) {
		this.runtask = task;
	}

	public void setGameStatus(ArenaStatusEnum status) {
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
		SplatColor.SetColor(this);
		this.team1_count = this.team2_count = new TeamCountManager();
		this.runtask = null;
		this.rollbackblocks.clear();
		this.setGameStatus(ArenaStatusEnum.ENABLE);
		this.areastands.clear();
	}
}
