package com.bukkit.zand.blockhead;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


/**
 * Handle events for all Player related events
 * @author zand
 */
public class BlockHeadPlayerListener extends PlayerListener {
	public final BlockHead plugin;

    public BlockHeadPlayerListener(BlockHead instance) {
    	plugin = instance;
    }
    
    public void onPlayerCommand(PlayerChatEvent event) {
    	
    	if (event.isCancelled()) return;
    	
    	// remove the / and split to args
    	String[] args = event.getMessage().toLowerCase().substring(1).split(" ");
    	
    	if (plugin.isCommand(args[0])) {
    		Player player = event.getPlayer();
    		
    		if (args.length > 1) {	
    			// print the current version
    			if (args[1].equals("help")) {
    				ChatColor ch = ChatColor.LIGHT_PURPLE;
    				ChatColor cc = ChatColor.WHITE;
    				ChatColor cd = ChatColor.GOLD;
    				ChatColor ct = ChatColor.YELLOW;
    				player.sendMessage(ch + plugin.versionInfo);
    				player.sendMessage(cc + "/" + args[0] + " help " + cd + "-" + ct + " Displays this");
    				player.sendMessage(cc + "/" + args[0] + " " + cd + "-" + ct + " Puts the currently held item on your head");
    				player.sendMessage(cc + "/" + args[0] + " version " + cd + "-" + ct + " Displays the current version");
    			}
    			if (args[1].startsWith("ver")) player.sendMessage(plugin.versionInfo);
    			
    		} else {
	    		
	    		placeOnHead(player, player.getItemInHand());
    		}
    		
    		event.setCancelled(true);
    	}
    	
    }
    
    private boolean placeOnHead(Player player, ItemStack item) {
    	PlayerInventory inv = player.getInventory();
		if (item.getAmount() < 1) {
			player.sendMessage("You have no item in your hand");
			return false;
		}
		
		int id = item.getTypeId();
		if (id < 1 || id > 255) {
			player.sendMessage("You can put that item on your head");
			return false;
		}
		
		ItemStack helmet = inv.getHelmet();
		
		
		inv.setHelmet(new ItemStack(id, 1));
		if (item.getAmount() > 1) item.setAmount(item.getAmount()-1);
		else inv.remove(item);
		
		// put what was in the helmet spot back
		if (helmet.getAmount() > 0) {
			HashMap<Integer, ItemStack> leftover = inv.addItem(helmet);
			if (!leftover.isEmpty()) {
				player.sendMessage("Was unble to put the old headhear away, droping it at your feet");
				
				// Drop the stacks
				for (Map.Entry<Integer, ItemStack> e : leftover.entrySet()) 
					player.getWorld().dropItem(player.getLocation(), e.getValue());
			}
		}
		 
		
		
		player.sendMessage("Enjoy your new Headgear");
		return true;
    }
    
}

