package jp.kotmw.splatoon.gamedatas;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

public class RankData {
	
	private Map<Integer, Integer> rankexp = new HashMap<>();
	
	public RankData(FileConfiguration file) {
		for(int i = 2; i <= 50; i++) {
			rankexp.put(i, file.getInt("Rank.Rank"+i));
		}
	}
	
	public int getRankExp(int rank) {
		return rankexp.get(rank).intValue();
	}
}
