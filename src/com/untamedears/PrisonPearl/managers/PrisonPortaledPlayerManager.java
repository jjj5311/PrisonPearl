package com.untamedears.PrisonPearl.managers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.untamedears.PrisonPearl.PrisonPearlPlugin;
import com.untamedears.PrisonPearl.PrisonPearlStorage;
import com.untamedears.PrisonPearl.SaveLoad;
import com.untamedears.PrisonPearl.database.PrisonPearlMysqlStorage;
import com.untamedears.PrisonPearl.events.PrisonPearlEvent;

public class PrisonPortaledPlayerManager implements Listener, SaveLoad {
	private final PrisonPearlPlugin plugin;
	private final PrisonPearlStorage pearls;
	private PrisonPearlMysqlStorage mysqlStorage;
	private boolean isMysql;
	
	private final Set<UUID> portaled_players;
	private boolean dirty;
	
	public PrisonPortaledPlayerManager(PrisonPearlPlugin plugin, PrisonPearlStorage pearls) {
		this.plugin = plugin;
		this.pearls = pearls;
		isMysql = plugin.getPPConfig().getMysqlEnabled();
		portaled_players = new HashSet<UUID>();
		mysqlStorage = plugin.getMysqlStorage();
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public boolean isDirty() {
		return dirty;
	}
	
	public void load(File file) throws NumberFormatException, IOException {
		if (isMysql){
			loadMysql();
			return;
		}
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String id;
		while ((id = br.readLine()) != null) {
			portaled_players.add(UUID.fromString(id));
		}
		
		fis.close();
		dirty = false;
	}
	
	public void save(File file) throws IOException {
		if (isMysql){
			saveMysql();
			return;
		}
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fos));
		
		for (UUID id: portaled_players) {
			br.append(id.toString()).append("\n");
		}
		
		br.flush();
		fos.close();
		dirty = false;
	}
	
    public boolean isPlayerPortaledToPrison(Player player) {
		return isPlayerPortaledToPrison(player.getUniqueId());
	}
	
    public boolean isPlayerPortaledToPrison(UUID playerid) {
		return portaled_players.contains(playerid);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (pearls.isImprisoned(player.getUniqueId()))
			return;
		
		if (event.getRespawnLocation().getWorld() != getPrisonWorld()) {
			portaled_players.remove(player.getUniqueId());
			if (isMysql)
				mysqlStorage.removePortaledPlayer(player.getUniqueId());
			dirty = true;
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerPortalEvent(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		if (pearls.isImprisoned(player.getUniqueId()))
			return;
		Location toLoc = event.getTo();
		if (toLoc == null) {
			return;
		}
		if (toLoc.getWorld() == getPrisonWorld())
			portaled_players.add(player.getUniqueId());
		else{
			portaled_players.remove(player.getUniqueId());
			if (isMysql)
				mysqlStorage.removePortaledPlayer(player.getUniqueId());
		}
		dirty = true;
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPrisonPearlEvent(PrisonPearlEvent event) {
		if (event.getType() == PrisonPearlEvent.Type.NEW) {
			UUID uuid = event.getPrisonPearl().getImprisonedId();
			portaled_players.remove(uuid);
			if (isMysql)
				mysqlStorage.removePortaledPlayer(uuid);
			dirty = true;
		}
	}
	
	private World getPrisonWorld() {
		return Bukkit.getWorld(plugin.getConfig().getString("prison_world"));
	}
	
	public void loadMysql(){
    	portaled_players.addAll(mysqlStorage.getAllPortaledPlayers());
    }
    
    public void saveMysql(){
    	for (UUID uuid: portaled_players)
    		mysqlStorage.addPortaledPlayer(uuid);
    }
}
