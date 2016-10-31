package jp.kotmw.splatoon;

import java.util.Random;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.mainweapons.Roller;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

public class SplatColor {

	public static void SetColor(ArenaData data) {
		DyeColor team1 = randomColor(), team2 = randomColor();
		while(team1 == team2) {
			team2 = randomColor();
		}
		data.setTeam1Color(team1);
		data.setTeam2Color(team2);
	}

	/**
	 * 指定したブロックの色を指定した色に変更する
	 *
	 * @param block 色を変更するブロック
	 * @param color 色
	 */
	@SuppressWarnings("deprecation")
	public static void ColorChange(Block block, DyeColor color) {
		if(block == null)
			return;
		else switch(block.getType()) {
		case WOOL:
			BlockState state = block.getState();
			Wool wool = (Wool)state.getData();
			wool.setColor(color);
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
		block.setData(conversionColorByte(color));
	}

	/**
	 * ブロックに今ついている色をデータ値で返す
	 *
	 * @param b 調べるブロック
	 * @return 引数に入れられたブロックが色変更可能ブロックの場合は今の色を返す<br>
	 * 色変更可能ブロックじゃない場合は0が帰ってくる
	 */
	@SuppressWarnings("deprecation")
	public static byte getColorByte(Block b) {
		if(b == null)
			return 0;
		MaterialData materialdata = b.getState().getData();
		if(materialdata instanceof Wool)
			return conversionColorByte(((Wool)materialdata).getColor());
		if(materialdata.getItemType() == Material.STAINED_GLASS
				|| materialdata.getItemType() == Material.STAINED_GLASS_PANE
				|| materialdata.getItemType() == Material.STAINED_CLAY
				|| materialdata.getItemType() == Material.CARPET)
			return materialdata.getData();
		return 0;
	}

	/**
	 * 足元のブロックを取得して、それがチームカラーと一緒だったらtrue
	 *
	 * @param p 対象のプレイヤー
	 * @return チームカラーかどうか
	 */
	public static boolean isBelowBlockTeamColor(Player player) {
		PlayerData data = DataStore.getPlayerData(player.getName());
		Location loc = player.getLocation().clone();
		int teamcolorID = SplatColor.conversionColorByte(DataStore.getArenaData(data.getArena()).getDyeColor(data.getTeamid()));
		if(loc.getBlock().getType() != Material.CARPET)
			loc.add(0, -1, 0);
		return SplatColor.getColorByte(loc.getBlock()) == teamcolorID;
	}

	/**
	 * 足元のブロックを取得して、敵チームカラーだった場合にはtrue
	 *
	 * @param player 対象のプレイヤー
	 * @return 敵チームが塗ってあるブロックかどうか
	 */
	public static boolean isBelowBlockOpponentTeamColor(Player player) {
		PlayerData data = DataStore.getPlayerData(player.getName());
		Location loc = player.getLocation().clone();
		int opponentteamcolorID = SplatColor.conversionColorByte(DataStore.getArenaData(data.getArena()).getDyeColor(data.getOpponentTeamid()));
		if(loc.getBlock().getType() != Material.CARPET)
			loc.add(0, -1, 0);
		return SplatColor.getColorByte(loc.getBlock()) == opponentteamcolorID;

	}

	public static boolean isTargetBlockTeamColor(Player p) {
		PlayerData data = DataStore.getPlayerData(p.getName());
		return SplatColor.getColorByte(getTargetBlock(p)) ==
				SplatColor.conversionColorByte(DataStore.getArenaData(data.getArena()).getDyeColor(data.getTeamid()));
	}

	private static Block getTargetBlock(Player p) {
		Location loc = p.getLocation().clone();
		int directionID = Roller.PlayerDirectionID_Four(loc.getYaw());
		if(directionID == 0)
			return loc.add(0, 0.5, 1).getBlock();
		else if(directionID == 1)
			return loc.add(-1, 0.5, 0).getBlock();
		else if(directionID == 2)
			return loc.add(0, 0.5, -1).getBlock();
		else if(directionID == 3)
			return loc.add(1, 0.5, 0).getBlock();
		return loc.getBlock();
	}

	private static DyeColor randomColor() {
		DyeColor color[] = new DyeColor[8];
		Random random = new Random();
		int i = random.nextInt(8);
		color[0] = DyeColor.BLUE;
		color[1] = DyeColor.LIGHT_BLUE;
		color[2] = DyeColor.GREEN;
		color[3] = DyeColor.LIME;
		color[4] = DyeColor.YELLOW;
		color[5] = DyeColor.ORANGE;
		color[6] = DyeColor.PURPLE;
		color[7] = DyeColor.PINK;
		return color[i];
	}

	/**
	 * 入力されたDyeColorをColorに変換
	 *
	 * @param dyecolor 変換するDyeColor
	 */
	public static Color conversionColor(DyeColor dyecolor)
	{
		Color color = null;
		if(dyecolor.equals(DyeColor.BLUE))color = Color.fromRGB(0, 0, 255);//BLUE
		else if(dyecolor.equals(DyeColor.LIGHT_BLUE))color = Color.AQUA;// ok
		else if(dyecolor.equals(DyeColor.GREEN))color = Color.fromRGB(0, 255, 0);//GREEN
		else if(dyecolor.equals(DyeColor.LIME))color = Color.LIME;// ok
		else if(dyecolor.equals(DyeColor.YELLOW))color = Color.YELLOW;// ok
		else if(dyecolor.equals(DyeColor.ORANGE))color = Color.fromRGB(255, 170, 0);// ok
		else if(dyecolor.equals(DyeColor.PURPLE))color = Color.BLUE;//PURLE ok
		else if(dyecolor.equals(DyeColor.PINK))color = Color.FUCHSIA;// ok
		else if(dyecolor.equals(DyeColor.WHITE))color = Color.WHITE;
		return color;
	}

	/**
	 * 入力されたDyeColorをChatColorに変換
	 *
	 * @param dyecolor 変換するDyeColor
	 * @return 引数に入れられたDyeColorが使用可能な色だった場合はそれに対応したChatColorを返す
	 * 対応してない場合は null
	 */
	public static ChatColor conversionChatColor(DyeColor dyecolor)
	{
		ChatColor color = null;
		if(dyecolor.equals(DyeColor.BLUE))color = ChatColor.BLUE;
		else if(dyecolor.equals(DyeColor.LIGHT_BLUE))color =  ChatColor.AQUA;
		else if(dyecolor.equals(DyeColor.GREEN))color = ChatColor.DARK_GREEN;
		else if(dyecolor.equals(DyeColor.LIME))color = ChatColor.GREEN;
		else if(dyecolor.equals(DyeColor.YELLOW))color = ChatColor.YELLOW;
		else if(dyecolor.equals(DyeColor.ORANGE))color = ChatColor.GOLD;
		else if(dyecolor.equals(DyeColor.PURPLE))color = ChatColor.DARK_PURPLE;
		else if(dyecolor.equals(DyeColor.PINK))color = ChatColor.LIGHT_PURPLE;
		else if(dyecolor.equals(DyeColor.WHITE))color = ChatColor.WHITE;
		return color;
	}

	/**
	 * 入力されたDyeColorをデータ値(byte)に変換
	 *
	 * @param dyecolor 変換するDyeColor
	 * @return 引数に入れられたDyeColorが使用可能な色だった場合はそれに対応したデータ値(byte)を返す
	 * 対応してない場合は 0
	 */
	public static byte conversionColorByte(DyeColor dyecolor) {
		byte color = 0;
		if(dyecolor.equals(DyeColor.BLUE))color = 11;
		else if(dyecolor.equals(DyeColor.LIGHT_BLUE))color = 3;
		else if(dyecolor.equals(DyeColor.GREEN))color = 13;
		else if(dyecolor.equals(DyeColor.LIME))color = 5;
		else if(dyecolor.equals(DyeColor.YELLOW))color = 4;
		else if(dyecolor.equals(DyeColor.ORANGE))color = 1;
		else if(dyecolor.equals(DyeColor.PURPLE))color = 10;
		else if(dyecolor.equals(DyeColor.PINK))color = 6;
		return color;
	}
}
