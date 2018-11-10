package com.emilianomaccaferri.wgplts;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.RegionGroup;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlotCommands implements CommandExecutor {
  
	private Main instance;
  
	public PlotCommands(Main instance){
	  
		this.instance = instance;
    
	}
  
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if (!(sender instanceof Player)){
			sender.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.RED + " Eh no, devi essere un player");
			return true;
		}
		Player player = (Player)sender;
		World w;
		if (cmd.getName().equalsIgnoreCase("appartamenti")){
			if (args.length < 1){
				player.sendMessage(
						ChatColor.WHITE + "/appartamenti" + ChatColor.AQUA + " list -" + ChatColor.WHITE + " Lista di appartamenti\n" + 
						ChatColor.WHITE + "/appartamenti" + ChatColor.AQUA + " create <nome_appartamento> <prezzo> -" + ChatColor.WHITE + " Crea un appartamento in base a una selezione\n");
        
				return true;
			}
			
			if (args[0].equalsIgnoreCase("create")){
				if (args.length < 3) {
					player.sendMessage(
							ChatColor.WHITE + "Utilizzo: /appartamenti" + ChatColor.AQUA + " create <nome_appartamento> <prezzo> -" + ChatColor.WHITE + " Crea un appartamento in base a una selezione\n");
				}
				if (!player.hasPermission("wgplots.create")){
					player.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.RED + ChatColor.BOLD + "Non hai il permesso di eseguire questo comando");
					return true;
				}
				w = WorldGuard.getInstance().getPlatform().getWorldByName("world");
				try{
					Region sel = Utils.getWorldEdit().getSession(player).getSelection(w);
					ProtectedCuboidRegion region = new ProtectedCuboidRegion(
							args[1], 
							new BlockVector(sel.getMinimumPoint()), 
							new BlockVector(sel.getMaximumPoint())
					);
					

					World wgWorld = WorldGuard.getInstance().getPlatform().getWorldByName("world");
          
					WorldGuard.getInstance().getPlatform().getRegionContainer().get(wgWorld).addRegion(region);
          
					ProtectedRegion reg = WorldGuard.getInstance().getPlatform().getRegionContainer().get(wgWorld).getRegion(args[1]);
          
					this.instance.getConfig().set("apartaments." + args[1] + ".price", args[2]);
          
					reg.setPriority(5);
          
					reg.setFlag(Flags.BUILD, StateFlag.State.DENY);
					reg.setFlag(Flags.BUILD.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
					reg.setFlag(Flags.CHEST_ACCESS, StateFlag.State.DENY);
					reg.setFlag(Flags.CHEST_ACCESS.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
					reg.setFlag(Flags.ENTRY, StateFlag.State.DENY);
					reg.setFlag(Flags.ENTRY.getRegionGroupFlag(), RegionGroup.NON_MEMBERS);
					reg.setFlag(Flags.BLOCK_BREAK, StateFlag.State.ALLOW);
					reg.setFlag(Flags.BLOCK_BREAK.getRegionGroupFlag(), RegionGroup.MEMBERS);
          
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "wg reload");
					player.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.GREEN + ChatColor.BOLD + "L'appartamento " + ChatColor.WHITE + ChatColor.BOLD + args[1] + ChatColor.GREEN + ChatColor.BOLD + " �� stato creato!");
          
					this.instance.saveConfig();
				}catch (IncompleteRegionException e){
					player.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.RED + ChatColor.BOLD + "Regione non completa...");
					return true;
				}
			}
		}	
		if (cmd.getName().equalsIgnoreCase("compra")){
			if (args.length < 2){
				player.sendMessage(
						ChatColor.WHITE + "Utilizzo: /compra" + ChatColor.AQUA + " appartamento <id appartamento> - " + ChatColor.WHITE + "Compra un appartamento");
				return true;
			}
			switch (args[0].toLowerCase()){
			
			case "appartamento": 
				String apartament = args[1];
				if (!ProtectedRegion.isValidId(apartament)){
					player.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.RED + ChatColor.BOLD + "L'appartamento " + apartament + " non esiste");
					return true;
				}
				LocalPlayer localPlayer = Utils.getWorldGuard().wrapPlayer(player);
				World wgWorld = WorldGuard.getInstance().getPlatform().getWorldByName("world");
				ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(wgWorld).getRegion(apartament);
				if (region.isOwner(localPlayer)){
					player.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.YELLOW + ChatColor.BOLD + "Sei già il proprietario di questo appartamento");
					return true;
				}
				
				EconomyResponse r = this.instance.eco.withdrawPlayer(player.getName(), Double.parseDouble(this.instance.getConfig().getString("apartaments." + apartament + ".price")));
				if (!r.transactionSuccess()){
					player.sendMessage(
            
							ChatColor.WHITE + "[Appartamenti] " + ChatColor.RED + ChatColor.BOLD + "Non hai abbastanza soldi.");
            
					return true;
				}
				DefaultDomain owners = new DefaultDomain();
				owners.addPlayer(Utils.getWorldGuard().wrapPlayer(player));
				region.setOwners(owners);
          
				player.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.GREEN + ChatColor.BOLD + "Appartamento comprato con successo!");
          
				player.sendMessage(ChatColor.WHITE + "[Appartamenti] " + ChatColor.YELLOW + ChatColor.BOLD + "Ora potrai modificare il lotto come ti pare");
				break;
			}
		}
		return true;
	}
}
