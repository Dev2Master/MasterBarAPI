package fr.devmaster.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

class BossBarRefresh {
	@SuppressWarnings("deprecation")
	public static void StartRefreshing(){
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(BossBarMain.getInstance(),new Runnable() {
			@Override
			public void run() {
				for(Player pls : BossBar.bossMap.keySet()){
					if(pls == null || !pls.isOnline()){
						BossBar.bossMap.remove(pls);
					} else{
						BossBar.bossMap.get(pls).updatePosition();
						BossBar.bossMap.get(pls).updateData();
					}
				}
			}
		}, 1,1);
	}

}
