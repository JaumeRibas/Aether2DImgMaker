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

public class ModelCrossSection<Source_Type extends Model> extends ModelDecorator<Source_Type> {

	protected int crossSectionAxis;
	protected int crossSectionCoordinate;
	protected PartialCoordinates crossSectionCoordinates;
	protected int sourceDimension;
	protected int dimension;
	
	public ModelCrossSection(Source_Type source, int axis, int coordinate) {
		super(source);
		if (axis < 0) {
			throw new IllegalArgumentException("The axis cannot be negative.");
		}
		sourceDimension = source.getGridDimension();
		dimension = sourceDimension - 1;
		if (axis > dimension) {
			throw new IllegalArgumentException("The axis cannot be greater than " + dimension + ".");
		}
		if (coordinate > source.getMaxCoordinate(axis) || coordinate < source.getMinCoordinate(axis)) {
			throw new IllegalArgumentException("The coordinate is out of bounds.");
		}
		this.source = source;
		this.crossSectionAxis = axis;
		this.crossSectionCoordinate = coordinate;
		Integer[] coordArray = new Integer[sourceDimension];
		coordArray[axis] = coordinate;
		crossSectionCoordinates = new PartialCoordinates(coordArray);
	}
	
	@Override
	public int getGridDimension() {
		return dimension;
	}
	
	protected int getSourceAxis(int axis) {
		if (axis < this.crossSectionAxis) {
			return axis;
		} else {
			return axis + 1;
		}
	}
	
	protected PartialCoordinates getSourceCoordinates(PartialCoordinates coordinates) {
		Integer[] sourceCoordinatesArray = new Integer[sourceDimension];
		int sourceAxis = 0;
		for (; sourceAxis != this.crossSectionAxis; sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(sourceAxis);
		}
		sourceCoordinatesArray[sourceAxis] = crossSectionCoordinate;
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
		for (; sourceAxis != this.crossSectionAxis; sourceAxis++) {
			sourceCoordinatesArray[sourceAxis] = coordinates.get(sourceAxis);
		}
		sourceCoordinatesArray[sourceAxis] = crossSectionCoordinate;
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
		return source.getMinCoordinate(getSourceAxis(axis), crossSectionCoordinates);
	}
	
	@Override
	public int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return source.getMinCoordinate(getSourceAxis(axis), getSourceCoordinates(coordinates));
	}
	
	@Override
	public int getMaxCoordinate(int axis) {
		return source.getMaxCoordinate(getSourceAxis(axis), crossSectionCoordinates);
	}
	
	@Override
	public int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return source.getMaxCoordinate(getSourceAxis(axis), getSourceCoordinates(coordinates));
	}
	
	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (crossSectionCoordinate > source.getMaxCoordinate(crossSectionAxis) || crossSectionCoordinate < source.getMinCoordinate(crossSectionAxis)) {
			throw new UnsupportedOperationException("The coordinate is out of bounds.");
		}
		return changed;
	}

	@Override
	public String getSubfolderPath() {
		return source.getSubfolderPath() + "/" + source.getAxisLabel(crossSectionAxis) + "=" + crossSectionCoordinate;
	}

}
