package fr.devmaster.bossbar;

import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BossBarMain extends JavaPlugin implements Listener{
	
	private static String nmsversion;
	private static String message;
	private static BossBarMain instance;
	@Override
	public void onEnable() {
		saveDefaultConfig();
		instance = this;
		nmsversion = Bukkit.getServer().getClass().getPackage().getName();
		nmsversion = nmsversion.substring(nmsversion.lastIndexOf(".") + 1);
		message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("defaultmessage"));
		Bukkit.getPluginManager().registerEvents(this, this);
		BossBarRefresh.StartRefreshing();
		Log("§aThe server has started MasterBarApi sucessfully loaded :)");
		new MasterBarAPI();
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		BossBar bossbar = new BossBar(message, e.getPlayer(), 1.0F);
		bossbar.sendToPlayer();
	}
	@SuppressWarnings("deprecation")
	public static void Debug(String message){ Date d = new Date(); Bukkit.getConsoleSender().sendMessage("§8[§bMaster§cBarAPI§8] §a[DEBUG] §c[§e"+d.getHours()+"§bH§e"+d.getMinutes()+"§b,§e"+d.getSeconds()+"§c] "+message); }
	public static void Log(String message){ Bukkit.getConsoleSender().sendMessage("§8[§bMaster§cBarAPI§8] "+message);}
	public static BossBarMain getInstance(){ return instance; }
	public static String getNmsVersion(){ return nmsversion; }
}
