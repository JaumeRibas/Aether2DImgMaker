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
package cellularautomata.model3d;

import java.util.Iterator;

import cellularautomata.model.ObjectModel;
import cellularautomata.model2d.ObjectModel2D;

public interface ObjectModel3D<T> extends Model3D, ObjectModel<T> {
	
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
	default ObjectModel3D<T> subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new ObjectSubModel3D<T, ObjectModel3D<T>>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default ObjectModel2D<T> crossSectionAtX(int x) {
		return new ObjectModel3DXCrossSection<T, ObjectModel3D<T>>(this, x);
	}
	
	@Override
	default ObjectModel2D<T> crossSectionAtY(int y) {
		return new ObjectModel3DYCrossSection<T, ObjectModel3D<T>>(this, y);
	}
	
	@Override
	default ObjectModel2D<T> crossSectionAtZ(int z) {
		return new ObjectModel3DZCrossSection<T, ObjectModel3D<T>>(this, z);
	}
	
	@Override
	default ObjectModel2D<T> diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new ObjectModel3DXYDiagonalCrossSection<T, ObjectModel3D<T>>(this, yOffsetFromX);
	}
	
	@Override
	default ObjectModel2D<T> diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new ObjectModel3DXZDiagonalCrossSection<T, ObjectModel3D<T>>(this, zOffsetFromX);
	}
	
	@Override
	default ObjectModel2D<T> diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new ObjectModel3DYZDiagonalCrossSection<T, ObjectModel3D<T>>(this, zOffsetFromY);
	}

	@Override
	default Iterator<T> iterator() {
		return new ObjectModel3DIterator<T>(this);
	}

}
