/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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
package cellularautomata.grid4d;

import cellularautomata.grid.Grid;
import cellularautomata.grid2d.Grid2D;
import cellularautomata.grid3d.Grid3D;

public interface Grid4D extends Grid {
	
	/**
	 * Returns the smallest w-coordinate
	 * 
	 * @return the smallest w
	 */
	int getMinW();
	
	/**
	 * Returns the largest w-coordinate
	 * 
	 * @return the largest w
	 */
	int getMaxW();
	
	/**
	 * Returns the smallest w-coordinate of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest w
	 */
	default int getMinW(int x, int y, int z) {
		return getMinW();
	}
	
	/**
	 * Returns the largest w-coordinate of the grid at (x,y,z).<br/>
	 * It's not defined to call this method on a 'x', 'y' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}], [{@link #getMinY()}, {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest w
	 */
	default int getMaxW(int x, int y, int z) {
		return getMaxW();
	}
	
	/**
	 * Returns the smallest x-coordinate
	 * 
	 * @return the smallest x
	 */
	int getMinX();
	
	/**
	 * Returns the largest x-coordinate
	 * 
	 * @return the largest x
	 */
	int getMaxX();
	
	/**
	 * Returns the smallest y-coordinate
	 * 
	 * @return the smallest y
	 */
	int getMinY();
	
	/**
	 * Returns the largest y-coordinate
	 * 
	 * @return the largest y
	 */
	int getMaxY();
	
	/**
	 * Returns the smallest z-coordinate
	 * 
	 * @return the smallest z
	 */
	int getMinZ();
	
	/**
	 * Returns the largest z-coordinate
	 * 
	 * @return the largest z
	 */
	int getMaxZ();

	Grid2D crossSectionAtYZ(int y, int z);

	Grid4D subGrid(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ);

	Grid3D projected3DEdgeMaxW();
}
