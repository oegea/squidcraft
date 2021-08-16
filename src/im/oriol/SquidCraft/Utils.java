package im.oriol.SquidCraft;

import org.bukkit.WorldCreator;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import im.oriol.SquidCraft.Resources.Model.GameState;
import im.oriol.SquidCraft.Resources.Model.Lot;
import im.oriol.SquidCraft.Resources.Model.ResourceLocation;
import net.md_5.bungee.api.ChatColor;
/**
 * Different methods that can be used from different places
 * @author Oriol
 *
 */
public class Utils {
	

	/**
	 * Creates a new normal world
	 * @param name Name for the world
	 */
	public static void CreateNormalWorld(String name) {
		WorldCreator wc = new WorldCreator(name);

		wc.environment(World.Environment.NORMAL);
		wc.type(WorldType.NORMAL);

		wc.createWorld();
	}
	
	/**
	 * Creates a new flat world
	 * @param name Name for the world
	 */
	public static void CreateFlatWorld(String name) {
		WorldCreator wc = new WorldCreator(name);

		wc.environment(World.Environment.NORMAL);
		wc.type(WorldType.FLAT);
		wc.generatorSettings("2;0;1;");

		wc.createWorld();
	}
	
	/**
	 * Nos teleporta al pueblo
	 * @param player Jugador a teleportar
	 */
	public static void TeleportToTown(Player player) {
		World world = Bukkit.getWorld(Constants.LOTS_WORLD_NAME);
		Location location = new Location(world, Constants.TOWNHALL_X, Constants.TOWNHALL_Y, Constants.TOWNHALL_Z);
		player.teleport(location);
	}
	
	/**
	 * Nos teleporta a la mina
	 * @param player Jugador a teleportar
	 */
	public static void TeleportToMine(Player player) {
		World world = Bukkit.getWorld(Constants.MINE_WORLD_NAME);
		Location location = new Location(world, Constants.SPAWN_X, Constants.SPAWN_Y+100, Constants.SPAWN_Z);
		player.teleport(location);
		player.spigot().respawn();
	}
	
	/**
	 * Nos teleporta al area de login
	 * @param player Jugador a teleportar
	 */
	public static void TeleportToLogin(Player player) {
		World world = Bukkit.getWorld(Constants.LOTS_WORLD_NAME);
		Location location = new Location(world, Constants.SPAWN_X, Constants.SPAWN_Y, Constants.SPAWN_Z);
		player.teleport(location);
	}
	
	/**
	 * Nos teleporta a un punto especifico
	 * @param player Jugador a teleportar
	 */
	public static void TeleportToCoords(Player player, int x, int y, int z) {
		World world = Bukkit.getWorld(Constants.LOTS_WORLD_NAME);
		Location location = new Location(world, x, y, z);
		player.teleport(location);
	}

	/**
	 * Comprueba si somos administradores, y muestra un mensaje de error si no lo somos
	 * @param playerId Identificador único de minecraft
	 * @param playerName Nombre del jugador
	 * @param player Objeto del jugador
	 * @return Falso en el caso de que no seamos administradores
	 */
	public static boolean CheckAdmin(UUID playerId, String playerName, Player player) {
		
		boolean isSuperAdmin = false;
		for(UUID superAdmin : Constants.SUPER_ADMIN){
			if (!playerId.equals(superAdmin)) {
				isSuperAdmin = true;
				break;
			}
		}
		
		if (!GameState.userIsAdmin(playerId) && !isSuperAdmin) {
    		player.sendMessage(ChatColor.RED + "No puedes utilizar este comando sin ser administrador.");
    		return false;
    	}
		
		return true;
	}
	
	/**
	 * Comprueba si estamos logueados, y muestra un mensaje de error si no lo estamos
	 * @param playerId Identificador único de minecraft
	 * @param player Objeto del jugador
	 * @return Falso en el caso de que no estemos logueados
	 */
	public static boolean CheckLogin(UUID playerId, Player player) {
		if (!GameState.userIsLogged(playerId)) {
    		player.sendMessage(ChatColor.RED + "No puedes utilizar este comando sin estar logueado.");
    		return false;
    	}
		
		return true;
	}
	
	/**
	 * Comprueba si en una posición existen colisiones con otras parcelas
	 * @param x Coordenada X de la posición a comprobar
	 * @param z Coordenada Z de la posición a comprobar
	 * @param newLotSize Tamaño que tendrá la nueva parcela
	 * @param ignoreLot Nombre de parcela a ignorar, utilizado para cambiar tamaños
	 * @return Verdadero en caso de no haber colisiones
	 */
	public static boolean IsLandAvailable(int x, int z, int newLotSize, String ignoreLot) {
		//Recuperamos los lots que hay
		List<Lot> lots = GameState.getLots();
		
		//Iteramos
		for(Lot lot : lots) {
			
			if (ignoreLot != null && lot.getName().equals(ignoreLot))
				continue;
			
			ResourceLocation location = lot.getLocation();
			//Si hay colisión devolvemos falso
			if (x <= location.getX()+lot.getSize()+newLotSize && 
					x >= location.getX()-lot.getSize()-newLotSize &&
					z <= location.getZ()+lot.getSize()+newLotSize &&
					z >= location.getZ()-lot.getSize()-newLotSize)
				return false;
		}
		
		
		return true;
	}
	
	/**
	 * Elimina los directorios de un mundo
	 * @param path Ruta de los ficheros
	 * @return Verdadero en caso de éxito
	 */
	public static boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
	
	
}
