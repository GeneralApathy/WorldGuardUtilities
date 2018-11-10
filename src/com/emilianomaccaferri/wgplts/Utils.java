package com.emilianomaccaferri.wgplts;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public interface Utils {
 
	public static WorldGuardPlugin getWorldGuard() {
		
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
		if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin)))
			return null;

		return (WorldGuardPlugin) plugin;
		
	}

	public static WorldEditPlugin getWorldEdit() {
		
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		if ((plugin == null) || (!(plugin instanceof WorldEditPlugin)))
			return null;

		return (WorldEditPlugin) plugin;
		
	}
	
}