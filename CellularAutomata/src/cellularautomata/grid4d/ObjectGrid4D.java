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

import cellularautomata.grid3d.ObjectGrid3D;

public interface ObjectGrid4D<T> extends Grid4D {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-axis 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (w,x,y,z)
	 * @throws Exception 
	 */
	T getFromPosition(int w, int x, int y, int z) throws Exception;
	
	@Override
	default ObjectGrid4D<T> subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new ObjectSubGrid4D<T, ObjectGrid4D<T>>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default ObjectGrid3D<T> crossSectionAtW(int w) {
		return new ObjectGrid4DWCrossSection<T, ObjectGrid4D<T>>(this, w);
	}
	
	@Override
	default ObjectGrid3D<T> crossSectionAtX(int x) {
		return new ObjectGrid4DXCrossSection<T, ObjectGrid4D<T>>(this, x);
	}
	
	@Override
	default ObjectGrid3D<T> crossSectionAtY(int y) {
		return new ObjectGrid4DYCrossSection<T, ObjectGrid4D<T>>(this, y);
	}
	
	@Override
	default ObjectGrid3D<T> crossSectionAtZ(int z) {
		return new ObjectGrid4DZCrossSection<T, ObjectGrid4D<T>>(this, z);
	}

}
