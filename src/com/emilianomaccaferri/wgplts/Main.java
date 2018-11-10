package com.emilianomaccaferri.wgplts;

import com.emilianomaccaferri.wgplts.events.RegionRelatedEvents;
import java.util.List;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
  
	public Economy eco = null;
	public static List<String> protectedRegions = null;
  
	public void onEnable(){
    
		saveDefaultConfig();
		if (!setupEconomy()){
			getLogger().severe(String.format("[%s] - Non c'è Vault quindi mi disabilito!", new Object[] {
					getDescription().getName() }));
      
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getCommand("compra").setExecutor(new PlotCommands(this));
    	getCommand("appartamenti").setExecutor(new PlotCommands(this));
    	Bukkit.getLogger().info("Loading regions from config.yml");
    	protectedRegions = getConfig().getStringList("protected-regions");
    	getServer().getPluginManager().registerEvents(new RegionRelatedEvents(this), this);
    	Bukkit.getLogger().info("Loaded " + protectedRegions.toString());
    	getLogger().info("WorldGuard Plots v1.0 started!");
	}
  
  private boolean setupEconomy(){
    
	  if (getServer().getPluginManager().getPlugin("Vault") == null) {
		  return false;
	  }
	  RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
	  if (rsp == null) {
		  return false;
	  }
	  this.eco = ((Economy)rsp.getProvider());
	  return this.eco != null;
  }
}
