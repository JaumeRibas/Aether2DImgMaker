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
package cellularautomata.model2d;

/**
 * Represents a fixed size convex 2D region of a grid backed by a 2D array.
 * 
 * @author Jaume
 *
 */
public abstract class ArrayGrid2D implements Model2D {

	protected int minX;
	protected int maxX;
	protected int[] localYMinima;
	protected int minY;
	protected int maxY;
	
	private static final String UNSUPPORTED_SHAPE_ERROR = "The region must be convex.";
	
	/**
	 * Constructs an {@code ArrayGrid2D} with the specified bounds
	 * 
	 * @param minX the smallest x-coordinate within the region
	 * @param localYMinima an array of the smallest y-coordinates at each x-coordinate of the region. Beginning at {@code minX}.
	 */
	public ArrayGrid2D(int minX, int[] localYMinima) {
		if (localYMinima.length == 0) {
			throw new IllegalArgumentException("Local y minima's length cannot be 0.");
		}
		long longMaxX = (long)localYMinima.length + minX - 1;
		if (longMaxX > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Resulting max x (" + longMaxX + ") is greater than the supported max (" + Integer.MAX_VALUE + ").");
		}
		this.minX = minX;
		maxX = (int)longMaxX;
		this.localYMinima = localYMinima;				
	}
	
	protected void getYBounds(int[] localYMaxima) {
		//get absolute y bounds and validate convexity
		if (localYMinima.length > 2) {
			minY = Math.min(localYMinima[0], localYMinima[1]);
			maxY = Math.max(localYMaxima[0], localYMaxima[1]);
			long previousLocalMinimaSlope = localYMinima[1] - localYMinima[0];
			long previousLocalMaximaSlope = localYMaxima[1] - localYMaxima[0];		
			int previousLocalMinY = localYMinima[1];
			int previousLocalMaxY = localYMaxima[1];
			for (int i = 2; i < localYMinima.length; i++) {
				int localMinY = localYMinima[i];
				int localMaxY = localYMaxima[i];
				minY = Math.min(minY, localMinY);
				maxY = Math.max(maxY, localMaxY);
				long localMinimaSlope = localMinY - previousLocalMinY;
				long localMaximaSlope = localMaxY - previousLocalMaxY;
				if (localMinimaSlope < previousLocalMinimaSlope) {
					throw new IllegalArgumentException("Unsupported local y minima. " + UNSUPPORTED_SHAPE_ERROR);
				}
				if (localMaximaSlope > previousLocalMaximaSlope) {
					throw new IllegalArgumentException("Unsupported resulting local y maxima. " + UNSUPPORTED_SHAPE_ERROR);
				}
				previousLocalMinimaSlope = localMinimaSlope;
				previousLocalMaximaSlope = localMaximaSlope;		
				previousLocalMinY = localMinY;
				previousLocalMaxY = localMaxY;
			}
		} else {
			minY = localYMinima[0];
			maxY = localYMaxima[0];
			if (localYMinima.length > 1) {
				minY = Math.min(minY, localYMinima[1]);
				maxY = Math.max(maxY, localYMaxima[1]);				
			}
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
	public int getMinY(int x) {
		return localYMinima[x - minX];
	}
	
}
