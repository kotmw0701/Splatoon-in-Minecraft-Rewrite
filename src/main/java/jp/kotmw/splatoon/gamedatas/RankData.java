package jp.kotmw.splatoon.gamedatas;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public class RankData {
	
	private Map<Integer, Double> rankexp = new HashMap<>();
	
	public RankData(FileConfiguration file) {
		for(int i = 2; i <= 50; i++) {
			rankexp.put(i, file.getDouble("Rank.Rank"+i));
		}
	}
	
	public double getNextRankExp(int rank) {
		return rankexp.get(rank+1).doubleValue();
	}
	
	public double getRankExp(int rank) {
		return rankexp.get(rank).doubleValue();
	}
}
