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

import cellularautomata.Utils;

public class HyperrectangularObjectArrayGrid5D<Object_Type> extends HyperrectangularArrayGrid5D implements ObjectModel5D<Object_Type> {

	private Object_Type[][][][][] values;
	
	/**
	 * Constructs a {@code HyperrectangularLongArrayGrid5D} with the specified bounds
	 * 
	 * @param minV the smallest v-coordinate within the region
	 * @param minW the smallest w-coordinate within the region
	 * @param minX the smallest x-coordinate within the region
	 * @param minY the smallest y-coordinate within the region
	 * @param minZ the smallest z-coordinate within the region
	 * @param values a 5D array containing the values of the region
	 */
	public HyperrectangularObjectArrayGrid5D(int minV, int minW, int minX, int minY, int minZ, Object_Type[][][][][] values) {
		super(minV, minW, minX, minY, minZ);
		if (!Utils.isHyperrectangular(values)) {
			throw new IllegalArgumentException("The values array must be hyperrectangular.");
		}
		if (values.length == 0 || values[0].length == 0 || values[0][0].length == 0 || values[0][0][0].length == 0 || values[0][0][0][0].length == 0) {
			throw new IllegalArgumentException("The values array cannot be empty.");
		}
		setMaxCoordinates(values.length, values[0].length, values[0][0].length, values[0][0][0].length, values[0][0][0][0].length);
		this.values = values;
	}

	@Override
	public Object_Type getFromPosition(int v, int w, int x, int y, int z) {
		int i = v - minV;
		int j = w - minW;
		int k = x - minX;
		int l = y - minY;
		int m = z - minZ;
		return values[i][j][k][l][m];
	}
}
