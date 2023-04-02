/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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
 * Hypercubic pyramid shaped region of a grid
 * 
 * @author Jaume
 *
 */
public class HypercubicPyramidGrid implements Model {
	
	protected final Coordinates baseCenterCoordinates;
	protected final int halfBaseSide;
	protected final int heightAxis;
	protected final int baseCenterHeightAxisCoord;
	protected final int dimension;

	public HypercubicPyramidGrid(Coordinates baseCenterCoordinates, int baseSide, int heightAxis) {
		if (baseCenterCoordinates == null) {
			throw new IllegalArgumentException("The coordinates cannot be null.");
		}
		if (baseSide < 1) {
			throw new IllegalArgumentException("The base's side must be greater than zero.");
		}
		if (baseSide%2 == 0) {
			throw new IllegalArgumentException("The base's side must be odd.");
		}
		if (heightAxis < 0) {
			throw new IllegalArgumentException("The height's axis cannot be smaller than zero.");
		}
		dimension = baseCenterCoordinates.getCount();
		if (heightAxis >= dimension) {
			throw new IllegalArgumentException("The height's axis must be smaller than the dimension.");
		}
		halfBaseSide = baseSide/2;
		this.baseCenterCoordinates = baseCenterCoordinates;
		this.heightAxis = heightAxis;
		baseCenterHeightAxisCoord = baseCenterCoordinates.get(heightAxis);
		//TODO check that resulting bounds don't overflow int type
	}

	@Override
	public int getGridDimension() {
		return dimension;
	}

	@Override
	public int getMaxCoordinate(int axis) {
		return baseCenterCoordinates.get(axis) + halfBaseSide;
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
			return baseCenterHeightAxisCoord + halfBaseSide - greaterRelativeAbsoluteCoord;
		} else {
			int relativeHeightCoord = 0;
			Integer heightCoord = coordinates.get(heightAxis);
			if (heightCoord != null) {
				relativeHeightCoord = heightCoord - baseCenterHeightAxisCoord;
			}
			return baseCenterCoordinates.get(axis) + halfBaseSide - relativeHeightCoord;
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
			return baseCenterCoordinates.get(axis) - halfBaseSide + relativeHeightCoord;
		}
	}

	@Override
	public Boolean nextStep() throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public Boolean isChanged() {
		throw new UnsupportedOperationException();
	}

	@Override
	public long getStep() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getSubfolderPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		throw new UnsupportedOperationException();
	}

}
