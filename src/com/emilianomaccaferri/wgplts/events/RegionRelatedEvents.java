package com.emilianomaccaferri.wgplts.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.connorlinfoot.titleapi.TitleAPI;
import com.emilianomaccaferri.wgplts.GettableRegion;
import com.emilianomaccaferri.wgplts.Main;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.Vector2D;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionRelatedEvents implements Listener {
	
	private Main instance;
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<String> worlds = new ArrayList<String>();
	public static HashMap<String, GettableRegion> actualRegions = new HashMap<String, GettableRegion>();
	ScoreboardManager sb = Bukkit.getServer().getScoreboardManager();
	Scoreboard nullScore = sb.getNewScoreboard();
	//private World wgWorld = WorldGuard.getInstance().getPlatform().getWorldByName("world"); // currently supporting one world
	
	public RegionRelatedEvents(Main p) {

		this.instance = p;
		
		for(RegionManager rm: WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded()) {
			
			this.worlds.add(rm.getName());
			
		}
		
	    for(String r: Main.protectedRegions) {
	    	
	    	String regionName = r.split(":")[0];
	    	String regionWorld = r.split(":")[1];
	    	
	    	Bukkit.getLogger().info("Loading region " + ChatColor.GREEN + regionName + ChatColor.WHITE + " in world " + ChatColor.GREEN + regionWorld);
	    	World wgWorld = WorldGuard.getInstance().getPlatform().getWorldByName(regionWorld);
	    	try {
	    		
	    		ProtectedRegion re = WorldGuard.getInstance().getPlatform().getRegionContainer().get(wgWorld).getRegion(regionName);
	    		re.getId();
	    		actualRegions.put(regionWorld, new GettableRegion(regionName, re));
	    		
	    	}catch(Exception e) {
	    		
	    		Bukkit.getLogger().info("Region " + ChatColor.RED + regionName + ChatColor.WHITE + " in world " + ChatColor.GREEN + regionWorld + ChatColor.WHITE + " doesn't exist, so I didn't load it");
	    		
	    	}
	    	
	    }
		
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) { 
		
		Player player = e.getPlayer();
		Vector v = new Vector(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		int index = worlds.indexOf(e.getPlayer().getWorld().getName());
		
		Scoreboard board = sb.getNewScoreboard();
		Score s, n;
		
		Objective south = board.registerNewObjective("posizione", "test");
		south.setDisplayName(ChatColor.GREEN + "Posizione dal confine (in blocchi)");
		south.setDisplaySlot(DisplaySlot.SIDEBAR);
		s = south.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.YELLOW + "Bordo Sud:"));
		n = south.getScore(Bukkit.getServer().getOfflinePlayer(ChatColor.YELLOW + "Bordo Nord:"));
		
		Stream<Entry<String, GettableRegion>> stream = actualRegions
		.entrySet()
		.parallelStream()
		.filter(region -> WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().get(index).getApplicableRegions(v).getRegions().contains(region.getValue().getRegion()));
		
		Optional<Entry<String, GettableRegion>> a = stream.findFirst();
		
		if(a.isPresent()) {
			
			List<BlockVector2D> radius = a.get().getValue().getRegion().getPoints();
			Vector2D other = v.toVector2D();
			Vector2D ssouth = radius.get(0).subtract(other);
			Vector2D north = radius.get(1).subtract(other);
				player.setScoreboard(board);
				s.setScore(Math.abs(ssouth.getBlockX()));
				n.setScore(Math.abs(north.getBlockX()));
				
			if(!this.players.contains(player.getName())) {
			
				Bukkit.getLogger().info(player.getLocation().toString());
				TitleAPI.sendTitle(e.getPlayer(), 20, 60, 20, this.instance.getConfig().getString("safe-zone"), this.instance.getConfig().getString("safe-zone-subtitle"));
				player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "[ServerProtection] Sei entrato nella regione protetta "+ ChatColor.WHITE + ChatColor.BOLD + a.get().getValue().getRegionName());
				player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				this.players.add(player.getName());
				
			}
			
		}else {
			
			if(this.players.contains(player.getName())) {
				
				player.setScoreboard(nullScore);
				this.players.remove(players.indexOf(player.getName()));
				TitleAPI.sendTitle(e.getPlayer(), 20, 60, 20, this.instance.getConfig().getString("war-zone"), this.instance.getConfig().getString("war-zone-subtitle"));
				player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "[ServerProtection] Sei uscito dalla regione protetta");
				player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1, 1);
				
			}
			
		}
	
	} 

}