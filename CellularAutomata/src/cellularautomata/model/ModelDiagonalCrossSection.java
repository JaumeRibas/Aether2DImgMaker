/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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

public class ModelDiagonalCrossSection<Source_Type extends Model> implements Model {
	
	protected Source_Type source;
	protected int firstAxis;
	protected int secondAxis;
	protected int slope;
	protected int offset;
	protected int[] crossSectionMinCoordinates;
	protected int[] crossSectionMaxCoordinates;
	protected int sourceDimension;
	protected int dimension;
	
	public ModelDiagonalCrossSection(Source_Type source, int firstAxis, int secondAxis, boolean positiveSlope, int offset) {		
		this.source = source;
		if (firstAxis < 0 || secondAxis < 0) {
			throw new IllegalArgumentException("The axes cannot be negative.");
		}
		if (firstAxis == secondAxis) {
			throw new IllegalArgumentException("The axes cannot be equal.");
		}
		if (firstAxis < secondAxis) {
			this.firstAxis = firstAxis;
			this.secondAxis = secondAxis;
			this.slope = positiveSlope ? 1 : -1;
			this.offset = offset;
		} else {
			this.firstAxis = secondAxis;
			this.secondAxis = firstAxis;
			if (positiveSlope) {
				this.slope = 1;
				this.offset = -offset;
			} else {
				this.slope = -1;
				this.offset = offset;
			}
		}
		sourceDimension = source.getGridDimension();
		if (sourceDimension < 2) {
			throw new IllegalArgumentException("The dimension must be greater than 1.");
		}
		dimension = sourceDimension - 1;
		if (this.secondAxis > dimension) {
			throw new IllegalArgumentException("The axes cannot be greater than " + dimension + ".");
		}
		if (!getBounds()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
	}
	
	protected boolean getBounds() {
		crossSectionMinCoordinates = new int[dimension];
		crossSectionMaxCoordinates = new int[dimension];
		int firstAxisCoordinate = source.getMinCoordinate(firstAxis);
		int firstAxisMaxCoordinate = source.getMaxCoordinate(firstAxis);
		int crossSectionSecondAxisCoordinate = slope*firstAxisCoordinate + offset;
		Integer[] partialCoords = new Integer[sourceDimension];
		partialCoords[firstAxis] = firstAxisCoordinate;
		PartialCoordinates partialCoordsObj = new PartialCoordinates(partialCoords);
		while (firstAxisCoordinate <= firstAxisMaxCoordinate 
				&& (crossSectionSecondAxisCoordinate < source.getMinCoordinate(secondAxis, partialCoordsObj) || crossSectionSecondAxisCoordinate > source.getMaxCoordinate(secondAxis, partialCoordsObj))) {
			firstAxisCoordinate++;
			partialCoords[firstAxis] = firstAxisCoordinate;
			partialCoordsObj = new PartialCoordinates(partialCoords);
			crossSectionSecondAxisCoordinate += slope;
		}
		if (firstAxisCoordinate <= firstAxisMaxCoordinate) {
			crossSectionMinCoordinates[firstAxis] = firstAxisCoordinate;
			crossSectionMaxCoordinates[firstAxis] = firstAxisCoordinate;
			partialCoords[secondAxis] = crossSectionSecondAxisCoordinate;
			partialCoordsObj = new PartialCoordinates(partialCoords);			
			int axis = 0;
			for (; axis != firstAxis; axis++) {
				crossSectionMinCoordinates[axis] = source.getMinCoordinate(axis, partialCoordsObj);
				crossSectionMaxCoordinates[axis] = source.getMaxCoordinate(axis, partialCoordsObj);
			}
			axis++;
			for (; axis != dimension; axis++) {
				crossSectionMinCoordinates[axis] = source.getMinCoordinate(axis, partialCoordsObj);
				crossSectionMaxCoordinates[axis] = source.getMaxCoordinate(axis, partialCoordsObj);
			}
			firstAxisCoordinate++;
			crossSectionSecondAxisCoordinate += slope;
			partialCoords[firstAxis] = firstAxisCoordinate;
			partialCoords[secondAxis] = crossSectionSecondAxisCoordinate;
			partialCoordsObj = new PartialCoordinates(partialCoords);
			while (firstAxisCoordinate <= firstAxisMaxCoordinate 
					&& crossSectionSecondAxisCoordinate >= source.getMinCoordinate(secondAxis, partialCoordsObj) && crossSectionSecondAxisCoordinate <= source.getMaxCoordinate(secondAxis, partialCoordsObj)) {
				crossSectionMaxCoordinates[firstAxis] = firstAxisCoordinate;			
				axis = 0;
				for (; axis != firstAxis; axis++) {
					int localMinCoord = source.getMinCoordinate(axis, partialCoordsObj);
					int localMaxCoord = source.getMaxCoordinate(axis, partialCoordsObj);
					if (localMaxCoord > crossSectionMaxCoordinates[axis]) {
						crossSectionMaxCoordinates[axis] = localMaxCoord;
					}
					if (localMinCoord < crossSectionMinCoordinates[axis]) {
						crossSectionMinCoordinates[axis] = localMinCoord;
					}
				}
				axis++;
				for (; axis != dimension; axis++) {
					int localMinCoord = source.getMinCoordinate(axis, partialCoordsObj);
					int localMaxCoord = source.getMaxCoordinate(axis, partialCoordsObj);
					if (localMaxCoord > crossSectionMaxCoordinates[axis]) {
						crossSectionMaxCoordinates[axis] = localMaxCoord;
					}
					if (localMinCoord < crossSectionMinCoordinates[axis]) {
						crossSectionMinCoordinates[axis] = localMinCoord;
					}
				}
				firstAxisCoordinate++;
				crossSectionSecondAxisCoordinate += slope;
				partialCoords[firstAxis] = firstAxisCoordinate;
				partialCoords[secondAxis] = crossSectionSecondAxisCoordinate;
				partialCoordsObj = new PartialCoordinates(partialCoords);
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public int getGridDimension() {
		return dimension;
	}
	
	protected int getSourceAxis(int axis) {
		if (axis < this.secondAxis) {
			return axis;
		} else {
			return axis + 1;
		}
	}
	
	protected PartialCoordinates getSourceCoordinates(PartialCoordinates coordinates) {
		Integer[] sourceCoordinatesArray = new Integer[sourceDimension];
		int sourceAxis = 0;
		for (; sourceAxis != firstAxis; sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(sourceAxis);
		}
		Integer firstAixsCoordinate = coordinates.get(firstAxis);
		sourceCoordinatesArray[firstAxis] = firstAixsCoordinate;
		sourceAxis++;
		for (; sourceAxis != secondAxis; sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(sourceAxis);
		}
		if (firstAixsCoordinate != null) {
			sourceCoordinatesArray[secondAxis] = slope*firstAixsCoordinate + offset;
		}
		int crossSectionAxis = sourceAxis; 
		sourceAxis++;
		for (; sourceAxis != sourceDimension; crossSectionAxis = sourceAxis, sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(crossSectionAxis);
		}
		return new PartialCoordinates(sourceCoordinatesArray);
	}
	
	protected Coordinates getSourceCoordinates(Coordinates coordinates) {
		int[] sourceCoordinatesArray = new int[sourceDimension];
		int sourceAxis = 0;
		for (; sourceAxis != firstAxis; sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(sourceAxis);
		}
		int firstAxisCoordinate = coordinates.get(firstAxis);
		sourceCoordinatesArray[firstAxis] = firstAxisCoordinate;
		sourceAxis++;
		for (; sourceAxis != secondAxis; sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(sourceAxis);
		}
		sourceCoordinatesArray[secondAxis] = slope*firstAxisCoordinate + offset;
		int crossSectionAxis = sourceAxis; 
		sourceAxis++;
		for (; sourceAxis != sourceDimension; crossSectionAxis = sourceAxis, sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(crossSectionAxis);
		}
		return new Coordinates(sourceCoordinatesArray);
	}
	
	@Override
	public String getAxisLabel(int axis) {
		return source.getAxisLabel(getSourceAxis(axis));
	}
	
	@Override
	public int getMinCoordinate(int axis) { 
		return crossSectionMinCoordinates[axis]; 
	}
	
	@Override
	public int getMaxCoordinate(int axis) { 
		return crossSectionMaxCoordinates[axis]; 
	}
	
	@Override
	public int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		PartialCoordinates sourceCoordinates = getSourceCoordinates(coordinates);
		if (axis == firstAxis) {
			Integer[] sourceCoordinatesArray = sourceCoordinates.getCopyAsArray();
			int crossSectionMaxAxis1Coordinate = crossSectionMaxCoordinates[firstAxis];
			for (int crossSectionAxis1Coordinate = crossSectionMinCoordinates[firstAxis], crossSectionAxis2Coordinate = slope*crossSectionAxis1Coordinate + offset; 
					crossSectionAxis1Coordinate <= crossSectionMaxAxis1Coordinate; 
					crossSectionAxis1Coordinate++, crossSectionAxis2Coordinate += slope) {
				sourceCoordinatesArray[firstAxis] = crossSectionAxis1Coordinate;
				sourceCoordinatesArray[secondAxis] = crossSectionAxis2Coordinate;
				if (source.isWithinBounds(new PartialCoordinates(sourceCoordinatesArray))) {
					return crossSectionAxis1Coordinate;
				}
			}
			throw new IllegalArgumentException("Coordinates out of bounds.");
		} else {
			int sourceAxis = getSourceAxis(axis);
			if (coordinates.get(firstAxis) == null) {
				Integer[] sourceCoordinatesArray = sourceCoordinates.getCopyAsArray();
				int crossSectionAxis1Coordinate = crossSectionMinCoordinates[firstAxis];
				sourceCoordinatesArray[firstAxis] = crossSectionAxis1Coordinate;
				sourceCoordinatesArray[secondAxis] = slope*crossSectionAxis1Coordinate + offset;
				int minCoordinate = source.getMinCoordinate(sourceAxis, new PartialCoordinates(sourceCoordinatesArray));
				int localMinCoordinate;
				int crossSectionMaxAxis1Coordinate = crossSectionMaxCoordinates[firstAxis];
				for (crossSectionAxis1Coordinate = ++sourceCoordinatesArray[firstAxis], sourceCoordinatesArray[secondAxis] += slope; 
						crossSectionAxis1Coordinate <= crossSectionMaxAxis1Coordinate && (localMinCoordinate = source.getMinCoordinate(sourceAxis, new PartialCoordinates(sourceCoordinatesArray))) <= minCoordinate; 
						crossSectionAxis1Coordinate = ++sourceCoordinatesArray[firstAxis], sourceCoordinatesArray[secondAxis] += slope) {
					minCoordinate = localMinCoordinate;
				}
				return minCoordinate;
			} else {
				return source.getMinCoordinate(sourceAxis, sourceCoordinates);
			}
		}
	}
	
	@Override
	public int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		PartialCoordinates sourceCoordinates = getSourceCoordinates(coordinates);
		if (axis == firstAxis) {
			Integer[] sourceCoordinatesArray = sourceCoordinates.getCopyAsArray();
			int crossSectionMinAxis1Coordinate = crossSectionMinCoordinates[firstAxis];
			for (int crossSectionAxis1Coordinate = crossSectionMaxCoordinates[firstAxis], crossSectionAxis2Coordinate = slope*crossSectionAxis1Coordinate + offset; 
					crossSectionAxis1Coordinate >= crossSectionMinAxis1Coordinate; 
					crossSectionAxis1Coordinate--, crossSectionAxis2Coordinate -= slope) {
				sourceCoordinatesArray[firstAxis] = crossSectionAxis1Coordinate;
				sourceCoordinatesArray[secondAxis] = crossSectionAxis2Coordinate;
				if (source.isWithinBounds(new PartialCoordinates(sourceCoordinatesArray))) {
					return crossSectionAxis1Coordinate;
				}
			}
			throw new IllegalArgumentException("Coordinates out of bounds.");
		} else {
			int sourceAxis = getSourceAxis(axis);
			if (coordinates.get(firstAxis) == null) {
				Integer[] sourceCoordinatesArray = sourceCoordinates.getCopyAsArray();
				int crossSectionAxis1Coordinate = crossSectionMinCoordinates[firstAxis];
				sourceCoordinatesArray[firstAxis] = crossSectionAxis1Coordinate;
				sourceCoordinatesArray[secondAxis] = slope*crossSectionAxis1Coordinate + offset;
				int maxCoordinate = source.getMaxCoordinate(sourceAxis, new PartialCoordinates(sourceCoordinatesArray));
				int localMaxCoordinate;
				int crossSectionMaxAxis1Coordinate = crossSectionMaxCoordinates[firstAxis];
				for (crossSectionAxis1Coordinate = ++sourceCoordinatesArray[firstAxis], sourceCoordinatesArray[secondAxis] += slope; 
						crossSectionAxis1Coordinate <= crossSectionMaxAxis1Coordinate && (localMaxCoordinate = source.getMaxCoordinate(sourceAxis, new PartialCoordinates(sourceCoordinatesArray))) >= maxCoordinate; 
						crossSectionAxis1Coordinate = ++sourceCoordinatesArray[firstAxis], sourceCoordinatesArray[secondAxis] += slope) {
					maxCoordinate = localMaxCoordinate;
				}
				return maxCoordinate;
			} else {
				return source.getMaxCoordinate(sourceAxis, sourceCoordinates);
			}
		}
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (!getBounds()) {
			throw new UnsupportedOperationException("The cross section is out of bounds.");
		}
		return changed;
	}

	@Override
	public Boolean isChanged() {
		return source.isChanged();
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName();
	}

	@Override
	public String getSubfolderPath() {
		StringBuilder path = new StringBuilder();
		path.append(source.getSubfolderPath()).append("/").append(source.getAxisLabel(secondAxis)).append("=");
		if (slope == -1) {
			path.append("-");
		}
		path.append(source.getAxisLabel(firstAxis));
		if (offset < 0) {
			path.append(offset);
		} else if (offset > 0) {
			path.append("+").append(offset);
		}
		return path.toString();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}

}
