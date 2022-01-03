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
package cellularautomata.model1d;

/**
 * An isotropic region of a 1D grid with center at the origin of coordinates.
 * Its underlying asymmetric section being the one where x >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicModel1DA extends SymmetricModel1D {

	@Override
	default int getAsymmetricMinX() { return 0; }

	@Override
	default int getMinX() {
		return -getAsymmetricMaxX();
	}

	@Override
	default int getMaxX() {
		return getAsymmetricMaxX();
	}
	
}
