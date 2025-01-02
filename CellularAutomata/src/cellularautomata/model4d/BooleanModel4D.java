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
package cellularautomata.model4d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.BooleanModel;
import cellularautomata.model3d.BooleanModel3D;

public interface BooleanModel4D extends Model4D, BooleanModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region.</p>
	 * 
	 * @param w the position on the w-axis 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (w,x,y,z)
	 * @throws Exception 
	 */
	boolean getFromPosition(int w, int x, int y, int z) throws Exception;
	
	@Override
	default boolean getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3));
	}

	@Override
	default BooleanModel4D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (BooleanModel4D) Model4D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default BooleanModel4D subsection(Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new BooleanSubModel4D(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default BooleanModel3D crossSection(int axis, int coordinate) {
		return (BooleanModel3D) Model4D.super.crossSection(axis, coordinate);
	}
	
	@Override
	default BooleanModel3D crossSectionAtW(int w) {
		return new BooleanModel4DWCrossSection(this, w);
	}
	
	@Override
	default BooleanModel3D crossSectionAtX(int x) {
		return new BooleanModel4DXCrossSection(this, x);
	}
	
	@Override
	default BooleanModel3D crossSectionAtY(int y) {
		return new BooleanModel4DYCrossSection(this, y);
	}
	
	@Override
	default BooleanModel3D crossSectionAtZ(int z) {
		return new BooleanModel4DZCrossSection(this, z);
	}
	
	@Override
	default BooleanModel3D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (BooleanModel3D) Model4D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default BooleanModel3D diagonalCrossSectionOnWX(boolean positiveSlope, int xOffsetFromW) {
		return new BooleanModel4DWXDiagonalCrossSection(this, positiveSlope, xOffsetFromW);
	}
	
	@Override
	default BooleanModel3D diagonalCrossSectionOnWY(boolean positiveSlope, int yOffsetFromW) {
		return new BooleanModel4DWYDiagonalCrossSection(this, positiveSlope, yOffsetFromW);
	}
	
	@Override
	default BooleanModel3D diagonalCrossSectionOnWZ(boolean positiveSlope, int zOffsetFromW) {
		return new BooleanModel4DWZDiagonalCrossSection(this, positiveSlope, zOffsetFromW);
	}
	
	@Override
	default BooleanModel3D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new BooleanModel4DXYDiagonalCrossSection(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default BooleanModel3D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new BooleanModel4DXZDiagonalCrossSection(this, positiveSlope, zOffsetFromX);
	}

	@Override
	default BooleanModel3D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new BooleanModel4DYZDiagonalCrossSection(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Boolean> iterator() {
		return new BooleanModel4DIterator(this);
	}
	
}
