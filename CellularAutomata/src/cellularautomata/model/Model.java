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
package cellularautomata.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;
import cellularautomata.Utils;

/**
 * A model consisting of a finite convex region of an n-dimensional grid whose shape, size and configuration evolve in discrete time steps.
 *  
 * @author Jaume
 *
 */
public interface Model {
	
	/**
	 * Returns the dimension of the grid that contains this region.
	 * 
	 * @return the dimension of the grid containing this region.
	 */
	int getGridDimension();
	
	/**
	 * Returns the label of the given axis.
	 * 
	 * @param axis the index of the axis whose label is requested.
	 * @return the label
	 */
	default String getAxisLabel(int axis) {
		return "" + Utils.getAxisLetterFromIndex(getGridDimension(), axis);
	}
	
	/**
	 * Returns the max coordinate of the region on the given axis.
	 * 
	 * @param axis the index of the axis on which the max coordinate is requested.
	 * @return the max coordinate
	 */
	int getMaxCoordinate(int axis);
	
	/**
	 * <p>Returns the local max coordinate of the region on the given axis at the given coordinates.</p>
	 * <p>The coordinate of the axis whose bound is being requested is ignored.</p>
	 * <p>{@link null} coordinate values can be used to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method with a {@link PartialCoordinates} object with {@link PartialCoordinates#getCount()} different from {@link #getGridDimension()}.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the region.</p>
	 * <p>These bounds are obtained by first calling the {@link #getMaxCoordinate(int axis)} and {@link #getMinCoordinate(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getMaxCoordinate(int axis, PartialCoordinates coordinates)} 
	 * and {@link #getMinCoordinate(int axis, PartialCoordinates coordinates)} adding one extra coordinate value in every call. 
	 * This extra coordinate being within the bounds obtained in the previous calls.</p>
	 * <p>For example, consider a region in a 3D grid, assuming <strong>x</strong>, <strong>y</strong> and <strong>z</strong> to be the axis 0, 1 and 2 respectively:</p>
	 * <ol>
	 * <li>Get the global bounds of the region on the <strong>z</strong> axis: getUpperBound(2) getLowerBound(2) (1 <= z <= 5)</li>
	 * <li>Using the global <strong>z</strong> bounds, get the local <strong>y</strong> bounds where <strong>z</strong> = 3:
	 * getUpperBound(1, new PartialCoordinates(null, null, 3)) getLowerBound(1, new PartialCoordinates(null, null, 3)) (10 <= y <= 12)</li>
	 * <li>Using both the global <strong>z</strong> bounds and the local <strong>y</strong> bounds obtained, 
	 * get the local <strong>x</strong> bounds where <strong>z</strong> = 3 and <strong>y</strong> = 11:
	 * getUpperBound(0, new PartialCoordinates(null, 11, 3)) getLowerBound(0, new PartialCoordinates(null, 11, 3))</li>
	 * </ol>
	 * 
	 * @param  axis the index of the axis on which the max coordinate is requested.
	 * @param coordinates a {@link PartialCoordinates} object.
	 * @return  the max coordinate
	 */
	default int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return getMaxCoordinate(axis);
	}
	
	/**
	 * Returns the min coordinate of the region on the given axis.
	 * 
	 * @param axis the index of the axis on which the min coordinate is requested.
	 * @return the min coordinate
	 */
	int getMinCoordinate(int axis);
	
	/**
	 * <p>Returns the local min coordinate of the region on the given axis at the given coordinates.</p>
	 * <p>The coordinate of the axis whose bound is being requested is ignored.</p>
	 * <p>{@link null} coordinate values can be used to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method with a {@link PartialCoordinates} object with {@link PartialCoordinates#getCount()} different from {@link #getGridDimension()}.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the region.</p>
	 * <p>These bounds are obtained by first calling the {@link #getMaxCoordinate(int axis)} and {@link #getMinCoordinate(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getMaxCoordinate(int axis, PartialCoordinates coordinates)} 
	 * and {@link #getMinCoordinate(int axis, PartialCoordinates coordinates)} adding one extra coordinate value in every call. 
	 * This extra coordinate being within the bounds obtained in the previous calls.</p>
	 * <p>For example, consider a region in a 3D grid, assuming <strong>x</strong>, <strong>y</strong> and <strong>z</strong> to be the axis 0, 1 and 2 respectively:</p>
	 * <ol>
	 * <li>Get the global bounds of the region on the <strong>z</strong> axis: getUpperBound(2) getLowerBound(2) (1 <= z <= 5)</li>
	 * <li>Using the global <strong>z</strong> bounds, get the local <strong>y</strong> bounds where <strong>z</strong> = 3:
	 * getUpperBound(1, new PartialCoordinates(null, null, 3)) getLowerBound(1, new PartialCoordinates(null, null, 3)) (10 <= y <= 12)</li>
	 * <li>Using both the global <strong>z</strong> bounds and the local <strong>y</strong> bounds obtained, 
	 * get the local <strong>x</strong> bounds where <strong>z</strong> = 3 and <strong>y</strong> = 11:
	 * getUpperBound(0, new PartialCoordinates(null, 11, 3)) getLowerBound(0, new PartialCoordinates(null, 11, 3))</li>
	 * </ol>
	 * 
	 * @param  axis the index of the axis on which the min coordinate is requested.
	 * @param coordinates a {@link PartialCoordinates} object.
	 * @return  the min coordinate
	 */
	default int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return getMinCoordinate(axis);
	}
	
	/**
	 * Feeds the coordinates of every position of the region to the consumer.
	 * @param consumer
	 */
	default void forEachPosition(Consumer<Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int dimension = getGridDimension();
		int[] coordinates = new int[dimension];
		if (dimension == 0) {
			consumer.accept(new Coordinates(coordinates));
		} else {
			Integer[] partialCoordinates = new Integer[dimension];
			int[] maxCoords = new int[dimension];
			int currentAxis = dimension - 1;
			boolean isBeginningOfLoop = true;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates.clone());
					int minCoord = getMinCoordinate(0, partialCoordinatesObj);
					int maxCoord = getMaxCoordinate(0, partialCoordinatesObj);
					for (int currentCoordinate = minCoord; currentCoordinate <= maxCoord; currentCoordinate++) {
						coordinates[0] = currentCoordinate;
						consumer.accept(new Coordinates(coordinates.clone()));
					}
					isBeginningOfLoop = false;
					currentAxis++;
				} else if (isBeginningOfLoop) {
					PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates.clone());
					int localMinCoord = getMinCoordinate(currentAxis, partialCoordinatesObj);
					maxCoords[currentAxis] = getMaxCoordinate(currentAxis, partialCoordinatesObj);
					coordinates[currentAxis] = localMinCoord;
					partialCoordinates[currentAxis] = localMinCoord;
					currentAxis--;
				} else {
					int currentCoordinate = coordinates[currentAxis];
					if (currentCoordinate < maxCoords[currentAxis]) {
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
	
	/**
	 * Feeds the coordinates of every even position of the region to the consumer.
	 * @param consumer
	 */
	default void forEachEvenPosition(Consumer<Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int dimension = getGridDimension();
		int[] coordinates = new int[dimension];
		if (dimension == 0) {
			consumer.accept(new Coordinates(coordinates));			
		} else {
			Integer[] partialCoordinates = new Integer[dimension];
			int[] maxCoords = new int[dimension];
			int currentAxis = dimension - 1;
			boolean isBeginningOfLoop = true;
			while (currentAxis < dimension) {
				if (currentAxis == 0) {
					PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates.clone());
					int minCoord = getMinCoordinate(0, partialCoordinatesObj);
					int maxCoord = getMaxCoordinate(0, partialCoordinatesObj);
					int currentCoordinate = minCoord;
					coordinates[0] = currentCoordinate;
					if (!Utils.isEvenPosition(coordinates)) {
						currentCoordinate++;
					}
					for (; currentCoordinate <= maxCoord; currentCoordinate += 2) {
						coordinates[0] = currentCoordinate;
						consumer.accept(new Coordinates(coordinates.clone()));
					}
					isBeginningOfLoop = false;
					currentAxis++;
				} else if (isBeginningOfLoop) {
					PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates.clone());
					int localMinCoord = getMinCoordinate(currentAxis, partialCoordinatesObj);
					maxCoords[currentAxis] = getMaxCoordinate(currentAxis, partialCoordinatesObj);
					coordinates[currentAxis] = localMinCoord;
					partialCoordinates[currentAxis] = localMinCoord;
					currentAxis--;
				} else {
					int currentCoordinate = coordinates[currentAxis];
					if (currentCoordinate < maxCoords[currentAxis]) {
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
	
	/**
	 * Feeds the coordinates of every odd position of the region to the consumer.
	 * @param consumer
	 */
	default void forEachOddPosition(Consumer<Coordinates> consumer) {
		if (consumer == null) {
			throw new IllegalArgumentException("The consumer cannot be null.");
		}
		int dimension = getGridDimension();
		int[] coordinates = new int[dimension];
		Integer[] partialCoordinates = new Integer[dimension];
		int[] maxCoords = new int[dimension];
		int currentAxis = dimension - 1;
		boolean isBeginningOfLoop = true;
		while (currentAxis < dimension) {
			if (currentAxis == 0) {
				PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates.clone());
				int minCoord = getMinCoordinate(0, partialCoordinatesObj);
				int maxCoord = getMaxCoordinate(0, partialCoordinatesObj);
				int currentCoordinate = minCoord;
				coordinates[0] = currentCoordinate;
				if (Utils.isEvenPosition(coordinates)) {
					currentCoordinate++;
				}
				for (; currentCoordinate <= maxCoord; currentCoordinate += 2) {
					coordinates[0] = currentCoordinate;
					consumer.accept(new Coordinates(coordinates.clone()));
				}
				isBeginningOfLoop = false;
				currentAxis++;
			} else if (isBeginningOfLoop) {
				PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoordinates.clone());
				int localMinCoord = getMinCoordinate(currentAxis, partialCoordinatesObj);
				maxCoords[currentAxis] = getMaxCoordinate(currentAxis, partialCoordinatesObj);
				coordinates[currentAxis] = localMinCoord;
				partialCoordinates[currentAxis] = localMinCoord;
				currentAxis--;
			} else {
				int currentCoordinate = coordinates[currentAxis];
				if (currentCoordinate < maxCoords[currentAxis]) {
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
	 * Returns a decorated {@link Model} with the passed bounds.
	 * 
	 * @param minCoordinates
	 * @param maxCoordinates
	 * @return a {@link Model} decorating the current grid 
	 */
	default Model subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return new SubModel<Model>(this, minCoordinates, maxCoordinates);
	}
	
	default Model crossSection(int axis, int coordinate) {
		return new ModelCrossSection<Model>(this, axis, coordinate);
	}
	
	/**
	 * Computes the next step of the model and returns whether
	 * or not the state changed. 
	 *  
	 * @return true if the state changed or false otherwise
	 * @throws Exception 
	 */
	default boolean nextStep() throws Exception {
		throw new UnsupportedOperationException("The method is not implemented by this class.");
	}
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	default long getStep() {
		throw new UnsupportedOperationException("The method is not implemented by this class.");
	}
	
	/**
	 * Return the model's name in a format that can be used in file names
	 * 
	 * @return the name
	 */
	default String getName() {
		throw new UnsupportedOperationException("The method is not implemented by this class.");
	}
	
	/**
	 * Return the model's name and configuration as a folder and subfolder(s) path.
	 * 
	 * @return the path
	 */
	default String getSubfolderPath() {
		throw new UnsupportedOperationException("The method is not implemented by this class.");
	}
	
	/**
	 * Backs up the state of the model to a file or folder for future restoration.<br/>
	 * The state can be restored passing a path to this file or folder to the constructor.<br/>
	 * 
	 * @param backupPath the storage location where the backup will be stored
	 * @param backupName the name of the backup
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	default void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		throw new UnsupportedOperationException("The method is not implemented by this class.");
	}
	
}
