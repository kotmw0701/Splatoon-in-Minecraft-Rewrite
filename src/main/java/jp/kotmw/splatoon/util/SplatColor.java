package jp.kotmw.splatoon.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

public enum SplatColor {
	BLUE(DyeColor.BLUE, ChatColor.BLUE, Color.fromRGB(0, 0, 255), 11), 
	LIGHT_BLUE(DyeColor.LIGHT_BLUE, ChatColor.AQUA, Color.AQUA, 3), 
	GREEN(DyeColor.GREEN, ChatColor.DARK_GREEN, Color.fromRGB(0, 255, 0), 13), 
	LIME(DyeColor.LIME, ChatColor.GREEN, Color.LIME, 5), 
	YELLOW(DyeColor.YELLOW, ChatColor.YELLOW, Color.YELLOW, 4), 
	ORANGE(DyeColor.ORANGE, ChatColor.GOLD, Color.fromRGB(255, 170, 0), 1), 
	PURPLE(DyeColor.PURPLE, ChatColor.DARK_PURPLE, Color.BLUE, 10), 
	PINK(DyeColor.PINK, ChatColor.LIGHT_PURPLE, Color.FUCHSIA, 6),
	WHITE(DyeColor.WHITE, ChatColor.WHITE, null, 0);
	
	private final DyeColor dyeColor;
	private final ChatColor chatColor;
	private final Color color;
	private final int colornum;
	
	private SplatColor(final DyeColor dyeColor, final ChatColor chatColor, final Color color, final int colornum) {
		this.dyeColor = dyeColor;
		this.chatColor = chatColor;
		this.color = color;
		this.colornum = colornum;
	}
	
	public DyeColor getDyeColor() {return dyeColor;}
	public ChatColor getChatColor() {return chatColor;}
	public Color getColor() {return color;}
	public int getColorID() {return colornum;}
	
	public static SplatColor conversion(DyeColor dyeColor) {
		for(SplatColor color : SplatColor.values()) {
			if(color.getDyeColor().equals(dyeColor))
				return color;
		}
		return WHITE;
	}
	
	public static SplatColor conversion(ChatColor chatColor) {
		for(SplatColor color : SplatColor.values()) {
			if(color.getChatColor().equals(chatColor))
				return color;
		}
		return WHITE;
	}
	
	public static SplatColor conversion(int colornum) {
		for(SplatColor color : SplatColor.values()) {
			if(color.getColorID() == colornum)
				return color;
		}
		return WHITE;
	}
}
