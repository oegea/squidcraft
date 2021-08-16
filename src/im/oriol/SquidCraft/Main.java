package im.oriol.SquidCraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import im.oriol.SquidCraft.Listeners.EntityListener;
import im.oriol.SquidCraft.Listeners.PlayerListener;
import im.oriol.SquidCraft.Resources.Model.GameState;
import im.oriol.SquidCraft.Resources.Model.Lot;
import im.oriol.SquidCraft.Resources.Model.ResourceLocation;
import im.oriol.SquidCraft.Resources.Model.User;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Main extends JavaPlugin {
	

	/**
	 * Event fired when the plugin is enabled (startup, reloads, plugin reloads)
	 */
	@Override
	public void onEnable() {
		
		//Load state
		StateWriter.ReadState();
		
		//Register custom events
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new EntityListener(), this);
		
		//Create world without protection
		if (Bukkit.getWorld(Constants.MINE_WORLD_NAME) == null) {
			Utils.CreateNormalWorld(Constants.MINE_WORLD_NAME);
		}
		
    	new BukkitRunnable() {
		    @Override
		    public void run() {
		       StateWriter.WriteState();
		    }
    	}.runTaskTimer(this, 5L, 20*60*5); //20 ticks = 1 segundo. 1200 = 1 minuto
		
	}
	
	/**
	 * Event fired when the plugin is disabled (shutdown, reloads, plugin reloads)
	 */
	@Override
	public void onDisable() { 

		
	}
	
	/**
	 * Evento disparado al ejecutar un comando
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		switch(label.toLowerCase()) {
		
			//comando para ver mi id de minecraft
			case "id":
				return onId(sender, cmd, label, args);

			//comando para ir a zona de login
			case "login":
				return onLogin(sender, cmd, label, args);
	
			//comando para ir al pueblo
			case "pueblo":
				return onPueblo(sender, cmd, label, args);
				
			//comando para ir a minar
			case "mina":
				return onMina(sender, cmd, label, args);
				
			//comando para crear mi lot default (máx 1)	
			case "creaparcela":
				return onCreaParcela(sender, cmd, label, args);
				
			//comando para ver mis lots
			case "verparcelas":
				return onVerParcelas(sender, cmd, label, args);
				
			//comando para ir a mi lot
			case "ir":
				return onIr(sender, cmd, label, args);
				
			//comando para destruir mi lot con confirmación
			case "eliminaparcela":
				return onEliminaParcela(sender, cmd, label, args);
				
			//TODO: comando para transferir mi lot a alguien sin lots
			
			//comando para ver mis tenants
			case "verinquilinos":
				return onVerInquilinos(sender, cmd, label, args);
			
			//comando para añadir tenant a mi lot
			case "nuevoinquilino":
				return onNuevoInquilino(sender, cmd, label, args);
				
			//comando para quitar tenant de mi lot
			case "eliminainquilino":
				return onEliminaInquilino(sender, cmd, label, args);
				
			//comando admin para cambiar el tamaño de la parcela
			case "cambiaparcela":
				return onCambiaParcela(sender, cmd, label, args);
				
			//comando admin para autenticar a un usuario
			case "auth":
				return onAuth(sender, cmd, label, args);
				
			//comando admin para hacer admin a otra persona
			case "makeadmin":
				return onMakeAdmin(sender, cmd, label, args);
				
			//comando admin para regenerar el mapa de la mina
			case "regeneramina":
				return onRegeneraMina(sender, cmd, label, args);
		}
		
		return false;
	}
	
	private boolean onVerInquilinos(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			//Recopilar datos básicos
			Player player = (Player) sender;
	    	UUID playerId = player.getUniqueId();
	    	User user = GameState.getPlayer(playerId);
	    	String playerName = player.getName();
	    	
	    	//Comprobar que estamos logueados
	    	if (!Utils.CheckLogin(playerId, player))
	    		return true;
	    	
	    	//Parcela en la que nos encontramos
	    	Location playerLocation = player.getLocation();
	    	Lot currentLot = GameState.getCurrentLot(playerLocation.getBlockX(), playerLocation.getBlockZ());
			
			//Comprobar que estamos en una parcela
			if (currentLot == null) {
	    		player.sendMessage(ChatColor.RED + "No te encuentras en ninguna parcela.");
	    		return true;
			}
	    	
			//Comprobar que la parcela es nuestra, o somos admins
			if(!user.getIsAdmin() && !currentLot.getOwner().getMinecraftId().equals(user.getMinecraftId())) {
	    		player.sendMessage(ChatColor.RED + "Esta parcela no te pertenece, no puedes ver la lista de inquilinos.");
	    		return true;
			}
			
			List<UUID> tenants = currentLot.getTenants();
			if (tenants.size() < 1) {
	    		player.sendMessage(ChatColor.RED + "No hay inquilinos en esta parcela.");
	    		return true;
			}
			
			player.sendMessage(ChatColor.RED + "Estos son los inquilinos de la parcela:");
			for (UUID tenant : tenants) {
				User tenantUser = GameState.getPlayer(tenant);
				if (tenantUser == null) {
					currentLot.deleteTenant(tenant);
				}else {
					player.sendMessage(tenantUser.getName()+" (en Twitch "+tenantUser.getTwitchName()+")");
				}
			}
			
		}
		return true;
	}
	
	private boolean onEliminaInquilino(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			//Recopilar datos básicos
			Player player = (Player) sender;
	    	UUID playerId = player.getUniqueId();
	    	User user = GameState.getPlayer(playerId);
	    	String playerName = player.getName();
	    	
	    	//Comprobar que estamos logueados
	    	if (!Utils.CheckLogin(playerId, player))
	    		return true;
			
			//Comprobar que hemos recibido un destinatario
			if (args.length < 1) {
	    		player.sendMessage(ChatColor.RED + "Debes especificar el nombre de la persona a eliminar.");
	    		return true;
			}
			
			//Jugador que va a ser eliminado como inquilino
			User newTenant = GameState.getPlayer(args[0]);
			
			//Comprobar que el destinatario existe
			if (newTenant == null) {
	    		player.sendMessage(ChatColor.RED + "El jugador especificado no existe.");
	    		return true;
			}
			
			//Parcela en la que nos encontramos
	    	Location playerLocation = player.getLocation();
	    	Lot currentLot = GameState.getCurrentLot(playerLocation.getBlockX(), playerLocation.getBlockZ());
			
			//Comprobar que estamos en una parcela
			if (currentLot == null) {
	    		player.sendMessage(ChatColor.RED + "No te encuentras en ninguna parcela.");
	    		return true;
			}
	    	
			//Comprobar que la parcela es nuestra, o somos admins
			if(!user.getIsAdmin() && !currentLot.getOwner().getMinecraftId().equals(user.getMinecraftId())) {
	    		player.sendMessage(ChatColor.RED + "Esta parcela no te pertenece, no puedes eliminar inquilinos.");
	    		return true;
			}
			
			//Eliminamos inquilino
			currentLot.deleteTenant(newTenant.getMinecraftId());
			
			//Avisamos por broadcast
			Bukkit.broadcastMessage(newTenant.getName()+" ya no es inquilino de "+currentLot.getName());
		}
		
		return true;
	}
	
	/**
	 * Añade un colaborador a la parcela en la que nos encontramos
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onNuevoInquilino(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			//Recopilar datos básicos
			Player player = (Player) sender;
	    	UUID playerId = player.getUniqueId();
	    	User user = GameState.getPlayer(playerId);
	    	String playerName = player.getName();
	    	
	    	//Comprobar que estamos logueados
	    	if (!Utils.CheckLogin(playerId, player))
	    		return true;
			
			//Comprobar que hemos recibido un destinatario
			if (args.length < 1) {
	    		player.sendMessage(ChatColor.RED + "Debes especificar el nombre de la persona a añadir.");
	    		return true;
			}
			
			//Jugador que va a ser añadido como inquilino
			User newTenant = GameState.getPlayer(args[0]);
			
			//Comprobar que el destinatario existe
			if (newTenant == null) {
	    		player.sendMessage(ChatColor.RED + "El jugador especificado no existe.");
	    		return true;
			}
			
			//Parcela en la que nos encontramos
	    	Location playerLocation = player.getLocation();
	    	Lot currentLot = GameState.getCurrentLot(playerLocation.getBlockX(), playerLocation.getBlockZ());
			
			//Comprobar que estamos en una parcela
			if (currentLot == null) {
	    		player.sendMessage(ChatColor.RED + "No te encuentras en ninguna parcela.");
	    		return true;
			}
	    	
			//Comprobar que la parcela es nuestra, o somos admins
			if(!user.getIsAdmin() && !currentLot.getOwner().getMinecraftId().equals(user.getMinecraftId())) {
	    		player.sendMessage(ChatColor.RED + "Esta parcela no te pertenece, no puedes añadir inquilinos.");
	    		return true;
			}
			
			//Añadimos inquilino
			currentLot.addTenant(newTenant.getMinecraftId());
			
			//Avisamos por broadcast
			Bukkit.broadcastMessage(newTenant.getName()+" ahora es inquilino de "+currentLot.getName());
		}
		
		return true;
	}
	
	/**
	 * Crea una parcela si no tenemos ninguna
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onCreaParcela(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	User user = GameState.getPlayer(playerId);
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
	    	
	    	//Si ya tenemos una parcela, no podemos tener otra
	    	int currentAmount = GameState.getPlayerLotsAmount(playerId);
	    	if (currentAmount >= Constants.MAX_LOTS && !user.getIsAdmin()) {
	    		player.sendMessage(ChatColor.RED + "Has alcanzado el límite de parcelas ("+Constants.MAX_LOTS+").");
	    		return true;
	    	}
	    	
	    	//Necesitamos que nos hayan dado un nombre para la parcela
			if (args.length < 1) {
	    		player.sendMessage(ChatColor.RED + "Debes especificar nombre para tu parcela.");
	    		return true;
			}
			
			//El nombre entre 3 y 15 chars
			if (args[0].length() < Constants.LOT_NAME_MIN || args[0].length() > Constants.LOT_NAME_MAX) {
	    		player.sendMessage(ChatColor.RED + "El nombre de la parcela debe tener entre "+Constants.LOT_NAME_MIN+" y "+Constants.LOT_NAME_MAX+" caracteres.");
	    		return true;
			}
			
			//El nombre no puede existir
			if (!GameState.lotNameIsAvailable(args[0])){
	    		player.sendMessage(ChatColor.RED + "El nombre de la parcela ya se encuentra en uso.");
	    		return true;
			}
	    	
			Location playerLocation = player.getLocation();
	    	//La parcela no puede colisionar con otras parcelas
			if (!Utils.IsLandAvailable((int)playerLocation.getX(), (int)playerLocation.getZ(), Constants.LOT_DEFAULT_SIZE, null)){
				player.sendMessage(ChatColor.RED + "No puedes crear aquí tu parcela porque colisionaría con otra.");
				return true;
			}
	    	
	    	//Creamos la parcela y lo notificamos a todo el server
			GameState.addLot(args[0], user, (int)playerLocation.getX(), (int)playerLocation.getY(), (int)playerLocation.getZ());
			
			//Mostramos scoreboard
			//GameScoreBoard.PrintScoreBoard(player, user);

		}
		return true;
	}
	
	/**
	 * Nos muestra un listado de nuestras parcelas
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onVerParcelas(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	User currentUser = GameState.getPlayer(playerId);
	    	
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
	    	
	    	List<Lot> lots = ((currentUser.getIsAdmin())?GameState.getLots():GameState.getPlayerLots(playerId));
	    	if (lots.size() < 1) {
	    		player.sendMessage(ChatColor.RED + "No se han encontrado parcelas.");
	    		return true;
	    	}
	    	player.sendMessage(ChatColor.GREEN + "Estas son las parcelas encontradas:");
	    	for(Lot lot : lots){
	    		player.sendMessage(lot.getName()+" | "+lot.getSize()+" bloques | "+lot.getOwner().getName()+" | ("+lot.getLocation().getX()+", "+lot.getLocation().getZ()+")");
	    	}
	    	player.sendMessage("Utiliza /ir <nombre de la parcela> para ir a una parcela.");

		}
		return true;
	}
	
	/**
	 * Nos permite teleportarnos a una de nuestras parcelas
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onIr(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	User currentUser = GameState.getPlayer(playerId);
	    	
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
	    	//Si somos admin podemos viajar a cualquier parcela
	    	List<Lot> lots = ((currentUser.getIsAdmin())?GameState.getLots():GameState.getPlayerLots(playerId));
	    	
	    	if (lots.size() < 1) {
	    		player.sendMessage(ChatColor.RED + "No se ha encontrado ninguna parcela.");
	    		return true;
	    	}
	    	
	    	//Necesitamos que nos hayan dado un nombre para la parcela
			if (args.length < 1) {
	    		player.sendMessage(ChatColor.RED + "Debes especificar el nombre de la parcela.");
	    		return true;
			}

	    	
	    	for(Lot lot : lots){
	    		if (lot.getName().equals(args[0])) {
	    			ResourceLocation lotLocation = lot.getLocation();
	    			player.sendMessage(ChatColor.GREEN + "Viajando a "+lot.getName()+".");
	    			Utils.TeleportToCoords(player, lotLocation.getX(), lotLocation.getY(), lotLocation.getZ());
	    			return true;
	    		}
	    	}
	    	player.sendMessage(ChatColor.RED + "No se ha encontrado la parcela.");
		}
		
		return true;
	}
	
	/**
	 * Elimina la parcela donde nos encontramos actualmente
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onEliminaParcela(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	User currentUser = GameState.getPlayer(playerId);
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
	    	
	    	Location playerLocation = player.getLocation();

	    	Lot currentLot = GameState.getCurrentLot(playerLocation.getBlockX(), playerLocation.getBlockZ());
	    	
	    	if (currentLot == null) {
	    		player.sendMessage(ChatColor.RED + "No te encuentras en ninguna parcela.");
	    		return true;
	    	}
	    	
	    	if (!currentLot.getOwner().getMinecraftId().equals(playerId) && !currentUser.getIsAdmin()) {
	    		player.sendMessage(ChatColor.RED + "No puedes destruir esta parcela, pertenece a "+currentLot.getOwner().getName()+".");
	    		return true;
	    	}
	    	
	    	GameState.deleteLot(currentLot.getName(), currentLot.getOwner());

			//Mostramos scoreboard
			//GameScoreBoard.PrintScoreBoard(player, currentUser);
		}
		return true;
	}
	
	/**
	 * Cambia el tamaño de la parcela donde nos encontramos actualmente
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onCambiaParcela(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	User currentUser = GameState.getPlayer(playerId);
	    	String playerName = player.getName();
	    	
	    	if (!Utils.CheckAdmin(playerId, playerName, player))
	    		return true;
	    	
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
	    	
	    	if (args.length < 1) {
	    		player.sendMessage(ChatColor.RED + "Debes especificar el tamaño en bloques.");
	    		return true;
	    	}
	    	
	    	Location playerLocation = player.getLocation();

	    	Lot currentLot = GameState.getCurrentLot(playerLocation.getBlockX(), playerLocation.getBlockZ());
	    	
	    	if (currentLot == null) {
	    		player.sendMessage(ChatColor.RED + "No te encuentras en ninguna parcela.");
	    		return true;
	    	}
	    	
	    	int newSize;
	    	try {
	    		newSize = Integer.parseInt(args[0]);
	    	} catch(Exception e) {
	    		newSize = Constants.LOT_DEFAULT_SIZE;
	    	}
	    	
	    	//La parcela no puede colisionar con otras parcelas
			if (!Utils.IsLandAvailable(currentLot.getLocation().getX(), currentLot.getLocation().getZ(), newSize, currentLot.getName())){
				player.sendMessage(ChatColor.RED + "No puedes ampliar tanto esta parcela porque colisionaría con otra.");
				return true;
			}
	    	
			currentLot.setSize(newSize);

	    	Bukkit.broadcastMessage(currentLot.getName()+" tiene ahora un tamaño de "+newSize+" bloques.");

			//Mostramos scoreboard
			//GameScoreBoard.PrintScoreBoard(player, currentUser);
		}
		return true;
	}
	
	/**
	 * Nos dice cuál es nuestra id
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onId(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			player.sendMessage(ChatColor.GREEN + "Tu id es "+player.getUniqueId());
		}
		return true;
	}
	
	/**
	 * Nos teleporta al área de login si somos administradores
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onLogin(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

	    	UUID playerId = player.getUniqueId();
	    	String playerName = player.getName();
	    	
	    	if (!Utils.CheckAdmin(playerId, playerName, player))
	    		return true;
	    	
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
			
			Utils.TeleportToLogin(player);
		}
		return true;
	}
	

	/**
	 * Nos teleporta al pueblo principal
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onPueblo(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
			
			Utils.TeleportToTown(player);

		}
		return true;
	}
	
	/**
	 * Nos teleporta al mapa de mina
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onMina(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	if (!Utils.CheckLogin(playerId, player)) {
	    		return true;
	    	}
			
			Utils.TeleportToMine(player);

		}
		return true;
	}
	
	/**
	 * Si somos administradores nos permite autenticar a otros usuarios
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onAuth(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	String playerName = player.getName();
	    	
	    	if (!Utils.CheckAdmin(playerId, playerName, player))
	    		return true;
			
			
			if (args.length < 1) {
	    		player.sendMessage(ChatColor.RED + "Debes especificar nombre de usuario. (/auth <playerName> <twitchName>)");
	    		return true;
			}
			

			User user = GameState.getPlayer(args[0]);
			Player target = Bukkit.getPlayer(user.getMinecraftId());
			
			//Si estábamos logueados, ya no lo estaremos, por lo que vamos al cielo
			if (user.getIsLogged()) {
				Utils.TeleportToLogin(target);
			}else {
				//Si no nos habían pasado nombre de twitch
				if (args.length < 2) {
					player.sendMessage(ChatColor.RED + "Debes especificar su nombre en twitch. (/auth <playerName> <twitchName>)");
					return true;
				}
				
				user.setTwitchName(args[1]);
				
				//Nos estamos logueando ahora, vamos al pueblo
				Utils.TeleportToTown(target);
				
	    		//Mostramos scoreboard
	    		//GameScoreBoard.PrintScoreBoard(target, user);
			}
			user.toggleIsLogged();
		}
		return true;
	}
	
	/**
	 * Si somos administradores nos permite hacer administradores a otros usuarios
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onMakeAdmin(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	String playerName = player.getName();
	    	
	    	if (!Utils.CheckAdmin(playerId, playerName, player))
	    		return true;
			
			if (args.length < 1) {
	    		player.sendMessage(ChatColor.RED + "Debes especificar nombre de usuario.");
	    		return true;
			}
			
			User user = GameState.getPlayer(args[0]);
			if (user.getIsAdmin()) {
				player.sendMessage(ChatColor.RED + args[0]+" deja de ser admin.");
			}else {
				player.sendMessage(ChatColor.RED + args[0]+" es admin desde ahora.");
			}
			
			GameState.makeAdmin(args[0]);
		}
		return true;
	}
	
	/**
	 * Si somos administradores nos permite regenerar el mapa de la mina
	 * @return Indicador de estado de ejecución del comando
	 */
	private boolean onRegeneraMina(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
	    	UUID playerId = player.getUniqueId();
	    	String playerName = player.getName();
	    	
	    	if (!Utils.CheckAdmin(playerId, playerName, player))
	    		return true;
			
			//Eliminamos la mina
			World notProtectedWorld = Bukkit.getWorld(Constants.MINE_WORLD_NAME);
			if (notProtectedWorld != null) {
				File worldFolder = notProtectedWorld.getWorldFolder();
				Bukkit.unloadWorld(notProtectedWorld, false);
				Utils.deleteWorld(worldFolder);
			}else {
				player.sendMessage(ChatColor.RED + "No se ha podido eliminar el mapa de la mina.");
				return true;
			}
			
			//Creamos la mina
			if (Bukkit.getWorld(Constants.MINE_WORLD_NAME) == null) {
				Utils.CreateNormalWorld(Constants.MINE_WORLD_NAME);
			}else {
				player.sendMessage(ChatColor.RED + "No se ha podido crear el mapa de la mina.");
				return true;
			}
			
			player.sendMessage(ChatColor.GREEN + "Mina regenerada correctamente.");
		}
		return true;
	}
	
	
	
}
