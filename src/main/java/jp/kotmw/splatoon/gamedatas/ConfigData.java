package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigData {

	private String configversion;
	private int TransfarCount;
	private int TurfWar;
	private int SplatZones;
	private boolean finishteleportlobby;
	private boolean usesql;
	private List<String> canpaintcolors = new ArrayList<>();

	public ConfigData(FileConfiguration file) {
		this.configversion = file.getString("ConfigVersion");
		this.TransfarCount = file.getInt("TransfarCount");
		this.TurfWar = file.getInt("Time.Turf_War");
		this.SplatZones = file.getInt("Time.Splat_Zones");
		this.finishteleportlobby = file.getBoolean("FinishTeleportLobby");
		this.usesql = file.getBoolean("UseDatabase");
		this.canpaintcolors = file.getStringList("CanPaintColors");
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
}
