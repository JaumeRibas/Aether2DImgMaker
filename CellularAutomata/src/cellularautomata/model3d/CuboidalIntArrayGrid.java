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

import cellularautomata.Utils;

public class CuboidalIntArrayGrid extends CuboidalArrayGrid implements IntModel3D {
	
	private int[][][] values;
	
	/**
	 * Constructs a {@code CuboidalIntArrayGrid} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 * @param minY the smallest y-coordinate within the region
	 * @param minZ the smallest z-coordinate within the region
	 * @param values a 3D int array containing the values of the region
	 */
	public CuboidalIntArrayGrid(int minX, int minY, int minZ, int[][][] values) {
		super(minX, minY, minZ);
		if (!Utils.isHyperrectangular(values)) {
			throw new IllegalArgumentException("The values array must be cuboidal.");
		}
		if (values.length == 0 || values[0].length == 0 || values[0][0].length == 0) {
			throw new IllegalArgumentException("The values array cannot be empty.");
		}
		setMaxCoordinates(values.length, values[0].length, values[0][0].length);
		this.values = values;
	}

	@Override
	public int getFromPosition(int x, int y, int z) {
		int i = x - minX;
		int j = y - minY;
		int k = z - minZ;
		return values[i][j][k];
	}
}
