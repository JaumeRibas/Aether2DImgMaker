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
import cellularautomata.model.BooleanModel;
import cellularautomata.model4d.BooleanModel4D;

public interface BooleanModel5D extends Model5D, BooleanModel {
	
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
	boolean getFromPosition(int v, int w, int x, int y, int z) throws Exception;
	
	@Override
	default boolean getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3), coordinates.get(4));
	}

	@Override
	default BooleanModel5D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (BooleanModel5D) Model5D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default BooleanModel5D subsection(Integer minV, Integer maxV, Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new BooleanSubModel5D(this, minV, maxV, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default BooleanModel4D crossSection(int axis, int coordinate) {
		return (BooleanModel4D) Model5D.super.crossSection(axis, coordinate);
	}

	@Override
	default BooleanModel4D crossSectionAtV(int v) {
		return new BooleanModel5DVCrossSection(this, v);
	}

	@Override
	default BooleanModel4D crossSectionAtW(int w) {
		return new BooleanModel5DWCrossSection(this, w);
	}

	@Override
	default BooleanModel4D crossSectionAtX(int x) {
		return new BooleanModel5DXCrossSection(this, x);
	}

	@Override
	default BooleanModel4D crossSectionAtY(int y) {
		return new BooleanModel5DYCrossSection(this, y);
	}

	@Override
	default BooleanModel4D crossSectionAtZ(int z) {
		return new BooleanModel5DZCrossSection(this, z);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (BooleanModel4D) Model5D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnVW(boolean positiveSlope, int wOffsetFromV) {
		return new BooleanModel5DVWDiagonalCrossSection(this, positiveSlope, wOffsetFromV);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnVX(boolean positiveSlope, int xOffsetFromV) {
		return new BooleanModel5DVXDiagonalCrossSection(this, positiveSlope, xOffsetFromV);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnVY(boolean positiveSlope, int yOffsetFromV) {
		return new BooleanModel5DVYDiagonalCrossSection(this, positiveSlope, yOffsetFromV);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnVZ(boolean positiveSlope, int zOffsetFromV) {
		return new BooleanModel5DVZDiagonalCrossSection(this, positiveSlope, zOffsetFromV);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new BooleanModel5DWXDiagonalCrossSection(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new BooleanModel5DWYDiagonalCrossSection(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new BooleanModel5DWZDiagonalCrossSection(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new BooleanModel5DXYDiagonalCrossSection(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new BooleanModel5DXZDiagonalCrossSection(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default BooleanModel4D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new BooleanModel5DYZDiagonalCrossSection(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Boolean> iterator() {
		return new BooleanModel5DIterator(this);
	}
	
}
