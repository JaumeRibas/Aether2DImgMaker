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

public class CuboidalObjectArrayGrid<Object_Type> extends CuboidalArrayGrid implements ObjectModel3D<Object_Type> {
	
	private Object_Type[][][] values;
	
	/**
	 * Constructs a {@code CuboidalObjectArrayGrid} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 * @param minY the smallest y-coordinate within the region
	 * @param minZ the smallest z-coordinate within the region
	 * @param values a 3D array containing the values of the region
	 */
	public CuboidalObjectArrayGrid(int minX, int minY, int minZ, Object_Type[][][] values) {
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
	public Object_Type getFromPosition(int x, int y, int z) {
		int i = x - minX;
		int j = y - minY;
		int k = z - minZ;
		return values[i][j][k];
	}
}
