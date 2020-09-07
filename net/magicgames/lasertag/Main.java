package net.magicgames.lasertag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

public class Main extends JavaPlugin implements Listener {

	public static boolean gameStarted = false;
	public static Scoreboard sb;
	public static Team admin,spieler,blue,red;
	public static ItemStack defweapon,superweapon;
	public static Objective o;
	public static Main pl;
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
	
	public void onEnable() {
		pl = this;
		Util.createSB();
		Util.updateSB();
		
		defweapon = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta meta = defweapon.getItemMeta();
		meta.setDisplayName("§eMunition§6: §a▎▎▎▎▎▎▎▎▎▎▎§2§l/§a▎▎▎▎▎▎▎▎▎▎▎▎");
		defweapon.setItemMeta(meta);
		
		superweapon = new ItemStack(Material.DIAMOND_HOE);
		ItemMeta meta2 = superweapon.getItemMeta();
		meta2.setDisplayName("§eSuperwaffe");
		superweapon.setItemMeta(meta2);
		
		this.getCommand("lasertag").setExecutor(new CMD_Lasertag());
		this.getServer().getPluginManager().registerEvents(this, this);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				for(Player p : Bukkit.getOnlinePlayers()) {
					for(Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 20, 20, 20)) {
						if(e instanceof Snowball) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "execute @r ~ ~ ~ execute @e[type=Snowball] ~ ~ ~ /particle instantSpell ~ ~ ~ 0 0 0 0 3");
							break;
						}
					}
				}
			}
		}, 0*0L, 0*0L);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			
			@Override
			public void run() {
				for(Player pl : Bukkit.getOnlinePlayers()) {	
					try {
						String txt = pl.getItemInHand().getItemMeta().getDisplayName();
						IChatBaseComponent com = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + txt + "\"}");
						PacketPlayOutChat p = new PacketPlayOutChat(com, (byte)2);
						((CraftPlayer)pl).getHandle().playerConnection.sendPacket(p);
					}catch(Exception ex) {}
				}
			}
		}, 1*2L, 1*2L);
	
	}

	
	@EventHandler
	public void noUproot(PlayerInteractEvent event)
	{
	    if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
	        event.setCancelled(true);
	}
	
	@EventHandler
	public void onMoveArmor(InventoryClickEvent e) {
		try {
			if(e.getCurrentItem().getType().toString().contains("LEATHER")) e.setCancelled(true);
		}catch(Exception ex) {}
	}
	
	@EventHandler
	public void onWeather(WeatherChangeEvent e) {
		e.setCancelled(true);
	}
	
	public static ArrayList<Player> reloadFix = new ArrayList<Player>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onShot(PlayerInteractEvent e) {
		try {
			Player p = e.getPlayer();
			if((!(e.getAction() == Action.RIGHT_CLICK_BLOCK)) && (!(e.getAction() == Action.RIGHT_CLICK_AIR))) return;
			String s = p.getItemInHand().getItemMeta().getDisplayName();
			
			if(p.getItemInHand().getType() == Material.DIAMOND_HOE) {
				s = s.replaceAll("§eMunition§6: §a", "");
				s = s.replaceAll("§2", "");
				s = s.replaceAll("§l", "");
				s = s.replaceAll("§a", "");
				String[] a = s.split("/");
				int nomuni = 0;
				for(char c : a[1].toCharArray()) {
					if(c == '╻') {
						nomuni++;
					}
				}
				if(nomuni == 12) {
					if(!reloadFix.contains(p)) {
						p.sendMessage("§2» §aLasertag §2┃ §aBitte warte bis deine Waffe nachgeladen ist§2!");
						reloadFix.add(p);
						Bukkit.getScheduler().runTaskLater(this, new Runnable() {
							
							@Override
							public void run() {
								reloadFix.remove(p);
								if(p.getItemInHand().getType() == Material.DIAMOND_HOE) {
									p.setItemInHand(defweapon);
									p.sendMessage("§2» §aLasertag §2┃ §aDeine Waffe wurde nachgeladen§2!");
								}
							}
							
						}, 2*20L);	
					}
				}else {
					Snowball sn = p.throwSnowball();
					for(Player pl : Bukkit.getOnlinePlayers()) {
						PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(sn.getEntityId());
						((CraftPlayer)pl).getHandle().playerConnection.sendPacket(packet);
					}
					sn.setMetadata("Owner", new FixedMetadataValue(this, p.getName()));
					if(spawnProtection.contains(e.getPlayer())) spawnProtection.remove(e.getPlayer());
 					
					nomuni++;
					String name = "§eMunition§6: §a▎▎▎▎▎▎▎▎▎▎▎§2§l/§a";
					int muni = 12-nomuni;
					
					for(int c = 0; c<nomuni; c++) {
						name = name + "╻";
					}
					
					for(int c = 0; c<muni; c++) {
						name = name + "▎";
					}
				
					ItemStack it = new ItemStack(Material.DIAMOND_HOE);
					ItemMeta meta = it.getItemMeta();
					meta.setDisplayName(name);
					it.setItemMeta(meta);
					p.setItemInHand(it);

				}		
			}
		}catch(Exception ex) {}
	}
	
	public static ArrayList<Player> spawnProtection = new ArrayList<Player>();
	
	@EventHandler
	public void onKill(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity entity = e.getEntity();
		
		if((entity.getType() == EntityType.PLAYER) && (damager.getType() == EntityType.SNOWBALL)){
			damager.remove();
			if(damager.hasMetadata("Owner")) {
				List<MetadataValue> v = damager.getMetadata("Owner");
				String t = v.get(0).asString();
				Player target =Bukkit.getPlayer(t);
				Player player = (Player) entity;
				if(spawnProtection.contains(player)) {
					target.sendMessage("§2» §aLasertag §2┃ §e" + player.getName() + " ist gerade erst gestorben§6! §eBitte warte paar Sekunden§6...");
				}else {
					String teamPlayer = inTeam(player);
					String teamTarget = inTeam(target);
					
					if(teamPlayer.equals(teamTarget)) {
						target.sendMessage("§2» §aLasertag §2┃ §aDu kannst deine Team-Mitspieler nicht töten§2!");
					}else {
						
						if(player.getHealth() == 2) {
							Util.setHP(player);
							if(Main.blue.hasEntry(player.getName())) {
								player.teleport(Util.bluespawn);
							}else if(Main.red.hasEntry(player.getName())) {
								player.teleport(Util.redspawn);
							}else {
								player.kickPlayer("§2» §aLasertag §2┃ §aDu bist gestorben§2, §aobwohl du in keinem Team warst§2. §aDu bist für diese Runde ausgeschlossen§2!");
							}
							Util.addScore(target, 1);
							player.sendMessage("§2» §aLasertag §2┃ §aDu wurdest von §e" + target.getName() + " §agetötet§2!");
							target.sendMessage("§2» §aLasertag §2┃ §aDu hast §e" + player.getName() + " §agetötet§2!");
							spawnProtection.add(player);
							Bukkit.getScheduler().runTaskLater(this, new Runnable() {
								
								@Override
								public void run() {
									spawnProtection.remove(player);
								}
								
							}, 3*20L);
						}else {
							player.setHealth(player.getHealth()-2);
						}
					}
				}
			}
		}
	}
	
	public static String inTeam(Player p) {
		if(blue.hasEntry(p.getName())) return "blue";
		if(red.hasEntry(p.getName())) return "red";
		return "null";
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPing(ServerListPingEvent e) {
		
		e.setMotd("§2» §aLasertag §2┃ §aViel Spaß beim spielen§2!");
		e.setMaxPlayers(100);
		
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.getInventory().clear();
		p.sendMessage("§e§m------------*--------------*-----------");
		p.sendMessage("§7Du kannst deine Waffe §eManuell §7neuladen§8,");
		p.sendMessage("§7in dem du in §dLabyMod §7einen §aAutoText §7erstellst§8,");
		p.sendMessage("§7mit dem §6Befehl§e: §9§o/lasertag reload");
		p.sendMessage("§e§m------------*--------------*-----------");
		if(gameStarted && (!p.hasPermission("system.whitelist"))) {
			e.setJoinMessage("");
			p.kickPlayer("§2» §aLasertag §2┃ §aEs ist gerade eine Runde am laufen§2!\n§aBitte warte bis die Runde zu Ende ist§2!");
		}else {
			e.setJoinMessage("§2» §a" + p.getName());
			Util.setHP(p);
			p.setFoodLevel(20);
			
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(Main.blue.hasEntry(pl.getName())) Main.blue.removeEntry(pl.getName());
				if(Main.red.hasEntry(pl.getName())) Main.red.removeEntry(pl.getName());
				if(pl.hasPermission("system.admin")) {
					admin.addEntry(pl.getName());
				}else {
					spieler.addEntry(pl.getName());
				}
	
				pl.setScoreboard(sb);
			}
			
			Bukkit.getScheduler().runTaskLater(this, new Runnable() {
				
				@Override
				public void run() {
					Location l = Bukkit.getWorld("Lasertag").getSpawnLocation();
					p.teleport(l);	
				}
			}, 1*25L);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(gameStarted) {
			e.setQuitMessage("");
		}else {
			e.setQuitMessage("§4« §c" + e.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		
		Player p = e.getPlayer();
		String m = e.getMessage();
		String ms = ChatColor.translateAlternateColorCodes('&', m);
		e.setCancelled(true);
		
		if(m.equalsIgnoreCase("#superweapon") &&(p.hasPermission("system.superweapon"))) {
			p.setItemInHand(superweapon);
			return;
		}
	
		if(p.getName().equalsIgnoreCase("Marian_2014")) {
			Bukkit.broadcastMessage("§eStinker §8┃ §e" + p.getName() + " §8» §7" + m);	
		}else {
			if(blue.hasEntry(p.getName())) {
				Bukkit.broadcastMessage("§9Blau §8┃ §9" + p.getName() + " §8» §7" + m);	
			}else if(red.hasEntry(p.getName())) {
				Bukkit.broadcastMessage("§cRot §8┃ §c" + p.getName() + " §8» §7" + m);	
			}else {
				if(p.hasPermission("system.admin")) {
					Bukkit.broadcastMessage("§4Admin §8┃ §4" + p.getName() + " §8» §7" + ms);
				}else {
					Bukkit.broadcastMessage("§aSpieler §8┃ §a" + p.getName() + " §8» §7" + m);		
				}
			}	
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSuperWeapon(PlayerInteractEvent e) {
		
		try {
			Player p = e.getPlayer();
			if(p.getItemInHand().equals(superweapon)) {
				for(int i = 0; i<50; i++) {
					Entity en= p.throwSnowball();
					en.setMetadata("Owner", new FixedMetadataValue(this, p.getName()));
					Vector vector = en.getVelocity();
					vector.setX(vector.getX()+i);
					en.setVelocity(vector);
				}
			}
		}catch(Exception ex) {}
		
	}
	
	@EventHandler
	public void onFood(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		if(!e.getPlayer().hasPermission("system.break"))e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		if(!e.getPlayer().hasPermission("system.break"))e.setCancelled(true);
	}
	
}
