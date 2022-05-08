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

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;

public abstract class ModelIterator<Source_Type extends Model, Element_Type> implements Iterator<Element_Type> {
	
	protected Source_Type region;
	private int dimension;
	private int dimensionMinusOne;
	private int[] coords;
	private int[] maxCoords;
	private Integer[] partialCoords;
	private boolean hasNext;
	
	public ModelIterator(Source_Type region) {
		this.region = region;
		dimension = region.getGridDimension();
		dimensionMinusOne = dimension - 1;
		coords = new int[dimension];
		maxCoords = new int[dimension];
		partialCoords = new Integer[dimension];
		for (int axis = 0; axis < dimension; axis++) {
			PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoords.clone());
			int minCoord = region.getMinCoordinate(axis, partialCoordinatesObj);
			coords[axis] = minCoord;
			maxCoords[axis] = region.getMaxCoordinate(axis, partialCoordinatesObj);
			partialCoords[axis] = minCoord;
		}
		hasNext = true;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Element_Type next() {
		if (!hasNext)
			throw new NoSuchElementException();
		Element_Type next = null;
		try {
			next = getFromModelPosition(new Coordinates(coords.clone()));
		} catch (Exception e) {
			throw new NoSuchElementException(e.toString());
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
				PartialCoordinates partialCoordinatesObj = new PartialCoordinates(partialCoords.clone());
				int minCoord = region.getMinCoordinate(axis, partialCoordinatesObj);
				coords[axis] = minCoord;
				maxCoords[axis] = region.getMaxCoordinate(axis, partialCoordinatesObj);
				partialCoords[axis] = minCoord;
				axis++;
			}
		} else {
			hasNext = false;
		}
		return next;
	}
	
	protected abstract Element_Type getFromModelPosition(Coordinates coordinates) throws Exception;

}
