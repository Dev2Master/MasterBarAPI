package fr.devmaster.bossbar;

import org.bukkit.entity.Player;

public class MasterBarAPI {

	private static MasterBarAPI instance;
	public void Load(){
		instance = this;
	}
	public static MasterBarAPI HookOnAPI(){
		return instance;
	}
	public BossBar getBossBar(Player p){
		return BossBar.bossMap.get(p);
	}
	public BossBar createBossBar(Player p,String text,float percent) throws BossBarException{
		if(hasBossBar(p)){
			throw new BossBarException("A plugin tried to create a bossbar on a player who already had a bossbar active");
		} else {
			BossBar bossbar = new BossBar(text, p, percent);
			bossbar.sendToPlayer();
			return bossbar;
		}
	}
	public boolean hasBossBar(Player p){
		if(getBossBar(p) == null)return false;
		else return true;
	}
}