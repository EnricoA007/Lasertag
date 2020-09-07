package net.magicgames.lasertag;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.NameTagVisibility;

public class Util {
	
	public static Location redspawn = new Location(Bukkit.getWorld("Lasertag"), -11,4,-59);
	public static Location bluespawn = new Location(Bukkit.getWorld("Lasertag"), -69,4,-59);
	
	public static Location randomTP() {
		Random r = new Random();
		World w = Bukkit.getWorld("Lasertag");
		
		Location l1 = new Location(w, 36,12,-8);
		Location l2 = new Location(w, 30,4,-8);
		Location l3 = new Location(w, 30,4,-33);
		Location l4 = new Location(w, 42,4,-18);
		Location l5 = new Location(w, 62,4,-5);
		Location l6 = new Location(w, 60,6,-17);
		Location l7 = new Location(w, 41,21,-64);
		Location l8 = new Location(w, 60,6,-69);
		Location l9 = new Location(w, 59,7,-27);	
		Location l10 = new Location(w, 27,1,-23);	
		
		Location[] l = new Location[10];
		l[0] = l1; l[1] = l2; l[2] = l3; l[3] = l4; l[4] = l5; l[5] = l6;l[6] = l7;l[7] = l8;l[8] = l9;l[9] = l10;
		return l[r.nextInt(l.length)];
		
	}
	
	public static void setHP(Player p) {
		p.setMaxHealth(4);
		p.setHealth(p.getMaxHealth());
	}
	
	public static void createSB() {
		Main.sb=null;
		Main.sb= Bukkit.getScoreboardManager().getNewScoreboard();
		Main.admin =Main.sb.registerNewTeam("01Admin");
		Main.spieler = Main.sb.registerNewTeam("03Spieler");
		Main.admin.setPrefix("§4Admin §8┃ §4");
		Main.spieler.setPrefix("§aSpieler §8┃ §a");
		
		Main.blue = Main.sb.registerNewTeam("02Blue");
		Main.blue.setPrefix("§9Blau §8┃ §9");
		Main.red = Main.sb.registerNewTeam("02Red");
		Main.red.setPrefix("§cRot §8┃ §c");
		Main.red.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);		
		Main.blue.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);	
		
		Main.o = Main.sb.registerNewObjective("Scoreboard", "dummy");
		Main.o.setDisplaySlot(DisplaySlot.SIDEBAR);
		Main.o.setDisplayName("§2» §aLasertag §2┃ §a00:00");
		Main.o.getScore("§2×» §aPunktestände §2➦").setScore(9999);
	}
	
	public static boolean hasScore(Player p) {
		
		return Main.o.getScore("§c"+p.getName()).isScoreSet();
		
	}
	
	public static int getScore(Player p) {
		
		if(hasScore(p)) {
			return Main.o.getScore("§c"+p.getName()).getScore();
		}else {
			return -1;
		}
		
	}
	
	public static void setScore(Player p, int i) {
		Main.o.getScore("§c"+p.getName()).setScore(i);
	}
	
	public static void addScore(Player p, int i) {
		int a = i;
		if(hasScore(p)) {
			a = a + getScore(p);
		}
		setScore(p, a);
	}

	public static void updateSB() {
		for(Player pl : Bukkit.getOnlinePlayers()) {
			if(pl.hasPermission("system.admin")) {
				Main.admin.addEntry(pl.getName());
			}else {
				Main.spieler.addEntry(pl.getName());
			}
			pl.setScoreboard(Main.sb);
		}
	}
	
	public static int schedulersign;
	public static Timestamp currentTime = new Timestamp(3,0);
	
	public static void startScheduler() {
		
		schedulersign = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.pl, new Runnable() {
			
			@Override
			public void run() {
			
				currentTime.calculate();
				String time = currentTime.toString();
				
				Main.o.setDisplayName("§2» §aLasertag §2┃ §a" + time);
				
				if(currentTime.isFinished()) {
					Util.stop();
				}
				
			}
			
		}, 1*20L, 1*20L);
		
	}
	
	public static void stop() {
		Main.gameStarted = false;
		Bukkit.getScheduler().cancelTask(schedulersign);
		schedulersign = -1;
		currentTime = new Timestamp(3, 0);
		Location l = Bukkit.getWorld("Lasertag").getSpawnLocation();
		int red = 0;
		int blue = 0;
		for(Player pl: Bukkit.getOnlinePlayers()) {
			pl.teleport(l);
			pl.getInventory().clear();
			int i = Util.getScore(pl);
			if(i == -1) i= 0;
			pl.sendMessage("§2» §aLasertag §2┃ §aDein Punktestand ist§2: §a"+ i);
			if(Main.blue.hasEntry(pl.getName())) {
				Main.blue.removeEntry(pl.getName());
				if(Util.hasScore(pl)) blue = blue + Util.getScore(pl);
			}
			if(Main.red.hasEntry(pl.getName())) {
				Main.red.removeEntry(pl.getName());
				if(Util.hasScore(pl)) red = red + Util.getScore(pl);
			}
			
			if(pl.hasPermission("system.admin")) {
				Main.admin.addEntry(pl.getName());
			}else {
				Main.spieler.addEntry(pl.getName());
			}
		}
		
		Bukkit.broadcastMessage("§2» §aLasertag §2┃ §cTeam Rot §ahat §c" + red + " §aPunkte erreicht§2! §9Team Blau §ahat §9" + blue + " §aPunkte erreicht§2!");
		
		if(red == blue) {
			Bukkit.broadcastMessage("§2» §aLasertag §2┃ §aKein Team hat Gewonnen§2! §aEs ist Unentschieden§2!");
		}else {
			if(red > blue) {
				Bukkit.broadcastMessage("§2» §aLasertag §2┃ §aGlückwunsch §cTeam Rot §ahat gewonnen§2!");
			}else if(blue > red) {
				Bukkit.broadcastMessage("§2» §aLasertag §2┃ §aGlückwunsch §9Team Blau §ahat gewonnen§2!");									
			}else {
				Bukkit.broadcastMessage("§2» §aLasertag §2┃ §cKeine Auswertung konnte erfolgen§4!");
			}
		}
		
		Util.createSB();
		Util.updateSB();
	}
	
}
