package jp.kotmw.splatoon;

import java.io.File;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import jp.kotmw.splatoon.commands.ConsoleCommands;
import jp.kotmw.splatoon.commands.PlayerCommands;
import jp.kotmw.splatoon.commands.SettingCommands;
import jp.kotmw.splatoon.filedatas.OtherFiles;
import jp.kotmw.splatoon.filedatas.PlayerFiles;
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
import jp.kotmw.splatoon.mainweapons.Roller;
import jp.kotmw.splatoon.mainweapons.Shooter;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.subweapon.Bomb;

public class Main extends JavaPlugin{

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
		//pm.registerEvents(new Barrier(), this);
		OtherFiles.AllTemplateFileGenerator();
		PlayerFiles.AllPlayerFileReload();
		StageFiles.AllStageReload();
		WaitRoomFiles.AllRoomReload();
		OtherFiles.AllSignReload();
		WeaponFiles.AllWeaponReload();
		WeaponFiles.AllSubWeaponReload();
		OtherFiles.ConfigReload();
		OtherFiles.RankFileReload();
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
}
