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
package cellularautomata2.arrays;

/**
 * A square-like multidimensional array.
 * 
 * @author Jaume
 *
 */
public abstract class SquareArray extends RectangularArray {

	protected int dimension;
	protected int side;
	
	/**
	 * Creates a square-like multidimensional array with the given dimension and side.
	 * 
	 * @param dimension
	 * @param side
	 */
	public SquareArray(int dimension, int side) {
		if (dimension < 0) {
			throw new IllegalArgumentException("Dimension cannot be smaller than zero.");
		}
		if (side < 0) {
			throw new NegativeArraySizeException();
		}
		this.dimension = dimension;
		this.side = side;
	}
	
	public int getSide() {
		return side;
	}
	
	@Override
	protected int getInternalArrayIndex(Coordinates indexes) {
		int internalIndex = 0;
		for (int partialDimension = dimension - 1; partialDimension > -1; partialDimension--) {
			internalIndex += indexes.get(partialDimension) * Math.pow(side, partialDimension);
		}
		return internalIndex;
	}
	
	@Override
	public void forEachIndex(PositionCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The command cannot be null.");
		}
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		int sideMinusOne = side - 1;
		int currentAxis = 0;
		while (currentAxis < dimension) {
			if (currentAxis == 0) {
				for (int currentCoordinate = 0; currentCoordinate < side; currentCoordinate++) {
					coordinates[0] = currentCoordinate;
					command.execute(immutableCoordinates);
				}
				currentAxis++;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				if (currentCoordinate < sideMinusOne) {
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					currentAxis = 0;
				} else {
					coordinates[currentAxis] = 0;
					currentAxis++;
				}
			}
		}
	}
	
	@Override
	public void forEachEdgeIndex(int edgeWidth, PositionCommand command) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater than or equal to one.");
		}
		if (command == null) {
			throw new IllegalArgumentException("The command cannot be null.");
		}
		if (side <= 2*edgeWidth) {
			forEachIndex(command);
		} else {
			int[] upperBounds = new int[dimension];
			int[] lowerBounds = new int[dimension];
			int sideMinusOne = side - 1;  
			for (int i = 0; i < dimension; i++) {
				upperBounds[i] = sideMinusOne;
				//lowerBounds[i] = 0; default value
			}
			int edgeWidthMinusOne = edgeWidth - 1;
			for (int i = 0; i < dimension; i++) {
				//top edge
				int realUpperBound = upperBounds[i];
				upperBounds[i] = edgeWidthMinusOne;
				forEachIndexWithinBounds(upperBounds, lowerBounds, command);
				upperBounds[i] = realUpperBound;
				//bottom edge
				lowerBounds[i] = upperBounds[i] - edgeWidthMinusOne;
				forEachIndexWithinBounds(upperBounds, lowerBounds, command);
				//new bounds to prevent repeating positions
				upperBounds[i] = lowerBounds[i] - 1;
				lowerBounds[i] = edgeWidth;
			}
		}		
	}

	@Override
	public int getDimension() {
		return dimension;
	}

	@Override
	public int getSize(int axis) {
		return side;
	}
	
	@Override
	public long getVolume() {
		return (long) Math.pow(side, dimension);
	}
	
	public static long getVolume(int dimension, int side) {
		return (long) Math.pow(side, dimension);
	}
}
