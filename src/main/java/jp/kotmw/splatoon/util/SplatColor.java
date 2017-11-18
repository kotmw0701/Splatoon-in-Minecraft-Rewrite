package jp.kotmw.splatoon.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import jp.kotmw.splatoon.util.DetailsColor.DetailsColorType;


public enum SplatColor {
	BLUE(DyeColor.BLUE, ChatColor.BLUE, Color.fromRGB(0, 0, 255), DetailsColorType.WoolColor_BLUE.getColor(), 11), 
	LIGHT_BLUE(DyeColor.LIGHT_BLUE, ChatColor.AQUA, Color.AQUA, DetailsColorType.WoolColor_AQUA.getColor(), 3), 
	GREEN(DyeColor.GREEN, ChatColor.DARK_GREEN, Color.fromRGB(0, 255, 0), DetailsColorType.WoolColor_GREEN.getColor(), 13), 
	LIME(DyeColor.LIME, ChatColor.GREEN, Color.LIME, DetailsColorType.WoolColor_LIME.getColor(), 5), 
	YELLOW(DyeColor.YELLOW, ChatColor.YELLOW, Color.YELLOW, DetailsColorType.WoolColor_YELLOW.getColor(), 4), 
	ORANGE(DyeColor.ORANGE, ChatColor.GOLD, Color.fromRGB(255, 170, 0), DetailsColorType.WoolColor_ORANGE.getColor(), 1), 
	PURPLE(DyeColor.PURPLE, ChatColor.DARK_PURPLE, Color.BLUE, DetailsColorType.WoolColor_PURPLE.getColor(), 10), 
	PINK(DyeColor.PINK, ChatColor.LIGHT_PURPLE, Color.FUCHSIA, DetailsColorType.WoolColor_PINK.getColor(), 6),
	WHITE(DyeColor.WHITE, ChatColor.WHITE, Color.WHITE, DetailsColorType.WoolColor_WHITE.getColor(), 0);
	
	private final DyeColor dyeColor;
	private final ChatColor chatColor;
	private final Color color;
	private final DetailsColor detailsColor;
	private final int colornum;
	
	private SplatColor(final DyeColor dyeColor, final ChatColor chatColor, final Color color, final DetailsColor detailsColor, final int colornum) {
		this.dyeColor = dyeColor;
		this.chatColor = chatColor;
		this.color = color;
		this.detailsColor = detailsColor;
		this.colornum = colornum;
	}
	
	public DyeColor getDyeColor() {return dyeColor;}
	public ChatColor getChatColor() {return chatColor;}
	public Color getColor() {return color;}
	public DetailsColor getDetailsColor() {return detailsColor;}
	
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
