package com.untamedears.PrisonPearl;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class PPConfig {
	private final ConfigurationSection config;
	private int upkeep_resource;
	private int upkeep_quantity;
	
	public PPConfig(ConfigurationSection config) {
		this.config = config;
		this.upkeep_resource = config.getInt("upkeep.resource");
		this.upkeep_quantity = config.getInt("upkeep.quantity"); 
	}

	public ItemStack getUpkeepResource() {
		return new ItemStack(upkeep_resource, upkeep_quantity);
	}
}
