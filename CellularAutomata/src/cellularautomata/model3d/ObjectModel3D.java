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

import cellularautomata.Coordinates;
import cellularautomata.model.ObjectModel;
import cellularautomata.model2d.ObjectModel2D;

public interface ObjectModel3D<Object_Type> extends Model3D, ObjectModel<Object_Type> {
	
	/**
	 * Returns the object at a given position
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the object at (x,y,z)
	 * @throws Exception 
	 */
	Object_Type getFromPosition(int x, int y, int z) throws Exception;

	@Override
	default Object_Type getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2));
	}
	
	@Override
	default ObjectModel3D<Object_Type> subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new ObjectSubModel3D<ObjectModel3D<Object_Type>, Object_Type>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default ObjectModel2D<Object_Type> crossSectionAtX(int x) {
		return new ObjectModel3DXCrossSection<ObjectModel3D<Object_Type>, Object_Type>(this, x);
	}
	
	@Override
	default ObjectModel2D<Object_Type> crossSectionAtY(int y) {
		return new ObjectModel3DYCrossSection<ObjectModel3D<Object_Type>, Object_Type>(this, y);
	}
	
	@Override
	default ObjectModel2D<Object_Type> crossSectionAtZ(int z) {
		return new ObjectModel3DZCrossSection<ObjectModel3D<Object_Type>, Object_Type>(this, z);
	}
	
	@Override
	default ObjectModel2D<Object_Type> diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new ObjectModel3DXYDiagonalCrossSection<ObjectModel3D<Object_Type>, Object_Type>(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default ObjectModel2D<Object_Type> diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new ObjectModel3DXZDiagonalCrossSection<ObjectModel3D<Object_Type>, Object_Type>(this, zOffsetFromX);
	}
	
	@Override
	default ObjectModel2D<Object_Type> diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new ObjectModel3DYZDiagonalCrossSection<ObjectModel3D<Object_Type>, Object_Type>(this, zOffsetFromY);
	}

	@Override
	default Iterator<Object_Type> iterator() {
		return new ObjectModel3DIterator<Object_Type>(this);
	}

}
