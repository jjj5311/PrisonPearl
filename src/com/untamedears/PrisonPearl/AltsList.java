package com.untamedears.PrisonPearl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

class AltsList implements Listener {
	private HashMap<UUID, List<UUID>> altsHash;
	private PrisonPearlPlugin plugin_;

	public AltsList(PrisonPearlPlugin plugin) {
		plugin_ = plugin;
		altsHash = new HashMap<UUID, List<UUID>>();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onAltsListUpdate(AltsListEvent event) {
			PrisonPearlPlugin
					.info("Grabbing alts for players");
		final List<UUID> altsList = event.getAltsList();
		// Save the old alt lists in their entirety to reduce all the cross
		// checking
		// players for existence within the Set.
		final Set<List<UUID>> banListsToCheck = new HashSet<List<UUID>>(
				altsList.size());
		final List<UUID> normalizedList = new ArrayList<UUID>(altsList.size());
		for (UUID playerUUID : altsList) {
			normalizedList.add(playerUUID);
			altsHash.put(playerUUID, normalizedList);
			banListsToCheck.add(altsHash.get(playerUUID));
		}
		// Unroll the ban lists into the playerBansToCheck. Only need a single
		// account from the banlist we just built to check it.
		final Set<UUID> playerBansToCheck = new HashSet<UUID>(
				banListsToCheck.size() * 10);
		playerBansToCheck.add(normalizedList.get(0));
		for (List<UUID> banList : banListsToCheck) {
			playerBansToCheck.addAll(banList);
		}
		// Check each player for bans, removing their alt list from the check
		// list
		// after they have been checked.
		int bannedCount = 0, unbannedCount = 0, total = 0, result;
		while (!playerBansToCheck.isEmpty()) {
			final UUID playerUUID = playerBansToCheck.iterator().next();
			final List<UUID> thisAltList = altsHash.get(playerUUID);
			if (thisAltList == null) {
				playerBansToCheck.remove(playerUUID);
				continue;
			}
			playerBansToCheck.removeAll(thisAltList);
			for (UUID altUUID : thisAltList) {
				result = plugin_.checkBan(altUUID);
				if (result == 2)
					bannedCount++;
				else if (result == 1)
					unbannedCount++;
				total++;
			}
		}
		PrisonPearlPlugin.info(bannedCount + " players were banned, " + unbannedCount + " were unbanned out of " + total + " accounts.");
	}

	public void queryForUpdatedAltLists(List<UUID> playersToCheck) {
		// Fires the RequestAltsListEvent event with the list of players to
		// check. This event won't contain results upon return. It is up to
		// the upstream event handler to fire the AltsListEvent synchronously
		// back to this class for each updated alts list to provide results.
		Bukkit.getServer()
				.getPluginManager()
				.callEvent(
						new RequestAltsListEvent(new ArrayList<UUID>(
								playersToCheck)));
	}

	public void cacheAltListFor(UUID playerUUID) {
		if (altsHash.containsKey(playerUUID)) {
			return;
		}
		List<UUID> singleton = new ArrayList<UUID>(1);
		singleton.add(playerUUID);
		Bukkit.getServer().getPluginManager()
				.callEvent(new RequestAltsListEvent(singleton));
	}

	public UUID[] getAltsArray(UUID uuid) {
		if (!altsHash.containsKey(uuid))
			return new UUID[0];
		List<UUID> uuids = altsHash.get(uuid);
		List<UUID> alts = new ArrayList<UUID>(uuids.size() - 1);
		for (UUID altUUID : uuids) {
			if (!altUUID.equals(uuid)) {
				alts.add(altUUID);
			}
		}
		return alts.toArray(new UUID[alts.size()]);
	}

	public Set<UUID> getAllNames() {
		return altsHash.keySet();
	}
}
