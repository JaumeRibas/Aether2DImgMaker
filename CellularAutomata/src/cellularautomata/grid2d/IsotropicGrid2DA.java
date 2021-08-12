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
package cellularautomata.grid2d;

/**
 * An isotropic region of a 2D grid with center at the origin of coordinates.
 * Its underlying asymmetric section being the one within the region where x >= y >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicGrid2DA extends SymmetricGrid2D {

	@Override
	default int getAsymmetricMinX() { return 0; }

	@Override
	default int getAsymmetricMinX(int y) { return y; }

	@Override
	default int getAsymmetricMaxX(int y) { return getAsymmetricMaxX(); }

	@Override
	default int getAsymmetricMinY() { return 0; }

	@Override
	default int getAsymmetricMinY(int x) { return 0; }

	@Override
	default int getAsymmetricMaxY(int x) { return Math.min(x, getAsymmetricMaxY()); }

	@Override
	default int getMinX() {
		return -getAsymmetricMinX();
	}

	@Override
	default int getMaxX() {
		return getAsymmetricMinX();
	}

	@Override
	default int getMinY() {
		return -getAsymmetricMinX();
	}

	@Override
	default int getMaxY() {
		return getAsymmetricMinX();
	}
	
	//TODO add missing symmetric bounds methods
	
}
