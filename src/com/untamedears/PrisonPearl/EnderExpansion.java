package com.untamedears.PrisonPearl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.untamedears.PrisonPearl.events.PrisonPearlEvent;

public class EnderExpansion {
	private PrisonPearlStorage pearls;
	
	public EnderExpansion (PrisonPearlStorage pearls){
		this.pearls= pearls;
	}
	public void updateEnderStoragePrison(PrisonPearl pearl, Cancellable event, Location loc){
		if (event.isCancelled()) return;
		updatePearl(pearl, loc);
	}
	private void updatePearl(
			PrisonPearl pp, Location loc) {
		pp.setHolder(loc);
		pearls.markDirty();
		Bukkit.getPluginManager().callEvent(
				new PrisonPearlEvent(pp, PrisonPearlEvent.Type.HELD));
	}
}
