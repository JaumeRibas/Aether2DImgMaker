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
package cellularautomata.grid;

/**
 * A convex region of a grid.
 *  
 * @author Jaume
 *
 */
public interface GridRegion {
	
	/**
	 * Returns the dimension of the grid that contains this region.
	 * 
	 * @return the dimension of the grid containing this region.
	 */
	int getGridDimension();
	
	/**
	 * Returns the upper bound of the region on the given axis.
	 * 
	 * @param axis the index of the axis on which the upper bound is requested.
	 * @return the upper bound
	 */
	default int getUpperBound(int axis) {
		return getUpperBound(axis, new PartialCoordinates(new Integer[getGridDimension() - 1]));
	}
	
	/**
	 * <p>Returns the local upper bound of the region on the given axis at the given coordinates.</p>
	 * <p>The coordinate of the axis whose bound is being requested is ignored.</p>
	 * <p>{@link null} coordinate values can be used to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method with a {@link PartialCoordinates} object with {@link PartialCoordinates#getCount()} different from {@link #getGridDimension()}.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the region.</p>
	 * <p>These bounds are obtained by first calling the {@link #getUpperBound(int axis)} and {@link #getLowerBound(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getUpperBound(int axis, PartialCoordinates coordinates)} 
	 * and {@link #getLowerBound(int axis, PartialCoordinates coordinates)} adding one extra coordinate value in every call. 
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
	 * @param  axis the index of the axis on which the upper bound is requested.
	 * @param coordinates a {@link PartialCoordinates} object.
	 * @return  the upper bound
	 */
	int getUpperBound(int axis, PartialCoordinates coordinates);
	
	/**
	 * Returns the lower bound of the region on the given axis.
	 * 
	 * @param axis the index of the axis on which the lower bound is requested.
	 * @return the lower bound
	 */
	default int getLowerBound(int axis) {
		return getLowerBound(axis, new PartialCoordinates(new Integer[getGridDimension() - 1]));
	}
	
	/**
	 * <p>Returns the local lower bound of the region on the given axis at the given coordinates.</p>
	 * <p>The coordinate of the axis whose bound is being requested is ignored.</p>
	 * <p>{@link null} coordinate values can be used to indicate no restriction on one or more axes.</p>
	 * <p>It is not defined to call this method with a {@link PartialCoordinates} object with {@link PartialCoordinates#getCount()} different from {@link #getGridDimension()}.
	 * <p>It is also not defined to call this method on a set of coordinates outside the bounds of the region.</p>
	 * <p>These bounds are obtained by first calling the {@link #getUpperBound(int axis)} and {@link #getLowerBound(int axis)} to get the global bounds on one axis. 
	 * And progressively narrowing down the coordinates by calling {@link #getUpperBound(int axis, PartialCoordinates coordinates)} 
	 * and {@link #getLowerBound(int axis, PartialCoordinates coordinates)} adding one extra coordinate value in every call. 
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
	 * @param  axis the index of the axis on which the lower bound is requested.
	 * @param coordinates a {@link PartialCoordinates} object.
	 * @return  the lower bound
	 */
	int getLowerBound(int axis, PartialCoordinates coordinates);
	
}
