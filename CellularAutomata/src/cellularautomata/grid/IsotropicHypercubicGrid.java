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
package cellularautomata.grid;

/**
 * An isotropic hypercubic region of a grid with center at the origin of coordinates.
 * Its underlying asymmetric section being the one within the region where c1 >= c2 >= c3 ... >= cN >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicHypercubicGrid extends SymmetricGrid {

	@Override
	default int getMinCoordinate(int axis) { 
		return -getAsymmetricMaxCoordinate(axis); 
	}

	@Override
	default int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return -getAsymmetricMaxCoordinate(axis);
	}

	@Override
	default int getMaxCoordinate(int axis) {
		return getAsymmetricMaxCoordinate(axis);
	}

	@Override
	default int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return getAsymmetricMaxCoordinate(axis);
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis) {
		return 0;
	}

	@Override
	default int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		if (axis > 0) {
			for (int i = axis - 1; i >= 0; i--) {
				Integer coord = coordinates.get(i);
				if (coord != null) {
					return coord;
				}
			}
		}
		return getAsymmetricMaxCoordinate(axis);
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis, PartialCoordinates coordinates) {
		int coordCount = coordinates.getCount();
		if (axis < coordCount - 1) {
			for (int i = axis + 1; i < coordCount; i++) {
				Integer coord = coordinates.get(i);
				if (coord != null) {
					return coord;
				}
			}
		}
		return 0;
	}
	
}
