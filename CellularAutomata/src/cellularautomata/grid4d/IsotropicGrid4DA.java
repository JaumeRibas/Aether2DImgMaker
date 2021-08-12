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
package cellularautomata.grid4d;

/**
 * An isotropic region of a 4D grid with center at the origin of coordinates.
 * Its underlying asymmetric section being the one within the region where w >= x >= y >= z >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicGrid4DA extends SymmetricGrid4D {

	@Override
	default int getAsymmetricMinW() { return 0; }

	@Override
	default int getAsymmetricMinWAtX(int x) { return x; }

	@Override
	default int getAsymmetricMaxWAtX(int x) { return getAsymmetricMaxW(); }

	@Override
	default int getAsymmetricMinWAtY(int y) { return y; }

	@Override
	default int getAsymmetricMaxWAtY(int y) { return getAsymmetricMaxW(); }

	@Override
	default int getAsymmetricMinWAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxWAtZ(int z) { return getAsymmetricMaxW(); }

	@Override
	default int getAsymmetricMinWAtXY(int x, int y) { return x; }

	@Override
	default int getAsymmetricMaxWAtXY(int x, int y) { return getAsymmetricMaxW(); }

	@Override
	default int getAsymmetricMinWAtXZ(int x, int z) { return x; }

	@Override
	default int getAsymmetricMaxWAtXZ(int x, int z) { return getAsymmetricMaxW(); }

	@Override
	default int getAsymmetricMinWAtYZ(int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxWAtYZ(int y, int z) { return getAsymmetricMaxW(); }

	@Override
	default int getAsymmetricMinW(int x, int y, int z) { return x; }

	@Override
	default int getAsymmetricMaxW(int x, int y, int z) { return getAsymmetricMaxW(); }

	@Override
	default int getAsymmetricMinX() { return 0; }

	@Override
	default int getAsymmetricMinXAtW(int w) { return 0; }

	@Override
	default int getAsymmetricMaxXAtW(int w) { return Math.min(w, getAsymmetricMaxX()); }

	@Override
	default int getAsymmetricMinXAtY(int y) { return y; }

	@Override
	default int getAsymmetricMaxXAtY(int y) { return getAsymmetricMaxX(); }

	@Override
	default int getAsymmetricMinXAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxXAtZ(int z) { return getAsymmetricMaxX(); }

	@Override
	default int getAsymmetricMinXAtWY(int w, int y) { return y; }

	@Override
	default int getAsymmetricMaxXAtWY(int w, int y) { return Math.min(w, getAsymmetricMaxX()); }

	@Override
	default int getAsymmetricMinXAtWZ(int w, int z) { return z; }

	@Override
	default int getAsymmetricMaxXAtWZ(int w, int z) { return Math.min(w, getAsymmetricMaxX()); }

	@Override
	default int getAsymmetricMinXAtYZ(int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxXAtYZ(int y, int z) { return getAsymmetricMaxX(); }

	@Override
	default int getAsymmetricMinX(int w, int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxX(int w, int y, int z) { return Math.min(w, getAsymmetricMaxX()); }

	@Override
	default int getAsymmetricMinY() { return 0; }

	@Override
	default int getAsymmetricMinYAtW(int w) { return 0; }

	@Override
	default int getAsymmetricMaxYAtW(int w) { return Math.min(w, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinYAtX(int x) { return 0; }

	@Override
	default int getAsymmetricMaxYAtX(int x) { return Math.min(x, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinYAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtZ(int z) { return getAsymmetricMaxY(); }

	@Override
	default int getAsymmetricMinYAtWX(int w, int x) { return 0; }

	@Override
	default int getAsymmetricMaxYAtWX(int w, int x) { return Math.min(x, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinYAtWZ(int w, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtWZ(int w, int z) { return Math.min(w, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinYAtXZ(int x, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtXZ(int x, int z) { return Math.min(x, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinY(int w, int x, int z) { return z; }

	@Override
	default int getAsymmetricMaxY(int w, int x, int z) { return Math.min(x, getAsymmetricMaxY()); }

	@Override
	default int getAsymmetricMinZ() { return 0; }

	@Override
	default int getAsymmetricMinZAtW(int w) { return 0; }

	@Override
	default int getAsymmetricMaxZAtW(int w) { return Math.min(w, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZAtX(int x) { return 0; }

	@Override
	default int getAsymmetricMaxZAtX(int x) { return Math.min(x, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZAtY(int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtY(int y) { return Math.min(y, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZAtWX(int w, int x) { return 0; }

	@Override
	default int getAsymmetricMaxZAtWX(int w, int x) { return Math.min(x, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZAtWY(int w, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtWY(int w, int y) { return Math.min(y, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZAtXY(int x, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtXY(int x, int y) { return Math.min(y, getAsymmetricMaxZ()); }

	@Override
	default int getAsymmetricMinZ(int w, int x, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZ(int w, int x, int y) { return Math.min(y, getAsymmetricMaxZ()); }
	
	@Override
	default int getMinW() {
		return -getAsymmetricMaxW();
	}

	@Override
	default int getMaxW() {
		return getAsymmetricMaxW();
	}

	@Override
	default int getMinX() {
		return -getAsymmetricMaxW();
	}

	@Override
	default int getMaxX() {
		return getAsymmetricMaxW();
	}

	@Override
	default int getMinY() {
		return -getAsymmetricMaxW();
	}

	@Override
	default int getMaxY() {
		return getAsymmetricMaxW();
	}

	@Override
	default int getMinZ() {
		return -getAsymmetricMaxW();
	}

	@Override
	default int getMaxZ() {
		return getAsymmetricMaxW();
	}
	
	//TODO add missing symmetric bounds methods
	
}
