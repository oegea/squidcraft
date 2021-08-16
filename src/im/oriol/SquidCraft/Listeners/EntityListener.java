package im.oriol.SquidCraft.Listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import im.oriol.SquidCraft.Constants;
import im.oriol.SquidCraft.Main;
import im.oriol.SquidCraft.Utils;
import im.oriol.SquidCraft.Resources.Model.GameState;
import im.oriol.SquidCraft.Resources.Model.Lot;
import im.oriol.SquidCraft.Resources.Model.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class EntityListener implements Listener {
	
	public EntityListener(){
	}

	/**
	 * Evento lanzado al explotar una entidad, utilizado para evitar que los creepers exploten en parcelas
	 * @param event
	 */
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType().equals(EntityType.CREEPER) && entity.getWorld().getName().equals(Constants.LOTS_WORLD_NAME)) {
			Location location = entity.getLocation();
			
			Lot currentLot = GameState.getCurrentLot(location.getBlockX(), location.getBlockZ());
			
			//No se puede explotar en parcelas
			if (currentLot != null)
				event.setCancelled(true);
		}
	}

	/**
	 * Evento lanzado cuando una entidad cambia bloques (evitamos que enders cojan bloques en parcelas)
	 * @param event
	 */
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType().equals(EntityType.ENDERMAN) && entity.getWorld().getName().equals(Constants.LOTS_WORLD_NAME)) {
			Location location = entity.getLocation();
			
			Lot currentLot = GameState.getCurrentLot(location.getBlockX(), location.getBlockZ());
			
			//No se puede explotar en parcelas
			if (currentLot != null)
				event.setCancelled(true);
		}
	}
    
}
