/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
package cellularautomata.arrays;

import org.apache.commons.math3.FieldElement;

import cellularautomata.Coordinates;

public interface MultidimensionalNumericArray<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends MultidimensionalObjectArray<Number_Type> {

	/**
	 * <p>Adds a value to the position at the given indexes and returns the resulting value.</p>
	 * <p>It is not defined to call this method with an index count different form the dimension of the array. 
	 * These can be obtained by calling {@link Coordinates#getCount()} and {@link #getDimension()} respectively.</p>
	 * <p>It is also not defined to call this method with indexes outside the bounds of the array.</p>
	 * 
	 * @param indexes
	 * @param value
	 * @return
	 */
	Number_Type addAndGet(Coordinates indexes, Number_Type value);
	
}
