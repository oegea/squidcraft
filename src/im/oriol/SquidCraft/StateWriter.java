package im.oriol.SquidCraft;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import im.oriol.SquidCraft.Resources.Model.GameState;
import im.oriol.SquidCraft.Resources.Model.Lot;
import im.oriol.SquidCraft.Resources.Model.User;

public final class StateWriter {  
	
	public static void WriteObjectToFile(Object serObj, String filePath) {
    	 
        try {
 
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(serObj);
            objectOut.close();
            fileOut.close();
            System.out.println("The Object  was succesfully written to a file");
 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
	public static void ReadLots() {
    	try {
    		FileInputStream fi = new FileInputStream(new File("plugins/lots.state"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            
            List<Lot> lots = (List<Lot>) oi.readObject();
            GameState.setLots(lots);
            oi.close();
            fi.close();
    	} catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
	public static void ReadUsers() {
    	try {
    		FileInputStream fi = new FileInputStream(new File("plugins/users.state"));
            ObjectInputStream oi = new ObjectInputStream(fi);
            
            List<User> users = (List<User>) oi.readObject();
            GameState.setUsers(users);
            oi.close();
            fi.close();
    	} catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	public static void ReadState() {
    	ReadLots();
    	ReadUsers();
    }
    
	public static void WriteState() {
    	WriteObjectToFile(GameState.getUsers(), "plugins/users.state");
    	WriteObjectToFile(GameState.getLots(), "plugins/lots.state");
    }

}