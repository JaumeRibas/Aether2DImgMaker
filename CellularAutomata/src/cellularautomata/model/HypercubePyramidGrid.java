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

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;

/**
 * Hypercube pyramid shaped region of a grid
 * 
 * @author Jaume
 *
 */
public class HypercubePyramidGrid implements Model {
	
	protected final Coordinates baseCenterCoordinates;
	protected final int baseSide;
	protected final int height;
	protected final int heightAxis;
	protected final int halfBaseSide;
	protected final int heightMinusOne;
	protected final int baseCenterHeightAxisCoord;
	protected final int dimension;

	public HypercubePyramidGrid(Coordinates baseCenterCoordinates, int baseSide, int height, int heightAxis) {
		if (baseCenterCoordinates == null) {
			throw new IllegalArgumentException("The coordinates cannot be null.");
		}
		if (baseSide < 3) {
			throw new IllegalArgumentException("The base's side must be greater than two.");
		}
		if (baseSide%2 == 0) {
			throw new IllegalArgumentException("The base's side must be odd.");
		}
		if (height < 2) {
			throw new IllegalArgumentException("The height must be greater than one.");
		}
		this.baseCenterCoordinates = baseCenterCoordinates;
		dimension = baseCenterCoordinates.getCount();
		if (heightAxis < 0) {
			throw new IllegalArgumentException("The height's axis cannot be smaller than zero.");
		}
		if (heightAxis >= dimension) {
			throw new IllegalArgumentException("The height's axis must be smaller than the dimension.");
		}
		this.baseSide = baseSide;
		halfBaseSide = baseSide/2;
		this.height = height;
		heightMinusOne = height - 1;
		this.heightAxis = heightAxis;
		baseCenterHeightAxisCoord = baseCenterCoordinates.get(heightAxis);
	}

	@Override
	public int getGridDimension() {
		return dimension;
	}

	@Override
	public int getMaxCoordinate(int axis) {
		if (heightAxis == 0) {
			return baseCenterHeightAxisCoord + heightMinusOne;
		} else {
			return baseCenterCoordinates.get(axis) + halfBaseSide; 
		}
	}

	@Override
	public int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		if (coordinates.getCount() != dimension) {
			throw new IllegalArgumentException("The number of coordinates must be equal to the grid's dimension (" + dimension + ")");
		}
		if (axis == heightAxis) {
			int greaterRelativeAbsoluteCoord = 0;
			int i = 0;
			for (; i < heightAxis; i++) {
				Integer coord = coordinates.get(i);
				if (coord != null) {
					int relativeAbsoluteCoord = Math.abs(coord - baseCenterCoordinates.get(i));
					if (relativeAbsoluteCoord > greaterRelativeAbsoluteCoord) {
						greaterRelativeAbsoluteCoord = relativeAbsoluteCoord;
					}
				}
			}
			for (i++; i < dimension; i++) {
				Integer coord = coordinates.get(i);
				if (coord != null) {
					int relativeAbsoluteCoord = Math.abs(coord - baseCenterCoordinates.get(i));
					if (relativeAbsoluteCoord > greaterRelativeAbsoluteCoord) {
						greaterRelativeAbsoluteCoord = relativeAbsoluteCoord;
					}
				}
			}
			return baseCenterHeightAxisCoord + heightMinusOne - (int)Math.ceil(greaterRelativeAbsoluteCoord*(float)heightMinusOne/halfBaseSide);
		} else {
			int relativeHeightCoord = 0;
			Integer heightCoord = coordinates.get(heightAxis);
			if (heightCoord != null) {
				relativeHeightCoord = heightCoord - baseCenterHeightAxisCoord;
			}
			return baseCenterCoordinates.get(axis) + (int)Math.floor((heightMinusOne - relativeHeightCoord)*(float)halfBaseSide/heightMinusOne);
		}
	}

	@Override
	public int getMinCoordinate(int axis) {
		if (axis == heightAxis) {
			return baseCenterHeightAxisCoord;
		} else {
			return baseCenterCoordinates.get(axis) - halfBaseSide; 
		}
	}

	@Override
	public int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		if (coordinates.getCount() != dimension) {
			throw new IllegalArgumentException("The number of coordinates must be equal to the grid's dimension (" + dimension + ")");
		}
		if (axis == heightAxis) {
			return baseCenterHeightAxisCoord;
		} else {
			int relativeHeightCoord = 0;
			Integer heightCoord = coordinates.get(heightAxis);
			if (heightCoord != null) {
				relativeHeightCoord = heightCoord - baseCenterHeightAxisCoord;
			}
			return baseCenterCoordinates.get(axis) - (int)Math.floor((heightMinusOne - relativeHeightCoord)*(float)halfBaseSide/heightMinusOne);
		}
	}

}
