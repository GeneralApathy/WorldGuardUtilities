package com.emilianomaccaferri.wgplts.events;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.connorlinfoot.titleapi.TitleAPI;
import com.emilianomaccaferri.wgplts.Main;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionRelatedEvents implements Listener {
	
	private ArrayList<String> players = new ArrayList<String>();
	public static HashMap<String, ProtectedRegion> actualRegions = new HashMap<String, ProtectedRegion>();
	private World wgWorld = WorldGuard.getInstance().getPlatform().getWorldByName("world"); // currently supporting one world
	
	public RegionRelatedEvents(Main p) {
		
	    for(String r: Main.protectedRegions) {
	    	
	    	actualRegions.put(r, WorldGuard.getInstance().getPlatform().getRegionContainer().get(this.wgWorld).getRegion(r));
	    	
	    }
		
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		 
		Player player = e.getPlayer();
		Vector v = new Vector(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ());
		
		actualRegions
			.entrySet()
			.stream()
			.filter(region -> WorldGuard.getInstance().getPlatform().getRegionContainer().getLoaded().get(1).getApplicableRegions(v).getRegions().contains(region.getValue()) == true)
			.forEach(item -> {
				
				if(!this.players.contains(player.getName())) {

					 TitleAPI.sendTitle(e.getPlayer(), 20, 60, 20, "&c&lZona di guerra","&eMena la gente e spacca i blocchi");
					 player.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "[ServerProtection] Sei uscito dalla regione `"+ item.getValue().getId() +"`");
					 players.add(e.getPlayer().getName());
					 return;
				 
				 }else if(this.players.contains(player.getName())){

						 players.remove(players.indexOf(player.getName()));
						 TitleAPI.sendTitle(e.getPlayer(), 20, 60, 20, "&a&lZona sicura","&eNon ci sono pericoli qui");
						 player.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "[ServerProtection] Sei entrato nella regione `"+ item.getValue().getId() +"`");
						 return;
						 
					 
				 }
				
			});
		
	} 

}