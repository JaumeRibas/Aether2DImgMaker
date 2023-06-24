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

import java.util.function.Consumer;
import cellularautomata.Coordinates;

public interface MultidimensionalBooleanArray extends MultidimensionalArray {
	
	/**
	 * Feeds every value of the array to a {@link Consumer}.
	 * 
	 * @param consumer
	 */
	void forEach(Consumer<? super Boolean> consumer);
	
	/**
	 * <p>Gets the value at the given indexes.</p>
	 * <p>It is not defined to call this method with an index count different form the dimension of the array. 
	 * These can be obtained by calling {@link Coordinates#getCount()} and {@link #getDimension()} respectively.</p>
	 * <p>It is also not defined to call this method with indexes outside the bounds of the array.</p>
	 * 
	 * @param indexes
	 * @return
	 */
	boolean get(Coordinates indexes);

	/**
	 * <p>Sets the value at the given indexes.</p>
	 * <p>It is not defined to call this method with an index count different form the dimension of the array. 
	 * These can be obtained by calling {@link Coordinates#getCount()} and {@link #getDimension()} respectively.</p>
	 * <p>It is also not defined to call this method with indexes outside the bounds of the array.</p>
	 * 
	 * @param indexes
	 * @param value
	 */
	void set(Coordinates indexes, boolean value);

	/**
	 * Sets the value at all positions of the array.
	 * 
	 * @param indexes
	 */
	void fill(boolean value);
	
}
