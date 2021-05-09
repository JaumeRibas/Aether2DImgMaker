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
package cellularautomata.evolvinggrid;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid4d.NumberGrid4D;

public interface EvolvingNumberGrid4D<T extends FieldElement<T> & Comparable<T>> extends NumberGrid4D<T>, EvolvingNumberGrid<T> {

	@Override
	default EvolvingNumberGrid2D<T> crossSectionAtYZ(int y, int z) {
		return new EvolvingNumberGrid4DYZCrossSection<T>(this, y, z);
	}
	
	//TODO
//	@Override
//	default EvolvingNumberGrid4D<T> subGrid(int minW, int maxW, int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
//		return new EvolvingNumberSubGrid4D<T, EvolvingNumberGrid4D<T>>(this, minW, maxW, minX, maxX, minY, maxY, minZ, maxZ);
//	}
	
	@Override
	default EvolvingNumberGrid3D<T> crossSectionAtZ(int z) {
		return new EvolvingNumberGrid4DZCrossSection<T>(this, z);
	}
}
