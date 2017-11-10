/**
 * @author kotmw0701
 *
 */
package jp.kotmw.splatoon.util;

import java.lang.reflect.Constructor;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Title extends NMSBase{

	public static void sendTitle(Player p, String main, String sub) {
		sendTitle(p, 1, 5, 1, main, sub);
	}

	public static void sendMainTitle(Player p, int fadein, int stay, int fadeout, String main) {
		sendTitle(p, fadein, stay, fadeout, main, null);
	}

	public static void sendSubTitle(Player p, int fadein, int stay, int fadeout, String sub) {
		sendTitle(p, fadein, stay, fadeout, " ", sub);
	}

	public static void sendTitle(Player p, int fadein, int stay, int fadeout, String main, String sub) {
		try {
			fadein = fadein*20;
			stay = stay*20;
			fadeout = fadeout*20;
			Object mainPacket;
			Object subPacket;
			Object time = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TIMES").get(null);
			Object TimeIBComponent = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\""+main+"\"}");
			Constructor<?> Timeconstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent")
					, int.class, int.class, int.class);
			Object timepacket = Timeconstructor.newInstance(time, TimeIBComponent, fadein, stay, fadeout);
			sendPacket(p, timepacket);
			if(main != null) {
				main = ChatColor.translateAlternateColorCodes('&', main);

				Object maintitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
				Object IBComponent = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\""+main+"\"}");
				Constructor<?> constructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
				mainPacket = constructor.newInstance(maintitle, IBComponent);
				sendPacket(p, mainPacket);
			}
			if(sub != null) {
				sub = ChatColor.translateAlternateColorCodes('&', sub);

				Object subtitle = getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
				Object IBComponent = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\""+sub+"\"}");
				Constructor<?> constructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"));
				subPacket = constructor.newInstance(subtitle, IBComponent);
				sendPacket(p, subPacket);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendActionBar(Player p, String msg) {
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		try {
			Object IBComponent = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(getNMSClass("IChatBaseComponent"), "{\"text\":\""+msg+"\"}");
			Object packet = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), (isCMT() ? getNMSClass("ChatMessageType") : byte.class)).newInstance(IBComponent, (isCMT() ? getNMSClass("ChatMessageType").getDeclaredMethod("a", byte.class).invoke(getNMSClass("ChatMessageType"), (byte)2) : (byte)2));
			sendPacket(p, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
