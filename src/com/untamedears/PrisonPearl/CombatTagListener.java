package com.untamedears.PrisonPearl;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.topcat.npclib.entity.NPC;
import com.trc202.CombatTagEvents.NpcDespawnEvent;
import com.trc202.CombatTagEvents.NpcDespawnReason;

class CombatTagListener implements Listener {
    final PrisonPearlManager pearlman_;

    public CombatTagListener(final PrisonPearlPlugin plugin,
            final PrisonPearlManager pearlman) {
        this.pearlman_ = pearlman;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onNpcDespawn(NpcDespawnEvent event) {
        if (event.getReason() != NpcDespawnReason.DESPAWN_TIMEOUT) {
            return;
        }
        UUID plruuid = event.getPlayerUUID();
        NPC npc = event.getNpc();
        Location loc = npc.getBukkitEntity().getLocation();

        pearlman_.handleNpcDespawn(plruuid, loc);
    }
}
