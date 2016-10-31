package jp.kotmw.splatoon.mainweapons;

import jp.kotmw.splatoon.SplatColor;
import jp.kotmw.splatoon.event.BlockPaintEvent;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

public class Paint {

	public static void PaintWool(PlayerData data, Block block) {
		ArenaData arena = DataStore.getArenaData(data.getArena());
		if(block == null || block.getType() == Material.AIR)
			return;
		if(!isCanPaintBlock(block))
			return;
		if(SplatColor.getColorByte(block) == SplatColor.conversionColorByte(arena.getDyeColor(data.getTeamid())))
			return;
		boolean bonus = SplatColor.getColorByte(block) == SplatColor.conversionColorByte(arena.getDyeColor(data.getOpponentTeamid()));
		addScore(data, bonus);
		addRollBack(arena, block);
		BlockPaintEvent event = new BlockPaintEvent(block, data, arena);
		Bukkit.getPluginManager().callEvent(event);
		SplatColor.ColorChange(block, DataStore.getArenaData(data.getArena()).getDyeColor(data.getTeamid()));
	}

	public static void addRollBack(ArenaData data, Block block) {
		for(BlockState state : data.getRollbackblocks()) {
			Location l = block.getLocation();
			if(state.getLocation().equals(l)) {
				return;
			}
		}
		data.addRollBackBlock(block.getState());
	}

	private static void addScore(PlayerData data, boolean bonus) {
		float score = data.getScore();
		if(bonus) {
			data.setScore(score + 0.2f);
			return;
		}
		data.setScore(score + 0.1f);
	}

	public static void SpherePaint(Location center, double radius, PlayerData data) {
		double center_X = center.getX();
		double center_Y = center.getY();
		double center_Z = center.getZ();

		//boolean hollow = false;
		for(double x = center_X - radius; x <= center_X + radius ;x++)
			for(double y = center_Y - radius; y <= center_Y + radius ;y++)
				for(double z = center_Z - radius; z <= center_Z + radius ;z++) {
					double distance = ((center_X - x)*(center_X - x)) + ((center_Y - y)*(center_Y - y)) + ((center_Z - z)*(center_Z - z));
					if(distance < (radius*radius)) {
						Location l = new Location(center.getWorld(), x, y, z);
						Paint.PaintWool(data, l.getBlock());
					}
				}
	}

	public static void RollBack(ArenaData data) {
		for(BlockState block : data.getRollbackblocks()) {
			switch(block.getType()) {
			case WOOL:
				Wool wool = (Wool) block.getData();
				wool.setColor(wool.getColor());
				break;
			case GLASS:
				block.setType(Material.GLASS);
				break;
			case THIN_GLASS:
				block.setType(Material.THIN_GLASS);
				break;
			case HARD_CLAY:
				block.setType(Material.HARD_CLAY);
				break;
			case STAINED_CLAY:
			case STAINED_GLASS:
			case STAINED_GLASS_PANE:
			case CARPET:
				block.setData(block.getData());
				break;
			default:
				return;
			}
			block.update();
		}
	}

	public static boolean isCanPaintBlock(Block block) {
		switch(block.getType()) {
		case WOOL:
		case GLASS:
		case THIN_GLASS:
		case HARD_CLAY:
		case STAINED_CLAY:
		case STAINED_GLASS:
		case STAINED_GLASS_PANE:
		case CARPET:
			return true;
		default:
			return false;
		}
	}
}
