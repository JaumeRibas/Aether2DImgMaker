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

public class HyperrectangularLongArrayGrid4D extends HyperrectangularArrayGrid4D implements LongModel4D {

	private final long[][][][] values;
	
	/**
	 * Constructs a {@code HyperrectangularLongArrayGrid4D} with the specified bounds
	 * 
	 * @param minW the smallest w-coordinate within the region
	 * @param minX the smallest x-coordinate within the region
	 * @param minY the smallest y-coordinate within the region
	 * @param minZ the smallest z-coordinate within the region
	 * @param values a 4D long array containing the values of the region
	 */
	public HyperrectangularLongArrayGrid4D(int minW, int minX, int minY, int minZ, long[][][][] values) {
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
	public long getFromPosition(int w, int x, int y, int z) {
		int i = w - minW;
		int j = x - minX;
		int k = y - minY;
		int l = z - minZ;
		return values[i][j][k][l];
	}
}
