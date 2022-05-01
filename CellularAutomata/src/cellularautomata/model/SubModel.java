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

import cellularautomata.PartialCoordinates;

public class SubModel<Source_Type extends Model> extends ModelDecorator<Source_Type> implements Model {
	
	protected int[] minCoordinates;
	protected int[] maxCoordinates;
	protected PartialCoordinates absoluteMinCoordinates;
	protected PartialCoordinates absoluteMaxCoordinates;
	
	public SubModel(Source_Type source, PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		super(source);
		int dimension = getGridDimension();
		if (minCoordinates.getCount() != dimension) {
			throw new IllegalArgumentException("The number of min coordinates must be equal to the grid dimension (" + dimension + ").");
		}
		if (maxCoordinates.getCount() != dimension) {
			throw new IllegalArgumentException("The number of max coordinates must be equal to the grid dimension (" + dimension + ").");
		}
		boolean allNull = true;
		for (int axis = 0; axis < dimension; axis++) {
			Integer minCoord = minCoordinates.get(axis);
			Integer maxCoord = maxCoordinates.get(axis);
			if (minCoord != null) {
				allNull = false;
				if (maxCoord != null && minCoord > maxCoord) {
					throw new IllegalArgumentException("Min coordinates cannot be bigger than max coordinates.");
				}
			} else if (maxCoord != null) {
				allNull = false;
			}
		}
		if (allNull) {
			throw new IllegalArgumentException("All min and max coordinates are null.");
		}
		this.minCoordinates = new int[dimension];
		this.maxCoordinates = new int[dimension];
		if (!getActualBounds(minCoordinates, maxCoordinates)) {
			throw new IllegalArgumentException("Subsection is out of bounds.");
		}
		absoluteMinCoordinates = minCoordinates;
		absoluteMaxCoordinates = maxCoordinates;
	}
	
	protected boolean getActualBounds(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		int dimension = getGridDimension();
		for (int axis = 0; axis < dimension; axis++) {
			int sourceMinCoordinate = source.getMinCoordinate(axis);
			int sourceMaxCoordinate = source.getMaxCoordinate(axis);
			Integer minCoordinate = minCoordinates.get(axis);
			if (minCoordinate == null) {
				this.minCoordinates[axis] = sourceMinCoordinate;
			} else {
				if (minCoordinate > sourceMaxCoordinate) {
					return false;
				} else {
					this.minCoordinates[axis] = Math.max(minCoordinate, sourceMinCoordinate);
				}
			}
			Integer maxCoordinate = maxCoordinates.get(axis);
			if (maxCoordinate == null) {
				this.maxCoordinates[axis] = sourceMaxCoordinate;
			} else {
				if (maxCoordinate < sourceMinCoordinate) {
					return false;
				} else {
					this.maxCoordinates[axis] = Math.min(maxCoordinate, sourceMaxCoordinate);
				}
			}
			//TODO validate that passed bounds are within local bounds
		}
		return true;
	}

	@Override
	public int getMinCoordinate(int axis) {
		return minCoordinates[axis];
	}

	@Override
	public int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return Math.max(minCoordinates[axis], source.getMinCoordinate(axis, coordinates));
	}

	@Override
	public int getMaxCoordinate(int axis) {
		return maxCoordinates[axis];
	}

	@Override
	public int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return Math.min(maxCoordinates[axis], source.getMaxCoordinate(axis, coordinates));
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinCoordinates, absoluteMaxCoordinates)) {
			throw new UnsupportedOperationException("Subsection is out of bounds.");
		}
		return changed;
	}

	@Override
	public String getSubfolderPath() {
		StringBuilder strCoordinateBounds = new StringBuilder();
		if (minCoordinates.length > 0) {
			strCoordinateBounds.append("/");
			int axis = 0;
			Integer minCoord = absoluteMinCoordinates.get(axis);
			Integer maxCoord = absoluteMaxCoordinates.get(axis);
			if (minCoord != null || maxCoord != null) {
				strCoordinateBounds.append(getAxisLabel(axis));
				if (minCoord == null) {
					strCoordinateBounds.append("(∞");
				} else {
					strCoordinateBounds.append("[").append(minCoord);
				}
				strCoordinateBounds.append(",");
				if (maxCoord == null) {
					strCoordinateBounds.append("∞)");
				} else {
					strCoordinateBounds.append(maxCoord).append("]");
				}
			}
			for (axis++; axis < minCoordinates.length; axis++) {
				minCoord = absoluteMinCoordinates.get(axis);
				maxCoord = absoluteMaxCoordinates.get(axis);
				if (minCoord != null || maxCoord != null) {
					strCoordinateBounds.append("_").append(getAxisLabel(axis));
					if (minCoord == null) {
						strCoordinateBounds.append("(∞");
					} else {
						strCoordinateBounds.append("[").append(minCoord);
					}
					strCoordinateBounds.append(",");
					if (maxCoord == null) {
						strCoordinateBounds.append("∞)");
					} else {
						strCoordinateBounds.append(maxCoord).append("]");
					}
				}
			}
		}
		return source.getSubfolderPath() + strCoordinateBounds.toString();
	}

}
