package im.oriol.SquidCraft.Resources.Model;

import java.io.Serializable;
import java.util.UUID;

import org.bukkit.entity.Player;

import im.oriol.SquidCraft.Constants;
import im.oriol.SquidCraft.GameScoreBoard;

/**
 * Player data
 * @author Oriol
 */
public class User implements Serializable {


	private static final long serialVersionUID = -8066837451845452322L;
	//Properties
	private UUID Id;
	private UUID MinecraftId;
	private boolean IsAdmin;
	private boolean IsLogged;
	private String Name;
	private String TwitchName;
	private int Money;
	
	/**
	 * CTOR
	 * @param minecraftId Identificador de minecraft del player
	 * @param name Nombre del player
	 */
	public User(UUID minecraftId, String name) {
		Id = UUID.randomUUID();
		MinecraftId = minecraftId;
		IsAdmin = false;
		IsLogged = false;
		Name = name;
		TwitchName = "";
		Money = 0;
	}
	
	/**
	 * Recupera el dinero del jugador
	 * @return Dinero del jugador
	 */
	public int getMoney() {
		return Money;
	}
	
	/**
	 * Define el dinero del jugador
	 * @param money Nuevo dinero del jugador
	 */
	public void setMoney(Player player, int money) {
		Money = money;
		
		//Mostramos scoreboard
		GameScoreBoard.PrintScoreBoard(player, this);
	}
	
	/**
	 * Recupera el identificador de minecraft
	 * @return Identificador de minecraft
	 */
	public UUID getMinecraftId() {
		return MinecraftId;
	}
	
	/**
	 * Recupera el nombre de usuario
	 * @return Nombre de usuario
	 */
	public String getName() {
		return Name;
	}
	
	/**
	 * Recupera el nombre de Twitch
	 * @return Nombre de twitch
	 */
	public String getTwitchName() {
		return TwitchName;
	}
	
	/**
	 * Define el nombre de Twitch
	 * @param twitchName Nombre de Twitch
	 */
	public void setTwitchName(String twitchName) {
		TwitchName = twitchName;
	}
	
	/**
	 * Comprueba si el usuario se encuentra logueado y autorizado
	 * @return Verdadero en caso de estar logueado
	 */
	public boolean getIsLogged() {
		return IsLogged;
	}
	
	/**
	 * Alterna el valor de logueo entre verdadero y falso
	 */
	public void toggleIsLogged() {
		IsLogged = !IsLogged;
	}
	
	/**
	 * Recupera si el usuario es administrador
	 * @return Verdadero si es administrador
	 */
	public boolean getIsAdmin() {
		
		boolean isSuperAdmin = false;
		for(UUID superAdmin : Constants.SUPER_ADMIN){
			if (MinecraftId.equals(superAdmin)) {
				isSuperAdmin = true;
				break;
			}
		}
		
		return (IsAdmin || isSuperAdmin);
	}
	
	/**
	 * Alterna si el usuario es administrador entre verdadero y falso
	 */
	public void toggleIsAdmin() {
		IsAdmin = !IsAdmin;
	}

}
