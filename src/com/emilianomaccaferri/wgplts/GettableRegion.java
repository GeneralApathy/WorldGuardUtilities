package com.emilianomaccaferri.wgplts;

import org.bukkit.Bukkit;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class GettableRegion {
	
	private String regionName = null;
	private ProtectedRegion region = null;
	
	public GettableRegion(String regionName, ProtectedRegion region) {
		
		Bukkit.getLogger().info("Creating " + regionName);
		Bukkit.getLogger().info("\t@ region " + region.toString());
		this.regionName = regionName;
		this.region = region;
		
	}
	
	public ProtectedRegion getRegion() {
		return this.region;
	}

	public String getRegionName() {
		return this.regionName;		
	}
	
}
