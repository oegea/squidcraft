package im.oriol.SquidCraft;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Class to manage constants
 * @author Oriol
 */
public class Constants {

	public static final String LOTS_WORLD_NAME = "world";
	public static final String MINE_WORLD_NAME = "mine";

	public static final int SPAWN_X = 706;
	public static final int SPAWN_Y = 243;
	public static final int SPAWN_Z = -1176;
	
	public static final int TOWNHALL_X = 642;
	public static final int TOWNHALL_Y = 65;
	public static final int TOWNHALL_Z = -1225;
	
	public static final List<UUID> SUPER_ADMIN = Arrays.asList(
			UUID.fromString("1fcc38b7-68da-396a-b245-b2b80781f09f"),
			UUID.fromString("660626d9-2870-3301-9e3b-7e6b5ecd9425")
	);
	
	public static final int MAX_LOTS = 1;
	public static final int LOT_DEFAULT_SIZE = 32;
	public static final int LOT_NAME_MIN = 3;
	public static final int LOT_NAME_MAX = 25;
}
