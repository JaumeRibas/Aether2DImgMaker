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
package cellularautomata.model5d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.IsotropicHypercubicModelAsymmetricSection;

/**
 * An asymmetric section of an isotropic hypercubic region of a 5D grid with center at the origin of coordinates.
 * The asymmetric section is the one where v >= w >= x >= y >= z >= 0.
 *  
 * @author Jaume
 *
 */
public interface IsotropicHypercubicModelAsymmetricSection5D extends Model5D, IsotropicHypercubicModelAsymmetricSection {
	
	@Override
	default Model5D wholeGrid() {
		return new IsotropicHypercubicModel5D<IsotropicHypercubicModelAsymmetricSection5D>(this);
	}

	@Override
	default int getMinV() { return 0; }

	@Override
	default int getMaxV() { return getSize(); }

	@Override
	default int getMinVAtW(int w) { return w; }

	@Override
	default int getMaxVAtW(int w) { return getSize(); }

	@Override
	default int getMinVAtX(int x) { return x; }

	@Override
	default int getMaxVAtX(int x) { return getSize(); }

	@Override
	default int getMinVAtY(int y) { return y; }

	@Override
	default int getMaxVAtY(int y) { return getSize(); }

	@Override
	default int getMinVAtZ(int z) { return z; }

	@Override
	default int getMaxVAtZ(int z) { return getSize(); }

	@Override
	default int getMinVAtWX(int w, int x) { return w; }

	@Override
	default int getMaxVAtWX(int w, int x) { return getSize(); }

	@Override
	default int getMinVAtWY(int w, int y) { return w; }

	@Override
	default int getMaxVAtWY(int w, int y) { return getSize(); }

	@Override
	default int getMinVAtWZ(int w, int z) { return w; }

	@Override
	default int getMaxVAtWZ(int w, int z) { return getSize(); }

	@Override
	default int getMinVAtXY(int x, int y) { return x; }

	@Override
	default int getMaxVAtXY(int x, int y) { return getSize(); }

	@Override
	default int getMinVAtXZ(int x, int z) { return x; }

	@Override
	default int getMaxVAtXZ(int x, int z) { return getSize(); }

	@Override
	default int getMinVAtYZ(int y, int z) { return y; }

	@Override
	default int getMaxVAtYZ(int y, int z) { return getSize(); }

	@Override
	default int getMinVAtWXY(int w, int x, int y) { return w; }

	@Override
	default int getMaxVAtWXY(int w, int x, int y) { return getSize(); }

	@Override
	default int getMinVAtWXZ(int w, int x, int z) { return w; }

	@Override
	default int getMaxVAtWXZ(int w, int x, int z) { return getSize(); }

	@Override
	default int getMinVAtWYZ(int w, int y, int z) { return w; }

	@Override
	default int getMaxVAtWYZ(int w, int y, int z) { return getSize(); }

	@Override
	default int getMinVAtXYZ(int x, int y, int z) { return x; }

	@Override
	default int getMaxVAtXYZ(int x, int y, int z) { return getSize(); }

	@Override
	default int getMinV(int w, int x, int y, int z) { return w; }

	@Override
	default int getMaxV(int w, int x, int y, int z) { return getSize(); }

	@Override
	default int getMinW() { return 0; }

	@Override
	default int getMaxW() { return getSize(); }

	@Override
	default int getMinWAtV(int v) { return 0; }

	@Override
	default int getMaxWAtV(int v) { return v; }

	@Override
	default int getMinWAtX(int x) { return x; }

	@Override
	default int getMaxWAtX(int x) { return getSize(); }

	@Override
	default int getMinWAtY(int y) { return y; }

	@Override
	default int getMaxWAtY(int y) { return getSize(); }

	@Override
	default int getMinWAtZ(int z) { return z; }

	@Override
	default int getMaxWAtZ(int z) { return getSize(); }

	@Override
	default int getMinWAtVX(int v, int x) { return x; }

	@Override
	default int getMaxWAtVX(int v, int x) { return v; }

	@Override
	default int getMinWAtVY(int v, int y) { return y; }

	@Override
	default int getMaxWAtVY(int v, int y) { return v; }

	@Override
	default int getMinWAtVZ(int v, int z) { return z; }

	@Override
	default int getMaxWAtVZ(int v, int z) { return v; }

	@Override
	default int getMinWAtXY(int x, int y) { return x; }

	@Override
	default int getMaxWAtXY(int x, int y) { return getSize(); }

	@Override
	default int getMinWAtXZ(int x, int z) { return x; }

	@Override
	default int getMaxWAtXZ(int x, int z) { return getSize(); }

	@Override
	default int getMinWAtYZ(int y, int z) { return y; }

	@Override
	default int getMaxWAtYZ(int y, int z) { return getSize(); }

	@Override
	default int getMinWAtVXY(int v, int x, int y) { return x; }

	@Override
	default int getMaxWAtVXY(int v, int x, int y) { return v; }

	@Override
	default int getMinWAtVXZ(int v, int x, int z) { return x; }

	@Override
	default int getMaxWAtVXZ(int v, int x, int z) { return v; }

	@Override
	default int getMinWAtVYZ(int v, int y, int z) { return y; }

	@Override
	default int getMaxWAtVYZ(int v, int y, int z) { return v; }

	@Override
	default int getMinWAtXYZ(int x, int y, int z) { return x; }

	@Override
	default int getMaxWAtXYZ(int x, int y, int z) { return getSize(); }

	@Override
	default int getMinW(int v, int x, int y, int z) { return x; }

	@Override
	default int getMaxW(int v, int x, int y, int z) { return v; }

	@Override
	default int getMinX() { return 0; }

	@Override
	default int getMaxX() { return getSize(); }

	@Override
	default int getMinXAtV(int v) { return 0; }

	@Override
	default int getMaxXAtV(int v) { return v; }

	@Override
	default int getMinXAtW(int w) { return 0; }

	@Override
	default int getMaxXAtW(int w) { return w; }

	@Override
	default int getMinXAtY(int y) { return y; }

	@Override
	default int getMaxXAtY(int y) { return getSize(); }

	@Override
	default int getMinXAtZ(int z) { return z; }

	@Override
	default int getMaxXAtZ(int z) { return getSize(); }

	@Override
	default int getMinXAtVW(int v, int w) { return 0; }

	@Override
	default int getMaxXAtVW(int v, int w) { return w; }

	@Override
	default int getMinXAtVY(int v, int y) { return y; }

	@Override
	default int getMaxXAtVY(int v, int y) { return v; }

	@Override
	default int getMinXAtVZ(int v, int z) { return z; }

	@Override
	default int getMaxXAtVZ(int v, int z) { return v; }

	@Override
	default int getMinXAtWY(int w, int y) { return y; }

	@Override
	default int getMaxXAtWY(int w, int y) { return w; }

	@Override
	default int getMinXAtWZ(int w, int z) { return z; }

	@Override
	default int getMaxXAtWZ(int w, int z) { return w; }

	@Override
	default int getMinXAtYZ(int y, int z) { return y; }

	@Override
	default int getMaxXAtYZ(int y, int z) { return getSize(); }

	@Override
	default int getMinXAtVWY(int v, int w, int y) { return y; }

	@Override
	default int getMaxXAtVWY(int v, int w, int y) { return w; }

	@Override
	default int getMinXAtVWZ(int v, int w, int z) { return z; }

	@Override
	default int getMaxXAtVWZ(int v, int w, int z) { return w; }

	@Override
	default int getMinXAtVYZ(int v, int y, int z) { return y; }

	@Override
	default int getMaxXAtVYZ(int v, int y, int z) { return v; }

	@Override
	default int getMinXAtWYZ(int w, int y, int z) { return y; }

	@Override
	default int getMaxXAtWYZ(int w, int y, int z) { return w; }

	@Override
	default int getMinX(int v, int w, int y, int z) { return y; }

	@Override
	default int getMaxX(int v, int w, int y, int z) { return w; }

	@Override
	default int getMinY() { return 0; }

	@Override
	default int getMaxY() { return getSize(); }

	@Override
	default int getMinYAtV(int v) { return 0; }

	@Override
	default int getMaxYAtV(int v) { return v; }

	@Override
	default int getMinYAtW(int w) { return 0; }

	@Override
	default int getMaxYAtW(int w) { return w; }

	@Override
	default int getMinYAtX(int x) { return 0; }

	@Override
	default int getMaxYAtX(int x) { return x; }

	@Override
	default int getMinYAtZ(int z) { return z; }

	@Override
	default int getMaxYAtZ(int z) { return getSize(); }

	@Override
	default int getMinYAtVW(int v, int w) { return 0; }

	@Override
	default int getMaxYAtVW(int v, int w) { return w; }

	@Override
	default int getMinYAtVX(int v, int x) { return 0; }

	@Override
	default int getMaxYAtVX(int v, int x) { return x; }

	@Override
	default int getMinYAtVZ(int v, int z) { return z; }

	@Override
	default int getMaxYAtVZ(int v, int z) { return v; }

	@Override
	default int getMinYAtWX(int w, int x) { return 0; }

	@Override
	default int getMaxYAtWX(int w, int x) { return x; }

	@Override
	default int getMinYAtWZ(int w, int z) { return z; }

	@Override
	default int getMaxYAtWZ(int w, int z) { return w; }

	@Override
	default int getMinYAtXZ(int x, int z) { return z; }

	@Override
	default int getMaxYAtXZ(int x, int z) { return x; }

	@Override
	default int getMinYAtVWX(int v, int w, int x) { return 0; }

	@Override
	default int getMaxYAtVWX(int v, int w, int x) { return x; }

	@Override
	default int getMinYAtVWZ(int v, int w, int z) { return z; }

	@Override
	default int getMaxYAtVWZ(int v, int w, int z) { return w; }

	@Override
	default int getMinYAtVXZ(int v, int x, int z) { return z; }

	@Override
	default int getMaxYAtVXZ(int v, int x, int z) { return x; }

	@Override
	default int getMinYAtWXZ(int w, int x, int z) { return z; }

	@Override
	default int getMaxYAtWXZ(int w, int x, int z) { return x; }

	@Override
	default int getMinY(int v, int w, int x, int z) { return z; }

	@Override
	default int getMaxY(int v, int w, int x, int z) { return x; }

	@Override
	default int getMinZ() { return 0; }

	@Override
	default int getMaxZ() { return getSize(); }

	@Override
	default int getMinZAtV(int v) { return 0; }

	@Override
	default int getMaxZAtV(int v) { return v; }

	@Override
	default int getMinZAtW(int w) { return 0; }

	@Override
	default int getMaxZAtW(int w) { return w; }

	@Override
	default int getMinZAtX(int x) { return 0; }

	@Override
	default int getMaxZAtX(int x) { return x; }

	@Override
	default int getMinZAtY(int y) { return 0; }

	@Override
	default int getMaxZAtY(int y) { return y; }

	@Override
	default int getMinZAtVW(int v, int w) { return 0; }

	@Override
	default int getMaxZAtVW(int v, int w) { return w; }

	@Override
	default int getMinZAtVX(int v, int x) { return 0; }

	@Override
	default int getMaxZAtVX(int v, int x) { return x; }

	@Override
	default int getMinZAtVY(int v, int y) { return 0; }

	@Override
	default int getMaxZAtVY(int v, int y) { return y; }

	@Override
	default int getMinZAtWX(int w, int x) { return 0; }

	@Override
	default int getMaxZAtWX(int w, int x) { return x; }

	@Override
	default int getMinZAtWY(int w, int y) { return 0; }

	@Override
	default int getMaxZAtWY(int w, int y) { return y; }

	@Override
	default int getMinZAtXY(int x, int y) { return 0; }

	@Override
	default int getMaxZAtXY(int x, int y) { return y; }

	@Override
	default int getMinZAtVWX(int v, int w, int x) { return 0; }

	@Override
	default int getMaxZAtVWX(int v, int w, int x) { return x; }

	@Override
	default int getMinZAtVWY(int v, int w, int y) { return 0; }

	@Override
	default int getMaxZAtVWY(int v, int w, int y) { return y; }

	@Override
	default int getMinZAtVXY(int v, int x, int y) { return 0; }

	@Override
	default int getMaxZAtVXY(int v, int x, int y) { return y; }

	@Override
	default int getMinZAtWXY(int w, int x, int y) { return 0; }

	@Override
	default int getMaxZAtWXY(int w, int x, int y) { return y; }

	@Override
	default int getMinZ(int v, int w, int x, int y) { return 0; }

	@Override
	default int getMaxZ(int v, int w, int x, int y) { return y; }
	
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
