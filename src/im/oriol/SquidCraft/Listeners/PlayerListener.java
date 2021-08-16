package im.oriol.SquidCraft.Listeners;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import im.oriol.SquidCraft.Constants;
import im.oriol.SquidCraft.GameScoreBoard;
import im.oriol.SquidCraft.Main;
import im.oriol.SquidCraft.Utils;
import im.oriol.SquidCraft.Resources.Model.GameState;
import im.oriol.SquidCraft.Resources.Model.Lot;
import im.oriol.SquidCraft.Resources.Model.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerListener implements Listener {
	
	private Main plugin;
	
	public PlayerListener(Main plugin){
		this.plugin = plugin;
	}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	UUID playerId = player.getUniqueId();
    	if (!GameState.userIsLogged(playerId)) {
	    	Bukkit.broadcastMessage("¡"+ player.getName()+" acaba de llegar al servidor!");
	    	
	    	Utils.TeleportToLogin(player);
			
			//Añadimos a la lista si no está
			UUID playerMinecraftId = player.getUniqueId();
			String playerName = player.getName();
			GameState.addUser(playerMinecraftId, playerName);
			
    	}else {
    		//Si estamos logeados
    		String username = player.getName();
    		User user = GameState.getPlayer(username);
    		Bukkit.broadcastMessage("¡Acaba de llegar "+username+" (en Twitch "+user.getTwitchName()+")!");
    	
    		//Solo teleportamos si estábamos en la mina
    		if (player.getWorld().getName().equals(Constants.MINE_WORLD_NAME)) {
    			Utils.TeleportToTown(player);
    		}
    		
    		//Mostramos scoreboard
    		//GameScoreBoard.PrintScoreBoard(player, user);
    	}
    	
    	//Sistema de action bars
    	new BukkitRunnable() {
		    @Override
		    public void run() {
		        if (!player.isOnline()) {
		          cancel(); // this cancels it when they leave
		        }
		        
		        //Si estamos en mina no enviamos
		        if (player.getWorld().getName().equals(Constants.MINE_WORLD_NAME))
		        	return;
		        
		        // Send to the player in the event
		        UUID playerId = player.getUniqueId();
		        User user = GameState.getPlayer(playerId);
		        String message = getUserActionBar(player, user);
		        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
		        
		    }
    	}.runTaskTimer(plugin, 5L, 15);
    	
    	//Sistema de ScoreBoard
    	new BukkitRunnable() {
    		@Override
		    public void run() {
		        if (!player.isOnline()) {
		          cancel(); // this cancels it when they leave
		        }
		        
		        User user = GameState.getPlayer(playerId);
		        GameScoreBoard.PrintScoreBoard(player, user);
		    }
    	}.runTaskTimer(plugin, 5L, 20*5);
    }
    
    private String getUserActionBar(Player player, User user) {
    	if (!user.getIsLogged()) {
    		return "Un admin debe autorizar tu cuenta para poder jugar."; 
    	}
    	
    	Location playerLocation = player.getLocation();
    	
    	Lot currentLot = GameState.getCurrentLot(playerLocation.getBlockX(), playerLocation.getBlockZ());
    	
    	String currentLotName = "Tierra libre";
    	
    	if (currentLot != null) {
    		ChatColor color = ChatColor.RED;
      
        	List<UUID> tenants = currentLot.getTenants();
        	for (UUID tenant: tenants) {
        		if (tenant.equals(user.getMinecraftId()))
        			color = ChatColor.GREEN;
        	}
        	
        	if (currentLot.getOwner().getMinecraftId().equals(user.getMinecraftId())) {
        		color = ChatColor.GREEN;
        	}
        	
    		currentLotName = color + currentLot.getName()+" ("+currentLot.getOwner().getName()+")";
    	}

    	return currentLotName;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	Player player = event.getPlayer();
    	CancelIfNotAvailable(player, event);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	Player player = event.getPlayer();
    	CancelIfNotAvailable(player, event);
    }
    
    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
    	Player player = event.getPlayer();
    	CancelIfNotAvailable(player, event);
    }
    
    @EventHandler
    public void catchChestOpen(InventoryOpenEvent event)
    {
        if(event.getInventory().getType().equals(InventoryType.CHEST))
        {
        	Player player = (Player) event.getPlayer();
        	CancelIfNotAvailable(player, event);
        }
    }
    
    private void CancelIfNotAvailable(Player player, Cancellable event) {
    	Location playerLocation = player.getLocation();
    	UUID playerId = player.getUniqueId();
    	
    	if (!GameState.userIsLogged(playerId)) {
    		event.setCancelled(true);
    		player.sendMessage("Debes identificarte para poder jugar. Contacta con un administrador.");
    		return;
    	}

    	Lot currentLot = GameState.getCurrentLot(playerLocation.getBlockX(), playerLocation.getBlockZ());
    	boolean imTenant = false;
    	
    	if (currentLot != null) {
        	List<UUID> tenants = currentLot.getTenants();
        	for (UUID tenant: tenants) {
        		if (tenant.equals(playerId))
        			imTenant = true;
        	}
    	}

    	
    	if (currentLot != null && !currentLot.getOwner().getMinecraftId().equals(playerId) && !imTenant) {
    		event.setCancelled(true);
    		player.sendMessage("No puedes hacer esto en "+currentLot.getName()+", dominios de "+currentLot.getOwner().getName());
    		return;
    	}
    }
    
    
}
