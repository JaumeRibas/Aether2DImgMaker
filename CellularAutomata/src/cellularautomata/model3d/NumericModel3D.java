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
package cellularautomata.model3d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid3d.NumberGrid3D;
import cellularautomata.model.NumericModel;
import cellularautomata.model2d.NumericModel2D;

public interface NumericModel3D<T extends FieldElement<T> & Comparable<T>> extends NumberGrid3D<T>, NumericModel<T>, Model3D {
	
	@Override
	default NumericModel2D<T> crossSectionAtX(int x) {
		return new NumericModel3DXCrossSection<T>(this, x);
	}
	
	@Override
	default NumericModel2D<T> crossSectionAtY(int y) {
		return new NumericModel3DYCrossSection<T>(this, y);
	}
	
	@Override
	default NumericModel2D<T> crossSectionAtZ(int z) {
		return new NumericModel3DZCrossSection<T>(this, z);
	}
	
	@Override
	default NumericModel2D<T> diagonalCrossSectionOnXY(int yOffsetFromX) {
		return new NumericModel3DXYDiagonalCrossSection<T>(this, yOffsetFromX);
	}
	
	@Override
	default NumericModel2D<T> diagonalCrossSectionOnXZ(int zOffsetFromX) {
		return new NumericModel3DXZDiagonalCrossSection<T>(this, zOffsetFromX);
	}
	
	@Override
	default NumericModel2D<T> diagonalCrossSectionOnYZ(int zOffsetFromY) {
		return new NumericModel3DYZDiagonalCrossSection<T>(this, zOffsetFromY);
	}
	
	@Override
	default NumericModel3D<T> subsection(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new NumericSubModel3D<T>(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
}
