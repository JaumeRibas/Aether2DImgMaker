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

import org.apache.commons.math3.FieldElement;

public class HyperrectangularNumericArrayGrid5D<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends HyperrectangularObjectArrayGrid5D<Number_Type> implements NumericModel5D<Number_Type> {

	/**
	 * Constructs a {@code HyperrectangularNumericArrayGrid5D} with the specified bounds
	 * 
	 * @param minV the smallest v-coordinate within the region
	 * @param minW the smallest w-coordinate within the region
	 * @param minX the smallest x-coordinate within the region
	 * @param minY the smallest y-coordinate within the region
	 * @param minZ the smallest z-coordinate within the region
	 * @param values a 5D array containing the values of the region
	 */
	public HyperrectangularNumericArrayGrid5D(int minV, int minW, int minX, int minY, int minZ, Number_Type[][][][][] values) {
		super(minV, minW, minX, minY, minZ, values);
	}
	
}
