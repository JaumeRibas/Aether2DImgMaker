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

import cellularautomata.grid2d.ObjectGrid2D;
import cellularautomata.grid3d.ObjectGrid3D;

public interface ObjectGrid4D<T> extends Grid4D {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-coordinate 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (w,x,y,z)
	 * @throws Exception 
	 */
	T getFromPosition(int w, int x, int y, int z) throws Exception;
	
	@Override
	default ObjectGrid2D<T> crossSectionAtYZ(int y, int z) {
		return new ObjectGrid4DYZCrossSection<T, ObjectGrid4D<T>>(this, y, z);
	}
	
	@Override
	default ObjectGrid4D<T> subGrid(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new ObjectSubGrid4D<T, ObjectGrid4D<T>>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default ObjectGrid3D<T> crossSectionAtZ(int z) {
		return new ObjectGrid4DZCrossSection<T, ObjectGrid4D<T>>(this, z);
	}

}
