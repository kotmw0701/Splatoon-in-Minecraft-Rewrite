package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jp.kotmw.splatoon.event.ZoneChangeEvent;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.manager.SplatColorManager;
import jp.kotmw.splatoon.manager.SplatScoreBoard;
import jp.kotmw.splatoon.manager.TeamCountManager;

public class SplatZones extends Turf_War {

	public SplatZones(ArenaData data) {
		super(data);
	}

	public void showZone() {
		List<ArmorStand> areastands = new ArrayList<ArmorStand>();
		int x1 = (int)data.getAreaPosition1().getX();
		int y1 = (int)data.getAreaPosition1().getY();
		int z1 = (int)data.getAreaPosition1().getZ();
		int x2 = (int)data.getAreaPosition2().getX();
		int z2 = (int)data.getAreaPosition2().getZ();
		for(int x = x2; x <= x1; x++) {
			for(int y = y1+2;y <= y1+20;y+=5) {
				for(int z = z2; z <= z1; z++) {
					if((x == x1 || x == x2) || (z == z1 || z == z2)) {
						Location l = new Location(Bukkit.getWorld(data.getWorld()), x+0.5, y, z+0.5);
						ArmorStand stand = (ArmorStand) Bukkit.getWorld(data.getWorld()).spawnEntity(l, EntityType.ARMOR_STAND);
						ItemStack item = new ItemStack(Material.STAINED_GLASS, 1, (short)0);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(ChatColor.RESET+"SplatPluginItem ["+data.getName()+"]");
						item.setItemMeta(meta);
						stand.setHelmet(item);
						stand.setVisible(false);
						stand.setGravity(false);
						stand.setMarker(true);
						areastands.add(stand);
					}
				}
			}
		}
		data.setTotalareablock(getTotalArea(Bukkit.getWorld(data.getWorld()), x1, x2, y1, y1, z1, z2));
		data.setAreastands(areastands);
	}

	public void checkArea() {
		int team1 = 0, team2 = 0;
		int x1 = (int)data.getAreaPosition1().getX();
		int y1 = (int)data.getAreaPosition1().getY();
		int z1 = (int)data.getAreaPosition1().getZ();
		int x2 = (int)data.getAreaPosition2().getX();
		int z2 = (int)data.getAreaPosition2().getZ();
		for(int x = x2; x <= x1; x++) {
			for(int z = z2; z <= z1; z++) {
				Block block = Bukkit.getWorld(data.getWorld()).getBlockAt(x, y1, z);
				Block aboveBlock = Bukkit.getWorld(data.getWorld()).getBlockAt(x, y1+1, z);
				if(block.getType() != Material.AIR
						&& aboveBlock.getType() == Material.AIR) {
					int colorbyte = SplatColorManager.getColorID(block);
					if(data.getSplatColor(1).getColorID() == colorbyte) {
						team1++;
					} else if(data.getSplatColor(2).getColorID() == colorbyte) {
						team2++;
					}
				}
			}
		}
		int totalareablock = data.getTotalareablock();
		if((totalareablock*0.5 > team1) && (totalareablock*0.5 > team2))
			return; //開始当初でteam1,team2が両方ともぜんぜん塗ってない時の処理
		TeamCountManager team1_manage = data.getTeam1_count(), team2_manage = data.getTeam2_count();
		if(team1_manage.ishavearea()) {//team1が既にエリアを確保している場合
			if(totalareablock*0.5 < team2) {//相手チームが5割以上になった場合
				//カウントストップ
				for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
					String text = player.getTeamid() == 1 ?
							data.getSplatColor(2).getChatColor()+"カウントストップされた!":
							data.getSplatColor(2).getChatColor()+"カウントストップした！";
					MainGame.sendTitle(player, 0, 5, 0, " ", text);
				}
				team1_manage.sethavearea(false);
				return;
			}
			team1_manage.updatecount();
			SplatScoreBoard.updateTeam1Count(data);
			return;
		} else if(team2_manage.ishavearea()) {
			if(totalareablock*0.5 < team1) {//相手チームが5割以上になった場合
				//カウントストップ
				for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
					String text = player.getTeamid() == 1 ?
							data.getSplatColor(1).getChatColor()+"カウントストップした!":
							data.getSplatColor(1).getChatColor()+"カウントストップされた！";
					MainGame.sendTitle(player, 0, 5, 0, " ", text);
				}
				team2_manage.sethavearea(false);
				return;
			}
			team2_manage.updatecount();
			SplatScoreBoard.updateTeam2Count(data);
			return;
		}
		if((totalareablock*0.8 < team1) || (totalareablock*0.8 < team2)) {
			System.out.println("Team1: "+team1+"      "+"Team2: "+team2);
			int ensureteam = team1 > team2 ? 1 : 2;
			switch(ensureteam) {
			case 1:
				team1_manage.sethavearea(true);
				int team1_before = team2_manage.setpenalty();
				SplatScoreBoard.updatePenalty(data, 1, team1_before);
				break;
			case 2:
				team2_manage.sethavearea(true);
				int team2_before = team1_manage.setpenalty();
				SplatScoreBoard.updatePenalty(data, 2, team2_before);
				break;
			}
			ZoneChangeEvent event = new ZoneChangeEvent();
			Bukkit.getPluginManager().callEvent(event);
			EnsureArea(ensureteam);
			for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
				String text = player.getTeamid() == ensureteam ?
						data.getSplatColor(ensureteam).getChatColor()+"ガチエリア確保した!":
						data.getSplatColor(ensureteam).getChatColor()+"ガチエリア確保された!";
				MainGame.sendTitle(player, 0, 5, 0, " ", text);
			}
		}
	}

	public void EnsureArea(int ensureteam) {
		int x1 = (int)data.getAreaPosition1().getX();
		int z1 = (int)data.getAreaPosition1().getZ();
		int x2 = (int)data.getAreaPosition2().getX();
		int z2 = (int)data.getAreaPosition2().getZ();
		for(int x = x2; x <= x1; x++)
			for(int z = z2; z <= z1; z++) {
				Block block = Bukkit.getWorld(data.getWorld()).getBlockAt(x, (int)data.getAreaPosition1().getY(), z);
				Paint.addRollBack(data, block);
				Paint.ColorChange(block, data.getSplatColor(ensureteam));
			}
		data.getAreastands().forEach(stand -> stand.setHelmet(new ItemStack(Material.STAINED_GLASS, 1, (byte)data.getSplatColor(ensureteam).getColorID())));
	}

	public static void clearAreaStand(ArenaData data) {
		for(Entity entity : Bukkit.getWorld(data.getWorld()).getEntities()) {
			if(entity.getType() != EntityType.ARMOR_STAND)
				return;
			if(((ArmorStand)entity).getHelmet().getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RESET+"SplatPluginItem ["+data.getName()+"]"))
				entity.remove();
		}
	}
}
