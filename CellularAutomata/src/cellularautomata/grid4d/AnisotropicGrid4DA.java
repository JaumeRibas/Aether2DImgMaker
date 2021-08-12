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
 * An asymmetric section of an isotropic region of a 4D grid with center at the origin of coordinates.
 * The asymmetric section is the one within the region where w >= x >= y >= z >= 0.
 *  
 * @author Jaume
 *
 */
public interface AnisotropicGrid4DA extends Grid4D {
	
	@Override
	default int getMinW() { return 0; }

	@Override
	default int getMinWAtX(int x) { return x; }

	@Override
	default int getMaxWAtX(int x) { return getMaxW(); }

	@Override
	default int getMinWAtY(int y) { return y; }

	@Override
	default int getMaxWAtY(int y) { return getMaxW(); }

	@Override
	default int getMinWAtZ(int z) { return z; }

	@Override
	default int getMaxWAtZ(int z) { return getMaxW(); }

	@Override
	default int getMinWAtXY(int x, int y) { return x; }

	@Override
	default int getMaxWAtXY(int x, int y) { return getMaxW(); }

	@Override
	default int getMinWAtXZ(int x, int z) { return x; }

	@Override
	default int getMaxWAtXZ(int x, int z) { return getMaxW(); }

	@Override
	default int getMinWAtYZ(int y, int z) { return y; }

	@Override
	default int getMaxWAtYZ(int y, int z) { return getMaxW(); }

	@Override
	default int getMinW(int x, int y, int z) { return x; }

	@Override
	default int getMaxW(int x, int y, int z) { return getMaxW(); }

	@Override
	default int getMinX() { return 0; }

	@Override
	default int getMinXAtW(int w) { return 0; }

	@Override
	default int getMaxXAtW(int w) { return Math.min(w, getMaxX()); }

	@Override
	default int getMinXAtY(int y) { return y; }

	@Override
	default int getMaxXAtY(int y) { return getMaxX(); }

	@Override
	default int getMinXAtZ(int z) { return z; }

	@Override
	default int getMaxXAtZ(int z) { return getMaxX(); }

	@Override
	default int getMinXAtWY(int w, int y) { return y; }

	@Override
	default int getMaxXAtWY(int w, int y) { return Math.min(w, getMaxX()); }

	@Override
	default int getMinXAtWZ(int w, int z) { return z; }

	@Override
	default int getMaxXAtWZ(int w, int z) { return Math.min(w, getMaxX()); }

	@Override
	default int getMinXAtYZ(int y, int z) { return y; }

	@Override
	default int getMaxXAtYZ(int y, int z) { return getMaxX(); }

	@Override
	default int getMinX(int w, int y, int z) { return y; }

	@Override
	default int getMaxX(int w, int y, int z) { return Math.min(w, getMaxX()); }

	@Override
	default int getMinY() { return 0; }

	@Override
	default int getMinYAtW(int w) { return 0; }

	@Override
	default int getMaxYAtW(int w) { return Math.min(w, getMaxY()); }

	@Override
	default int getMinYAtX(int x) { return 0; }

	@Override
	default int getMaxYAtX(int x) { return Math.min(x, getMaxY()); }

	@Override
	default int getMinYAtZ(int z) { return z; }

	@Override
	default int getMaxYAtZ(int z) { return getMaxY(); }

	@Override
	default int getMinYAtWX(int w, int x) { return 0; }

	@Override
	default int getMaxYAtWX(int w, int x) { return Math.min(x, getMaxY()); }

	@Override
	default int getMinYAtWZ(int w, int z) { return z; }

	@Override
	default int getMaxYAtWZ(int w, int z) { return Math.min(w, getMaxY()); }

	@Override
	default int getMinYAtXZ(int x, int z) { return z; }

	@Override
	default int getMaxYAtXZ(int x, int z) { return Math.min(x, getMaxY()); }

	@Override
	default int getMinY(int w, int x, int z) { return z; }

	@Override
	default int getMaxY(int w, int x, int z) { return Math.min(x, getMaxY()); }

	@Override
	default int getMinZ() { return 0; }

	@Override
	default int getMinZAtW(int w) { return 0; }

	@Override
	default int getMaxZAtW(int w) { return Math.min(w, getMaxZ()); }

	@Override
	default int getMinZAtX(int x) { return 0; }

	@Override
	default int getMaxZAtX(int x) { return Math.min(x, getMaxZ()); }

	@Override
	default int getMinZAtY(int y) { return 0; }

	@Override
	default int getMaxZAtY(int y) { return Math.min(y, getMaxZ()); }

	@Override
	default int getMinZAtWX(int w, int x) { return 0; }

	@Override
	default int getMaxZAtWX(int w, int x) { return Math.min(x, getMaxZ()); }

	@Override
	default int getMinZAtWY(int w, int y) { return 0; }

	@Override
	default int getMaxZAtWY(int w, int y) { return Math.min(y, getMaxZ()); }

	@Override
	default int getMinZAtXY(int x, int y) { return 0; }

	@Override
	default int getMaxZAtXY(int x, int y) { return Math.min(y, getMaxZ()); }

	@Override
	default int getMinZ(int w, int x, int y) { return 0; }

	@Override
	default int getMaxZ(int w, int x, int y) { return Math.min(y, getMaxZ()); }
	
}
