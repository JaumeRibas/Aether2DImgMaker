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
package cellularautomata.grid2d;

/**
 * Represents a fixed size 2D region of a grid backed by a 2D array.
 * The shape and orientation of the region is such that no line parallel to an axis crosses its bounds in more than two places.
 * 
 * @author Jaume
 *
 */
public class ArrayGrid2D<T> implements ObjectGrid2D<T> {

	private T[][] values;
	private int minX;
	private int maxX;
	private int[] localYMinima;
	private int minY;
	private int maxY;
	
	private static final String UNSUPPORTED_SHAPE_ERROR = 
			"The shape and orientation of the region must be such that no line parallel to an axis crosses its bounds in more than two places.";
	
	/**
	 * Constructs an {@code ArrayGrid2D} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 * @param localYMinima an array of the smallest y-coordinates at each x-coordinate of the region. Beginning at {@code minX}.
	 * @param values a 2D array containing the values of the region
	 */
	public ArrayGrid2D(int minX, int[] localYMinima, T[][] values) {
		if (localYMinima.length != values.length) {
			throw new IllegalArgumentException("Local y minima's length must be equal to values' length.");
		}
		if (localYMinima.length == 0) {
			throw new IllegalArgumentException("Local y minima's length cannot be 0.");
		}
		long longMaxX = (long)localYMinima.length + minX - 1;
		if (longMaxX > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max x (" + longMaxX + ") is too big. It cannot be bigger than " + Integer.MAX_VALUE + ".");
		}
		this.minX = minX;
		maxX = (int)longMaxX;
		this.localYMinima = localYMinima;
		this.values = values;
		boolean minimaAscending = false;
		boolean maximaDescending = false;
		int previousLocalMinY = localYMinima[0];
		int previousLocalMaxY = values[0].length + previousLocalMinY - 1;
		minY = previousLocalMinY;
		maxY = previousLocalMaxY;
		for (int i = 0; i < values.length; i++) {
			int localMinY = localYMinima[i];
			long longLocalMaxY = (long)values[i].length + localMinY - 1;
			if (longLocalMaxY > Integer.MAX_VALUE) {
				throw new IllegalArgumentException(
						"Resulting max y at index " + i + " (" + longLocalMaxY + ") is too big. It cannot be bigger than " + Integer.MAX_VALUE + ".");
			}
			int localMaxY = (int)longLocalMaxY;
			if (minimaAscending) {
				if (localMinY < previousLocalMinY) {
					throw new IllegalArgumentException("Unsupported local y minima. " + UNSUPPORTED_SHAPE_ERROR);
				}
			} else if (localMinY > previousLocalMinY) {
				minimaAscending = true;
			} else if (localMinY < minY) {
				minY = localMinY;
			}
			if (maximaDescending) {
				if (localMaxY > previousLocalMaxY) {
					throw new IllegalArgumentException("Unsupported resulting local y maxima. " + UNSUPPORTED_SHAPE_ERROR);
				}
			} else if (localMaxY < previousLocalMaxY) {
				maximaDescending = true;
			} else if (localMaxY > maxY) {
				maxY = localMaxY;
			}
			previousLocalMinY = localMinY;
			previousLocalMaxY = localMaxY;
		}
	}
	
	@Override
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMinY() {
		return minY;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}
	
	@Override
	public int getMinX(int y) {
		int localMinX = minX;
		int edgeMinY = localYMinima[0];
		int edgeMaxY = values[0].length + edgeMinY - 1;
		if (y > edgeMaxY) {
			int i = 1;
			localMinX++;
			while (y > values[i].length + localYMinima[i] - 1) {
				localMinX++;
				i++;
			}
		} else if (y < edgeMinY) {
			int i = 1;
			localMinX++;
			while (y < localYMinima[i]) {
				localMinX++;
				i++;
			}
		}
		return localMinX;
	}
	
	@Override
	public int getMaxX(int y) {
		int localMaxX = maxX;
		int i = values.length - 1;
		int edgeMinY = localYMinima[i];
		int edgeMaxY = values[i].length + edgeMinY - 1;
		if (y > edgeMaxY) {
			i--;
			localMaxX--;
			while (y > values[i].length + localYMinima[i] - 1) {
				localMaxX--;
				i--;
			}
		} else if (y < edgeMinY) {
			i--;
			localMaxX--;
			while (y < localYMinima[i]) {
				localMaxX--;
				i--;
			}
		}
		return localMaxX;
	}
	
	@Override
	public int getMinY(int x) {
		return localYMinima[x - minX];
	}
	
	@Override
	public int getMaxY(int x) {
		int index = x - minX;
		return values[index].length + localYMinima[index] - 1;
	}

	@Override
	public T getFromPosition(int x, int y) {
		int i = x - minX;
		int j = y - localYMinima[i];
		return values[i][j];
	}
}
