/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.model;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid.GridRegion;

public interface Model extends GridRegion {	
		
	/**
	 * Computes the next step of the model and returns whether
	 * or not the state changed. 
	 *  
	 * @return true if the state changed or false otherwise
	 * @throws Exception 
	 */
	boolean nextStep() throws Exception;
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	long getStep();
	
	/**
	 * Return the model's name in a format that can be used in file names
	 * 
	 * @return the name
	 */
	String getName();
	
	/**
	 * Return the model's name and configuration as a folder and subfolder(s) path.
	 * 
	 * @return the path
	 */
	String getSubfolderPath();

	/**
	 * Backs up the state of the model to a file or folder for future restoration.<br/>
	 * The state can be restored passing a path to this file or folder to the constructor.<br/>
	 * 
	 * @param backupPath the storage location where the backup will be stored
	 * @param backupName the name of the backup
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException;
}
