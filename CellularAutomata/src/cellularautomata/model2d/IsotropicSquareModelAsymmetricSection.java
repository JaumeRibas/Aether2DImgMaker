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
package cellularautomata.model2d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.IsotropicHypercubicModelAsymmetricSection;

/**
 * An asymmetric section of an isotropic square region of a 2D grid with center at the origin of coordinates.
 * The asymmetric section is the one where x >= y >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicSquareModelAsymmetricSection extends Model2D, IsotropicHypercubicModelAsymmetricSection {
	
	@Override
	default Model2D wholeGrid() {
		return new IsotropicSquareModel<IsotropicSquareModelAsymmetricSection>(this);
	}

	@Override
	default int getMinX() { return 0; }

	@Override
	default int getMaxX() { return getSize(); }

	@Override
	default int getMinX(int y) { return y; }

	@Override
	default int getMaxX(int y) { return getSize(); }

	@Override
	default int getMinY() { return 0; }

	@Override
	default int getMaxY() { return getSize(); }

	@Override
	default int getMinY(int x) { return 0; }

	@Override
	default int getMaxY(int x) { return x; }
	
	@Override
	default int getMaxCoordinate(int axis) {
		return IsotropicHypercubicModelAsymmetricSection.super.getMaxCoordinate(axis);
	}

	@Override
	default int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return IsotropicHypercubicModelAsymmetricSection.super.getMaxCoordinate(axis, coordinates);
	}
	
	@Override
	default int getMinCoordinate(int axis) {
		return IsotropicHypercubicModelAsymmetricSection.super.getMinCoordinate(axis);
	}

	@Override
	default int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return IsotropicHypercubicModelAsymmetricSection.super.getMinCoordinate(axis, coordinates);
	}
}
