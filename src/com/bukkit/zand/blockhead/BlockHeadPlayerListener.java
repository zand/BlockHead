package com.bukkit.zand.blockhead;


import java.util.HashMap;
import java.util.List;
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
    		
    		if (args.length == 2) {	
    			// print the current version
    			if (args[1].equals("help")) {
    				ChatColor ch = ChatColor.LIGHT_PURPLE;
    				ChatColor cc = ChatColor.WHITE;
    				ChatColor cd = ChatColor.GOLD;
    				ChatColor ct = ChatColor.YELLOW;
    				player.sendMessage(ch + plugin.versionInfo);
    				player.sendMessage(cc + "/" + args[0] + " help " + cd + "-" + ct + " Displays this");
    				player.sendMessage(cc + "/" + args[0] + " version " + cd + "-" + ct + " Displays the current version");
    				if (plugin.checkPermission(player, "blockhead.hat"))
    					player.sendMessage(cc + "/" + args[0] + " " + cd + "-" + ct + " Puts the currently held item on your head");
    				if (plugin.checkPermission(player, "blockhead.hat.items"))
	    				player.sendMessage(cc + "/" + args[0] + " [block id]" + cd + "-" + ct + " Puts a block with block id on your head");
    				if (plugin.checkPermission(player, "blockhead.hat.give.players.items"))
	    				player.sendMessage(cc + "/" + args[0] + " [player] [block id]" + cd + "-" + ct + " Puts a block on another player");
    				if (plugin.checkPermission(player, "blockhead.hat.give.groups.items"))
	    				player.sendMessage(cc + "/" + args[0] + " group [group] [block id]" + cd + "-" + ct + " Puts blocks on all the players in that group");
    			}
    			else if (args[1].startsWith("ver")) player.sendMessage(plugin.versionInfo);
    			else { // /hat [item id]
    				try {
    					if (plugin.checkPermission(player, "blockhead.hat.items")) placeOnHead(player, new ItemStack(Integer.valueOf(args[1]), 1));
        				else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
    				}
    				catch (NumberFormatException e) {
    					player.sendMessage(ChatColor.DARK_RED + args[1] + " is not a number.");
    				}
    			}
    			
    		} else if (args.length == 2) { // hat [player] [itemid]
    			if (plugin.checkPermission(player, "blockhead.hat.give.players.items")) {
	    				try {
	    				// Get the stack
	    				ItemStack stack = new ItemStack(Integer.valueOf(args[2]), 1);
	    				
	    				// Check the stack
	    				if (stack.getTypeId() > 255 || stack.getTypeId() < 1) {
	    					player.sendMessage(ChatColor.RED + "Not a valid block id");
	    					return;
	    				}
	    				
	    				// Look for Player
	    				List<Player> players = plugin.getServer().matchPlayer(args[1]);
	    				
	    				// Player not Found
	    				if (players.size() < 1) player.sendMessage(ChatColor.RED + "Could not find player");
	    				
	    				// More than 1 Player Found
	    				else if (players.size() > 1) {
	    					player.sendMessage(ChatColor.RED + "More than one player found");
	    					String msg = "";
	    					for (Player other : players) msg += " " + other.getName();
	    					player.sendMessage(msg.trim());
	    				}
	    				
	    				// Player Found
	    				else {
	    					Player other = players.get(0);
	    					placeOnHead(other, stack);
	    					player.sendMessage("Putting a block on " + other.getName() + "'s head.");
	    				}
	    			}
	    			catch (NumberFormatException e) {
	    				player.sendMessage(ChatColor.DARK_RED + args[2] + " is not a number.");
	    			}
    			}
				else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
    		
    		} else if (args.length > 3 && args[1].equalsIgnoreCase("group")) { // hat group [group] [block id]
    			if (plugin.checkPermission(player, "blockhead.hat.give.groups.items")) {
    				try {
    				// Get the stack
    				ItemStack stack = new ItemStack(Integer.valueOf(args[3]), 1);
    				
    				// Check the stack
    				if (stack.getTypeId() > 255 || stack.getTypeId() < 1) {
    					player.sendMessage(ChatColor.RED + "Not a valid block id");
    					return;
    				}
    				
    				// Look for Player
    				List<Player> players = plugin.getGroupPlayers(args[2]);
    				
    				// Player not Found
    				if (players.size() < 1) player.sendMessage(ChatColor.RED + "Could not find any players in " + args[2]);
    				
    				// Player Found
    				else {
    					for (Player other : players)
    						placeOnHead(other, stack);
    					player.sendMessage("Putting blocks on players in " + args[2] + " heads.");
    				}
    			}
    			catch (NumberFormatException e) {
    				player.sendMessage(ChatColor.DARK_RED + args[2] + " is not a number.");
    			}
			}
			else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
		
		} else if (plugin.checkPermission(player, "blockhead.hat")) { // hat
	    		placeOnHead(player, player.getItemInHand());
    		} else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
    		
    		event.setCancelled(true);
    	}
    	
    }
    
    private boolean placeOnHead(Player player, ItemStack item) {
    	PlayerInventory inv = player.getInventory();
		if (item.getAmount() < 1) {
			player.sendMessage(ChatColor.RED + "You have no item in your hand");
			return false;
		}
		
		int id = item.getTypeId();
		if (id < 1 || id > 255) {
			player.sendMessage(ChatColor.RED + "You can't put that item on your head");
			return false;
		}
		
		ItemStack helmet = inv.getHelmet();
		ItemStack hat = new ItemStack(item.getType());
		hat.setData(item.getData()); // For colored cloth
		hat.setDamage(item.getDamage());
		
		inv.setHelmet(hat);
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

