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

import cellularautomata.PartialCoordinates;

public class ModelCrossSection<Source_Type extends Model> implements Model {

	protected Source_Type source;
	protected int axis;
	protected int coordinate;
	protected int dimension;
	protected PartialCoordinates coordinates;
	
	public ModelCrossSection(Source_Type source, int axis, int coordinate) {
		if (axis < 0) {
			throw new IllegalArgumentException("Axis cannot be negative.");
		}
		int dimension = source.getGridDimension();
		int dimensionMinusOne = dimension - 1;
		if (axis > dimensionMinusOne) {
			throw new IllegalArgumentException("Axis cannot be greater than " + dimensionMinusOne + ".");
		}
		if (coordinate > source.getMaxCoordinate(axis) || coordinate < source.getMinCoordinate(axis)) {
			throw new IllegalArgumentException("The coordinate is out of bounds.");
		}
		this.source = source;
		this.dimension = dimensionMinusOne;
		this.axis = axis;
		this.coordinate = coordinate;
		Integer[] coordArray = new Integer[dimension];
		coordArray[axis] = coordinate;
		this.coordinates = new PartialCoordinates(coordArray);
	}
	
	@Override
	public int getGridDimension() {
		return dimension;
	}
	
	protected int getSourceAxis(int axis) {
		if (axis < this.axis) {
			return axis;
		} else {
			return axis + 1;
		}
	}
	
	@Override
	public String getAxisLabel(int axis) {
		return source.getAxisLabel(getSourceAxis(axis));
	}
	
	@Override
	public int getMinCoordinate(int axis) {
		return source.getMinCoordinate(getSourceAxis(axis), coordinates);
	}
	
	@Override
	public int getMaxCoordinate(int axis) {
		return source.getMaxCoordinate(getSourceAxis(axis), coordinates);
	}
	
	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (coordinate > source.getMaxCoordinate(axis) || coordinate < source.getMinCoordinate(axis)) {
			throw new UnsupportedOperationException("The coordinate is out of bounds.");
		}
		return changed;
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
		return source.getSubfolderPath() + "/" + source.getAxisLabel(axis) + "=" + coordinate;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
