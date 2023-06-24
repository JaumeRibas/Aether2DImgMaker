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
import cellularautomata.Utils;

/**
 * An hypercube shaped multidimensional array.
 * 
 * @author Jaume
 *
 */
public abstract class HypercubicArray extends HyperrectangularArray {

	protected int dimension;
	protected int side;
	
	/**
	 * Creates an hypercube shaped multidimensional array with the given dimension and side.
	 * 
	 * @param dimension
	 * @param side
	 */
	public HypercubicArray(int dimension, int side) {
		if (dimension < 0) {
			throw new IllegalArgumentException("Dimension cannot be smaller than zero.");
		}
		if (side < 0) {
			throw new NegativeArraySizeException();
		} else if (side == 0) {
			throw new UnsupportedOperationException("Arrays with zero positions are not supported.");
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
	public void forEachIndex(Consumer<? super Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] indexes = new int[dimension];
		if (dimension == 0) {
			consumer.accept(new Coordinates(indexes));
		} else {
			int sideMinusOne = side - 1;
			int currentAxis = 0;
			while (currentAxis < dimension) {
				if (currentAxis == 0) { 
					consumer.accept(new Coordinates(indexes));
				}
				int currentIndex = indexes[currentAxis];
				if (currentIndex < sideMinusOne) {
					currentIndex++;
					indexes[currentAxis] = currentIndex;
					currentAxis = 0;
				} else {
					indexes[currentAxis] = 0;
					currentAxis++;
				}
			}
		}
	}
	
	@Override
	public void forEachEvenIndex(Consumer<? super Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] indexes = new int[dimension];
		if (dimension == 0) {
			consumer.accept(new Coordinates(indexes));
		} else {
			int sideMinusOne = side - 1;
			int currentAxis = 0;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					int currentIndex = 0;
					indexes[0] = currentIndex;
					if (!Utils.isEvenPosition(indexes)) {
						currentIndex++;
					}
					for (; currentIndex <= sideMinusOne; currentIndex += 2) {//TODO fix infinite loop if sideMinusOne >= Integer.MAX_VALUE - 1
						indexes[0] = currentIndex;
						consumer.accept(new Coordinates(indexes));
					}
					currentAxis = 1;
				} else {
					int currentIndex = indexes[currentAxis];
					if (currentIndex < sideMinusOne) {
						currentIndex++;
						indexes[currentAxis] = currentIndex;
						currentAxis = 0;
					} else {
						indexes[currentAxis] = 0;
						currentAxis++;
					}
				}
			}
		}
	}
	
	@Override
	public void forEachOddIndex(Consumer<? super Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int[] indexes = new int[dimension];
		if (dimension == 0) {
			consumer.accept(new Coordinates(indexes));
		} else {
			int sideMinusOne = side - 1;
			int currentAxis = 0;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					int currentIndex = 0;
					indexes[0] = currentIndex;
					if (Utils.isEvenPosition(indexes)) {
						currentIndex++;
					}
					for (; currentIndex <= sideMinusOne; currentIndex += 2) {//TODO fix infinite loop if sideMinusOne >= Integer.MAX_VALUE - 1
						indexes[0] = currentIndex;
						consumer.accept(new Coordinates(indexes));
					}
					currentAxis = 1;
				} else {
					int currentIndex = indexes[currentAxis];
					if (currentIndex < sideMinusOne) {
						currentIndex++;
						indexes[currentAxis] = currentIndex;
						currentAxis = 0;
					} else {
						indexes[currentAxis] = 0;
						currentAxis++;
					}
				}
			}
		}
	}
	
	@Override
	public void forEachEdgeIndex(int edgeWidth, Consumer<? super Coordinates> consumer) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater than or equal to one.");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		if (dimension > 0 && side <= 2*edgeWidth) {
			forEachIndex(consumer);
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
				forEachIndexWithinBounds(upperBounds, lowerBounds, consumer);
				upperBounds[i] = realUpperBound;
				//bottom edge
				lowerBounds[i] = upperBounds[i] - edgeWidthMinusOne;
				forEachIndexWithinBounds(upperBounds, lowerBounds, consumer);
				//new bounds to prevent repeating positions
				upperBounds[i] = lowerBounds[i] - 1;
				lowerBounds[i] = edgeWidth;
			}
		}		
	}
	
	@Override
	public void forEachEvenEdgeIndex(int edgeWidth, Consumer<? super Coordinates> consumer) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater than or equal to one.");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		if (dimension > 0 && side <= 2*edgeWidth) {
			forEachEvenIndex(consumer);
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
				forEachEvenIndexWithinBounds(upperBounds, lowerBounds, consumer);
				upperBounds[i] = realUpperBound;
				//bottom edge
				lowerBounds[i] = upperBounds[i] - edgeWidthMinusOne;
				forEachEvenIndexWithinBounds(upperBounds, lowerBounds, consumer);
				//new bounds to prevent repeating positions
				upperBounds[i] = lowerBounds[i] - 1;
				lowerBounds[i] = edgeWidth;
			}
		}		
	}
	
	@Override
	public void forEachOddEdgeIndex(int edgeWidth, Consumer<? super Coordinates> consumer) {
		if (edgeWidth < 1) {
			throw new IllegalArgumentException("The edge width must be greater than or equal to one.");
		}
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		if (dimension > 0 && side <= 2*edgeWidth) {
			forEachOddIndex(consumer);
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
				forEachOddIndexWithinBounds(upperBounds, lowerBounds, consumer);
				upperBounds[i] = realUpperBound;
				//bottom edge
				lowerBounds[i] = upperBounds[i] - edgeWidthMinusOne;
				forEachOddIndexWithinBounds(upperBounds, lowerBounds, consumer);
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
	public long getPositionCount() {
		return (long) Math.pow(side, dimension);
	}
	
	public static long getPositionCount(int dimension, int side) {
		return (long) Math.pow(side, dimension);
	}

	@Override
	public int getSize(int axis, Coordinates indexes) {
		return side;
	}
}
