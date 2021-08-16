package im.oriol.SquidCraft.Resources.Model;
import java.util.*;

import org.bukkit.Bukkit;

import im.oriol.SquidCraft.Constants;

public final class GameState {
	//properties
	private static List<User> Users = new ArrayList<User>();
	private static List<Lot> Lots = new ArrayList<Lot>();
	
	public static void setLots(List<Lot> lots) {
		Lots = lots;
	}
	
	public static void setUsers(List<User> users) {
		Users = users;
	}
	
	public static List<User> getUsers(){
		return Users;
	}
	
	public static void addUser(UUID minecraftId, String name) {
		//Si ya estamos en la lista no hacemos nada
		if (playerExists(minecraftId))
			return;
		
		User user = new User(minecraftId, name);
		Users.add(user);
		
	}
	
	public static boolean playerExists(UUID minecraftId) {
		for(User user : Users){
			if (user.getMinecraftId().equals(minecraftId))
				return true;
		}
		
		return false;
	}
	
	public static boolean playerExists(String name) {
		for(User user : Users){
			if (user.getName().equals(name))
				return true;
		}
		
		return false;
	}
	
	public static User getPlayer(UUID minecraftId) {
		for(User user : Users){
			if (user.getMinecraftId().equals(minecraftId))
				return user;
		}
		
		return null;
	}
	
	public static User getPlayer(String name) {
		for(User user : Users){
			if (user.getName().equals(name))
				return user;
		}
		
		return null;
	}
	
	
	public static boolean userIsLogged(UUID minecraftId) {
		if (!playerExists(minecraftId)) {
			return false;
		}
		
		User user = getPlayer(minecraftId);
		if (user.getIsLogged())
			return true;
		
		
		return false;
	}
	
	public static boolean userIsAdmin(UUID minecraftId) {
		if (!playerExists(minecraftId)) {
			return false;
		}
		
		User user = getPlayer(minecraftId);
		if (user.getIsAdmin())
			return true;
		
		
		return false;
	}
	
	public static boolean authenticateUser(String name) {
		if (!playerExists(name)) {
			return false;
		}
		
		User user = getPlayer(name);
		user.toggleIsLogged();
		
		return true;
	}
	
	public static boolean makeAdmin(String name) {
		if (!playerExists(name)) {
			return false;
		}
		
		User user = getPlayer(name);
		user.toggleIsAdmin();
		
		return true;
	}
	
	/**
	 * Retorna el número de bloques (suma de todos los bloques) que tiene un jugador
	 * @param minecraftId Id del jugador
	 * @return Número de bloques
	 */
	public static int getPlayerLotsSizeSum(UUID minecraftId) {
		int sum = 0;
		for(Lot lot : Lots){
			if (lot.getOwner().getMinecraftId().equals(minecraftId))
				sum += lot.getSize();
		}
		
		return sum;
	}
	
	public static int getPlayerLotsAmount(UUID minecraftId) {
		int amount = 0;
		
		for(Lot lot : Lots){
			if (lot.getOwner().getMinecraftId().equals(minecraftId))
				amount++;
		}
		
		return amount;
	}
	
	public static List<Lot> getPlayerLots(UUID minecraftId) {
		List<Lot> lots = new ArrayList<Lot>();
		
		for(Lot lot : Lots){
			if (lot.getOwner().getMinecraftId().equals(minecraftId))
				lots.add(lot);
		}
		
		return lots;
	}
	
	public static List<Lot> getLots(){
		return Lots;
	}
	
	public static void addLot(String name, User owner, int x, int y, int z) {
		Lot lot = new Lot(name, owner, Constants.LOT_DEFAULT_SIZE, x, y, z);
		Lots.add(lot);
		Bukkit.broadcastMessage(owner.getName()+" funda la parcela de '"+name+"' en "+x+","+z);
	}
	
	public static Lot getCurrentLot(int x, int z) {
		for(Lot lot : Lots){
			int size = lot.getSize();
			ResourceLocation lotLocation = lot.getLocation();
			int lotX = lotLocation.getX();
			int lotZ = lotLocation.getZ();
			
			//Si el x está dentro de los límites
			if (x >= lotX-size && x <= lotX+size && z >= lotZ-size && z <= lotZ+size) {
				return lot;
			}
		}
		return null;
	}
	
	public static boolean lotNameIsAvailable(String name) {
		for(Lot lot : Lots){
			if (lot.getName().equals(name))
				return false;
		}
		return true;
	}
	
	public static void deleteLot(String name, User owner) {
		Lots.removeIf(l -> (l.getName().equals(name)));
		Bukkit.broadcastMessage(owner.getName()+" ha renunciado a la parcela de '"+name+"'.");
	}
}
