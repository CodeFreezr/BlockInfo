package org.dbt.mc.BlockInfo;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
 

public class BlockInfo extends JavaPlugin {

	public String biversion = "0.5";
	
	private final BlockInfoPlayerListener playerListener = new BlockInfoPlayerListener(this);
	static String mainDirectory = "plugins/BlockInfo";

	
	//default Languages
	static File deProbe = new File(mainDirectory + File.separator + "de.lst");
	static File nlProbe = new File(mainDirectory + File.separator + "nl.lst");
	static File esProbe = new File(mainDirectory + File.separator + "es.lst");
	static File frProbe = new File(mainDirectory + File.separator + "fr.lst");	
	static File enProbe = new File(mainDirectory + File.separator + "en.lst");
	
	
	private File fileLang; 
	static Properties prop = new Properties();
	
	private ArrayList<String> arrLang = new ArrayList<String>();
	
	
	//The config
	public Configuration config;

	//Add variables that the user can define. We'll add one each of common types:
	public String cfgLang;

	//Logger & Output-Settings
	Logger log = Logger.getLogger("Minecraft");
	public static String logPrefix = "[BI] ";
	public static String chatPrefix = ChatColor.BLUE + logPrefix;
	public static String oops = "Oops, here went something wrong. Please reload. If this doesn't work, please post a hint in the Bukkit-Plugin-Forum. thx-a-lot";
	
	public void onEnable() {
		
		
		new File(mainDirectory).mkdir();
		
		if(!deProbe.exists()) createDefaultLangFile("de.lst");
		if(!esProbe.exists()) createDefaultLangFile("es.lst");
		if(!frProbe.exists()) createDefaultLangFile("fr.lst");
		if(!nlProbe.exists()) createDefaultLangFile("nl.lst");
		if(!enProbe.exists()) createDefaultLangFile("en.lst");
		
		
	    config = getConfiguration(); 
	    cfgLang = config.getString("Lang", "de"); 
	    config.save();
	
		
		
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);

		arrLang = loadLang(cfgLang, null);
		
		
		log.info(logPrefix + "Language set to: " + cfgLang);
		log.info(logPrefix + "BlockInfo " + biversion + " enabled.");		
		
		
	}
 
	public void onDisable(){
		log.info(logPrefix + " Plugin in Version " + biversion + " disabled.");
	}


/**
* onCommand
* 
* Central onCommandHandler
*
* @param sender
* @param cmd
* @param commandLabel
* @param args
*/	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player)sender;			
		int id = 0;
		
		// BI
		if (cmd.getName().equalsIgnoreCase("bi")) {

			if(args.length == 0 ) { // just /bi
				try {
					HashSet<Byte> paramHashSet = null;
					int paramInt = 0;
					Block targetBlock = player.getTargetBlock(paramHashSet, paramInt);
					id = targetBlock.getTypeId();
					player.sendMessage(chatPrefix + id + ","  + targetBlock.getData() + " [" + targetBlock.getType() + "] " + cfgLang + ": " + arrLang.get(id));	
					return true;									
				} catch (Exception e) {
					log.info(logPrefix + oops);
					player.sendMessage(ChatColor.RED + oops);
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else if (args.length == 1 ) {
				
				try {
					id = Integer.parseInt(args[0]);
				} catch (Exception e) {
					id = -1;
				}
				if (id >= 0 && id < 97) { // /bi <0-96>
					player.sendMessage(chatPrefix + id + " [" + Material.getMaterial(id) + "] " + cfgLang + ": " + arrLang.get(id));
					return true;
				} else if (id < 0) { // /bi <searchstring>
					searchBlock(arrLang, args[0], player);
					return true;
					} else { // ID > 96
						return false;
					}  
			} else {
				// mehr als 1 Argument
				return false; 
			}	
		}
		
		//BILANG
		if (cmd.getName().equalsIgnoreCase("bilang")) {
			
			if (args.length == 1 ) {
				arrLang = loadLang(args[0], player);
				if (arrLang.size() > 0) {
					player.sendMessage(chatPrefix + "Selected Language is now: " + args[0] + " with "+ arrLang.size() + " Entries.");
				}
				return true;
			} else if (args.length == 0 ) {
				player.sendMessage(chatPrefix + "Selected Language: " + cfgLang);
				
				return true;
			} else {
				return false;
			}
		}
		
		//BILIST
		if (cmd.getName().equalsIgnoreCase("bilist")) {
			player.sendMessage(chatPrefix + "List of Languages (Work in Progress)");
			return true;
		}
		
		return false;							
	} 

	
	/**
	* Find Block beginning with searchstring and printing result 
	*
	* @param mylang
	* @param searchstring
	* @param player
	*/	
	protected void searchBlock(ArrayList<String> myLang, String searchstring, Player player) {
		int found = 0;
		player.sendMessage("");
		for (int i = 0; i < arrLang.size() - 1; i++) {
		      if (myLang.get(i).startsWith(searchstring)) {
		    	  
		    	  player.sendMessage(chatPrefix + i + " [" + Material.getMaterial(i) + "] "+ cfgLang + ": " + myLang.get(i));
		    	  found = 1;
		        }
		}
		if (found == 0) {
			player.sendMessage(chatPrefix + "Nothing found, please try another string.");
		}
	}


	/**
	* Load Language
	*
	* @param lang of the Language
	*/
	protected ArrayList<String> loadLang(String lang, Player player) {
		String line = "";
		ArrayList<String> myLang = new ArrayList<String>();
		fileLang = new File(mainDirectory + File.separator + lang + ".lst");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileLang));
			while (line != null) {
				line = reader.readLine();
				myLang.add(line);						
			}
		} catch (IOException e ) {
			if (!player.equals(null)) {
				player.sendMessage(ChatColor.RED + "CAN'T READ LANGUAGE: " + lang);
			} 
			
			log.info(logPrefix + "IO-Error on loading Language: " + lang);
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		
		return myLang;
		
	}
	
	
	
	
	/**
	* Create the Language-File from the .jar.
	*
	* @param name
	*/
	protected void createDefaultLangFile(String name) {
        File actual = new File(getDataFolder(), name);
        log.info(logPrefix + "creating defaut Language-File: " + name);
            
            InputStream input = this.getClass().getResourceAsStream("/defaults/" + name);
            
            if (input != null) {

            	FileOutputStream output = null;

                try {
                    output = new FileOutputStream(actual);
                    byte[] buf = new byte[8192];
                    int length = 0;
                    while ((length = input.read(buf)) > 0) {
                        output.write(buf, 0, length);
                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (input != null)
                            input.close();
                    } catch (IOException e) {}

                    try {
                        if (output != null)
                            output.close();
                    } catch (IOException e) {}
                }
            }
        
    }
	
	
}
