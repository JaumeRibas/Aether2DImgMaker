/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package cellularautomata.model2d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.Model;

public class ModelAs2D<Source_Type extends Model> implements Model2D {

	protected Source_Type source;
	
	public ModelAs2D(Source_Type source) {
		this.source = source;
		int dimension = source.getGridDimension();
		if (dimension != 2) {
			throw new IllegalArgumentException("Model's grid dimension (" + dimension + ") must be 2.");
		}
	}

	@Override
	public String getXLabel() {
		return source.getAxisLabel(0);
	}

	@Override
	public String getYLabel() {
		return source.getAxisLabel(1);
	}

	@Override
	public int getMinX() {
	    return source.getMinCoordinate(0);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxCoordinate(0);
	}

	@Override
	public int getMinX(int y) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, y));
	}

	@Override
	public int getMaxX(int y) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, y));
	}

	@Override
	public int getMinY() {
	    return source.getMinCoordinate(1);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxCoordinate(1);
	}

	@Override
	public int getMinY(int x) {
	    return source.getMinCoordinate(1, new PartialCoordinates(x, null));
	}

	@Override
	public int getMaxY(int x) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(x, null));
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
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}

}
