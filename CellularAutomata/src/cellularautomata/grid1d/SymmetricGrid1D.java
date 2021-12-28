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
package cellularautomata.grid1d;

import cellularautomata.grid.PartialCoordinates;
import cellularautomata.grid.SymmetricGrid;

public interface SymmetricGrid1D extends Grid1D, SymmetricGrid {

	/**
	 * Returns the smallest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the smallest x
	 */
	int getAsymmetricMinX();
	
	/**
	 * Returns the largest x-coordinate of the asymmetric section of the grid
	 * 
	 * @return the largest x
	 */
	int getAsymmetricMaxX();

	@Override
	default int getAsymmetricMaxCoordinate(int axis) {
		return getAsymmetricMaxX();
	}

	@Override
	default int getAsymmetricMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return getAsymmetricMaxX();
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis) {
		return getAsymmetricMinX();
	}

	@Override
	default int getAsymmetricMinCoordinate(int axis, PartialCoordinates coordinates) {
		return getAsymmetricMinX();
	}
	
	@Override
	default Grid1D asymmetricSection() {
		return new AsymmetricGridSection1D<SymmetricGrid1D>(this);
	}

}
