package net.magicgames.lasertag;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;

public class CMD_Lasertag implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command c, String arg2, String[] args) {
		
		if(s instanceof Player) {
			Player p = (Player) s;
			
			if(p.hasPermission("system.lasertag")) {
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("stop")) {
						if(Main.gameStarted) {
							Util.stop();
							p.sendMessage("§2» §aLasertag §2┃ §aLasertag wurde beendet§2!");
							for(Player pl : Bukkit.getOnlinePlayers()) {
								Util.setHP(pl);
							}
						}else {
							p.sendMessage("§2» §aLasertag §2┃ §aLasertag wurde bereits zurückgesetzt§2!");
						}
					}else if(args[0].equalsIgnoreCase("start")) {
						if(Main.gameStarted) {
							p.sendMessage("§2» §aLasertag §2┃ §aLasertag wurde bereits gestartet§2!");
						}else {
							Util.startScheduler();
							Main.gameStarted = true;
							for(Player pl : Bukkit.getOnlinePlayers()) {
								Util.setHP(pl);
							}
							p.sendMessage("§2» §aLasertag §2┃ §aLasertag wird gestartet§2!");
							int online = Bukkit.getOnlinePlayers().size();
							int p1 = online / 2;
							for(Player pl : Bukkit.getOnlinePlayers()) {
								((CraftPlayer)pl).getHandle().playerConnection.sendPacket(new PacketPlayOutHeldItemSlot(0));
								pl.setItemInHand(Main.defweapon);
								if(Main.spieler.hasEntry(pl.getName())) Main.spieler.removeEntry(pl.getName());
								if(Main.admin.hasEntry(pl.getName())) Main.admin.removeEntry(pl.getName());
							}
							
							int amount =0;
							boolean change = false;
							
							for(Player pl : Bukkit.getOnlinePlayers()) {
								
								if(amount == p1) change = true;
								amount++;
								
								if(change) {
									Main.blue.addEntry(pl.getName());
									pl.teleport(Util.bluespawn);
									changeColor(pl,"BLUE");
								}else {
									Main.red.addEntry(pl.getName());
									changeColor(pl,"RED");
									pl.teleport(Util.redspawn);
								}
								
							}
							
							String blues = "";
							String reds = "";
							
							for(String t : Main.blue.getEntries()) {
								blues = blues + t + ", ";
							}
							if(!(blues == "")) blues = blues.substring(0,blues.length()-2);
						
							for(String t : Main.red.getEntries()) {
								reds = reds + t + ", ";
							}
							if(!(reds == "")) reds = reds.substring(0,reds.length()-2);
							
							if(reds == "") reds = "Keiner";
							if(blues == "") blues = "Keiner";
							
							for(Player pl : Bukkit.getOnlinePlayers()) {
								pl.sendMessage("§2» §aLasertag §2┃ §aSpieler in §9Team Blau §2» §9" + blues);
								pl.sendMessage("§2» §aLasertag §2┃ §aSpieler in §cTeam Rot §2» §c" +reds);
							}
						}
					}else if(args[0].equalsIgnoreCase("reload")) {
						try {
							
							ItemStack hand = p.getItemInHand();
							
							if(hand.getType() == Material.DIAMOND_HOE) {
								p.sendMessage("§2» §aLasertag §2┃ §aBitte warte bis deine Waffe nachgeladen ist§2!");
								Bukkit.getScheduler().runTaskLater(Main.pl,new Runnable() {
									
									@Override
									public void run() {
									
										if(hand.getType() == Material.DIAMOND_HOE) {
											p.setItemInHand(Main.defweapon);
											p.sendMessage("§2» §aLasertag §2┃ §aDeine Waffe wurde nachgeladen§2!");
										}else {
											p.sendMessage("§2» §aLasertag §2┃ §aDu hast keine Waffe in der Hand§2!");
										}
										
									}
									
								}, 2*20L);
								
							}else {
								p.sendMessage("§2» §aLasertag §2┃ §aDu hast keine Waffe in der Hand§2!");
							}
							
						}catch(Exception ex) {
							p.sendMessage("§2» §aLasertag §2┃ §aDu hast keine Waffe in der Hand§2!");
						}
					}else {
						p.sendMessage("§2» §aLasertag §2┃ §a/§2lasertag " + args[0] + " §a- §2ist kein gültiges Argument§a!");						
					}
				}else {
					p.sendMessage("§2» §aLasertag §2┃ §a/lasertag stop");
					p.sendMessage("§2» §aLasertag §2┃ §a/lasertag start");
				}
				
			}else {
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("reload")) {
						try {
							
							ItemStack hand = p.getItemInHand();
							
							if(hand.getType() == Material.DIAMOND_HOE) {
								p.sendMessage("§2» §aLasertag §2┃ §aBitte warte bis deine Waffe nachgeladen ist§2!");
								Bukkit.getScheduler().runTaskLater(Main.pl,new Runnable() {
									
									@Override
									public void run() {
									
										if(hand.getType() == Material.DIAMOND_HOE) {
											p.setItemInHand(Main.defweapon);
											p.sendMessage("§2» §aLasertag §2┃ §aDeine Waffe wurde nachgeladen§2!");
										}else {
											p.sendMessage("§2» §aLasertag §2┃ §aDu hast keine Waffe in der Hand§2!");
										}
										
									}
									
								}, 2*20L);
								
							}else {
								p.sendMessage("§2» §aLasertag §2┃ §aDu hast keine Waffe in der Hand§2!");
							}
							
						}catch(Exception ex) {
							p.sendMessage("§2» §aLasertag §2┃ §aDu hast keine Waffe in der Hand§2!");
						}
					}else {
						p.sendMessage("§2» §aLasertag §2┃ §aKeine Berechtigung§2!");
					}
				}else {
					p.sendMessage("§2» §aLasertag §2┃ §aKeine Berechtigung§2!");
				}
			}
			
		}
		
		return true;
	}
	
	private ItemStack setColor(Material m, Color c) {
		ItemStack i = new ItemStack(m);
		LeatherArmorMeta me =(LeatherArmorMeta) i.getItemMeta();
		me.setColor(c);
		i.setItemMeta(me);
		return i;
	}

	private void changeColor(Player pl, String c) {
		
		if(c.equals("RED")) {
			pl.getInventory().setHelmet(setColor(Material.LEATHER_HELMET, Color.RED));
			pl.getInventory().setChestplate(setColor(Material.LEATHER_CHESTPLATE, Color.RED));
			pl.getInventory().setLeggings(setColor(Material.LEATHER_LEGGINGS, Color.RED));
			pl.getInventory().setBoots(setColor(Material.LEATHER_BOOTS, Color.RED));
		}else if(c.equals("BLUE")) {
			pl.getInventory().setHelmet(setColor(Material.LEATHER_HELMET, Color.BLUE));
			pl.getInventory().setChestplate(setColor(Material.LEATHER_CHESTPLATE, Color.BLUE));
			pl.getInventory().setLeggings(setColor(Material.LEATHER_LEGGINGS, Color.BLUE));
			pl.getInventory().setBoots(setColor(Material.LEATHER_BOOTS, Color.BLUE));
		}
		
	}

}
