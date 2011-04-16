package com.elmakers.mine.bukkit.plugins.wandmin.dao;

import java.util.ArrayList;
import java.util.List;

import com.elmakers.mine.bukkit.persisted.PersistClass;
import com.elmakers.mine.bukkit.persisted.PersistField;
import com.elmakers.mine.bukkit.persisted.Persisted;
import com.elmakers.mine.bukkit.persistence.dao.PlayerData;

@PersistClass(schema="wands", name="player")
public class PlayerWands extends Persisted
{
	private PlayerData			player;
	private List<Wand>			wands;
	
	private Wand				currentWand;
	
	public PlayerWands()
	{
		
	}
	
	public PlayerWands(PlayerData player)
	{
		this.player = player;
		this.wands = new ArrayList<Wand>();
		this.currentWand = null;
	}
	
	@PersistField(id=true)
	public void setPlayer(PlayerData player)
	{
		this.player = player;
	}
	
	public PlayerData getPlayer()
	{
		return player;
	}
	
	@PersistField
	public List<Wand> getWands()
	{
		return wands;
	}
	
	public void setWands(List<Wand> wands)
	{
		this.wands = wands;

		this.currentWand = null;
		if (wands != null && wands.size() > 0)
		{
			this.currentWand = wands.get(0);
		}
	}
	
	public void copyTo(PlayerWands other)
	{
		other.clear();
		if (wands == null)
		{
			return;
		}
		
		for (Wand wand : wands)
		{
			Wand newWand = other.addWand(wand.getName());
			wand.copyTo(newWand);
		}
		if (currentWand != null)
		{
			other.selectWand(currentWand.getName());
		}
	}
	
	public void clear()
	{
		currentWand = null;
		if (wands != null)
		{
			wands.clear();
		}
	}
	
	public boolean isEmpty()
	{
		return wands == null || wands.isEmpty();
	}
	
	public Wand getCurrentWand()
	{
		return currentWand;
	}
	
	public Wand addWand(String name)
	{
		if (wands == null)
		{
			wands = new ArrayList<Wand>();
		}
		Wand newWand = new Wand();
		newWand.setName(name);
		wands.add(newWand);
		currentWand = newWand;
		
		persistence.put(this);
		persistence.put(newWand);
		
		return newWand;
	}
	
	public boolean removeWand(String name)
	{
		if (wands == null) return false;
		
		Wand foundWand = null;
		for (Wand lookWand : wands)
		{
			if (lookWand.getName().equalsIgnoreCase(name))
			{
				foundWand = lookWand;
				wands.remove(foundWand);
				persistence.put(this);
				persistence.remove(foundWand);
				break;
			}
		}
		if (currentWand == foundWand)
		{
			currentWand = null;
			if (wands.size() > 0)
			{
				currentWand = wands.get(0);
			}
		}
		return (foundWand != null);
	}
	
	public void selectWand(String name)
	{
		if (wands == null) return;
		
		if (wands.size() > 0)
		{
			currentWand = wands.get(0);
		}
		for (Wand lookWand : wands)
		{
			if (lookWand.getName().equalsIgnoreCase(name))
			{
				currentWand = lookWand;
				break;
			}
		}
	}
	
	public void nextWand()
	{
		if (wands == null) return;
		
		if (currentWand != null)
		{
			int index = wands.indexOf(currentWand);
			index = (index + 1) % wands.size();
			currentWand = wands.get(index);
		}
	}
}
