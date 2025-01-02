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
package cellularautomata.model3d;

/**
 * An asymmetric section of an isotropic region of a 3D grid with its center at the origin of coordinates.
 * The asymmetric section is the one within the region where x >= y >= z >= 0.
 *  
 * @author Jaume
 *
 */
public interface AnisotropicModel3DA extends Model3D {
	
	@Override
	default int getMinX() { return 0; }

	@Override
	default int getMinXAtY(int y) { return y; }

	@Override
	default int getMaxXAtY(int y) { return getMaxX(); }

	@Override
	default int getMinXAtZ(int z) { return z; }

	@Override
	default int getMaxXAtZ(int z) { return getMaxX(); }

	@Override
	default int getMinX(int y, int z) { return y; }

	@Override
	default int getMaxX(int y, int z) { return getMaxX(); }

	@Override
	default int getMinY() { return 0; }

	@Override
	default int getMinYAtX(int x) { return 0; }

	@Override
	default int getMaxYAtX(int x) { return Math.min(x, getMaxY()); }

	@Override
	default int getMinYAtZ(int z) { return z; }

	@Override
	default int getMaxYAtZ(int z) { return getMaxY(); }

	@Override
	default int getMinY(int x, int z) { return z; }

	@Override
	default int getMaxY(int x, int z) { return Math.min(x, getMaxY()); }

	@Override
	default int getMinZ() { return 0; }

	@Override
	default int getMinZAtX(int x) { return 0; }

	@Override
	default int getMaxZAtX(int x) { return Math.min(x, getMaxZ()); }

	@Override
	default int getMinZAtY(int y) { return 0; }

	@Override
	default int getMaxZAtY(int y) { return Math.min(y, getMaxZ()); }

	@Override
	default int getMinZ(int x, int y) { return 0; }

	@Override
	default int getMaxZ(int x, int y) { return Math.min(y, getMaxZ()); }
	
}
