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

/**
 * An asymmetric section of an isotropic region of a 2D grid with center at the origin of coordinates.
 * The asymmetric section is the one within the region where x >= y >= 0.
 *  
 * @author Jaume
 *
 */
public interface AnisotropicModel2DA extends Model2D {

	@Override
	default int getMinX() { return 0; }

	@Override
	default int getMinX(int y) { return y; }

	@Override
	default int getMaxX(int y) { return getMaxX(); }

	@Override
	default int getMinY() { return 0; }

	@Override
	default int getMinY(int x) { return 0; }

	@Override
	default int getMaxY(int x) { return Math.min(x, getMaxY()); }
	
}
