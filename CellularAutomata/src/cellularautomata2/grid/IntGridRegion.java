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
package cellularautomata2.grid;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import cellularautomata.grid.Coordinates;
import cellularautomata.grid.PartialCoordinates;

public abstract class IntGridRegion implements GridRegion {
	
	/**
	 * <p>Returns the value at the given coordinates.</p>
	 * <p>It is not defined to call this method with coordinates of a dimension different form the grid's dimension. This is obtained by calling the {@link #getGridDimension()} method.
	 * <p>It is also not defined to call this method passing coordinates outside the bounds of the region. 
	 * To get these bounds use the {@link #getBounds(int)} and {@link #getBounds(int, Integer[])} methods.</p>
	 * 
	 * @param coordinates a {@link int} array
	 * @return the value at the given position.
	 */
	public abstract int getValue(Coordinates coordinates);
	
	/**
	 * Feeds every value of the region to an {@link IntConsumer}.
	 * @param consumer
	 */
	public abstract void forEachValue(IntConsumer consumer);

	public Bounds getValueBounds() throws Exception {
		GetValueBoundsValueConsumer consumer = new GetValueBoundsValueConsumer();
		forEachValue(consumer);
		return new Bounds(consumer.lowerBound, consumer.upperBound);
	}
	
	public Bounds getEvenPositionsValueBounds() throws Exception {
		GetValueBoundsCoordinateConsumer consumer = new GetValueBoundsCoordinateConsumer(this);
		forEachEvenPosition(consumer);
		return new Bounds(consumer.lowerBound, consumer.upperBound);
	}
	
	public Bounds getOddPositionsValueBounds() throws Exception {
		GetValueBoundsCoordinateConsumer consumer = new GetValueBoundsCoordinateConsumer(this);
		forEachOddPosition(consumer);
		return new Bounds(consumer.lowerBound, consumer.upperBound);
	}
	
	public long getTotalValue() throws Exception {
		GetTotalValueConsumer consumer = new GetTotalValueConsumer();
		forEachValue(consumer);
		return consumer.totalValue;
	}
	
	public String toString2DCrossSection(int horizontalAxis, int verticalAxis, Coordinates coordinates) {
		return toString2DCrossSection(horizontalAxis, verticalAxis, coordinates, true, true);
	}	
	
	public String toString2DCrossSection(int horizontalAxis, 
			int verticalAxis, Coordinates coordinates, boolean horizontalDirectionAscending, boolean verticalDirectionAscending) {
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
			if (coordinate < getLowerBound(axis, immutablePartialCoordinates)
					|| coordinate > getUpperBound(axis, immutablePartialCoordinates)) {
				throw new IllegalArgumentException(outOfBoundsMessage);
			}
			partialCoordinates[axis] = coordinate;
		}
		for (axis++; axis < secondCrossSectionAxis; axis++) {
			int coordinate = coordinatesArray[axis];
			if (coordinate < getLowerBound(axis, immutablePartialCoordinates)
					|| coordinate > getUpperBound(axis, immutablePartialCoordinates)) {
				throw new IllegalArgumentException(outOfBoundsMessage);
			}
			partialCoordinates[axis] = coordinate;
		}
		for (axis++; axis < gridDimension; axis++) {
			int coordinate = coordinatesArray[axis];
			if (coordinate < getLowerBound(axis, immutablePartialCoordinates)
					|| coordinate > getUpperBound(axis, immutablePartialCoordinates)) {
				throw new IllegalArgumentException(outOfBoundsMessage);
			}
			partialCoordinates[axis] = coordinate;
		}
		int maxVerticalCoordinate = getUpperBound(verticalAxis, immutablePartialCoordinates);
		int minVerticalCoordinate = getLowerBound(verticalAxis, immutablePartialCoordinates);
//		int maxHorizontalCoordinate = getUpperBound(horizontalAxis, immutablePartialCoordinates);
//		int minHorizontalCoordinate = getLowerBound(horizontalAxis, immutablePartialCoordinates);
		StringBuilder strBuilder = new StringBuilder();
		//TODO use directions
		Coordinates immutableCoordinates = new Coordinates(coordinatesArray);
		for (int verticalCoordinate = maxVerticalCoordinate; verticalCoordinate >= minVerticalCoordinate; verticalCoordinate--) {
			partialCoordinates[verticalAxis] = verticalCoordinate;
			coordinatesArray[verticalAxis] = verticalCoordinate;
			int localMinHorizontalCoordinate = getLowerBound(horizontalAxis, immutablePartialCoordinates);
			int localMaxHorizontalCoordinate = getUpperBound(horizontalAxis, immutablePartialCoordinates);
			//TODO use margins
//			int localHorizontalMarginLowerEnd = localMinHorizontalCoordinate - minHorizontalCoordinate;
//			int localHorizontalMarginUpperEnd = maxHorizontalCoordinate - localMaxHorizontalCoordinate;
			for (int horizontalCoordinate = localMinHorizontalCoordinate; horizontalCoordinate <= localMaxHorizontalCoordinate; horizontalCoordinate++) {
				coordinatesArray[horizontalAxis] = horizontalCoordinate;
				strBuilder.append(getValue(immutableCoordinates)).append(",");
			}
			strBuilder.append(System.lineSeparator());
		}
		return strBuilder.toString();
	}
	
	class GetValueBoundsValueConsumer implements IntConsumer {
		public int lowerBound = Integer.MAX_VALUE;
		public int upperBound = Integer.MIN_VALUE;
		
		@Override
		public void accept(int value) {
			if (value > upperBound)
				upperBound = value;
			if (value < lowerBound)
				lowerBound = value;
		}
	}
	
	class GetValueBoundsCoordinateConsumer implements Consumer<Coordinates> {
		public int lowerBound = Integer.MAX_VALUE;
		public int upperBound = Integer.MIN_VALUE;
		private IntGridRegion region;
		
		public GetValueBoundsCoordinateConsumer(IntGridRegion region) {
			this.region = region;
		}
		
		@Override
		public void accept(Coordinates coordinates) {
			int value = region.getValue(coordinates);
			if (value > upperBound)
				upperBound = value;
			if (value < lowerBound)
				lowerBound = value;
		}
	}
	
	class GetTotalValueConsumer implements IntConsumer {
		public int totalValue = 0;
		
		@Override
		public void accept(int value) {
			totalValue += value;
		}
	}
}
