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
package cellularautomata.grid3d;

import cellularautomata.grid.Grid;
import cellularautomata.grid2d.Grid2D;

public interface Grid3D extends Grid {
	
	/**
	 * Returns the smallest x-coordinate of the grid
	 * 
	 * @return the smallest x
	 */
	int getMinX();
	
	/**
	 * Returns the smallest x-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest x
	 */
	default int getMinXAtY(int y) {
		return getMinX();
	}
	
	/**
	 * Returns the smallest x-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	default int getMinXAtZ(int z) {
		return getMinX();
	}
	
	/**
	 * Returns the smallest x-coordinate of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getMinY()}, 
	 * {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the smallest x
	 */
	default int getMinX(int y, int z) {
		return getMinX();
	}
	
	/**
	 * Returns the largest x-coordinate of the grid
	 * 
	 * @return the largest x
	 */
	int getMaxX();
	
	/**
	 * Returns the largest x-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest x
	 */
	default int getMaxXAtY(int y) {
		return getMaxX();
	}
	
	/**
	 * Returns the largest x-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	default int getMaxXAtZ(int z) {
		return getMaxX();
	}
	
	/**
	 * Returns the largest x-coordinate of the grid at (y,z).<br/>
	 * It's not defined to call this method on a 'y' and 'z' coordinates outside the bounds of [{@link #getMinY()}, 
	 * {@link #getMaxY()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param y the y-coordinate
	 * @param z the z-coordinate
	 * @return the largest x
	 */
	default int getMaxX(int y, int z) {
		return getMaxX();
	}
	
	/**
	 * Returns the smallest y-coordinate of the grid
	 * 
	 * @return the smallest y
	 */
	int getMinY();
	
	/**
	 * Returns the smallest y-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest y
	 */
	default int getMinYAtX(int x) {
		return getMinY();
	}
	
	/**
	 * Returns the smallest y-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	default int getMinYAtZ(int z) {
		return getMinY();
	}
	
	/**
	 * Returns the smallest y-coordinate of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the smallest y
	 */
	default int getMinY(int x, int z) {
		return getMinY();
	}

	/**
	 * Returns the largest y-coordinate of the grid
	 * 
	 * @return the largest y
	 */
	int getMaxY();
	
	/**
	 * Returns the largest y-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest y
	 */
	default int getMaxYAtX(int x) {
		return getMaxY();
	}
	
	/**
	 * Returns the largest y-coordinate of the grid at z.<br/>
	 * It's not defined to call this method on a z-coordinate smaller than {@link #getMinZ()} 
	 * or bigger than {@link #getMaxZ()}
	 * 
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	default int getMaxYAtZ(int z) {
		return getMaxY();
	}
	
	/**
	 * Returns the largest y-coordinate of the grid at (x,z).<br/>
	 * It's not defined to call this method on a 'x' and 'z' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinZ()}, {@link #getMaxZ()}] 
	 * 
	 * @param x the x-coordinate
	 * @param z the z-coordinate
	 * @return the largest y
	 */
	default int getMaxY(int x, int z) {
		return getMaxY();
	}
	
	/**
	 * Returns the smallest z-coordinate of the grid
	 * 
	 * @return the smallest z
	 */
	int getMinZ();
	
	/**
	 * Returns the smallest z-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the smallest z
	 */
	default int getMinZAtX(int x) {
		return getMinZ();
	}
	
	/**
	 * Returns the smallest z-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	default int getMinZAtY(int y) {
		return getMinZ();
	}
	
	/**
	 * Returns the smallest z-coordinate of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinY()}, {@link #getMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the smallest z
	 */
	default int getMinZ(int x, int y) {
		return getMinZ();
	}
	
	/**
	 * Returns the largest z-coordinate of the grid
	 * 
	 * @return the largest z
	 */
	int getMaxZ();
	
	/**
	 * Returns the largest z-coordinate of the grid at x.<br/>
	 * It's not defined to call this method on a x-coordinate smaller than {@link #getMinX()} 
	 * or bigger than {@link #getMaxX()}
	 * 
	 * @param x the x-coordinate
	 * @return the largest z
	 */
	default int getMaxZAtX(int x) {
		return getMaxZ();
	}
	
	/**
	 * Returns the largest z-coordinate of the grid at y.<br/>
	 * It's not defined to call this method on a y-coordinate smaller than {@link #getMinY()} 
	 * or bigger than {@link #getMaxY()}
	 * 
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	default int getMaxZAtY(int y) {
		return getMaxZ();
	}
	
	/**
	 * Returns the largest z-coordinate of the grid at (x,y).<br/>
	 * It's not defined to call this method on a 'x' and 'y' coordinates outside the bounds of [{@link #getMinX()}, 
	 * {@link #getMaxX()}] and [{@link #getMinY()}, {@link #getMaxY()}] 
	 * 
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the largest z
	 */
	default int getMaxZ(int x, int y) {
		return getMaxZ();
	}
	
	/**
	 * Returns a decorated {@link Grid2D} with the passed bounds.
	 * 
	 * @param minX
	 * @param maxX
	 * @param minY
	 * @param maxY
	 * @param minZ
	 * @param maxZ
	 * @return a {@link Grid2D} decorating the current grid 
	 */
	default Grid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new SubGrid3D<Grid3D>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	default Grid2D crossSectionAtZ(int z) {
		return new Grid3DZCrossSection<Grid3D>(this, z);
	}
	
	default Grid2D crossSectionAtX(int x) {
		return new Grid3DXCrossSection<Grid3D>(this, x);
	}

}
