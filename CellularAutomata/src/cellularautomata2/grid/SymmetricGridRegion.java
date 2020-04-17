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

import cellularautomata2.arrays.Coordinates;
import cellularautomata2.arrays.PositionCommand;
import cellularautomata2.arrays.Utils;

/**
 * A region of a grid with two or more symmetric sub-regions whose shapes and orientations are such that no line parallel to an axis crosses their bounds in more than two places.
 * 
 * @author Jaume
 *
 */
public interface SymmetricGridRegion extends GridRegion {
	
	/**
	 * Returns the upper bound of the asymmetric sub-region on the given axis.
	 * 
	 * @param axis the index of the axis on which the upper bound is requested.
	 * @return the upper bound
	 */
	default int getUpperBoundOfAsymmetricRegion(int axis) {
		return getUpperBoundOfAsymmetricRegion(axis, new PartialCoordinates(new Integer[getGridDimension() - 1]));
	}
	
	/**
	 * <p>Returns the local upper bound of the asymmetric sub-region on the given axis at the given coordinates.</p>
	 * <p>The coordinate of the axis whose bound is being requested is ignored.</p>
	 * <p>{@link null} coordinate values can be used to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method with a {@link PartialCoordinates} object with {@link PartialCoordinates#getCount()} different from {@link #getGridDimension()}.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the asymmetric sub-region.</p>
	 * <p>These bounds are obtained by first calling the {@link #getUpperBoundOfAsymmetricRegion(int axis)} and {@link #getLowerBoundOfAsymmetricRegion(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getUpperBoundOfAsymmetricRegion(int axis, PartialCoordinates coordinates)} 
	 * and {@link #getLowerBoundOfAsymmetricRegion(int axis, PartialCoordinates coordinates)} adding one extra coordinate value in every call. 
	 * This extra coordinate being within the bounds obtained in the previous calls.</p>
	 * <p>For example, consider a asymmetric sub-region in a 3D grid region, assuming <strong>x</strong>, <strong>y</strong> and <strong>z</strong> to be the axis 0, 1 and 2 respectively:</p>
	 * <ol>
	 * <li>Get the global bounds of the asymmetric sub-region on the <strong>z</strong> axis: getUpperBoundOfAsymmetricRegion(2) getLowerBoundOfAsymmetricRegion(2) (1 <= z <= 5)</li>
	 * <li>Using the global <strong>z</strong> bounds, get the local <strong>y</strong> bounds where <strong>z</strong> = 3:
	 * getUpperBoundOfAsymmetricRegion(1, new PartialCoordinates(null, null, 3)) getLowerBoundOfAsymmetricRegion(1, new PartialCoordinates(null, null, 3)) (10 <= y <= 12)</li>
	 * <li>Using both the global <strong>z</strong> bounds and the local <strong>y</strong> bounds obtained, 
	 * get the local <strong>x</strong> bounds where <strong>z</strong> = 3 and <strong>y</strong> = 11:
	 * getUpperBoundOfAsymmetricRegion(0, new PartialCoordinates(null, 11, 3)) getLowerBoundOfAsymmetricRegion(0, new PartialCoordinates(null, 11, 3))</li>
	 * </ol>
	 * 
	 * @param  axis the index of the axis on which the upper bound is requested.
	 * @param coordinates a {@link PartialCoordinates} object.
	 * @return  the upper bound
	 */
	int getUpperBoundOfAsymmetricRegion(int axis, PartialCoordinates coordinates);
	
	/**
	 * Returns the lower bound of the asymmetric sub-region on the given axis.
	 * 
	 * @param axis the index of the axis on which the lower bound is requested.
	 * @return the lower bound
	 */
	default int getLowerBoundOfAsymmetricRegion(int axis) {
		return getLowerBoundOfAsymmetricRegion(axis, new PartialCoordinates(new Integer[getGridDimension() - 1]));
	}
	
	/**
	 * <p>Returns the local lower bound of the asymmetric sub-region on the given axis at the given coordinates.</p>
	 * <p>The coordinate of the axis whose bound is being requested is ignored.</p>
	 * <p>{@link null} coordinate values can be used to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method with a {@link PartialCoordinates} object with {@link PartialCoordinates#getCount()} different from {@link #getGridDimension()}.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the asymmetric sub-region.</p>
	 * <p>These bounds are obtained by first calling the {@link #getUpperBoundOfAsymmetricRegion(int axis)} and {@link #getLowerBoundOfAsymmetricRegion(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getUpperBoundOfAsymmetricRegion(int axis, PartialCoordinates coordinates)} 
	 * and {@link #getLowerBoundOfAsymmetricRegion(int axis, PartialCoordinates coordinates)} adding one extra coordinate value in every call. 
	 * This extra coordinate being within the bounds obtained in the previous calls.</p>
	 * <p>For example, consider an asymmetric sub-region in a 3D grid region, assuming <strong>x</strong>, <strong>y</strong> and <strong>z</strong> to be the axis 0, 1 and 2 respectively:</p>
	 * <ol>
	 * <li>Get the global bounds of the asymmetric sub-region on the <strong>z</strong> axis: getUpperBoundOfAsymmetricRegion(2) getLowerBoundOfAsymmetricRegion(2) (1 <= z <= 5)</li>
	 * <li>Using the global <strong>z</strong> bounds, get the local <strong>y</strong> bounds where <strong>z</strong> = 3:
	 * getUpperBoundOfAsymmetricRegion(1, new PartialCoordinates(null, null, 3)) getLowerBoundOfAsymmetricRegion(1, new PartialCoordinates(null, null, 3)) (10 <= y <= 12)</li>
	 * <li>Using both the global <strong>z</strong> bounds and the local <strong>y</strong> bounds obtained, 
	 * get the local <strong>x</strong> bounds where <strong>z</strong> = 3 and <strong>y</strong> = 11:
	 * getUpperBoundOfAsymmetricRegion(0, new PartialCoordinates(null, 11, 3)) getLowerBoundOfAsymmetricRegion(0, new PartialCoordinates(null, 11, 3))</li>
	 * </ol>
	 * 
	 * @param  axis the index of the axis on which the lower bound is requested.
	 * @param coordinates a {@link PartialCoordinates} object.
	 * @return  the lower bound
	 */
	int getLowerBoundOfAsymmetricRegion(int axis, PartialCoordinates coordinates);
	
	/**
	 * Executes a {@link PositionCommand} for every position of the asymmetric sub-region.
	 * @param command
	 */
	default void forEachPositionInAsymmetricRegion(PositionCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The command cannot be null.");
		}
		int dimension = getGridDimension();
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		Integer[] partialCoordinates = new Integer[dimension];
		PartialCoordinates immutablePartialCoordinates = new PartialCoordinates(partialCoordinates);
		int[] upperBounds = new int[dimension];
		int[] lowerBounds = new int[dimension];
		int currentAxis = dimension - 1;
		boolean isBeginningOfLoop = true;
		while (currentAxis < dimension) {
			if (currentAxis == 0) {
				int lowerBound = getLowerBoundOfAsymmetricRegion(0, immutablePartialCoordinates);
				int upperBound = getUpperBoundOfAsymmetricRegion(0, immutablePartialCoordinates);
				for (int currentCoordinate = lowerBound; currentCoordinate <= upperBound; currentCoordinate++) {
					coordinates[0] = currentCoordinate;
					command.execute(immutableCoordinates);
				}
				isBeginningOfLoop = false;
				currentAxis++;
			} else if (isBeginningOfLoop) {
				int localLowerBound = getLowerBoundOfAsymmetricRegion(currentAxis, immutablePartialCoordinates);
				lowerBounds[currentAxis] = localLowerBound;
				upperBounds[currentAxis] = getUpperBoundOfAsymmetricRegion(currentAxis, immutablePartialCoordinates);
				coordinates[currentAxis] = localLowerBound;
				partialCoordinates[currentAxis] = localLowerBound;
				currentAxis--;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				if (currentCoordinate < upperBounds[currentAxis]) {
					isBeginningOfLoop = true;
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					partialCoordinates[currentAxis] = currentCoordinate;
					currentAxis--;
				} else {
					partialCoordinates[currentAxis] = null;
					currentAxis++;
				}
			}
		}
	}
	
	/**
	 * Executes a {@link PositionCommand} for every even position of the asymmetric sub-region.
	 * @param commands
	 */
	default void forEachEvenPositionInAsymmetricRegion(PositionCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The command cannot be null.");
		}
		int dimension = getGridDimension();
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		Integer[] partialCoordinates = new Integer[dimension];
		PartialCoordinates immutablePartialCoordinates = new PartialCoordinates(partialCoordinates);
		int[] upperBounds = new int[dimension];
		int[] lowerBounds = new int[dimension];
		int currentAxis = dimension - 1;
		boolean isBeginningOfLoop = true;
		while (currentAxis < dimension) {
			if (currentAxis == 0) {
				int lowerBound = getLowerBoundOfAsymmetricRegion(0, immutablePartialCoordinates);
				int upperBound = getUpperBoundOfAsymmetricRegion(0, immutablePartialCoordinates);
				int currentCoordinate = lowerBound;
				coordinates[0] = currentCoordinate;
				if (!Utils.isEvenPosition(coordinates)) {
					currentCoordinate++;
				}
				for (; currentCoordinate <= upperBound; currentCoordinate += 2) {
					coordinates[0] = currentCoordinate;
					command.execute(immutableCoordinates);
				}
				isBeginningOfLoop = false;
				currentAxis++;
			} else if (isBeginningOfLoop) {
				int localLowerBound = getLowerBoundOfAsymmetricRegion(currentAxis, immutablePartialCoordinates);
				lowerBounds[currentAxis] = localLowerBound;
				upperBounds[currentAxis] = getUpperBoundOfAsymmetricRegion(currentAxis, immutablePartialCoordinates);
				coordinates[currentAxis] = localLowerBound;
				partialCoordinates[currentAxis] = localLowerBound;
				currentAxis--;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				if (currentCoordinate < upperBounds[currentAxis]) {
					isBeginningOfLoop = true;
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					partialCoordinates[currentAxis] = currentCoordinate;
					currentAxis--;
				} else {
					partialCoordinates[currentAxis] = null;
					currentAxis++;
				}
			}
		}
	}
	
	/**
	 * Executes a {@link PositionCommand} for every odd position of the asymmetric sub-region.
	 * @param commands
	 */
	default void forEachOddPositionInAsymmetricRegion(PositionCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("The command cannot be null.");
		}
		int dimension = getGridDimension();
		int[] coordinates = new int[dimension];
		Coordinates immutableCoordinates = new Coordinates(coordinates);
		Integer[] partialCoordinates = new Integer[dimension];
		PartialCoordinates immutablePartialCoordinates = new PartialCoordinates(partialCoordinates);
		int[] upperBounds = new int[dimension];
		int[] lowerBounds = new int[dimension];
		int currentAxis = dimension - 1;
		boolean isBeginningOfLoop = true;
		while (currentAxis < dimension) {
			if (currentAxis == 0) {
				int lowerBound = getLowerBoundOfAsymmetricRegion(0, immutablePartialCoordinates);
				int upperBound = getUpperBoundOfAsymmetricRegion(0, immutablePartialCoordinates);
				int currentCoordinate = lowerBound;
				coordinates[0] = currentCoordinate;
				if (Utils.isEvenPosition(coordinates)) {
					currentCoordinate++;
				}
				for (; currentCoordinate <= upperBound; currentCoordinate += 2) {
					coordinates[0] = currentCoordinate;
					command.execute(immutableCoordinates);
				}
				isBeginningOfLoop = false;
				currentAxis++;
			} else if (isBeginningOfLoop) {
				int localLowerBound = getLowerBoundOfAsymmetricRegion(currentAxis, immutablePartialCoordinates);
				lowerBounds[currentAxis] = localLowerBound;
				upperBounds[currentAxis] = getUpperBoundOfAsymmetricRegion(currentAxis, immutablePartialCoordinates);
				coordinates[currentAxis] = localLowerBound;
				partialCoordinates[currentAxis] = localLowerBound;
				currentAxis--;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				if (currentCoordinate < upperBounds[currentAxis]) {
					isBeginningOfLoop = true;
					currentCoordinate++;
					coordinates[currentAxis] = currentCoordinate;
					partialCoordinates[currentAxis] = currentCoordinate;
					currentAxis--;
				} else {
					partialCoordinates[currentAxis] = null;
					currentAxis++;
				}
			}
		}
	}
	
}
