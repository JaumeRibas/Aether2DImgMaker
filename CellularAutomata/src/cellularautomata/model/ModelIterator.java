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

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class ModelIterator<G extends Model, T> implements Iterator<T> {
	
	protected G region;
	private int dimension;
	private int dimensionMinusOne;
	private int[] coords;
	private int[] maxCoords;
	private Coordinates immutableCoords;
	private Integer[] partialCoords;
	private PartialCoordinates immutablePartialCoords;
	private boolean hasNext;
	
	public ModelIterator(G region) {
		this.region = region;
		dimension = region.getGridDimension();
		dimensionMinusOne = dimension - 1;
		coords = new int[dimension];
		maxCoords = new int[dimension];
		immutableCoords = new Coordinates(coords);
		partialCoords = new Integer[dimension];
		immutablePartialCoords = new PartialCoordinates(partialCoords);
		for (int axis = 0; axis < dimension; axis++) {
			int minCoord = region.getMinCoordinate(axis, immutablePartialCoords);
			coords[axis] = minCoord;
			maxCoords[axis] = region.getMaxCoordinate(axis, immutablePartialCoords);
			partialCoords[axis] = minCoord;
		}
		hasNext = true;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public T next() {
		if (!hasNext)
			throw new NoSuchElementException();
		T next = null;
		try {
			next = getFromModelPosition(immutableCoords);
		} catch (Exception e) {
			throw new NoSuchElementException(e.getMessage());
		}
		int axis = dimensionMinusOne;
		while (axis >= 0 && coords[axis] == maxCoords[axis]) {
			partialCoords[axis] = null;
			axis--;
		}
		if (axis >= 0) {
			int newCoord = coords[axis] + 1;
			coords[axis] = newCoord;
			partialCoords[axis] = newCoord;
			axis++;
			while (axis < dimension) {
				int minCoord = region.getMinCoordinate(axis, immutablePartialCoords);
				coords[axis] = minCoord;
				maxCoords[axis] = region.getMaxCoordinate(axis, immutablePartialCoords);
				partialCoords[axis] = minCoord;
				axis++;
			}
		} else {
			hasNext = false;
		}
		return next;
	}
	
	protected abstract T getFromModelPosition(Coordinates coordinates) throws Exception;

}
