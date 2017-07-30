package jp.kotmw.splatoon.gamedatas;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerData {
	private String name; //プレイヤー名
	private String arena; //使用ステージ名
	private String room; //使用ルーム名
	private int teamid; //チームID
	private int killcount; //キル数
	private int deathcount; //デス数
	private LivingEntity squid; //プレイヤー専属のイカ
	private boolean Squidmode = false; //イカ状態かどうか
	private boolean climb = false; //壁上り状態かどうか
	private boolean move = true; //動ける状態かどうか
	private boolean allcancel = false;
	private Location loc; //プレイヤー参加前の座標
	private List<ItemStack> items; //プレイヤー参加前のインベントリデータ
	private float paintscore; //塗ったスコア
	private BukkitRunnable task; //プレイヤーの武器のRunnable
	private Thread thread;
	private BukkitRunnable squidtask;
	private int runnableTick; //武器のRunnableのtick
	private boolean paint = false; //ローラー使用の時の塗れる時間かどうか
	private boolean invincible = false; //無敵状態かどうか
	private boolean dead = false;
	private String Weapon;
	private int charge;//チャージャーで使う
	private int inkcooltime;

	public PlayerData(String name) {this.name = name;}

	public String getName() {return name;}

	public String getArena() {return arena;}

	public String getRoom() {return room;}

	public int getTeamid() {return teamid;}

	public int getKillcount() {return killcount;}

	public int getDeathcount() {return deathcount;}

	public LivingEntity getPlayerSquid() {return squid;}

	public boolean isSquidMode() {return Squidmode;}

	public boolean isClimb() {return climb;}

	public boolean isMove() {return move;}

	public boolean isAllCancel() {return allcancel;}

	public Location getRollBackLocation() {return loc;}

	public List<ItemStack> getRollbackItems() {return items;}

	public float getScore() {return paintscore;}

	public BukkitRunnable getTask() {return task;}
	
	public Thread getThread() {return thread;}/////////

	public BukkitRunnable getSquidTask() {return squidtask;}

	public int getTick() {return runnableTick;}

	public boolean isPaint() {return paint;}

	public boolean isInvincible() {return invincible;}

	public boolean isDead() {return dead;}

	public String getWeapon() {return Weapon;}

	public int getCharge() {return charge;}

	public int getInkCoolTime() {return inkcooltime;}

	public int getOpponentTeamid() {
		return teamid == 1 ? 2 : 1;
	}

	public void setName(String name) {this.name = name;}

	public void setArena(String arena) {this.arena = arena;}

	public void setRoom(String room) {this.room = room;}

	public void setTeamid(int teamid) {this.teamid = teamid;}

	public void setKillcount(int killcount) {this.killcount = killcount;}

	public void setDeathcount(int deathcount) {this.deathcount = deathcount;}

	public void setPlayerSquid(LivingEntity squid) {this.squid = squid;}

	public void setSquidMode(boolean Squidmode) {this.Squidmode = Squidmode;}

	public void setClimb(boolean climb) {this.climb = climb;}

	public void setMove(boolean move) {this.move = move;}

	public void setAllCansel(boolean allcancel) {this.allcancel = allcancel;}

	public void setRollBackLocation(Location loc) {this.loc = loc;}

	public void setRollBackItems(List<ItemStack> items) {this.items = items;}

	public void setScore(float paintscore) {this.paintscore = paintscore;}

	public void setTask(BukkitRunnable task) {this.task = task;}
	
	public void setThread(Thread thread) {this.thread = thread;}//////////////

	public void setSquidTask(BukkitRunnable squidtask) {this.squidtask = squidtask;}

	public void setTick(int runnableTick) {this.runnableTick = runnableTick;}

	public void setPaint(boolean paint) {this.paint = paint;}

	public void setInvincible(boolean invincible) {this.invincible = invincible;}

	public void setDead(boolean dead) {this.dead = dead;}

	public void setWeapon(String weapon) {this.Weapon = weapon;}

	public void setCharge(int charge) {this.charge = charge;}

	public void setInkCoolTime(int inkcooltime) {this.inkcooltime = inkcooltime;}
}
