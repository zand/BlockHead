package com.bukkit.zand.blockhead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * BlockHead for Bukkit
 *
 * @author zand
 */
public class BlockHead extends JavaPlugin {
	public String name; 
	public String versionInfo; 
	private static Logger log = Logger.getLogger("Minecraft");
	
    //private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final List<String> commands = new ArrayList<String>();
    
    public Permissions Permissions = null;
    
    @Override
    public void onEnable() {
    	PluginDescriptionFile desc = getDescription();
    	name = desc.getName();
        String authors = "";
        for (String author : desc.getAuthors()) authors += ", " + author;
        versionInfo = name + " version " + desc.getVersion() + 
        	(authors.isEmpty() ? "" : " by" + authors.substring(1));
        
        commands.add(name.toLowerCase());
        commands.add("hat");
        
        setupOtherPlugins();
        
        log.info( versionInfo + " is enabled!" );
    }
    
    @Override
    public void onDisable() {
    	if (true) return;
    }
    
    /*
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else { 
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    */
    
    public boolean isCommand(String command) {
    	return commands.contains(command);
    }
    
    /**
     * Setup dynamic support for other plugins.
     */
	private void setupOtherPlugins() {
     	// Permissions
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");
    	if (this.Permissions == null) {
    		if(test != null) {
    			this.Permissions = (Permissions)test;
    	    	log.info("[" + name + "] Found Permissions plugin. Using it.");
    	    }
    	}
    }
	
	@SuppressWarnings("static-access")
	public List<Player> getGroupPlayers(String group) {
		ArrayList<Player> ret = new ArrayList<Player>();
		
		if (this.Permissions != null)
			for (Player player : getServer().getOnlinePlayers())
				if (this.Permissions.Security
						.getGroup(player.getWorld().getName(), player.getName())
						.toLowerCase()
						.startsWith(group.toLowerCase()))
					ret.add(player);
		
    	return ret;
	}
	
	@SuppressWarnings("static-access")
	boolean checkPermission(Player player, String nodes) {
    	if (this.Permissions == null)
    		return nodes == "blockhead.hat" || player.isOp() && !nodes.startsWith("blockhead.hat.give.");
    	return this.Permissions.Security.permission(player, nodes);
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
    	
    	// remove the / and split to args
    	if (isCommand(command.getName().toLowerCase())) {
    		Player player = null;
    		if (sender instanceof Player) player = (Player)sender;
    		else return false;
    		
    		if (args.length == 1) {	
    			// print the current version
    			if (args[0].equals("help")) {
    				showHelp(command.getName().toLowerCase(), player);
    			}
    			else if (args[0].startsWith("ver")) player.sendMessage(versionInfo);
    			else { // /hat [item id]
    				try {
    					if (checkPermission(player, "blockhead.hat.items")) placeOnHead(player, new ItemStack(Integer.valueOf(args[0]), 1));
        				else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
    				}
    				catch (NumberFormatException e) {
    					player.sendMessage(ChatColor.DARK_RED + args[0] + " is not a number.");
    				}
    			}
    			
    		} else if (args.length == 2) { // hat [player] [itemid]
    			if (checkPermission(player, "blockhead.hat.give.players.items")) {
	    				try {
	    				// Get the stack
	    				ItemStack stack = new ItemStack(Integer.valueOf(args[1]), 1);
	    				
	    				// Check the stack
	    				if (stack.getTypeId() > 255 || stack.getTypeId() < 1) {
	    					player.sendMessage(ChatColor.RED + "Not a valid block id");
	    					return true;
	    				}
	    				
	    				// Look for Player
	    				List<Player> players = getServer().matchPlayer(args[0]);
	    				
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
	    				player.sendMessage(ChatColor.DARK_RED + args[1] + " is not a number.");
	    			}
    			}
				else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
    		
    		} else if (args.length > 2 && args[0].equalsIgnoreCase("group")) { // hat group [group] [block id]
    			if (checkPermission(player, "blockhead.hat.give.groups.items")) {
    				try {
    				// Get the stack
    				ItemStack stack = new ItemStack(Integer.valueOf(args[2]), 1);
    				
    				// Check the stack
    				if (stack.getTypeId() > 255 || stack.getTypeId() < 1) {
    					player.sendMessage(ChatColor.RED + "Not a valid block id");
    					return true;
    				}
    				
    				// Look for Player
    				List<Player> players = getGroupPlayers(args[1]);
    				
    				// Player not Found
    				if (players.size() < 1) player.sendMessage(ChatColor.RED + "Could not find any players in " + args[1]);
    				
    				// Player Found
    				else {
    					for (Player other : players)
    						placeOnHead(other, stack);
    					player.sendMessage("Putting blocks on players in " + args[1] + " heads.");
    				}
    			}
    			catch (NumberFormatException e) {
    				player.sendMessage(ChatColor.DARK_RED + args[1] + " is not a number.");
    			}
			}
			else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
		
		} else if (checkPermission(player, "blockhead.hat")) { // hat
	    		placeOnHead(player, player.getItemInHand());
    		} else player.sendMessage(ChatColor.DARK_RED + "Your not allowed to use that command");
    		return true;
    	}
    	return false;
    }
	
	/**
	 * Shows BlockHeads help to the player
	 * @param cmd		The command to show for.
	 * @param player	The player to show to.
	 */
	private void showHelp(String cmd, Player player) {
		ChatColor ch = ChatColor.LIGHT_PURPLE;
		ChatColor cc = ChatColor.WHITE;
		ChatColor cd = ChatColor.GOLD;
		ChatColor ct = ChatColor.YELLOW;
		player.sendMessage(ch + versionInfo);
		player.sendMessage(cc + "/" + cmd + " help " + cd + "-" + ct + " Displays this");
		player.sendMessage(cc + "/" + cmd + " version " + cd + "-" + ct + " Displays the current version");
		if (checkPermission(player, "blockhead.hat"))
			player.sendMessage(cc + "/" + cmd + " " + cd + "-" + ct + " Puts the currently held item on your head");
		if (checkPermission(player, "blockhead.hat.items"))
			player.sendMessage(cc + "/" + cmd + " [block id]" + cd + "-" + ct + " Puts a block with block id on your head");
		if (checkPermission(player, "blockhead.hat.give.players.items"))
			player.sendMessage(cc + "/" + cmd + " [player] [block id]" + cd + "-" + ct + " Puts a block on another player");
		if (checkPermission(player, "blockhead.hat.give.groups.items"))
			player.sendMessage(cc + "/" + cmd + " group [group] [block id]" + cd + "-" + ct + " Puts blocks on all the players in that group");
	}
    
	/**
	 * Places one block from the ItemStack onto the Players Head
	 * @param player	The player to place the block on.
	 * @param item		The ItemStack to take from.
	 * @return			If the item was placed on the players head.
	 */
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
		hat.setDurability(item.getDurability());
		
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
