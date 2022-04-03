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
package cellularautomata.model4d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.model.ObjectModel;
import cellularautomata.model3d.ObjectModel3D;

public interface ObjectModel4D<Object_Type> extends Model4D, ObjectModel<Object_Type> {
	
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
	Object_Type getFromPosition(int w, int x, int y, int z) throws Exception;

	@Override
	default Object_Type getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3));
	}
	
	@Override
	default ObjectModel4D<Object_Type> subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new ObjectSubModel4D<ObjectModel4D<Object_Type>, Object_Type>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default ObjectModel3D<Object_Type> crossSectionAtW(int w) {
		return new ObjectModel4DWCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, w);
	}
	
	@Override
	default ObjectModel3D<Object_Type> crossSectionAtX(int x) {
		return new ObjectModel4DXCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, x);
	}
	
	@Override
	default ObjectModel3D<Object_Type> crossSectionAtY(int y) {
		return new ObjectModel4DYCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, y);
	}
	
	@Override
	default ObjectModel3D<Object_Type> crossSectionAtZ(int z) {
		return new ObjectModel4DZCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, z);
	}
	
	@Override
	default ObjectModel3D<Object_Type> diagonalCrossSectionOnWX(int xOffsetFromW) {
		return new ObjectModel4DWXDiagonalCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, xOffsetFromW);
	}
	
	@Override
	default ObjectModel3D<Object_Type> diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new ObjectModel4DYZDiagonalCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, zOffsetFromY);
	}

	@Override
	default Iterator<Object_Type> iterator() {
		return new ObjectModel4DIterator<Object_Type>(this);
	}

}
