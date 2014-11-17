package com.untamedears.PrisonPearl;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PrisonCommandEvent extends Event{
	private String command; 
	private boolean cancelled;
	public PrisonCommandEvent(String command){
		this.command=command;
	}

	public boolean isCancelled(){
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled){
		this.cancelled = cancelled;
	}
	
	public String getCommand(){
		return command;
	}
	private static final HandlerList handlers = new HandlerList();
	public HandlerList getHandlers() {
	    return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
