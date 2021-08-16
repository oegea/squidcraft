package im.oriol.SquidCraft.Resources.Model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Specific location, without considering height.
 * @author Oriol
 */
public class ResourceLocation implements Serializable {

	private static final long serialVersionUID = -1353262119862316912L;
	//Properties
	private int X;
	private int Y;
	private int Z;
	
	public ResourceLocation(int x, int y, int z) {
		X = x;
		Y = y;
		Z = z;
	}
	
	//getters and setters
	public int getX() {
		return X;
	}
	public void setX(int x) {
		X = x;
	}
	public int getY() {
		return Y;
	}
	public void setY(int y) {
		Y = y;
	}
	public int getZ() {
		return Z;
	}
	public void setZ(int z) {
		Z = z;
	}
}
