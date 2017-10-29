package jp.kotmw.splatoon.manager;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

import jp.kotmw.splatoon.event.BlockPaintEvent;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.util.SplatColor;

public class Paint {

	@SuppressWarnings("deprecation")
	public static void PaintWool(PlayerData data, Block block) {
		ArenaData arena = DataStore.getArenaData(data.getArena());
		if(block == null || block.getType() == Material.AIR)
			return;
		if(!isCanPaintBlock(block))
			return;
		if(!isCanPaintColor(DyeColor.getByWoolData((byte) SplatColorManager.getColorID(block))))
			return;
		if(SplatColorManager.getColorID(block) == arena.getSplatColor(data.getTeamid()).getColorID())
			return;
		int bonus = 0;//塗ったブロックが敵チームのカラーだったら、この変数に敵チームの番号が入る
		for(int team = 1; team <= arena.getMaximumTeamNum(); team++)
			if((data.getTeamid() != team) && SplatColorManager.getColorID(block) == arena.getSplatColor(team).getColorID()) {
				bonus = team;
				break;
			}
		BlockPaintEvent event = new BlockPaintEvent(block, data, arena);
		Bukkit.getPluginManager().callEvent(event);
		addScore(data, (bonus != 0));//0じゃない場合は敵チームのを上書きしたという事だからボーナスに追加
		addRollBack(arena, block);
		arena.addTeamScore(data.getTeamid(), bonus);//TODO ここ
		ColorChange(block, DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid()));
	}
	
	/**
	 * 指定したブロックの色を指定した色に変更する
	 *
	 * @param block 色を変更するブロック
	 * @param color 色
	 */
	@SuppressWarnings("deprecation")
	public static void ColorChange(Block block, SplatColor color) {
		if(block == null)
			return;
		else MainGame.sync(() -> {
			switch(block.getType()) {
			case WOOL:
				BlockState state = block.getState();
				Wool wool = (Wool)state.getData();
				wool.setColor(color.getDyeColor());
				state.update();
				return;
			case GLASS:
			case STAINED_GLASS:
				block.setType(Material.STAINED_GLASS);
				break;
			case HARD_CLAY:
			case STAINED_CLAY:
				block.setType(Material.STAINED_CLAY);
				break;
			case THIN_GLASS:
			case STAINED_GLASS_PANE:
				block.setType(Material.STAINED_GLASS_PANE);
				break;
			case CARPET:
				block.setType(Material.CARPET);
				break;
			default:
				return;
			}
			block.setData((byte) color.getColorID());
		}); 
	}

	public static void addRollBack(ArenaData data, Block block) {
		for(BlockState state : data.getRollbackblocks()) {
			Location l = block.getLocation();
			if(state.getLocation().equals(l)) return;
		}
		data.addRollBackBlock(block.getState());
	}

	private static void addScore(PlayerData data, boolean bonus) {
		int score = data.getScore();
		if(bonus) {
			data.setScore(score + 2);
			return;
		}
		data.setScore(score + 1);
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
	
	/* block.getBlock().setType(Material)でやるか
	 * block.update(true)にするかを暫く検討
	 * 
	 * block.setType(Material)と
	 * update()じゃ内部データとその場所にあるブロックのデータが食い違い、ロールバックに失敗する。
	 */
	public static void RollBack(ArenaData data) {
		for(BlockState block : data.getRollbackblocks()) {
			switch(block.getType()) {
			case WOOL:
				Wool wool = (Wool) block.getData();
				wool.setColor(wool.getColor());
				break;
			case GLASS:
				block.getBlock().setType(Material.GLASS);
				break;
			case THIN_GLASS:
				block.getBlock().setType(Material.THIN_GLASS);
				break;
			case HARD_CLAY:
				block.getBlock().setType(Material.HARD_CLAY);
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
	
	private static boolean isCanPaintColor(DyeColor color) {
		return DataStore.getConfig().getCanpaintcolors().contains(color.toString());
	}
}
