/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
package cellularautomata.model5d;

/**
 * An isotropic hypercubic region of a 5D grid with center at the origin of coordinates.
 * Its underlying asymmetric section being the one within the region where v >= w >= x >= y >= z >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicHypercubicModel5DA extends SymmetricModel5D {

	@Override
	default int getMinV() { return -getAsymmetricMaxV(); }

	@Override
	default int getMaxV() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinV() { return 0; }

	@Override
	default int getAsymmetricMinVAtW(int w) { return w; }

	@Override
	default int getAsymmetricMaxVAtW(int w) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtX(int x) { return x; }

	@Override
	default int getAsymmetricMaxVAtX(int x) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtY(int y) { return y; }

	@Override
	default int getAsymmetricMaxVAtY(int y) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxVAtZ(int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtWX(int w, int x) { return w; }

	@Override
	default int getAsymmetricMaxVAtWX(int w, int x) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtWY(int w, int y) { return w; }

	@Override
	default int getAsymmetricMaxVAtWY(int w, int y) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtWZ(int w, int z) { return w; }

	@Override
	default int getAsymmetricMaxVAtWZ(int w, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtXY(int x, int y) { return x; }

	@Override
	default int getAsymmetricMaxVAtXY(int x, int y) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtXZ(int x, int z) { return x; }

	@Override
	default int getAsymmetricMaxVAtXZ(int x, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtYZ(int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxVAtYZ(int y, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtWXY(int w, int x, int y) { return w; }

	@Override
	default int getAsymmetricMaxVAtWXY(int w, int x, int y) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtWXZ(int w, int x, int z) { return w; }

	@Override
	default int getAsymmetricMaxVAtWXZ(int w, int x, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtWYZ(int w, int y, int z) { return w; }

	@Override
	default int getAsymmetricMaxVAtWYZ(int w, int y, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinVAtXYZ(int x, int y, int z) { return x; }

	@Override
	default int getAsymmetricMaxVAtXYZ(int x, int y, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinV(int w, int x, int y, int z) { return w; }

	@Override
	default int getAsymmetricMaxV(int w, int x, int y, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getMinW() { return -getAsymmetricMaxV(); }

	@Override
	default int getMaxW() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinW() { return 0; }

	@Override
	default int getAsymmetricMaxW() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinWAtV(int v) { return 0; }

	@Override
	default int getAsymmetricMaxWAtV(int v) { return v; }

	@Override
	default int getAsymmetricMinWAtX(int x) { return x; }

	@Override
	default int getAsymmetricMaxWAtX(int x) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinWAtY(int y) { return y; }

	@Override
	default int getAsymmetricMaxWAtY(int y) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinWAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxWAtZ(int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinWAtVX(int v, int x) { return x; }

	@Override
	default int getAsymmetricMaxWAtVX(int v, int x) { return v; }

	@Override
	default int getAsymmetricMinWAtVY(int v, int y) { return y; }

	@Override
	default int getAsymmetricMaxWAtVY(int v, int y) { return v; }

	@Override
	default int getAsymmetricMinWAtVZ(int v, int z) { return z; }

	@Override
	default int getAsymmetricMaxWAtVZ(int v, int z) { return v; }

	@Override
	default int getAsymmetricMinWAtXY(int x, int y) { return x; }

	@Override
	default int getAsymmetricMaxWAtXY(int x, int y) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinWAtXZ(int x, int z) { return x; }

	@Override
	default int getAsymmetricMaxWAtXZ(int x, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinWAtYZ(int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxWAtYZ(int y, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinWAtVXY(int v, int x, int y) { return x; }

	@Override
	default int getAsymmetricMaxWAtVXY(int v, int x, int y) { return v; }

	@Override
	default int getAsymmetricMinWAtVXZ(int v, int x, int z) { return x; }

	@Override
	default int getAsymmetricMaxWAtVXZ(int v, int x, int z) { return v; }

	@Override
	default int getAsymmetricMinWAtVYZ(int v, int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxWAtVYZ(int v, int y, int z) { return v; }

	@Override
	default int getAsymmetricMinWAtXYZ(int x, int y, int z) { return x; }

	@Override
	default int getAsymmetricMaxWAtXYZ(int x, int y, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinW(int v, int x, int y, int z) { return x; }

	@Override
	default int getAsymmetricMaxW(int v, int x, int y, int z) { return v; }

	@Override
	default int getMinX() { return -getAsymmetricMaxV(); }

	@Override
	default int getMaxX() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinX() { return 0; }

	@Override
	default int getAsymmetricMaxX() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinXAtV(int v) { return 0; }

	@Override
	default int getAsymmetricMaxXAtV(int v) { return v; }

	@Override
	default int getAsymmetricMinXAtW(int w) { return 0; }

	@Override
	default int getAsymmetricMaxXAtW(int w) { return w; }

	@Override
	default int getAsymmetricMinXAtY(int y) { return y; }

	@Override
	default int getAsymmetricMaxXAtY(int y) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinXAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxXAtZ(int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinXAtVW(int v, int w) { return 0; }

	@Override
	default int getAsymmetricMaxXAtVW(int v, int w) { return w; }

	@Override
	default int getAsymmetricMinXAtVY(int v, int y) { return y; }

	@Override
	default int getAsymmetricMaxXAtVY(int v, int y) { return v; }

	@Override
	default int getAsymmetricMinXAtVZ(int v, int z) { return z; }

	@Override
	default int getAsymmetricMaxXAtVZ(int v, int z) { return v; }

	@Override
	default int getAsymmetricMinXAtWY(int w, int y) { return y; }

	@Override
	default int getAsymmetricMaxXAtWY(int w, int y) { return w; }

	@Override
	default int getAsymmetricMinXAtWZ(int w, int z) { return z; }

	@Override
	default int getAsymmetricMaxXAtWZ(int w, int z) { return w; }

	@Override
	default int getAsymmetricMinXAtYZ(int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxXAtYZ(int y, int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinXAtVWY(int v, int w, int y) { return y; }

	@Override
	default int getAsymmetricMaxXAtVWY(int v, int w, int y) { return w; }

	@Override
	default int getAsymmetricMinXAtVWZ(int v, int w, int z) { return z; }

	@Override
	default int getAsymmetricMaxXAtVWZ(int v, int w, int z) { return w; }

	@Override
	default int getAsymmetricMinXAtVYZ(int v, int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxXAtVYZ(int v, int y, int z) { return v; }

	@Override
	default int getAsymmetricMinXAtWYZ(int w, int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxXAtWYZ(int w, int y, int z) { return w; }

	@Override
	default int getAsymmetricMinX(int v, int w, int y, int z) { return y; }

	@Override
	default int getAsymmetricMaxX(int v, int w, int y, int z) { return w; }

	@Override
	default int getMinY() { return -getAsymmetricMaxV(); }

	@Override
	default int getMaxY() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinY() { return 0; }

	@Override
	default int getAsymmetricMaxY() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinYAtV(int v) { return 0; }

	@Override
	default int getAsymmetricMaxYAtV(int v) { return v; }

	@Override
	default int getAsymmetricMinYAtW(int w) { return 0; }

	@Override
	default int getAsymmetricMaxYAtW(int w) { return w; }

	@Override
	default int getAsymmetricMinYAtX(int x) { return 0; }

	@Override
	default int getAsymmetricMaxYAtX(int x) { return x; }

	@Override
	default int getAsymmetricMinYAtZ(int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtZ(int z) { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinYAtVW(int v, int w) { return 0; }

	@Override
	default int getAsymmetricMaxYAtVW(int v, int w) { return w; }

	@Override
	default int getAsymmetricMinYAtVX(int v, int x) { return 0; }

	@Override
	default int getAsymmetricMaxYAtVX(int v, int x) { return x; }

	@Override
	default int getAsymmetricMinYAtVZ(int v, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtVZ(int v, int z) { return v; }

	@Override
	default int getAsymmetricMinYAtWX(int w, int x) { return 0; }

	@Override
	default int getAsymmetricMaxYAtWX(int w, int x) { return x; }

	@Override
	default int getAsymmetricMinYAtWZ(int w, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtWZ(int w, int z) { return w; }

	@Override
	default int getAsymmetricMinYAtXZ(int x, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtXZ(int x, int z) { return x; }

	@Override
	default int getAsymmetricMinYAtVWX(int v, int w, int x) { return 0; }

	@Override
	default int getAsymmetricMaxYAtVWX(int v, int w, int x) { return x; }

	@Override
	default int getAsymmetricMinYAtVWZ(int v, int w, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtVWZ(int v, int w, int z) { return w; }

	@Override
	default int getAsymmetricMinYAtVXZ(int v, int x, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtVXZ(int v, int x, int z) { return x; }

	@Override
	default int getAsymmetricMinYAtWXZ(int w, int x, int z) { return z; }

	@Override
	default int getAsymmetricMaxYAtWXZ(int w, int x, int z) { return x; }

	@Override
	default int getAsymmetricMinY(int v, int w, int x, int z) { return z; }

	@Override
	default int getAsymmetricMaxY(int v, int w, int x, int z) { return x; }

	@Override
	default int getMinZ() { return -getAsymmetricMaxV(); }

	@Override
	default int getMaxZ() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinZ() { return 0; }

	@Override
	default int getAsymmetricMaxZ() { return getAsymmetricMaxV(); }

	@Override
	default int getAsymmetricMinZAtV(int v) { return 0; }

	@Override
	default int getAsymmetricMaxZAtV(int v) { return v; }

	@Override
	default int getAsymmetricMinZAtW(int w) { return 0; }

	@Override
	default int getAsymmetricMaxZAtW(int w) { return w; }

	@Override
	default int getAsymmetricMinZAtX(int x) { return 0; }

	@Override
	default int getAsymmetricMaxZAtX(int x) { return x; }

	@Override
	default int getAsymmetricMinZAtY(int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtY(int y) { return y; }

	@Override
	default int getAsymmetricMinZAtVW(int v, int w) { return 0; }

	@Override
	default int getAsymmetricMaxZAtVW(int v, int w) { return w; }

	@Override
	default int getAsymmetricMinZAtVX(int v, int x) { return 0; }

	@Override
	default int getAsymmetricMaxZAtVX(int v, int x) { return x; }

	@Override
	default int getAsymmetricMinZAtVY(int v, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtVY(int v, int y) { return y; }

	@Override
	default int getAsymmetricMinZAtWX(int w, int x) { return 0; }

	@Override
	default int getAsymmetricMaxZAtWX(int w, int x) { return x; }

	@Override
	default int getAsymmetricMinZAtWY(int w, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtWY(int w, int y) { return y; }

	@Override
	default int getAsymmetricMinZAtXY(int x, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtXY(int x, int y) { return y; }

	@Override
	default int getAsymmetricMinZAtVWX(int v, int w, int x) { return 0; }

	@Override
	default int getAsymmetricMaxZAtVWX(int v, int w, int x) { return x; }

	@Override
	default int getAsymmetricMinZAtVWY(int v, int w, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtVWY(int v, int w, int y) { return y; }

	@Override
	default int getAsymmetricMinZAtVXY(int v, int x, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtVXY(int v, int x, int y) { return y; }

	@Override
	default int getAsymmetricMinZAtWXY(int w, int x, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZAtWXY(int w, int x, int y) { return y; }

	@Override
	default int getAsymmetricMinZ(int v, int w, int x, int y) { return 0; }

	@Override
	default int getAsymmetricMaxZ(int v, int w, int x, int y) { return y; }
	
}
