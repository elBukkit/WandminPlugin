package com.elmakers.mine.bukkit.plugins.wandmin;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import com.elmakers.mine.bukkit.plugins.wandmin.dao.PlayerWands;
import com.elmakers.mine.bukkit.plugins.wandmin.dao.Wand;

public class WandminPlayerListener extends PlayerListener 
{
	@Override
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if (event.getPlayer().getInventory().getItemInHand().getTypeId() == plugin.getWandTypeId())
        {
            PlayerWands wands = plugin.getPlayerWands(event.getPlayer());
            Wand wand = wands.getCurrentWand();
            if (wand == null)
            {
                return;
            }
            
            if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                wand.use(plugin, event.getPlayer());
            }
            else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
            {
                wand.nextCommand();
                event.getPlayer().sendMessage(" " + wand.getName() + " : " + wand.getCurrentCommand().getName());
            }           
        }
    }

    private WandminPlugin plugin;
	
	public void setPlugin(WandminPlugin plugin)
	{
		this.plugin = plugin;
	}
}
