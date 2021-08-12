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
package cellularautomata.grid3d;

import cellularautomata.grid2d.ObjectGrid2D;

public interface ObjectGrid3D<T> extends Grid3D {
	
	/**
	 * Returns the object at a given position
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the object at (x,y,z)
	 * @throws Exception 
	 */
	T getFromPosition(int x, int y, int z) throws Exception;
	
	@Override
	default ObjectGrid3D<T> subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new ObjectSubGrid3D<T, ObjectGrid3D<T>>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default ObjectGrid2D<T> crossSectionAtZ(int z) {
		return new ObjectGrid3DZCrossSection<T, ObjectGrid3D<T>>(this, z);
	}
	
	@Override
	default ObjectGrid2D<T> crossSectionAtX(int x) {
		return new ObjectGrid3DXCrossSection<T, ObjectGrid3D<T>>(this, x);
	}

}
