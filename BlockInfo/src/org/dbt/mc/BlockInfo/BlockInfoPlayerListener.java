package org.dbt.mc.BlockInfo;

import java.util.HashSet;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class BlockInfoPlayerListener extends PlayerListener {

	public static BlockInfo plugin; 
	
	public BlockInfoPlayerListener(BlockInfo instance) {
        plugin = instance;
	}
	
	public void onPlayerInteract(PlayerInteractEvent event){
		
		Player player = event.getPlayer();
		HashSet<Byte> paramHashSet = null;
		int paramInt = 0;
		
		Block targetBlock = player.getTargetBlock(paramHashSet, paramInt);
		//targetBlock.getType();
		
//		Player player = event.getPlayer();
		player.sendMessage("[BI]: Blocktype is:" + targetBlock.getType());
		//event.getClickedBlock().getType()
		
	}
	
	
}
