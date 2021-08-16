package im.oriol.SquidCraft.Resources.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

/**
 * Lot information
 * @author Oriol
 */
public class Lot implements Serializable {


	private static final long serialVersionUID = 3735357576781560865L;
	//Properties
	private String Name;
	private User Owner;
	private List<UUID> Tenants;
	private ResourceLocation Location;
	private int Size;
	
	public Lot(String name, User owner, int size, int x, int y, int z) {
		Name = name;
		Owner = owner;
		//Tenants = new List<User>();
		Location = new ResourceLocation(x, y, z);
		Size = size;
		Tenants = new ArrayList<UUID>();
	}
	
	/**
	 * Elimina un tenant para que juegue en la parcela
	 * @param id Identificador del usuario
	 */
	public void deleteTenant(UUID id) {
		if (Tenants == null) {
			Tenants = new ArrayList<UUID>();
		}
		Tenants.removeIf(l -> l.equals(id));
	}
	
	/**
	 * Añade un tenant para que juegue en la parcela
	 * @param id Identificador del usuario
	 */
	public void addTenant(UUID id) {
		if (Tenants == null) {
			Tenants = new ArrayList<UUID>();
		}
		Tenants.removeIf(l -> l.equals(id));
		Tenants.add(id);
	}
	
	//getters and setters
	public List<UUID> getTenants(){
		if (Tenants == null) {
			Tenants = new ArrayList<UUID>();
		}
		return Tenants;
	}
	
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public User getOwner() {
		return Owner;
	}

	public void setOwner(User owner) {
		Owner = owner;
	}

	/*public List<User> getTenants() {
		return Tenants;
	}

	public void setTenants(List<User> tenants) {
		Tenants = tenants;
	}*/

	public ResourceLocation getLocation() {
		return Location;
	}

	public void setLocation(ResourceLocation location) {
		Location = location;
	}

	public int getSize() {
		return Size;
	}

	public void setSize(int size) {
		Size = size;
	}


	//toString
	@Override
	public String toString() {
		return "Lot [Location=" + Location + ", Name=" + Name + ", Owner=" + Owner + ", Size=" + Size + "]";
	}


}
