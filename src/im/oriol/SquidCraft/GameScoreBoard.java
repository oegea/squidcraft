package im.oriol.SquidCraft;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import im.oriol.SquidCraft.Resources.Model.GameState;
import im.oriol.SquidCraft.Resources.Model.User;

public class GameScoreBoard {
	
	public static void PrintScoreBoard(Player player, User user) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        
        if (user.getIsAdmin()) {
            Objective oName = scoreboard.registerNewObjective("rango", "rango");
            oName.setDisplayName(ChatColor.RED + "Administrador");
            oName.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        Objective o = scoreboard.registerNewObjective("Title", "dummy");
        o.setDisplaySlot(DisplaySlot.SIDEBAR); 
        
        List<String> msg = new ArrayList<String>();
        List<Score> scores = new ArrayList<Score>();
        
        if (user == null || (user != null && !user.getIsLogged())) {
        	o.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Pendiente de autorización");
        	
        	msg.add("Necesitas que un GM autorice tu cuenta.");
        	msg.add("  ");
        	msg.add("Mientras esperas te recomendamos");
        	msg.add("leer nuestras normas y ayuda en");
        	msg.add("https://www.primeplay.es");
        	msg.add(" ");
        	msg.add("Muchas gracias");
        	msg.add("         ¡Bienvenida/o!");
        }
        else if (player.getWorld().getName().equals(Constants.LOTS_WORLD_NAME)) {
        	o.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Tu personaje");
        	msg.add("Propiedad: "+ ChatColor.GREEN + GameState.getPlayerLotsSizeSum(user.getMinecraftId())+" bloques");
        	msg.add("Dinero: " + ChatColor.GREEN + user.getMoney()+ " rupias");
        	msg.add("  ");
        	msg.add("Lee las normas y obtén");
        	msg.add("ayuda en nuestra web:");
        	msg.add(" ");
        	msg.add(ChatColor.BOLD + "https://primeplay.es");
        }else {
        	o.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Bienvenida/o al mapa libre");
        	msg.add("En este mapa puedes obtener recursos,");
        	msg.add("minar, talar, y construir dónde quieras.");
        	msg.add("  ");
        	msg.add("No destruyas la plataforma de llegada.");
        	msg.add(" ");
        	msg.add("La mina se regenera frecuentemente");
        	msg.add("no construyas ni guardes ítems");
        	msg.add("que quieras conservar.");
        }
        
        for (String message: msg) {
        	scores.add(mes(o, message));
        }
        
        int i = 0;
        for (Score score : scores) {
        	score.setScore(scores.size()-i);
        	i++;
        }

        player.setScoreboard(scoreboard);
	}
	
	public static Score mes(Objective objective, String message) {
		return objective.getScore(message); 
	}
}
