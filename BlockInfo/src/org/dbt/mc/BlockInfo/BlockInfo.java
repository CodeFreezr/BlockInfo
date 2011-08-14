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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public String biversion = "0.4";
	
	private final BlockInfoPlayerListener playerListener = new BlockInfoPlayerListener(this);
	static String mainDirectory = "plugins/BlockInfo";

	
	//default Languages
	static File deProbe = new File(mainDirectory + File.separator + "de.lst");
	static File nlProbe = new File(mainDirectory + File.separator + "nl.lst");
	static File esProbe = new File(mainDirectory + File.separator + "es.lst");
	static File frProbe = new File(mainDirectory + File.separator + "fr.lst");	
	
	
	private File fileLang; 
	static Properties prop = new Properties();
	
	private ArrayList<String> arrLang = new ArrayList<String>();
	private String line = "";
	
	//The config
	public Configuration config;

	//Add variables that the user can define. We'll add one each of common types:
	public String cfgLang;

	//Logger Setting
	Logger log = Logger.getLogger("Minecraft");
	public static String logPrefix = "[BI] ";
	
	
	//ZipExploder-Util
	//public ZipExploder ze;
	 
	
	public void onEnable() {
		
		
		new File(mainDirectory).mkdir();
		
		if(!deProbe.exists()) createLang("de.lst");
		if(!esProbe.exists()) createLang("es.lst");
		if(!frProbe.exists()) createLang("fr.lst");
		if(!nlProbe.exists()) createLang("nl.lst");
		
		
		
	    config = getConfiguration(); 
	    cfgLang = config.getString("Lang", "de"); 
	    config.save();
	
		log.info(logPrefix + "Language set to: " + cfgLang);
		log.info(logPrefix + "BlockInfo " + biversion + " enabled.");		
		
		fileLang = new File(mainDirectory + File.separator + cfgLang + ".lst");
		
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Event.Priority.Normal, this);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileLang));
			while (line != null) {
				line = reader.readLine();
				arrLang.add(line);						
			}
		} catch (IOException e ) {
			log.info(logPrefix + "IO-Error");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	public void onDisable(){
		log.info(logPrefix + " Plugin in Version " + biversion + "  has been disabled.");
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
			
			if(args.length == 0 ) { // just /bi
				HashSet<Byte> paramHashSet = null;
				int paramInt = 0;
				Block targetBlock = player.getTargetBlock(paramHashSet, paramInt);
				id = targetBlock.getTypeId();
				player.sendMessage(logPrefix + id + " ("  + targetBlock.getData() + ") " + targetBlock.getType() + " , " + cfgLang + ": " + arrLang.get(id));	
				return true;				
			} else if (args.length == 1 ) {
				
				try {
					id = Integer.parseInt(args[0]);
				} catch (Exception e) {
					id = -1;
				}
				if (id >= 0 && id < 97) { // /bi <0-96>
					player.sendMessage(logPrefix + id + " = " + Material.getMaterial(id) + " / "  + arrLang.get(id));
					return true;
				} else if (id < 0) { // /bi <searchstring>
					searchBlock(args[0]);
					return true;
					} else { // ID > 96
						return false;
					}  
			} else {
				// mehr als 1 Argument
				return false; 
			}
	
	} 

	
	/**
	* Find Block with 
	*
	* @param searchstring
	*/	
	protected void searchBlock(String searchstring) {
		ArrayList<String> arrResult = new ArrayList<String>();
		//1. iterieren durch arrLang 

		log.info(logPrefix + "Suche nach: " + searchstring);	    

		for (int i = 0; i < arrLang.size() - 1; i++) {
		      if (arrLang.get(i).startsWith(searchstring)) {
		    	  log.info(i + ".ter Eintrag: " + arrLang.get(i));
		          //System.out.println("MATCH: " + line);
		        }
		}
	}

	
/**
* Create the Language-File from the .jar.
*
* @param name
*/
   protected void createLang(String name) {
        File actual = new File(getDataFolder(), name);
        log.info(logPrefix + "creating defaut Language: " + name);
            
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
