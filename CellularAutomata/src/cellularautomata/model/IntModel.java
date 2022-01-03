/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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
package cellularautomata.model;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface IntModel extends SequentialIntModel {
	
	/**
	 * <p>Returns the value at the given coordinates.</p>
	 * <p>It is not defined to call this method with coordinates of a dimension different form the grid's dimension. This is obtained by calling the {@link #getGridDimension()} method.
	 * <p>It is also not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getMaxCoordinate(int)}, {@link #getMaxCoordinate(int, PartialCoordinates)}, 
	 * {@link #getMinCoordinate(int)} and {@link #getMinCoordinate(int, PartialCoordinates)} methods.</p>
	 * 
	 * @param coordinates a {@link Coordinates} object
	 * @return the value at the given position.
	 * @throws Exception 
	 */
	int getFromPosition(Coordinates coordinates) throws Exception;
	
	/**
	 * Feeds every value of the region, in a consistent order, to an {@link IntConsumer}.
	 * @param consumer
	 */
	default void forEach(IntConsumer consumer) {
		forEachPosition(new Consumer<Coordinates>() {
			
			@Override
			public void accept(Coordinates coords) {
				try {
					consumer.accept(getFromPosition(coords));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
	}
	
	default int[] getEvenPositionsMinAndMax() throws Exception {
		IntModelMinAndMaxCoordinateConsumer consumer = new IntModelMinAndMaxCoordinateConsumer(this);
		forEachEvenPosition(consumer);
		return new int[] {consumer.min, consumer.max};
	}
	
	default int[] getOddPositionsMinAndMax() throws Exception {
		IntModelMinAndMaxCoordinateConsumer consumer = new IntModelMinAndMaxCoordinateConsumer(this);
		forEachOddPosition(consumer);
		return new int[] {consumer.min, consumer.max};
	}
	
	//TODO review
	default String toString2DCrossSection(int horizontalAxis, int verticalAxis, Coordinates coordinates) throws Exception {
		return toString2DCrossSection(horizontalAxis, verticalAxis, coordinates, true, true);
	}	
	
	default String toString2DCrossSection(int horizontalAxis, 
			int verticalAxis, Coordinates coordinates, boolean horizontalDirectionAscending, boolean verticalDirectionAscending) throws Exception {
		//TODO add parameters for vertical and horizontal bounds (partial?)
		if (horizontalAxis == verticalAxis) {
			throw new IllegalArgumentException("Horizontal and vertical axes must be different.");
		}
		int gridDimension = getGridDimension();
		int lastAxis = gridDimension - 1;
		if (horizontalAxis < 0 || horizontalAxis > lastAxis) {
			throw new IllegalArgumentException("Horizontal axis doesn't exist.");
		}
		if (verticalAxis < 0 || verticalAxis > lastAxis) {
			throw new IllegalArgumentException("Vertical axis doesn't exist.");
		}
		int[] coordinatesArray = coordinates.getCopyAsArray();
		if (gridDimension != coordinatesArray.length) {
			throw new IllegalArgumentException("Region's grid dimension (" 
					+ gridDimension + ") doesn't match coordinate count (" 
					+ coordinatesArray.length + ")");
		}
		int firstCrossSectionAxis;
		int secondCrossSectionAxis;
		if (horizontalAxis < verticalAxis) {
			firstCrossSectionAxis = horizontalAxis;
			secondCrossSectionAxis = verticalAxis;
		} else {
			firstCrossSectionAxis = verticalAxis;
			secondCrossSectionAxis = horizontalAxis;
		}
		Integer[] partialCoordinates = new Integer[gridDimension];//filled with nulls by default
		PartialCoordinates immutablePartialCoordinates = new PartialCoordinates(partialCoordinates);
		//check whether or not coordinates are outside the region
		final String outOfBoundsMessage = "Coordinates are outside the region.";
		int axis = 0;
		for (; axis < firstCrossSectionAxis; axis++) {
			int coordinate = coordinatesArray[axis];
			if (coordinate < getMinCoordinate(axis, immutablePartialCoordinates)
					|| coordinate > getMaxCoordinate(axis, immutablePartialCoordinates)) {
				throw new IllegalArgumentException(outOfBoundsMessage);
			}
			partialCoordinates[axis] = coordinate;
		}
		for (axis++; axis < secondCrossSectionAxis; axis++) {
			int coordinate = coordinatesArray[axis];
			if (coordinate < getMinCoordinate(axis, immutablePartialCoordinates)
					|| coordinate > getMaxCoordinate(axis, immutablePartialCoordinates)) {
				throw new IllegalArgumentException(outOfBoundsMessage);
			}
			partialCoordinates[axis] = coordinate;
		}
		for (axis++; axis < gridDimension; axis++) {
			int coordinate = coordinatesArray[axis];
			if (coordinate < getMinCoordinate(axis, immutablePartialCoordinates)
					|| coordinate > getMaxCoordinate(axis, immutablePartialCoordinates)) {
				throw new IllegalArgumentException(outOfBoundsMessage);
			}
			partialCoordinates[axis] = coordinate;
		}
		int maxVerticalCoordinate = getMaxCoordinate(verticalAxis, immutablePartialCoordinates);
		int minVerticalCoordinate = getMinCoordinate(verticalAxis, immutablePartialCoordinates);
//		int maxHorizontalCoordinate = getUpperBound(horizontalAxis, immutablePartialCoordinates);
//		int minHorizontalCoordinate = getLowerBound(horizontalAxis, immutablePartialCoordinates);
		StringBuilder strBuilder = new StringBuilder();
		//TODO use directions
		Coordinates immutableCoordinates = new Coordinates(coordinatesArray);
		for (int verticalCoordinate = maxVerticalCoordinate; verticalCoordinate >= minVerticalCoordinate; verticalCoordinate--) {
			partialCoordinates[verticalAxis] = verticalCoordinate;
			coordinatesArray[verticalAxis] = verticalCoordinate;
			int localMinHorizontalCoordinate = getMinCoordinate(horizontalAxis, immutablePartialCoordinates);
			int localMaxHorizontalCoordinate = getMaxCoordinate(horizontalAxis, immutablePartialCoordinates);
			//TODO use margins
//			int localHorizontalMarginLowerEnd = localMinHorizontalCoordinate - minHorizontalCoordinate;
//			int localHorizontalMarginUpperEnd = maxHorizontalCoordinate - localMaxHorizontalCoordinate;
			for (int horizontalCoordinate = localMinHorizontalCoordinate; horizontalCoordinate <= localMaxHorizontalCoordinate; horizontalCoordinate++) {
				coordinatesArray[horizontalAxis] = horizontalCoordinate;
				strBuilder.append(getFromPosition(immutableCoordinates)).append(",");
			}
			strBuilder.append(System.lineSeparator());
		}
		return strBuilder.toString();
	}

	@Override
	default Iterator<Integer> iterator() {
		return new IntModelIterator(this);
	}
	
}
