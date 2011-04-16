
package com.elmakers.mine.bukkit.plugins.wandmin;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.elmakers.mine.bukkit.persistence.Persistence;
import com.elmakers.mine.bukkit.persistence.dao.PlayerData;
import com.elmakers.mine.bukkit.persistence.dao.PluginCommand;
import com.elmakers.mine.bukkit.plugins.persistence.PersistencePlugin;
import com.elmakers.mine.bukkit.plugins.wandmin.dao.PlayerWands;
import com.elmakers.mine.bukkit.plugins.wandmin.dao.Wand;
import com.elmakers.mine.bukkit.plugins.wandmin.dao.WandCommand;
import com.elmakers.mine.bukkit.utilities.PluginUtilities;

public class WandminPlugin extends JavaPlugin 
{
	private int wandTypeId = 280;

	private final Logger log = Logger.getLogger("Minecraft");
	private final WandminPlayerListener playerListener = new WandminPlayerListener();

	protected PluginCommand wandminCommand;
	protected PluginCommand createCommand;
	protected PluginCommand destroyCommand;
	protected PluginCommand spellsCommand;
	protected PluginCommand commandsCommand;
	protected PluginCommand bindCommand;
	protected PluginCommand unbindCommand;
	protected PluginCommand nextCommand;
	protected PluginCommand wandsCommand;

	protected Persistence persistence = null;
	protected PluginUtilities utilities = null;
	
	public PlayerWands getPlayerWands(Player player)
	{
		PlayerWands wands = persistence.get(player.getName(), PlayerWands.class);
		if (wands == null)
		{
			PlayerData playerData = utilities.getPlayer(player);
			if (playerData != null)
			{
				wands = new PlayerWands(playerData);
				persistence.put(wands);
			}
		}
		
		return wands;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] parameters)
	{
		 return utilities.dispatch(this, sender, command.getName(), parameters);
	}
	
	public boolean onNextWand(Player player, String[] parameters)
	{
		PlayerWands wands = getPlayerWands(player);
		Wand wand = wands.getCurrentWand();
		if (wand == null)
		{
			player.sendMessage("Create a wand first");
			return true;
		}
		wands.nextWand();
		wand = wands.getCurrentWand();
		player.sendMessage(" " + wand.getName() + " : " + wand.getCurrentCommand().getName());
		return true;
	}
	
	public boolean onListWands(Player player, String[] parameters)
	{
		PlayerWands wands = getPlayerWands(player);
    	
		player.sendMessage("You have " + wands.getWands().size() + " wands:");
		for (Wand wand : wands.getWands())
		{
			String prefix = " ";
			if (wand == wands.getCurrentWand())
			{
				prefix = "*";
			}
			String wandMessage = prefix + wand.getName();
			String wandDescription = wand.getDescription();
			if (wandDescription != null && wandDescription.length() > 0)
			{
				wandMessage = wandMessage + " : " + wandDescription;
			}
			player.sendMessage(wandMessage);
		}
		return true;
	}
    	
	public boolean onListCommands(Player player, String[] parameters)
	{
		PlayerWands wands = getPlayerWands(player);
	
		Wand wand = wands.getCurrentWand();
		if (wand == null)
		{
			player.sendMessage("Create a wand first");
			return true;
		}
		player.sendMessage("You have " + wand.getCommands().size() + " spells on your " + wand.getName() + " wand:");
		player.sendMessage(wand.getName());
		for (WandCommand wandCommandPart : wand.getCommands())
		{
			String prefix = " ";
			if (wandCommandPart == wand.getCurrentCommand())
			{
				prefix = "*";
			}
			String commandMessage = prefix + wandCommandPart.getName();
			String commandDescription = wandCommandPart.getDescription();
			if (commandDescription != null && commandDescription.length() > 0)
			{
				commandMessage = commandMessage + " : " + commandDescription;
			}
			player.sendMessage(commandMessage);
		}
		return true;
	}
    	
    
	public boolean onCreateWand(Player player, String[] parameters)
	{
		PlayerWands wands = getPlayerWands(player);
		String wandName = parameters[0];
		wands.addWand(wandName);
		player.sendMessage("Added wand '" + wandName + "'");
		return true;
	}
    	
	public boolean onDestroyWand(Player player, String[] parameters)
	{
		PlayerWands wands = getPlayerWands(player);		
		String wandName = parameters[0];
		wands.removeWand(wandName);
		player.sendMessage("Removed wand '" + wandName + "'");
		return true;
	}

	public boolean doBind(Player player, String[] parameters, boolean unbind)
	{
		PlayerWands wands = getPlayerWands(player);
		// Needs a wand
    	Wand wand = wands.getCurrentWand();
    	if (wand == null)
    	{
    		player.sendMessage("Create a wand first");
    		return true;
    	}
    	String castCommand = "";
    	for (int i = 0; i < parameters.length; i++) 
    	{
    		castCommand += parameters[i];
			if (i != parameters.length - 1)
			{
				castCommand += " ";
			}
		}
    	if (unbind)
    	{
    		wand.removeCommand(castCommand);
    		player.sendMessage("Unbound wand '" + wand.getName() + "' from '" + castCommand + "'");    		
    	}
    	else
    	{
    		wand.addCommand(castCommand);
    		player.sendMessage("Bound wand '" + wand.getName() + "' to '" + castCommand + "'");
    	}
		return true;
	}
	
	public boolean onBind(Player player, String[] parameters)
	{
		return doBind(player, parameters, false);
	}

	public boolean onUnbind(Player player, String[] parameters)
	{
		return doBind(player, parameters, true);
	}

	public void onEnable() 
	{
		PluginManager pm = getServer().getPluginManager();
		if (!initialize(pm))
		{
			pm.disablePlugin(this);
			return;
		}
		
		playerListener.setPlugin(this);
			
        pm.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        
        PluginDescriptionFile pdfFile = this.getDescription();
        log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled");
	}
	
	public boolean initialize(PluginManager pm)
	{
		Plugin checkForPersistence = this.getServer().getPluginManager().getPlugin("Persistence");
        if (checkForPersistence != null)
        {
            PersistencePlugin plugin = (PersistencePlugin) checkForPersistence;
            persistence = plugin.getPersistence();
            utilities = plugin.createUtilities(this);
        }
        else
        {
            log.warning("The NetherGate plugin depends on Persistence");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        
        wandminCommand = utilities.getPlayerCommand("wandmin", "Manage Wandmin wands", null);
        createCommand = wandminCommand.getSubCommand("create", "Create a wand", "create <name>");
        destroyCommand = wandminCommand.getSubCommand("destroy", "Destroy a wand", "destroy <name>");
        spellsCommand = wandminCommand.getSubCommand("spells", "List bound spells/commands", null);
        commandsCommand = wandminCommand.getSubCommand("commands", "List bound spells/commands", null);
        bindCommand = wandminCommand.getSubCommand("bind", "Bind a command to a wand", "bind <command to bind>");
        unbindCommand = wandminCommand.getSubCommand("unbind", "Remove a command from a wand", "unbind <command to unbind>");
        nextCommand = wandminCommand.getSubCommand("next", "Cycle to the next wand", null);
        wandsCommand = wandminCommand.getSubCommand("wands", "List creaed wands", null);

        createCommand.bind("onCreateWand");
        destroyCommand.bind("onDestroyWand");
        spellsCommand.bind("onListCommands");
        commandsCommand.bind("onListCommands");
        bindCommand.bind("onBind");
        unbindCommand.bind("onUnbind");
        nextCommand.bind("onNextWand");
        wandsCommand.bind("onListWands");
        
	    return true;
	}

	public void onDisable() 
	{
	}
	
	public int getWandTypeId()
	{
		return wandTypeId;
	}
}
