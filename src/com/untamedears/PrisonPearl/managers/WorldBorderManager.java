package com.untamedears.PrisonPearl.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import com.untamedears.PrisonPearl.PrisonPearlPlugin;
import com.untamedears.PrisonPearl.SaveLoad;
import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.Config;

public class WorldBorderManager implements SaveLoad{
	private final PrisonPearlPlugin plugin;
	private boolean isWorldBorder;
	private boolean dirty;
	private List<Location> WhiteListedLocations;
	
	public WorldBorderManager(PrisonPearlPlugin plugin){
		this.plugin = plugin;
		this.isWorldBorder=plugin.isWorldBorder();
	}
	
    public boolean isMaxFeed(Location loc){
    	if(!plugin.getPPConfig().getFreeOutsideWorldBorder() || !isWorldBorder || isOnWhiteList(loc)) {
    		return false;
    	}
    	World world = loc.getWorld();
    	BorderData border = Config.Border(world.getName());
    	return !border.insideBorder(loc);
    }
    
    public boolean isOnWhiteList(Location loc){
    	for(Location whLoc : WhiteListedLocations){
    		if(whLoc.equals(loc)){
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean isDirty() {
		return dirty;
	}
    
	@Override
	public void save(File file) throws IOException {
		FileOutputStream  fos = new FileOutputStream (file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for(Location loc : WhiteListedLocations){
			bw.append(loc.getX()+","+loc.getY()+","+loc.getZ());
			bw.append("\n");
		}
		bw.flush();
		bw.close();	
		dirty = false;
	}
	@Override
	public void load(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		if (file.exists()){
			if(br.readLine() == null){
				plugin.getLogger().log(Level.INFO, "WhiteListedCords file is empty");;
				br.close();
				return;
			}
			String line;
			while ((line = br.readLine()) != null) {
				plugin.getLogger().log(Level.INFO, ("Reading curLine: " + line));
				String parts[] = line.split(",");
				if(parts.length==3){
					try{
						Double x = stringToDouble(parts[0]);
						Double y = stringToDouble(parts[1]);
						Double z = stringToDouble(parts[2]);
						Location loc = new Location(plugin.getFreeWorld(), x, y, z);
						WhiteListedLocations.add(loc);
						plugin.getLogger().log(Level.INFO, ("Adding location=" +loc));
					}catch(NumberFormatException e){
						plugin.getLogger().log(Level.WARNING, "Current line is not in the right format");
						e.printStackTrace();
					}
				}else{
					plugin.getLogger().log(Level.WARNING, "Current line is not in the right format");
				}
			}
			br.close();
			fis.close();
		}
		dirty = false;
	}
	
    private Double stringToDouble(String str) throws NumberFormatException{
    	return Double.parseDouble(str);
    }
    
    public boolean addWhitelistedLocation(Location loc){
    	WhiteListedLocations.add(loc);
    	dirty=true;
    	return true;
    }
    public boolean removeWhitelistedLocation(Location loc){
    	for(Location whLoc : WhiteListedLocations){
    		if(whLoc.equals(loc)){
    			WhiteListedLocations.remove(whLoc);
    			dirty=true;
    			return true;
    		}
    	}
    	return false;
    }
    public boolean addWhitelistedLocation(String x, String y, String z){
    	try{
    		Double xd = stringToDouble(x);
			Double yd = stringToDouble(y);
			Double zd = stringToDouble(z);
    		WhiteListedLocations.add(new Location(plugin.getFreeWorld(), xd , yd, zd));
        	dirty=true;
        	return true;
    	}catch(NumberFormatException e){
    		return false;
    	}

    }
    
    public boolean removeWhitelistedLocation(String x, String y, String z){
    	try{
    		Double xd = stringToDouble(x);
			Double yd = stringToDouble(y);
			Double zd = stringToDouble(z);
    		if(removeWhitelistedLocation(new Location(plugin.getFreeWorld(), xd , yd, zd))){
    			return false;
    		}
        	return true;
    	}catch(NumberFormatException e){
    		return false;
    	}

    }
	
}
