package jp.kotmw.splatoon.manager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.mainweapons.Roller;

public class SplatColorManager {

	/**
	 * ブロックに今ついている色をデータ値で返す
	 *
	 * @param b 調べるブロック
	 * @return 引数に入れられたブロックが色変更可能ブロックの場合は今の色を返す<br>
	 * 色変更可能ブロックじゃない場合は0が帰ってくる
	 */
	@SuppressWarnings("deprecation")
	public static int getColorID(Block b) {
		if(b == null)
			return 0;
		MaterialData materialdata = b.getState().getData();
		if(materialdata.getItemType() == Material.WOOL
				|| materialdata.getItemType() == Material.STAINED_GLASS
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
	 * @param myteam 自チームか
	 * 
	 * @return チームカラーかどうか
	 */
	public static boolean isBelowBlockTeamColor(Player player, boolean myteam) {
		PlayerData data = DataStore.getPlayerData(player.getName());
		ArenaData data2 = DataStore.getArenaData(data.getArena());
		Location loc = player.getLocation().clone();
		if(loc.getBlock().getType() != Material.CARPET)
			loc.add(0, -1, 0);
		int belowColorID = getColorID(loc.getBlock());
		if(belowColorID == 0)
			return false;
		if(myteam)
			return belowColorID == data2.getSplatColor(data.getTeamid()).getColorID();
		for(int team = 1; team <= data2.getMaximumTeamNum(); team++) {
			if(data2.getSplatColor(team).getColorID() == belowColorID)
				return true;
		}
		return false;
	}

	public static boolean isTargetBlockTeamColor(Player p) {
		PlayerData data = DataStore.getPlayerData(p.getName());
		return SplatColorManager.getColorID(getTargetBlock(p)) ==
				DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid()).getColorID();
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
	
	/**
	 * 入力されたDyeColorをColorに変換
	 *
	 * @param dyecolor 変換するDyeColor
	 */
	/*public static Color conversionColor(DyeColor dyecolor)
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
	}*/

	/**
	 * 入力されたDyeColorをChatColorに変換
	 *
	 * @param dyecolor 変換するDyeColor
	 * @return 引数に入れられたDyeColorが使用可能な色だった場合はそれに対応したChatColorを返す
	 * 対応してない場合は null
	 */
	/*public static ChatColor conversionChatColor(DyeColor dyecolor)
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
	}*/

	/**
	 * 入力されたDyeColorをデータ値(byte)に変換
	 *
	 * @param dyecolor 変換するDyeColor
	 * @return 引数に入れられたDyeColorが使用可能な色だった場合はそれに対応したデータ値(byte)を返す
	 * 対応してない場合は 0
	 */
	/*public static byte conversionColorByte(DyeColor dyecolor) {
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
	}*/
}
