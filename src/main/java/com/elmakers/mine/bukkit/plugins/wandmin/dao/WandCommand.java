package com.elmakers.mine.bukkit.plugins.wandmin.dao;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.persisted.PersistClass;
import com.elmakers.mine.bukkit.persisted.PersistField;
import com.elmakers.mine.bukkit.persisted.Persisted;
import com.elmakers.mine.bukkit.plugins.wandmin.WandminPlugin;

@PersistClass(schema="wands", name="command")
public class WandCommand extends Persisted 
{
	private int id;
	private String command;
	private String name;
	private String description;
	
	@PersistField(id=true, auto=true)
	public int getId() 
	{
		return id;
	}

	public void setId(int id) 
	{
		this.id = id;
	}

	@PersistField
	public void setName(String name) 
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	@PersistField
	public void setDescription(String description) 
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}

	@PersistField
	public String getCommand()
	{
		return command;
	}

	public void setCommand(String command)
	{
		this.command = command;
		if (this.name == null)
		{
			this.name = command;
		}
	}

	public void copyTo(WandCommand other)
	{
		other.command = command;
		other.name = name;
		other.description = description;
	}
	
	public void use(WandminPlugin plugin, Player player)
	{
		CraftServer server = (CraftServer)plugin.getServer();
		server.dispatchCommand(player, command);
	}
}
