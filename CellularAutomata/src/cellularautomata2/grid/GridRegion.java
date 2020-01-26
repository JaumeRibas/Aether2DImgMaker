/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

/**
 * A region of a grid whose shape is such that no line parallel to an axis crosses its bounds in more than two places.
 *  
 * @author Jaume
 *
 */
public abstract class GridRegion implements GridEntity {
	
	/**
	 * Returns the upper bound of the region on the given axis.
	 * 
	 * @param axis the index of the axis on which the upper bound is requested.
	 * @return the upper bound
	 */
	public int getUpperBound(int axis) {
		return getUpperBound(axis, new Integer[getGridDimension() - 1]);
	}
	
	/**
	 * <p>Returns the local upper bound of the region on the given axis at the given coordinates.</p>
	 * <p>{@link null} coordinates may be passed to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method on a coordinate array with length different from {@link #getGridDimension()} - 1.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the region.</p>
	 * <p>This bounds are obtained by first calling the {@link #getUpperBound(int axis)} and {@link #getLowerBound(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getUpperBound(int axis, Integer[] coordinates)} 
	 * and {@link #getLowerBound(int axis, Integer[] coordinates)} adding one extra coordinate value in every call. 
	 * This extra coordinate being within the bounds obtained in the previous calls.</p>
	 * <p>For example, consider a region in a 3D grid, assuming <strong>x</strong>, <strong>y</strong> and <strong>z</strong> to be the axis 0, 1 and 2 respectively:</p>
	 * <ol>
	 * <li>Get the global bounds of the region on the <strong>z</strong> axis: getUpperBound(2) getLowerBound(2) (1 <= z <= 5)</li>
	 * <li>Using the global <strong>z</strong> bounds, get the local <strong>y</strong> bounds where <strong>z</strong> == 3:
	 * getUpperBound(1, new Integer[]{null, 3}) getLowerBound(1, new Integer[]{null, 3}) (note that the coordinates in the array must be in 
	 * the same order as the axes excluding the axis whose bounds are being requested)
	 * (10 <= y <= 12)</li>
	 * <li>Using both the global <strong>z</strong> bounds and the local <strong>y</strong> bounds obtained, 
	 * get the local <strong>x</strong> bounds where <strong>z</strong> == 3 and <strong>y</strong> == 11:
	 * getUpperBound(0, new Integer[]{11, 3}) getLowerBound(0, new Integer[]{11, 3})</li>
	 * </ol>
	 * 
	 * @param  axis the index of the axis on which the upper bound is requested.
	 * @param coordinates an {@link Integer} array of length {@link #getGridDimension()} - 1 representing the coordinates on the other axes in order.
	 * @return  the upper bound
	 */
	public abstract int getUpperBound(int axis, Integer[] coordinates); //Use PartialCoordinates?
	
	/**
	 * Returns the lower bound of the region on the given axis.
	 * 
	 * @param axis the index of the axis on which the lower bound is requested.
	 * @return the lower bound
	 */
	public int getLowerBound(int axis) {
		return getLowerBound(axis, new Integer[getGridDimension() - 1]);
	}
	
	/**
	 * <p>Returns the local lower bound of the region on the given axis at the given coordinates.</p>
	 * <p>{@link null} coordinates may be passed to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method on a coordinate array with length different from {@link #getGridDimension()} - 1.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the region.</p>
	 * <p>This bounds are obtained by first calling the {@link #getUpperBound(int axis)} and {@link #getLowerBound(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getUpperBound(int axis, Integer[] coordinates)} 
	 * and {@link #getLowerBound(int axis, Integer[] coordinates)} adding one extra coordinate value in every call. 
	 * This extra coordinate being within the bounds obtained in the previous calls.</p>
	 * <p>For example, consider a region in a 3D grid, assuming <strong>x</strong>, <strong>y</strong> and <strong>z</strong> to be the axis 0, 1 and 2 respectively:</p>
	 * <ol>
	 * <li>Get the global bounds of the region on the <strong>z</strong> axis: getUpperBound(2) getLowerBound(2) (1 <= z <= 5)</li>
	 * <li>Using the global <strong>z</strong> bounds, get the local <strong>y</strong> bounds where <strong>z</strong> == 3:
	 * getUpperBound(1, new Integer[]{null, 3}) getLowerBound(1, new Integer[]{null, 3}) (note that the coordinates in the array must be in 
	 * the same order as the axes excluding the axis whose bounds are being requested)
	 * (10 <= y <= 12)</li>
	 * <li>Using both the global <strong>z</strong> bounds and the local <strong>y</strong> bounds obtained, 
	 * get the local <strong>x</strong> bounds where <strong>z</strong> == 3 and <strong>y</strong> == 11:
	 * getUpperBound(0, new Integer[]{11, 3}) getLowerBound(0, new Integer[]{11, 3})</li>
	 * </ol>
	 * 
	 * @param  axis the index of the axis on which the lower bound is requested.
	 * @param coordinates an {@link Integer} array of length {@link #getGridDimension()} - 1 representing the coordinates on the other axes in order.
	 * @return  the lower bound
	 */
	public abstract int getLowerBound(int axis, Integer[] coordinates);
	
	/**
	 * Executes a {@link CoordinateCommand} for every coordinate of the region.
	 * @param command
	 */
	public void forEachPosition(CoordinateCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The coordinate command cannot be null.");
		}
		int dimension = getGridDimension();
		int dimensionMinusOne = dimension - 1;
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		Integer[] upperBounds = new Integer[dimension];
		Integer[] lowerBounds = new Integer[dimension];
		int currentAxis = dimensionMinusOne;
		while (currentAxis < dimension) {
			boolean isBeginningOfLoop = false;
			if (lowerBounds[currentAxis] == null) {
				isBeginningOfLoop = true;
				Integer[] boundsCoordinates = new Integer[dimensionMinusOne];
				for (int i = currentAxis + 1; i < dimension; i++) {
					boundsCoordinates[i-1] = new Integer(coordinates[i]);
				}
				lowerBounds[currentAxis] = getLowerBound(currentAxis, boundsCoordinates);
				upperBounds[currentAxis] = getUpperBound(currentAxis, boundsCoordinates);
			}
			if (currentAxis == 0) {
				int lowerBound = lowerBounds[0];
				int upperBound = upperBounds[0];
				for (coordinates[0] = lowerBound; coordinates[0] <= upperBound; coordinates[0]++) {
					command.execute(immutableCoordinates);
				}
				lowerBounds[0] = null;
				upperBounds[0] = null;
				currentAxis++;
			} else if (isBeginningOfLoop) {
				coordinates[currentAxis] = lowerBounds[currentAxis];
				currentAxis--;
			} else if (coordinates[currentAxis] < upperBounds[currentAxis]) {
				coordinates[currentAxis]++;
				currentAxis--;
			} else {
				lowerBounds[0] = null;
				upperBounds[0] = null;
				currentAxis++;
			}
		}
	}
	
	/**
	 * Executes a {@link CoordinateCommand} for every even coordinate of the region.
	 * @param commands
	 */
	public void forEachEvenPosition(CoordinateCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The coordinate command cannot be null.");
		}
		int dimension = getGridDimension();
		int dimensionMinusOne = dimension - 1;
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		Integer[] upperBounds = new Integer[dimension];
		Integer[] lowerBounds = new Integer[dimension];
		int currentAxis = dimensionMinusOne;
		while (currentAxis < dimension) {
			boolean isBeginningOfLoop = false;
			if (lowerBounds[currentAxis] == null) {
				isBeginningOfLoop = true;
				Integer[] boundsCoordinates = new Integer[dimensionMinusOne];
				for (int i = currentAxis + 1; i < dimension; i++) {
					boundsCoordinates[i-1] = new Integer(coordinates[i]);
				}
				lowerBounds[currentAxis] = getLowerBound(currentAxis, boundsCoordinates);
				upperBounds[currentAxis] = getUpperBound(currentAxis, boundsCoordinates);
			}
			if (currentAxis == 0) {
				coordinates[0] = lowerBounds[0];
				if (!Utils.isEvenPosition(coordinates)) {
					coordinates[0]++;
				}
				int upperBound = upperBounds[0];
				for (; coordinates[0] <= upperBound; coordinates[0] += 2) {
					command.execute(immutableCoordinates);
				}
				lowerBounds[0] = null;
				upperBounds[0] = null;
				currentAxis++;
			} else if (isBeginningOfLoop) {
				coordinates[currentAxis] = lowerBounds[currentAxis];
				currentAxis--;
			} else if (coordinates[currentAxis] < upperBounds[currentAxis]) {
				coordinates[currentAxis]++;
				currentAxis--;
			} else {
				lowerBounds[0] = null;
				upperBounds[0] = null;
				currentAxis++;
			}
		}
	}
	
	/**
	 * Executes a {@link CoordinateCommand} for every odd coordinate of the region.
	 * @param commands
	 */
	public void forEachOddPosition(CoordinateCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The coordinate command cannot be null.");
		}
		int dimension = getGridDimension();
		int dimensionMinusOne = dimension - 1;
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		Integer[] upperBounds = new Integer[dimension];
		Integer[] lowerBounds = new Integer[dimension];
		int currentAxis = dimensionMinusOne;
		while (currentAxis < dimension) {
			boolean isBeginningOfLoop = false;
			if (lowerBounds[currentAxis] == null) {
				isBeginningOfLoop = true;
				Integer[] boundsCoordinates = new Integer[dimensionMinusOne];
				for (int i = currentAxis + 1; i < dimension; i++) {
					boundsCoordinates[i-1] = new Integer(coordinates[i]);
				}
				lowerBounds[currentAxis] = getLowerBound(currentAxis, boundsCoordinates);
				upperBounds[currentAxis] = getUpperBound(currentAxis, boundsCoordinates);
			}
			if (currentAxis == 0) {
				coordinates[0] = lowerBounds[0];
				if (Utils.isEvenPosition(coordinates)) {
					coordinates[0]++;
				}
				int upperBound = upperBounds[0];
				for (; coordinates[0] <= upperBound; coordinates[0] += 2) {
					command.execute(immutableCoordinates);
				}
				lowerBounds[0] = null;
				upperBounds[0] = null;
				currentAxis++;
			} else if (isBeginningOfLoop) {
				coordinates[currentAxis] = lowerBounds[currentAxis];
				currentAxis--;
			} else if (coordinates[currentAxis] < upperBounds[currentAxis]) {
				coordinates[currentAxis]++;
				currentAxis--;
			} else {
				lowerBounds[0] = null;
				upperBounds[0] = null;
				currentAxis++;
			}
		}
	}
	
	/* Recursive version
	protected void loopCoord(PositionCommand command, int[] coordinates, int axis) {
		Integer[] boundsCoordinates = new Integer[getGridDimension() - 1];
		for (int i = 0; i < axis; i++) {
			boundsCoordinates[i] = new Integer(coordinates[i]);
		}
		Bounds[] bounds = getBounds(axis, boundsCoordinates);
		if (axis == 0) {
			for (int i = 0; i < bounds.length; i++) {
				int lowerBound = bounds[i].getLower();
				int upperBound = bounds[i].getUpper();
				for (coordinates[0] = lowerBound; coordinates[0] <= upperBound; coordinates[0]++) {
					command.execute(coordinates);
				}
			}
		} else {
			int previousAxis = axis - 1;
			for (int i = 0; i < bounds.length; i++) {
				int lowerBound = bounds[i].getLower();
				int upperBound = bounds[i].getUpper();
				for (coordinates[axis] = lowerBound; coordinates[axis] <= upperBound; coordinates[axis]++) {
					loopCoord(commands, coordinates, previousAxis);
				}
			}
		}
	}
	*/
	
	/**
	 * <p>Returns a subregion of the current region contained within the passed bounds.</p>
	 * <p>Only one {@link PartialBounds} object per axis is supported.</p>
	 * 
	 * @param bounds an array of {@link PartialBounds} objects. One for each axis.
	 * @return a {@link GridSubregion} decorating the current region 
	 */
//	public abstract GridRegion subRegion(PartialBounds[] bounds);
	
}
