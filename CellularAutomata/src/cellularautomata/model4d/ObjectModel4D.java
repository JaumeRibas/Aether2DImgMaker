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
import cellularautomata.PartialCoordinates;
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

	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel4D<Object_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (ObjectModel4D<Object_Type>) Model4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default ObjectModel4D<Object_Type> subsection(Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new ObjectSubModel4D<ObjectModel4D<Object_Type>, Object_Type>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}

	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel3D<Object_Type> crossSection(int axis, int coordinate) {
		return (ObjectModel3D<Object_Type>) Model4D.super.crossSection(axis, coordinate);
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

	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel3D<Object_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (ObjectModel3D<Object_Type>) Model4D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default ObjectModel3D<Object_Type> diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new ObjectModel4DWXDiagonalCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default ObjectModel3D<Object_Type> diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new ObjectModel4DYZDiagonalCrossSection<ObjectModel4D<Object_Type>, Object_Type>(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Object_Type> iterator() {
		return new ObjectModel4DIterator<Object_Type>(this);
	}

}
