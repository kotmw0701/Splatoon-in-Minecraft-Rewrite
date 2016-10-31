package jp.kotmw.splatoon;

import java.io.File;

import jp.kotmw.splatoon.commands.ConsoleCommands;
import jp.kotmw.splatoon.commands.PlayerCommands;
import jp.kotmw.splatoon.commands.SettingCommands;
import jp.kotmw.splatoon.filedatas.OtherFiles;
import jp.kotmw.splatoon.filedatas.StageFiles;
import jp.kotmw.splatoon.filedatas.WaitRoomFiles;
import jp.kotmw.splatoon.filedatas.WeaponFiles;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.InvMenu;
import jp.kotmw.splatoon.maingame.Listeners;
import jp.kotmw.splatoon.maingame.SquidMode;
import jp.kotmw.splatoon.mainweapons.Charger;
import jp.kotmw.splatoon.mainweapons.Paint;
import jp.kotmw.splatoon.mainweapons.Roller;
import jp.kotmw.splatoon.mainweapons.Shooter;
import jp.kotmw.splatoon.subweapon.Bomb;
import jp.kotmw.splatoon.subweapon.threads.SprinklerRunnable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	public static Main main;
	public String filepath = getDataFolder() + File.separator;
	public static double xz = 0.2,y = 0.2;
	public static boolean schedule = true;

	@Override
	public void onEnable() {
		main = this;
		getCommand("splatsetting").setExecutor(new SettingCommands());
		getCommand("splatconsole").setExecutor(new ConsoleCommands());
		getCommand("splatoon").setExecutor(new PlayerCommands());
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new Listeners(), this);
		pm.registerEvents(new SquidMode(), this);
		pm.registerEvents(new GameSigns(), this);
		pm.registerEvents(new InvMenu(), this);
		pm.registerEvents(new Shooter(), this);
		pm.registerEvents(new Roller(), this);
		pm.registerEvents(new Charger(), this);
		pm.registerEvents(new Bomb(), this);
		pm.registerEvents(this, this);
		OtherFiles.AllTemplateFileGenerator();
		StageFiles.AllStageReload();
		WaitRoomFiles.AllRoomReload();
		OtherFiles.AllSignReload();
		WeaponFiles.AllWeaponReload();
		WeaponFiles.AllSubWeaponReload();
		OtherFiles.ConfigReload();
	}

	@Override
	public void onDisable() {
		schedule = false;
		DataStore.datasAllClear();
		for(String arena : DataStore.getArenaList()) {
			ArenaData data = DataStore.getArenaData(arena);
			if(data.getTask()!=null) {
				data.getTask().cancel();
				Paint.RollBack(data);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		if(block.getType() != Material.END_ROD)
			return;
		new SprinklerRunnable(block.getLocation()).runTaskTimer(this, 0, 1);
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		Block block = e.getClickedBlock();
		if(block.getType() != Material.SIGN
				&& block.getType() != Material.SIGN_POST
				&& block.getType() != Material.WALL_SIGN)
			return;
		Sign sign = (Sign)block.getState();
		if(sign.getLine(0).equalsIgnoreCase("[Timer]")) {
			ScheduleTest thread = new ScheduleTest(e.getPlayer());
			thread.start();
		}
	}
}
