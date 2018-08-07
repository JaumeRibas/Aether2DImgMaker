package cellularautomata.automata;

import java.io.IOException;
import java.util.HashMap;

public class SerializedHashMapEditor {

	public static void main(String[] args) {

		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> properties = 
					(HashMap<String, Object>) Utils.deserializeFromFile("D:/data/Aether3D/-1073741823/backups/IntAether3DSwap_6513/properties.ser");
			properties.put("maxGridBlockSize", Long.parseLong("8589934592")/2);
			Utils.serializeToFile(properties, "D:/data/Aether3D/-1073741823/backupResized/IntAether3DSwap_6513/", "properties.ser");
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
