/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package cellularautomata.model5d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.ObjectModel;
import cellularautomata.model4d.ObjectModel4D;

public interface ObjectModel5D<Object_Type> extends Model5D, ObjectModel<Object_Type> {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region.</p>
	 * 
	 * @param v the position on the v-axis 
	 * @param w the position on the w-axis 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (v,w,x,y,z)
	 * @throws Exception 
	 */
	Object_Type getFromPosition(int v, int w, int x, int y, int z) throws Exception;
	
	@Override
	default Object_Type getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3), coordinates.get(4));
	}

	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel5D<Object_Type> subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (ObjectModel5D<Object_Type>) Model5D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default ObjectModel5D<Object_Type> subsection(Integer minV, Integer maxV, Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new ObjectSubModel5D<ObjectModel5D<Object_Type>, Object_Type>(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel4D<Object_Type> crossSection(int axis, int coordinate) {
		return (ObjectModel4D<Object_Type>) Model5D.super.crossSection(axis, coordinate);
	}

	@Override
	default ObjectModel4D<Object_Type> crossSectionAtV(int v) {
		return new ObjectModel5DVCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, v);
	}

	@Override
	default ObjectModel4D<Object_Type> crossSectionAtW(int w) {
		return new ObjectModel5DWCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, w);
	}

	@Override
	default ObjectModel4D<Object_Type> crossSectionAtX(int x) {
		return new ObjectModel5DXCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, x);
	}

	@Override
	default ObjectModel4D<Object_Type> crossSectionAtY(int y) {
		return new ObjectModel5DYCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, y);
	}

	@Override
	default ObjectModel4D<Object_Type> crossSectionAtZ(int z) {
		return new ObjectModel5DZCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, z);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (ObjectModel4D<Object_Type>) Model5D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return new ObjectModel5DVWDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, wOffsetFromV);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnVX(boolean positiveSlope, int xOffsetFromV) {
		return new ObjectModel5DVXDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, xOffsetFromV);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnVY(boolean positiveSlope, int yOffsetFromV) {
		return new ObjectModel5DVYDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, yOffsetFromV);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnVZ(boolean positiveSlope, int zOffsetFromV) {
		return new ObjectModel5DVZDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, zOffsetFromV);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new ObjectModel5DWXDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new ObjectModel5DWYDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new ObjectModel5DWZDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new ObjectModel5DXYDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new ObjectModel5DXZDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default ObjectModel4D<Object_Type> diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new ObjectModel5DYZDiagonalCrossSection<ObjectModel5D<Object_Type>, Object_Type>(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Object_Type> iterator() {
		return new ObjectModel5DIterator<Object_Type>(this);
	}

}
