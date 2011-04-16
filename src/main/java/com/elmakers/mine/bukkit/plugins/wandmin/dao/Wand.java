package com.elmakers.mine.bukkit.plugins.wandmin.dao;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.persisted.PersistClass;
import com.elmakers.mine.bukkit.persisted.PersistField;
import com.elmakers.mine.bukkit.persisted.Persisted;
import com.elmakers.mine.bukkit.plugins.wandmin.WandminPlugin;

@PersistClass(schema="wands", name="wand")
public class Wand extends Persisted
{
	private int id;
	private String name;
	private String description;
	private List<WandCommand> commands;
	
	private WandCommand currentCommand;
	
	@PersistField
	public void setCommands(List<WandCommand> commands)
	{
		this.commands = commands;
		this.currentCommand = null;
		if (commands != null && commands.size() > 0)
		{
			this.currentCommand = commands.get(0);
		}
	}
	
	public List<WandCommand> getCommands()
	{
		return commands;
	}
	
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
	public void setDescription(String description) 
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}

	@PersistField
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void copyTo(Wand other)
	{
		other.clear();
		other.name = name;
		other.description = description;
		if (commands != null)
		{
			for (WandCommand command : commands)
			{
				WandCommand newCommand = other.addCommand(command.getCommand());
				command.copyTo(newCommand);
			}
			
			other.selectCommand(currentCommand.getCommand());
		}
	}
	
	public void clear()
	{
		if (commands != null)
		{
			commands.clear();
		}
		currentCommand = null;
	}
	
	public WandCommand getCurrentCommand()
	{
		return currentCommand;
	}
	
	public WandCommand addCommand(String command)
	{
		if (commands == null)
		{
			commands = new ArrayList<WandCommand>();
		}
		WandCommand newCommand = new WandCommand();
		newCommand.setCommand(command);
		commands.add(newCommand);
		currentCommand = newCommand;
		persistence.put(this);
		persistence.put(newCommand);
		return newCommand;
	}
	
	public void selectCommand(String command)
	{
		if (commands == null)
		{
			return;
		}
		for (WandCommand lookCommand : commands)
		{
			if (lookCommand.getCommand().equalsIgnoreCase(command))
			{
				currentCommand = lookCommand;
				break;
			}
		}
	}
	
	public void use(WandminPlugin plugin, Player player)
	{
		if (currentCommand != null)
		{
			currentCommand.use(plugin, player);
		}
	}
	
	public void nextCommand()
	{
		if (currentCommand != null)
		{
			int index = commands.indexOf(currentCommand);
			index = (index + 1) % commands.size();
			currentCommand = commands.get(index);
		}
	}
	
	public boolean removeCommand(String command)
	{
		WandCommand foundCommand = null;
		for (WandCommand lookCommand : commands)
		{
			if (lookCommand.getCommand().equalsIgnoreCase(command))
			{
				foundCommand = lookCommand;
				commands.remove(foundCommand);
				persistence.put(this);
				persistence.remove(foundCommand);
				break;
			}
		}
		if (currentCommand == foundCommand)
		{
			currentCommand = null;
			if (commands.size() > 0)
			{
				currentCommand = commands.get(0);
			}
		}
		return (foundCommand != null);
	}
}
