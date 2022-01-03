/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package cellularautomata.automata;

import java.io.IOException;
import java.util.HashMap;

import cellularautomata.Utils;

public class SerializedHashMapEditor {

	public static void main(String[] args) {

		try {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> properties = 
					(HashMap<String, Object>) Utils.deserializeFromFile("D:/data/Aether3D/-1073741823/backups/IntAether3DSwap_6513/properties.ser");
			properties.put("maxGridBlockSize", Long.parseLong("8589934592")/2);
			Utils.serializeToFile(properties, "D:/data/Aether3D/-1073741823/backupResized/IntAether3DSwap_6513/", "properties.ser");
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

	}

}
