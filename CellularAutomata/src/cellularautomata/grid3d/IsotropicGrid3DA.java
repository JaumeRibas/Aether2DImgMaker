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
package cellularautomata.grid3d;

/**
 * An isotropic region of a 3D grid with center at the origin of coordinates.
 * Its underlying asymmetric section being the one within the region where x >= y >= z >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicGrid3DA extends SymmetricGrid3D {

	@Override
	default int getAsymmetricMinX() { return 0; }

	@Override
	default int getAsymmetricMinXAtY(int y) { return y; }

	@Override
	default int getAsymmetricMaxXAtY(int y) { return getAsymmetricMaxX(); }

	@Override
	default int getAsymmetricMinXAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxXAtZ(int z) { return getAsymmetricMaxX(); }

	@Override
	default int getAsymmetricMinX(int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxX(int y, int z) { return getAsymmetricMaxX(); }

	@Override
	default int getAsymmetricMinY() { return 0; }

	@Override
	default int getAsymmetricMinYAtX(int x) { return 0; }

	@Override
	default int getAsymmetricMaxYAtX(int x) { return Math.min(x, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinYAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtZ(int z) { return getAsymmetricMaxY(); }

	@Override
	default int getAsymmetricMinY(int x, int z) { return z; }

	@Override
	default int getAsymmetricMaxY(int x, int z) { return Math.min(x, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinZ() { return 0; }

	@Override
	default int getAsymmetricMinZAtX(int x) { return 0; }

	@Override
	default int getAsymmetricMaxZAtX(int x) { return Math.min(x, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZAtY(int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtY(int y) { return Math.min(y, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZ(int x, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZ(int x, int y) { return Math.min(y, getAsymmetricMaxZ()); }

	@Override
	default int getMinX() {
		return -getAsymmetricMaxX();
	}

	@Override
	default int getMaxX() {
		return getAsymmetricMaxX();
	}

	@Override
	default int getMinY() {
		return -getAsymmetricMaxX();
	}

	@Override
	default int getMaxY() {
		return getAsymmetricMaxX();
	}

	@Override
	default int getMinZ() {
		return -getAsymmetricMaxX();
	}

	@Override
	default int getMaxZ() {
		return getAsymmetricMaxX();
	}
	
	//TODO add missing symmetric bounds methods
	
}
