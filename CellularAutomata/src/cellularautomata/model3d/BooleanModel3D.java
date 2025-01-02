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
package cellularautomata.model3d;

import java.util.Iterator;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.model.BooleanModel;
import cellularautomata.model2d.BooleanModel2D;

public interface BooleanModel3D extends Model3D, BooleanModel {
	
	/**
	 * <p>Returns the value at a given position.</p>
	 * <p>It is not defined to call this method passing coordinates outside the bounds of the region.</p>
	 * 
	 * @param x the position on the x-axis
	 * @param y the position on the y-axis
	 * @param z the position on the z-axis
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	boolean getFromPosition(int x, int y, int z) throws Exception;
	
	@Override
	default boolean getFromPosition(Coordinates coordinates) throws Exception {
		return getFromPosition(coordinates.get(0), coordinates.get(1), coordinates.get(2));
	}
	
	@Override
	default BooleanModel3D subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return (BooleanModel3D) Model3D.super.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	default BooleanModel3D subsection(Integer minX, Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		return new BooleanSubModel3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default BooleanModel2D crossSection(int axis, int coordinate) {
		return (BooleanModel2D) Model3D.super.crossSection(axis, coordinate);
	}
	
	@Override
	default BooleanModel2D crossSectionAtX(int x) {
		return new BooleanModel3DXCrossSection(this, x);
	}
	
	@Override
	default BooleanModel2D crossSectionAtY(int y) {
		return new BooleanModel3DYCrossSection(this, y);
	}
	
	@Override
	default BooleanModel2D crossSectionAtZ(int z) {
		return new BooleanModel3DZCrossSection(this, z);
	}
	
	@Override
	default BooleanModel2D diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return (BooleanModel2D) Model3D.super.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}
	
	@Override
	default BooleanModel2D diagonalCrossSectionOnXY(boolean positiveSlope, int yOffsetFromX) {
		return new BooleanModel3DXYDiagonalCrossSection(this, positiveSlope, yOffsetFromX);
	}
	
	@Override
	default BooleanModel2D diagonalCrossSectionOnXZ(boolean positiveSlope, int zOffsetFromX) {
		return new BooleanModel3DXZDiagonalCrossSection(this, positiveSlope, zOffsetFromX);
	}
	
	@Override
	default BooleanModel2D diagonalCrossSectionOnYZ(boolean positiveSlope, int zOffsetFromY) {
		return new BooleanModel3DYZDiagonalCrossSection(this, positiveSlope, zOffsetFromY);
	}

	@Override
	default Iterator<Boolean> iterator() {
		return new BooleanModel3DIterator(this);
	}
	
}
