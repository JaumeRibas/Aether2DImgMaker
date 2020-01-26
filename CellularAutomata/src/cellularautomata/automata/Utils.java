/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Utils {
	
	public static Object deserializeFromFile(String pathName) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(pathName));
		Object obj = in.readObject();
		in.close();
		return obj;
	}
	
	public static void serializeToFile(Object obj, String path, String name) throws FileNotFoundException, IOException {
		String pathName = path + "/" + name;
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(pathName));
		out.writeObject(obj);
		out.flush();
		out.close();
	}

}
