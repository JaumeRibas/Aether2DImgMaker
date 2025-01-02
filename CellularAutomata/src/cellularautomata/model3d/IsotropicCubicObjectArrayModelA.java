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

public abstract class IsotropicCubicObjectArrayModelA<Object_Type> implements SymmetricObjectModel3D<Object_Type>, IsotropicCubicModelA {

	/** A 3D array representing the grid */
	protected Object_Type[][][] grid;

	@Override
	public Object_Type getFromPosition(int x, int y, int z) {	
		if (x < 0) x = -x;
		if (y < 0) y = -y;
		if (z < 0) z = -z;
		if (x >= y) {
			if (y >= z) {
				//x >= y >= z
				return grid[x][y][z];
			} else if (x >= z) { 
				//x >= z > y
				return grid[x][z][y];
			} else {
				//z > x >= y
				return grid[z][x][y];
			}
		} else if (y >= z) {
			if (x >= z) {
				//y > x >= z
				return grid[y][x][z];
			} else {
				//y >= z > x
				return grid[y][z][x];
			}
		} else {
			// z > y > x
			return grid[z][y][x];
		}
	}
	
	@Override
	public Object_Type getFromAsymmetricPosition(int x, int y, int z) {	
		return grid[x][y][z];
	}

}
