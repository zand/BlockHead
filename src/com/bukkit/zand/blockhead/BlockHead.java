package com.bukkit.zand.blockhead;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.General.General;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * BlockHead for Bukkit
 *
 * @author zand
 */
public class BlockHead extends JavaPlugin {
	public final String name; 
	public final String versionInfo; 
	private static Logger log = Logger.getLogger("Minecraft");
	
    private final BlockHeadPlayerListener playerListener = new BlockHeadPlayerListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final List<String> commands = new ArrayList<String>();
    
    public Permissions Permissions = null;

    public BlockHead(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        name = desc.getName();
        String authors = "";
        for (String author : desc.getAuthors()) authors += ", " + author;
        versionInfo = name + " version " + desc.getVersion() + 
        	(authors.isEmpty() ? "" : " by" + authors.substring(1));
        
        commands.add(name.toLowerCase());
        commands.add("hat");
        //setupCommands();
        
        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

    

    public void onEnable() {
        // Register our events
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(org.bukkit.event.Event.Type.PLAYER_COMMAND, playerListener, org.bukkit.event.Event.Priority.Normal, this);
        
        setupOtherPlugins();
        
        log.info( versionInfo + " is enabled!" );
    }
    
    public void onDisable() {
    }
    
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
    
    public boolean isCommand(String command) {
    	return commands.contains(command);
    }
    
	private void setupOtherPlugins() {
		
		// General
     	Plugin test = this.getServer().getPluginManager().getPlugin("General");
     	if (test != null) {
     	    General General = (General) test;
     	    // You can use color codes in the description, &[code] just like the simoleons!
     	    String command = "";
     	    for (String c : commands) command += "|" + c;
     	    if (!command.isEmpty())
     	    	General.l.save_command("/" + command.substring(1) + " (help)", "Puts the block in-hand on your head");
     	}
     	
     	// Permissions
    	test = this.getServer().getPluginManager().getPlugin("Permissions");
    	if (this.Permissions == null) {
    		if(test != null) {
    			this.Permissions = (Permissions)test;
    	    	log.info("[" + name + "] Found Permissions plugin. Using it.");
    	    }
    	}
     	
     	
    }
	
	@SuppressWarnings("static-access")
	boolean checkPermission(Player player, String nodes) {
    	if (this.Permissions == null)
    		return nodes == "blockhead.hat" || player.isOp();
    	return this.Permissions.Security.permission(player, nodes);
    }
}
