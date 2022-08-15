/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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

public interface MultidimensionalArray {
	
	/**
	 * Returns the dimension of the array.
	 * 
	 * @return the dimension of the array.
	 */
	int getDimension();	

	/**
	 * Returns the number of positions of the array
	 * 
	 * @return
	 */
	default long getPositionCount() {
		long positionCount = 0;
		int dimension = getDimension();
		if (dimension < 0) {
			throw new UnsupportedOperationException("Dimension cannot be smaller than zero.");
		}
		if (dimension == 0) {
			positionCount++;
		} else {
			int[] indexes = new int[dimension];
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			int sizeMinusOne = 0;
			int previousSizeAxis = 0;
			if (currentAxis != 0) {
				previousSizeAxis = currentAxis - 1;
				sizeMinusOne = getSize(previousSizeAxis, new Coordinates(indexes)) - 1;
			}
			while (currentAxis != -1) {
				if (currentAxis == dimensionMinusOne) { 
					positionCount += getSize(currentAxis, new Coordinates(indexes));
					currentAxis--;
					if (previousSizeAxis != currentAxis && currentAxis != -1) {
						previousSizeAxis = currentAxis;
						sizeMinusOne = getSize(currentAxis, new Coordinates(indexes)) - 1;
					}
				} else {
					int currentIndex = indexes[currentAxis];
					if (currentIndex < sizeMinusOne) {
						currentIndex++;
						indexes[currentAxis] = currentIndex;
						currentAxis = dimensionMinusOne;
					} else {
						indexes[currentAxis] = 0;
						currentAxis--;
						if (currentAxis != -1) {
							previousSizeAxis = currentAxis;
							sizeMinusOne = getSize(currentAxis, new Coordinates(indexes)) - 1;
						}
					}
				}
			}
		}
		return positionCount;
	}
	
	/**
	 * Feeds every index of the array to a {@link Consumer<Coordinates>}.
	 * 
	 * @param consumer
	 */
	default void forEachIndex(Consumer<Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int dimension = getDimension();
		if (dimension < 0) {
			throw new IllegalArgumentException("Dimension cannot be smaller than zero.");
		}
		int[] indexes = new int[dimension];
		if (dimension == 0) {
			Coordinates indexesObj = new Coordinates(indexes);
			consumer.accept(indexesObj);
		} else {
			int dimensionMinusOne = dimension - 1;
			int currentAxis = dimensionMinusOne;
			int sizeMinusOne = getSize(currentAxis, new Coordinates(indexes)) - 1;
			while (currentAxis != -1) {
				if (currentAxis == dimensionMinusOne) { 
					consumer.accept(new Coordinates(indexes));
				}
				int previousAxis = currentAxis;
				int currentIndex = indexes[currentAxis];
				if (currentIndex < sizeMinusOne) {
					currentIndex++;
					indexes[currentAxis] = currentIndex;
					currentAxis = dimensionMinusOne;
				} else {
					indexes[currentAxis] = 0;
					currentAxis--;
				}
				if (previousAxis != currentAxis && currentAxis != -1) {
					sizeMinusOne = getSize(currentAxis, new Coordinates(indexes)) - 1;
				}
			}
		}
	}	
	
	/**
	 * <p>Returns the array's size on the passed axis at the passed indexes.</p>
	 * <p>Only the indexes on the axes smaller than the passed axis are used.</p>
	 * <p>It is not defined to pass indexes outside the bounds of the array.</p>
	 * 
	 * @param  axis the axis on which the size is requested.
	 * @param indexes a {@link Coordinates} object.
	 * @return  the size
	 */
	int getSize(int axis, Coordinates indexes);
	
}
