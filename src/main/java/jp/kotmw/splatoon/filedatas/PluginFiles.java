package jp.kotmw.splatoon.filedatas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.Main;

public class PluginFiles {

	static String filepath = Main.main.getDataFolder()+File.separator;
	
	/**
	 * ファイルの保存
	 *
	 * @param fileconfiguration ファイルコンフィグを指定
	 * @param file ファイル指定
	 * @param save 上書きをするかリセットするか
	 */
	protected static void SettingFiles(FileConfiguration fileconfiguration, File file) {
		try {
			fileconfiguration.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * plugins/<プラグイン名>/
	 * 以下の階層にあるディレクトリの中にあるYAMLファイルを取得
	 *
	 * @param dirname ディレクトリ名
	 * @param name ファイル名
	 *
	 */
	protected static File DirFile(String dirname, String name) {
		return new File(filepath + dirname + File.separator + name +".yml");
	}

	/**
	 * 拡張子を抜いたファイル名を取得
	 *
	 * @param name 拡張子を含めたファイル名
	 *
	 */
	private static String getName(String name) {
		if (name == null)
			return null;
		int point = name.lastIndexOf(".");
		if (point != -1)
			return name.substring(0, point);
		return name;
	}

	/**
	 * 指定したディレクトリにあるファイルのリストを取得
	 * (フォルダは含まない)
	 *
	 * @param dir ファイルディレクトリ
	 *
	 */
	protected static List<String> getFileList(File dir) {
		List<String> names = new ArrayList<>();
		for(File file : Arrays.asList(dir.listFiles())) {
			if(file.isDirectory())
				continue;
			names.add(getName(file.getName()));
		}
		return names;
	}

	protected static void setData(File file, String path, Object param) {
		FileConfiguration fileconfig = YamlConfiguration.loadConfiguration(file);
		fileconfig.set(path, param);
		SettingFiles(fileconfig, file);
	}
}
