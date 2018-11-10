package com.emilianomaccaferri.wgplts;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.emilianomaccaferri.wgplts.events.RegionRelatedEvents;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class RegionRelatedCommands implements CommandExecutor {
	
	Main instance = null;
	
	public RegionRelatedCommands(Main plugin) {
		
		this.instance = plugin;
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


		if (!(sender instanceof Player)){
			
	      sender.sendMessage(ChatColor.WHITE + "[Server Protection] " + ChatColor.RED + " Eh no, devi essere un player");
	      return true;
	    
		}
		
		Player player = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("createprotectedregion")) {
			
			if(args.length == 0) {
				
				player.sendMessage(
					
						ChatColor.WHITE + "[Server Protection] /cpr" + ChatColor.AQUA + " <nome regione>"
						
				);
				
				World w = WorldGuard.getInstance().getPlatform().getWorldByName(player.getWorld().getName());
				Region sel;
				try {
					sel = Utils.getWorldEdit().getSession(player).getSelection(w);
					boolean result = createProtectedRegion(w, sel, player, args[0]);
					
					if(result) {
						
						player.sendMessage(ChatColor.GREEN + "[Server Protection] Regione creata (anche su WorldGuard)");
						return true;
						
					}else{
						
						player.sendMessage(ChatColor.RED + "[Server Protection] C'è stato un problemino...");
					}

				} catch (IncompleteRegionException e) {
					
					e.printStackTrace();
					return false;
					
				}				
				
			}
			
		}
		
		return true;
			
	}
	
	
	public boolean createProtectedRegion(World w, Region sel, Player player, String name) {
		
		if(sel == null)			
			return false;
		
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(
	            name, 
	            new BlockVector(sel.getMinimumPoint()), 
	            new BlockVector(sel.getMaximumPoint())
	    );
		
		this.instance.getConfig().set(name, this.instance.getConfig().getStringList("protected-regions"));
		WorldGuard.getInstance().getPlatform().getRegionContainer().get(w).addRegion(region);
		RegionRelatedEvents.actualRegions.put(name, WorldGuard.getInstance().getPlatform().getRegionContainer().get(w).getRegion(name));
		return true;
		
	}

}
