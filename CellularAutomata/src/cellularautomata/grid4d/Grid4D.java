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
package cellularautomata.grid4d;

import cellularautomata.grid.Grid;
import cellularautomata.grid3d.Grid3D;

public interface Grid4D extends Grid {
	
	/**
	 * Returns the smallest w-coordinate
	 * 
	 * @return the smallest w
	 */
	int getMinW();

	default int getMinWAtZ(int z) {
		return getMinW();
	}

	default int getMinWAtXZ(int x, int z) {
		return getMinW();
	}

	default int getMinWAtYZ(int y, int z) {
		return getMinW();
	}
	
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
	 * Returns the largest w-coordinate
	 * 
	 * @return the largest w
	 */
	int getMaxW();

	default int getMaxWAtZ(int z) {
		return getMaxW();
	}

	default int getMaxWAtXZ(int x, int z) {
		return getMaxW();
	}

	default int getMaxWAtYZ(int y, int z) {
		return getMaxW();
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
	
	default int getMinXAtW(int w) {
		return getMinX();
	}

	default int getMinXAtZ(int z) {
		return getMinX();
	}

	default int getMinXAtWZ(int w, int z) {
		return getMinX();
	}

	default int getMinXAtYZ(int y, int z) {
		return getMinX();
	}

	default int getMinX(int w, int y, int z) {
		return getMinX();
	}
	
	/**
	 * Returns the largest x-coordinate
	 * 
	 * @return the largest x
	 */
	int getMaxX();
	
	default int getMaxXAtW(int w) {
		return getMaxX();
	}

	default int getMaxXAtZ(int z) {
		return getMaxX();
	}

	default int getMaxXAtWZ(int w, int z) {
		return getMaxX();
	}

	default int getMaxXAtYZ(int y, int z) {
		return getMaxX();
	}

	default int getMaxX(int w, int y, int z) {
		return getMaxX();
	}
	
	/**
	 * Returns the smallest y-coordinate
	 * 
	 * @return the smallest y
	 */
	int getMinY();
	
	default int getMinYAtW(int w) {
		return getMinY();
	}

	default int getMinYAtZ(int z) {
		return getMinY();
	}
	
	default int getMinYAtWX(int w, int x) {
		return getMinY();
	}

	default int getMinYAtWZ(int w, int z) {
		return getMinY();
	}

	default int getMinYAtXZ(int x, int z) {
		return getMinY();
	}

	default int getMinY(int w, int x, int z) {
		return getMinY();
	}
	
	/**
	 * Returns the largest y-coordinate
	 * 
	 * @return the largest y
	 */
	int getMaxY();
	
	default int getMaxYAtW(int w) {
		return getMaxY();
	}
	
	default int getMaxYAtWX(int w, int x) {
		return getMaxY();
	}

	default int getMaxYAtWZ(int w, int z) {
		return getMaxY();
	}

	default int getMaxYAtZ(int z) {
		return getMaxY();
	}

	default int getMaxYAtXZ(int x, int z) {
		return getMaxY();
	}

	default int getMaxY(int w, int x, int z) {
		return getMaxY();
	}
	
	/**
	 * Returns the smallest z-coordinate
	 * 
	 * @return the smallest z
	 */
	int getMinZ();
	
	default int getMinZAtW(int w) {
		return getMinZ();
	}

	default int getMinZ(int w, int x, int y) {
		return getMinZ();
	}
	
	/**
	 * Returns the largest z-coordinate
	 * 
	 * @return the largest z
	 */
	int getMaxZ();
	
	default int getMaxZAtW(int w) {
		return getMaxZ();
	}

	default int getMaxZ(int w, int x, int y) {
		return getMaxZ();
	}

	default int getMinWAtX(int x) { return getMinW(); }

	default int getMaxWAtX(int x) { return getMaxW(); }

	default int getMinWAtY(int y) { return getMinW(); }

	default int getMaxWAtY(int y) { return getMaxW(); }

	default int getMinWAtXY(int x, int y) { return getMinW(); }

	default int getMaxWAtXY(int x, int y) { return getMaxW(); }

	default int getMinXAtY(int y) { return getMinX(); }

	default int getMaxXAtY(int y) { return getMaxX(); }

	default int getMinXAtWY(int w, int y) { return getMinX(); }

	default int getMaxXAtWY(int w, int y) { return getMaxX(); }

	default int getMinYAtX(int x) { return getMinY(); }

	default int getMaxYAtX(int x) { return getMaxY(); }

	default int getMinZAtX(int x) { return getMinZ(); }

	default int getMaxZAtX(int x) { return getMaxZ(); }

	default int getMinZAtY(int y) { return getMinZ(); }

	default int getMaxZAtY(int y) { return getMaxZ(); }

	default int getMinZAtWX(int w, int x) { return getMinZ(); }

	default int getMaxZAtWX(int w, int x) { return getMaxZ(); }

	default int getMinZAtWY(int w, int y) { return getMinZ(); }

	default int getMaxZAtWY(int w, int y) { return getMaxZ(); }

	default int getMinZAtXY(int x, int y) { return getMinZ(); }

	default int getMaxZAtXY(int x, int y) { return getMaxZ(); }

	default Grid4D subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new SubGrid4D<Grid4D>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	default Grid3D crossSectionAtW(int w) {
		return new Grid4DWCrossSection<Grid4D>(this, w);
	}
	
	default Grid3D crossSectionAtX(int x) {
		return new Grid4DXCrossSection<Grid4D>(this, x);
	}
	
	default Grid3D crossSectionAtY(int y) {
		return new Grid4DYCrossSection<Grid4D>(this, y);
	}
	
	/**
	 * <p>Returns a 3D grid decorating the current one to show only the positions whose z coordinate is equal to the passed value.</p>
	 * <p>It is important to note that due to the use of the names '<strong>w</strong>', '<strong>x</strong>', '<strong>y</strong>' and '<strong>z</strong>' 
	 * for the 4D coordinates and '<strong>x</strong>', '<strong>y</strong>' and '<strong>z</strong>' for the 3D coordinates, 
	 * there is a mismatch between the names of the coordinates in the 3D cross section and those of the same coordinates in the source 4D grid. 
	 * The source's '<strong>w</strong>' coordinate becomes the cross section's '<strong>x</strong>' coordinate, the '<strong>x</strong>' becomes the 
	 * '<strong>y</strong>' and the '<strong>y</strong>' becomes the '<strong>z</strong>'.</p>
	 * 
	 * @param z
	 * @return
	 */
	default Grid3D crossSectionAtZ(int z) {
		return new Grid4DZCrossSection<Grid4D>(this, z);
	}
	
	default Grid3D diagonalCrossSectionOnWX(int xOffsetFromW) {
		return new Grid4DWXDiagonalCrossSection<Grid4D>(this, xOffsetFromW);
	}
}
