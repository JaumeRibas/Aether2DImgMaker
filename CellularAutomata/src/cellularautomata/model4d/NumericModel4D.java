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
package cellularautomata.model4d;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid4d.NumberGrid4D;
import cellularautomata.model.NumericModel;
import cellularautomata.model3d.NumericModel3D;

public interface NumericModel4D<T extends FieldElement<T> & Comparable<T>> extends NumberGrid4D<T>, NumericModel<T>, Model4D {
	
	@Override
	default NumericModel4D<T> subsection(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		return new NumericSubModel4D<T>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtW(int w) {
		return new NumericModel4DWCrossSection<T>(this, w);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtX(int x) {
		return new NumericModel4DXCrossSection<T>(this, x);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtY(int y) {
		return new NumericModel4DYCrossSection<T>(this, y);
	}
	
	@Override
	default NumericModel3D<T> crossSectionAtZ(int z) {
		return new NumericModel4DZCrossSection<T>(this, z);
	}
	
	@Override
	default NumericModel3D<T> diagonalCrossSectionOnWX(int xOffsetFromW) {
		return new NumericModel4DWXDiagonalCrossSection<T>(this, xOffsetFromW);
	}
}
