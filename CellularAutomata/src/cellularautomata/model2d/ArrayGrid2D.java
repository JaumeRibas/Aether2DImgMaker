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
 * Represents a finite 2D region of a grid backed by a 2D array.
 * The shape of the region is such that no line parallel to an axis, or at a 45° angle of an axis, overlaps with it along more than one segment.
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
	
	private static final String UNSUPPORTED_SHAPE_ERROR = "The shape of the region must be such that no line parallel to an axis, or at a 45° angle of an axis, overlaps with it along more than one segment.";
	
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
	
	/**
	 * Get absolute y bounds and validate shape
	 * 
	 * @param localYMaxima
	 */
	protected void getYBounds(int[] localYMaxima) {
		if (localYMinima.length > 2) {
			boolean minimaAscending = false;
			boolean minimaHasFlatIntervalOnDescendingSide = false;
			boolean minimaHasLeapGreaterThanOneOnAscendingSide = false;
			boolean maximaDescending = false;
			boolean maximaHasFlatIntervalOnAscendingSide = false;
			boolean maximaHasLeapGreaterThanOnDescendingSide = false;
			minY = localYMinima[0];
			maxY = localYMaxima[0];
			int previousLocalMinY = minY;
			int previousLocalMaxY = maxY;
			for (int i = 1; i < localYMinima.length; i++) {
				//minima
				int localMinY = localYMinima[i];
				if (minimaAscending) {
					if (localMinY < previousLocalMinY) {
						//cross sections could break
						throw new IllegalArgumentException("Unsupported local y minima. " + UNSUPPORTED_SHAPE_ERROR);
					} else if (minimaHasLeapGreaterThanOneOnAscendingSide) {
						if (localMinY == previousLocalMinY) {
							//diagonal cross sections could break
							throw new IllegalArgumentException("Unsupported resulting local y maxima. " + UNSUPPORTED_SHAPE_ERROR);
						}
					} else if (localMinY - previousLocalMinY > 1) {
						minimaHasLeapGreaterThanOneOnAscendingSide = true;
					}
				} else if (localMinY > previousLocalMinY) {
					minimaAscending = true;
					if (localMinY - previousLocalMinY > 1) {
						minimaHasLeapGreaterThanOneOnAscendingSide = true;
					}
				} else {
					if (minimaHasFlatIntervalOnDescendingSide) {
						if (localMinY - previousLocalMinY < -1) {
							//diagonal cross sections could break
							throw new IllegalArgumentException("Unsupported local y minima. " + UNSUPPORTED_SHAPE_ERROR);
						}
					} else if (localMinY == previousLocalMinY) {
						minimaHasFlatIntervalOnDescendingSide = true;
					}
					minY = localMinY;
				}
				previousLocalMinY = localMinY;
				//maxima
				int localMaxY = localYMaxima[i];
				if (maximaDescending) {
					if (localMaxY > previousLocalMaxY) {
						//cross sections could break
						throw new IllegalArgumentException("Unsupported resulting local y maxima. " + UNSUPPORTED_SHAPE_ERROR);
					} else if (maximaHasLeapGreaterThanOnDescendingSide) {
						if (localMaxY == previousLocalMaxY) {
							//diagonal cross sections could break
							throw new IllegalArgumentException("Unsupported resulting local y maxima. " + UNSUPPORTED_SHAPE_ERROR);
						}
					} else if (localMaxY - previousLocalMaxY < -1) {
						maximaHasLeapGreaterThanOnDescendingSide = true;
					}
				} else if (localMaxY < previousLocalMaxY) {
					maximaDescending = true;
					if (localMaxY - previousLocalMaxY < -1) {
						maximaHasLeapGreaterThanOnDescendingSide = true;
					}
				} else {
					if (maximaHasFlatIntervalOnAscendingSide) {
						if (localMaxY - previousLocalMaxY > 1) {
							//diagonal cross sections could break
							throw new IllegalArgumentException("Unsupported resulting local y maxima. " + UNSUPPORTED_SHAPE_ERROR);
						}
					} else if (localMaxY == previousLocalMaxY) {
						maximaHasFlatIntervalOnAscendingSide = true;
					}
					maxY = localMaxY;
				}
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
