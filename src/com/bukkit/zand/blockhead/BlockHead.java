package com.bukkit.zand.blockhead;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.nijikokun.bukkit.General.General;

/**
 * BlockHead for Bukkit
 *
 * @author zand
 */
public class BlockHead extends JavaPlugin {
    private final BlockHeadPlayerListener playerListener = new BlockHeadPlayerListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private final List<String> commands = new ArrayList<String>();
    public final String versionInfo; 

    public BlockHead(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);

        versionInfo = desc.getName() + " version " + desc.getVersion() + " by zand";
        commands.add(desc.getName().toLowerCase());
        commands.add("hat");
        setupCommands();
        
        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

    

    public void onEnable() {
        // Register our events
		PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(org.bukkit.event.Event.Type.PLAYER_COMMAND, playerListener, org.bukkit.event.Event.Priority.Normal, this);
        
        setupCommands();
        

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        System.out.println( versionInfo + " is enabled!" );
    }
    public void onDisable() {
        // NOTE: All registered events are automatically unregistered when a plugin is disabled
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        
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
    
	private void setupCommands() {
     	Plugin test = this.getServer().getPluginManager().getPlugin("General");

     	if (test != null) {
     	    General General = (General) test;
     	    // You can use color codes in the description, &[code] just like the simoleons!
     	    String command = "";
     	    for (String c : commands) command += "|" + c;
     	    if (!command.isEmpty())
     	    	General.l.save_command("/" + command.substring(1), "Puts the currently held item on your head");
     	}
    }
}
