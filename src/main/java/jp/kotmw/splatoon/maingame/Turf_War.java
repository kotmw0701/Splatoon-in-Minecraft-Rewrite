package jp.kotmw.splatoon.maingame;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.maingame.threads.ResultRunnable;
import jp.kotmw.splatoon.manager.SplatColorManager;


public class Turf_War {

	protected ArenaData data;
	protected Map<Integer, Double> result = new HashMap<>();
	private boolean resutcount = false;//最終的に塗り面積を集計するかしないか、するならtrue

	public Turf_War(ArenaData data) {
		this.data = data;
		Map<Integer, Double> result = new HashMap<>();
		result.values();
	}

	public int getTotalArea() {
		int x1 = (int)data.getStagePosition1().getX(), x2 = (int)data.getStagePosition2().getX();
		int y1 = (int)data.getStagePosition1().getY(), y2 = (int)data.getStagePosition2().getY();
		int z1 = (int)data.getStagePosition1().getZ(), z2 = (int)data.getStagePosition2().getZ();
		World world = data.getAreaPosition1().convertLocation().getWorld();
		return getTotalArea(world, x1, x2, y1, y2, z1, z2);
	}
	
	public static int getTotalArea(World world, int x1, int x2, int y1, int y2, int z1, int z2) {
		int count = 0;
		for(int x = x2; x <= x1; x++) {
			for(int y = y2; y <= y1; y++) {
				for(int z = z2; z <= z1; z++) {
					Block block = world.getBlockAt(x, y, z);
					Block aboveBlock = world.getBlockAt(x, (y+1), z);
					if(block.getType() != Material.AIR
							&& isAbobe(aboveBlock.getLocation()))
						if(block.getType() == Material.WOOL
								|| block.getType() == Material.GLASS
								|| block.getType() == Material.THIN_GLASS
								|| block.getType() == Material.HARD_CLAY
								|| block.getType() == Material.STAINED_GLASS
								|| block.getType() == Material.STAINED_GLASS_PANE
								|| block.getType() == Material.STAINED_CLAY
								|| block.getType() == Material.CARPET)
							count++;
				}
			}
		}
		return count;
	}
	
	public void resultBattle() {
		if(resutcount) {
			int[] teamresult = new int[data.getMaximumTeamNum()];
			Location loc1 = data.getStagePosition1().convertLocation(), loc2 = data.getStagePosition2().convertLocation();
			//////////////////////////////////////////////////////////////////
			for(int x = loc2.getBlockX(); x <= loc1.getBlockX(); x++) {
				for(int y = loc2.getBlockY(); y <= loc1.getBlockY(); y++) {
					for(int z = loc2.getBlockZ(); z <= loc1.getBlockZ(); z++) {
						Block block = Bukkit.getWorld(data.getWorld()).getBlockAt(x, y, z);
						Block aboveBlock = Bukkit.getWorld(data.getWorld()).getBlockAt(x, y+1, z);
						if(block.getType() != Material.AIR
								&& isAbobe(aboveBlock.getLocation())) {
							int colorID = SplatColorManager.getColorID(Bukkit.getWorld(data.getWorld()).getBlockAt(x, y, z));
							if(colorID == 0)
								continue;
							int team = data.getColorTeam(colorID);
							if(team == 0)
								continue;
							teamresult[team-1]++;
						}
					}
				}
			}
			for(int team = 1; team <= data.getMaximumTeamNum(); team++) {
				double result_i = teamresult[team-1];
				result.put(team, result_i);
			}
			//////////////////////////////////////////////////////////////////
			//TODO 後に多分この範囲全部消して、戦闘中に集計するようにしていくと思うかな・・・？
			//ただ若干の負荷が不安
		}
		shiftScore();
		BukkitRunnable task = null;
		try {
			task = new ResultRunnable(this);
			task.runTaskTimer(Main.main, 20*5, 5);
		} catch (NoClassDefFoundError e) {
			if(task != null)
				task.cancel();
			Bukkit.broadcastMessage(MainGame.Prefix+ChatColor.RED+"重大なエラーが発生したため、エラーの発生したゲームは強制終了し、ロールバックを行います");
			MainGame.end(data, true);
		}
	}

	public void sendResult() {
		double total = data.getTotalpaintblock();
		Map<Integer, Double> parcent = new HashMap<>();
		result.entrySet().stream().forEach(entry -> parcent.put(entry.getKey(), (entry.getValue()/total)));
		String result = getResuleText(parcent);
		int winner = getMaximunScoreTeam(parcent);
		String win = ChatColor.GOLD.toString()+ChatColor.BOLD+"You Win!";
		String lose = ChatColor.BLUE.toString()+ChatColor.ITALIC+"You Lose...";
		for(int team = 1; team <= data.getMaximumTeamNum(); team++)
			MainGame.sendTitleforTeam(data, team, 0, 5, 0, winner == team ? win : lose, result);
		data.setTeamWin(winner);
	}
	
	public ArenaData getArena() {
		return data;
	}
	
	public double getTeamResult(int team) {
		if(team > data.getMaximumTeamNum() || team < 1)
			return 0.0;
		double fix = (team == 1 ? 0.01 : 0.0);
		if(!result.containsKey(team))
			result.put(team, 0.0);
		return (result.get(team) == 0.0 ? fix : result.get(team));
	}
	
	public double getTotalTeamResult() {
		double result = 0.0;
		for(int team = 1; team <= data.getMaximumTeamNum(); team++)
			result += getTeamResult(team);
		return result;
	}
	
	public void addTeamScore(int team, int beforeteam) {
		if(resutcount)
			return;
		double score = (result.containsKey(team) ? result.get(team) : 0.0);
		if(beforeteam != 0) {
			double score_ = result.get(beforeteam);
			result.put(beforeteam, --score_);
		}
		result.put(team, ++score);
		//負荷が怖い
		//戦闘の最後に一気に全範囲にfor走らせてやるのに比べれば局所的な重さは軽減されると思うけど、
		//戦闘中の平均的な重さが予想できない・・・
	}
	
	private String getResuleText(Map<Integer, Double> parcent) {
		DecimalFormat df = new DecimalFormat("##0.0%");
		String text = "";
		for(Entry<Integer, Double> entry : parcent.entrySet())
			text += ("[ "+data.getSplatColor(entry.getKey()).getChatColor()+df.format(entry.getValue())+ChatColor.WHITE+ " ]   ");
		return text;
	}
	
	private int getMaximunScoreTeam(Map<Integer, Double> parcent) {
		List<Entry<Integer, Double>> list = new ArrayList<>();
		parcent.entrySet().stream()
			.sorted(Collections.reverseOrder(Entry.comparingByValue()))
			.forEach(entry -> list.add(entry));
		return list.get(0).getKey();
	}
	
	private void shiftScore() {
		Map<Integer, Double> result_shift = new HashMap<>(result);
		result.entrySet().forEach(entry -> {
			while(result_shift.containsValue(entry.getValue()))
				result.put(entry.getKey(), entry.getValue()+0.01);
		});
		//TODO この記法だと乱数が入らないから同じスコアだったときに数字の大きいチームの方が必ず勝つ
	}
	
	private static boolean isAbobe(Location location) {
		switch(location.getBlock().getType()) {
		case AIR:
		case IRON_FENCE:
		case IRON_TRAPDOOR:
		case STAINED_GLASS_PANE:
		case THIN_GLASS:
		case FENCE:
		case ACACIA_FENCE:
		case BIRCH_FENCE:
		case DARK_OAK_FENCE:
		case JUNGLE_FENCE:
		case NETHER_FENCE:
		case SPRUCE_FENCE:
			return true;
		default:
			return false;
		}
		
	}
}
