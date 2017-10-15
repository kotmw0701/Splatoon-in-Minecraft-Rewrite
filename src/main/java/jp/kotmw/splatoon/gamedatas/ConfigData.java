package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.DyeColor;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigData {

	private String configversion;
	private int TransfarCount;
	private int TurfWar;
	private int SplatZones;
	private boolean finishteleportlobby;
	private boolean usesql;
	private List<String> canpaintcolors = new ArrayList<>();
	private List<String> cansplitblocks = new ArrayList<>();

	public ConfigData(FileConfiguration file) {
		this.configversion = file.getString("ConfigVersion", "4");
		this.TransfarCount = file.getInt("TransfarCount", 10);
		this.TurfWar = file.getInt("Time.Turf_War", 180);
		this.SplatZones = file.getInt("Time.Splat_Zones", 300);
		this.finishteleportlobby = file.getBoolean("FinishTeleportLobby", false);
		this.usesql = file.getBoolean("UseDatabase", false);
		this.canpaintcolors = (file.getStringList("CanPaintColors").isEmpty() 
				? Arrays.stream(DyeColor.values()).map(color -> color.toString()).collect(Collectors.toList())
						: file.getStringList("CanPaintColors"));
		this.cansplitblocks = (file.getStringList("CanSplitBlocks").isEmpty() 
				? Arrays.asList("IRON_FENCE", "IRON_TRAPDOOR")
						: file.getStringList("CanSplitBlocks"));
		//TODO 今後可変するようにした場合、上2つのArrays部分をArrayListでラップしないとエラー出る可能性
	}

	public String getConfigversion() {
		return configversion;
	}
	public int getTransfarCount() {
		return TransfarCount;
	}
	public int getTimeforTurfWar() {
		return TurfWar;
	}
	public int getTimeforSplatZones() {
		return SplatZones;
	}
	public boolean isFinishteleportlobby() {
		return finishteleportlobby;
	}
	public boolean isUsesql() {
		return usesql;
	}
	public List<String> getCanpaintcolors() {
		return canpaintcolors;
	}
	public List<String> getCanSplitBlocks() {
		return cansplitblocks;
	}
}
