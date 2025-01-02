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
package cellularautomata.model4d;

import cellularautomata.Utils;

public class HyperrectangularObjectArrayGrid4D<Object_Type> extends HyperrectangularArrayGrid4D implements ObjectModel4D<Object_Type> {

	private Object_Type[][][][] values;
	
	/**
	 * Constructs a {@code HyperrectangularObjectArrayGrid4D} with the specified bounds
	 * 
	 * @param minW the smallest w-coordinate within the region
	 * @param minX the smallest x-coordinate within the region
	 * @param minY the smallest y-coordinate within the region
	 * @param minZ the smallest z-coordinate within the region
	 * @param values a 4D array containing the values of the region
	 */
	public HyperrectangularObjectArrayGrid4D(int minW, int minX, int minY, int minZ, Object_Type[][][][] values) {
		super(minW, minX, minY, minZ);
		if (!Utils.isHyperrectangular(values)) {
			throw new IllegalArgumentException("The values array must be hyperrectangular.");
		}
		if (values.length == 0 || values[0].length == 0 || values[0][0].length == 0 || values[0][0][0].length == 0) {
			throw new IllegalArgumentException("The values array cannot be empty.");
		}
		setMaxCoordinates(values.length, values[0].length, values[0][0].length, values[0][0][0].length);
		this.values = values;
	}

	@Override
	public Object_Type getFromPosition(int w, int x, int y, int z) {
		int i = w - minW;
		int j = x - minX;
		int k = y - minY;
		int l = z - minZ;
		return values[i][j][k][l];
	}
}
