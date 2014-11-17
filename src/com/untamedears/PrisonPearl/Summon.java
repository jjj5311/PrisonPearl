package com.untamedears.PrisonPearl;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Summon {
	private final UUID summonedId;
	private final Location returnloc;
	private int alloweddistance;
    private int damageamount;
    private boolean canSpeak;
    private boolean canDealDamage;
    private boolean canBreakBlocks;

    public Summon(UUID summonedId, Location returnloc, int alloweddistance, int damageamount, boolean canSpeak, boolean canDealDamage, boolean canBreakBlocks) {
		this.summonedId = summonedId;
		this.returnloc = returnloc;
		this.alloweddistance = alloweddistance;
        this.damageamount = damageamount;
        this.canSpeak = canSpeak;
        this.canDealDamage = canDealDamage;
        this.canBreakBlocks = canBreakBlocks;
    }
	
	public UUID getSummonedId() {
		return summonedId;
	}
	
	public Player getSummonedPlayer() {
		return Bukkit.getPlayer(summonedId);
	}
	
	public Location getReturnLocation() {
		return returnloc;
	}
	
	public int getAllowedDistance() {
		return alloweddistance;
	}
	
	public void setAllowedDistance(int alloweddistance) {
		this.alloweddistance = alloweddistance;
	}

    public int getDamageAmount() {
        return damageamount;
    }

    public void setDamageAmount(int damageamount) {
        this.damageamount = damageamount;
    }

    public boolean isCanSpeak() {
        return canSpeak;
    }

    public void setCanSpeak(boolean canSpeak) {
        this.canSpeak = canSpeak;
    }

    public boolean isCanDealDamage() {
        return canDealDamage;
    }

    public void setCanDealDamage(boolean canDealDamage) {
        this.canDealDamage = canDealDamage;
    }

    public boolean isCanBreakBlocks() {
        return canBreakBlocks;
    }

    public void setCanBreakBlocks(boolean canBreakBlocks) {
        this.canBreakBlocks = canBreakBlocks;
    }
}
