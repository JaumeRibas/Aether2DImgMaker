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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Consumer;

import cellularautomata.Coordinates;
import cellularautomata.PartialCoordinates;

public class ModelDecorator<Source_Type extends Model> implements Model {
	
	protected Source_Type source;
	
	public ModelDecorator(Source_Type source) {
		this.source = source;
	}

	@Override
	public int getGridDimension() {
		return source.getGridDimension();
	}
	
	@Override
	public String getAxisLabel(int axis) {
		return source.getAxisLabel(axis);
	}
	
	@Override
	public int getMaxCoordinate(int axis) {
		return source.getMaxCoordinate(axis);
	}
	
	@Override
	public int getMaxCoordinate(int axis, PartialCoordinates coordinates) {
		return source.getMaxCoordinate(axis, coordinates);
	}
	
	@Override
	public int getMinCoordinate(int axis) {
		return source.getMaxCoordinate(axis);
	}
	
	@Override
	public int getMinCoordinate(int axis, PartialCoordinates coordinates) {
		return source.getMinCoordinate(axis, coordinates);
	}
	
	@Override
	public void forEachPosition(Consumer<Coordinates> consumer) {
		source.forEachPosition(consumer);
	}
	
	@Override
	public void forEachEvenPosition(Consumer<Coordinates> consumer) {
		source.forEachEvenPosition(consumer);
	}
	
	@Override
	public void forEachOddPosition(Consumer<Coordinates> consumer) {
		source.forEachOddPosition(consumer);
	}
	
	@Override
	public Model subsection(PartialCoordinates minCoordinates, PartialCoordinates maxCoordinates) {
		return source.subsection(minCoordinates, maxCoordinates);
	}
	
	@Override
	public Model crossSection(int axis, int coordinate) {
		return source.crossSection(axis, coordinate);
	}
	
	@Override
	public Model diagonalCrossSection(int firstAxis, int secondAxis, boolean positiveSlope, int offset) {
		return source.diagonalCrossSection(firstAxis, secondAxis, positiveSlope, offset);
	}

	@Override
	public Boolean nextStep() throws Exception {
		return source.nextStep();
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
		return source.getSubfolderPath();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
