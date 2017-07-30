package jp.kotmw.splatoon.specialweapon;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.specialweapon.threads.BarrierRunnable;

public class Barrier implements Listener {
	
	@EventHandler
	public void enableBarrier(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(!player.getInventory().getItemInMainHand().getType().equals(Material.BARRIER))
			return;
		new BarrierRunnable(player).runTaskTimer(Main.main, 0, 5);
	}
}
