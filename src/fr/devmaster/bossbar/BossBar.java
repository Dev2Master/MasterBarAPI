 package fr.devmaster.bossbar;

import java.lang.reflect.Method;
import java.util.HashMap;		
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BossBar {
	
	private Class<?> packetTeleport;
	private Object destroy;
	private String text;
	private float percent;
	private Player player;
	private int id;
	private Object ptp;
	private Object wither;
	private Object world;
	static Map<Player,BossBar> bossMap = new HashMap<Player,BossBar>();
	public BossBar (String text,Player player,float percent) {
		if (text.length() > 64) text = text.substring(0, 63);
        if (percent > 1.0f) percent = 1.0f;
        if (percent < 0.05f) percent = 0.05f;
		this.text = text;
		this.percent = percent;
		this.player = player;
		this.world = ReflectionUtils.getHandle(player.getWorld());
	}
	public static boolean contains(Class<?> interfaze,Class<?>[] interfaces){
		for(Class<?> clazz : interfaces){
			if(clazz == interfaze) return true;
		}
		return false;
	}
    private static void sendPacket(Player player, Object packet) {
        try {
			Object handle = player.getClass().getMethod("getHandle").invoke(player);
			Object connection = handle.getClass().getField("playerConnection").get(handle);
			Method sendPacket = getMethod(connection.getClass(), "sendPacket");
			sendPacket.setAccessible(true);
            sendPacket.invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static Location getBossLocation(Player player) {
        Location l = player.getLocation();
        l.add(l.getDirection().multiply(50));
        return l;
    }
	public void sendToPlayer(){
		try {
	        if (percent > 1.0f) percent = 1.0f;
	        if (percent < 0.05f) percent = 0.05f;
			sendPacket(player, getSpawnPacket());
			BossBar.bossMap.put(player,this);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	 public Object getSpawnPacket() {
         try {
             Class<?> entityLiving = ReflectionUtils.getCraftClass("EntityLiving");
             Class<?> witherClass = ReflectionUtils.getCraftClass("EntityWither");
             wither = witherClass.getConstructor(ReflectionUtils.getCraftClass("World")).newInstance(world);
             Location loc = getBossLocation(player);
             ReflectionUtils.getMethod(witherClass, "setLocation", double.class, double.class, double.class, float.class, float.class).invoke(wither, loc.getX(), loc.getY(), loc.getZ(),loc.getPitch() , loc.getYaw());
             ReflectionUtils.getMethod(witherClass, "setCustomName", String.class).invoke(wither, text);
             ReflectionUtils.getMethod(witherClass, "setHealth", float.class).invoke(wither, (percent * 300.0F));
             id = (Integer) ReflectionUtils.getMethod(witherClass, "getId").invoke(wither);
             Class<?> packetClass = ReflectionUtils.getCraftClass("PacketPlayOutSpawnEntityLiving");
             return packetClass.getConstructor(entityLiving).newInstance(wither);
         } catch (Exception e) {
             e.printStackTrace();
             return null;
         }
     }
	
	public void updatePosition(){
		try{
		packetTeleport = ReflectionUtils.getCraftClass("PacketPlayOutEntityTeleport");
		Location loc = getBossLocation(player);
		ptp = packetTeleport.getConstructor(new Class<?>[]{int.class, int.class, int.class, int.class, byte.class, byte.class, boolean.class}).newInstance(this.id, loc.getBlockX() * 32,loc.getBlockY() * 32, loc.getBlockZ() * 32, (byte) ((int) loc.getYaw() * 256 / 360), (byte) ((int) loc.getPitch() * 256 / 360), false);
		sendPacket(player, ptp);
		}catch(Exception e){ e.printStackTrace(); }
	}
	
	public void remove(Player player){
		try{
		Object handle = ReflectionUtils.getHandle(player);
		Object connection = handle.getClass().getField("playerConnection").get(handle);
		Method sendPacket = getMethod(connection.getClass(), "sendPacket");
		sendPacket.setAccessible(true);
		sendPacket.invoke(connection, destroy);
		BossBar.bossMap.remove(player);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void updateData(){
	try {
		Class<?> watcherClass = ReflectionUtils.getCraftClass("DataWatcher");
		Class<?> entityClass = ReflectionUtils.getCraftClass("Entity");
		Class<?> watcherPacketClass = ReflectionUtils.getCraftClass("PacketPlayOutEntityMetadata");
		Object watcher = watcherClass.getConstructor(entityClass).newInstance(wither);
		Method a = ReflectionUtils.getMethod(watcherClass, "a", int.class, Object.class);
		a.invoke(watcher, 0,(byte) 0x20);
		a.invoke(watcher, 2,(String) text);
		a.invoke(watcher, 3, (Float) (percent * 300.0F));
		a.invoke(watcher, 7,(Integer) 0);
		a.invoke(watcher, 8,(byte) 0);
		a.invoke(watcher, 20, (Integer) Integer.MAX_VALUE);
		Object packet = watcherPacketClass.getConstructor(int.class,watcherClass,boolean.class).newInstance(id,watcher,true);
		sendPacket(player, packet);
	} catch (Exception e) {	e.printStackTrace(); }
	}
	
	private static Method getMethod(Class<?> clazz,String name){
		for(Method m : clazz.getMethods()){
			if(m.getName() == name){
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}
	public String getText(){
		return text;
	}
	public float getPercent(){
		return percent;
	}
	public Player getPlayer(){
		return player;
	}
	public void setPercent(float percent){
		this.percent = percent;
	}
	public void setText(String text){
		this.text = ChatColor.translateAlternateColorCodes('&', text);
	}
	private static class ReflectionUtils {

        public static Class<?> getCraftClass(String ClassName) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            String version = name.substring(name.lastIndexOf('.') + 1) + ".";
            String className = "net.minecraft.server." + version + ClassName;
            Class<?> c = null;
            try {
                c = Class.forName(className);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        public static Object getHandle(Entity entity) {
            try {
                return getMethod(entity.getClass(), "getHandle").invoke(entity);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Object getHandle(World world) {
            try {
                return getMethod(world.getClass(), "getHandle").invoke(world);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public static Method getMethod(Class<?> cl, String method, Class<?>... args) {
            for (Method m : cl.getMethods())
                if (m.getName().equals(method) && ClassListEqual(args, m.getParameterTypes())) return m;
            return null;
        }

        public static Method getMethod(Class<?> cl, String method) {
            for (Method m : cl.getMethods())
                if (m.getName().equals(method)) return m;
            return null;
        }

        public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
            boolean equal = true;
            if (l1.length != l2.length) return false;
            for (int i = 0; i < l1.length; i++)
                if (l1[i] != l2[i]) {
                    equal = false;
                    break;
                }
            return equal;
        }

    }

}
