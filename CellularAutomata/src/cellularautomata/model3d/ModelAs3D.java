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
package cellularautomata.model3d;

import cellularautomata.PartialCoordinates;
import cellularautomata.model.Model;

public class ModelAs3D<Source_Type extends Model> implements Model3D {

	protected final Source_Type source;
	
	public ModelAs3D(Source_Type source) {
		this.source = source;
		int dimension = source.getGridDimension();
		if (dimension != 3) {
			throw new IllegalArgumentException("Model's grid dimension (" + dimension + ") must be 3.");
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
	public String getZLabel() {
		return source.getAxisLabel(2);
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
	public int getMinXAtY(int y) {
		return source.getMinCoordinate(0, new PartialCoordinates(null, y, null));
	}

	@Override
	public int getMaxXAtY(int y) {
		return source.getMaxCoordinate(0, new PartialCoordinates(null, y, null));
	}

	@Override
	public int getMinXAtZ(int z) {
		return source.getMinCoordinate(0, new PartialCoordinates(null, null, z));
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxCoordinate(0, new PartialCoordinates(null, null, z));
	}

	@Override
	public int getMinX(int y, int z) {
		return source.getMinCoordinate(0, new PartialCoordinates(null, y, z));
	}

	@Override
	public int getMaxX(int y, int z) {
		return source.getMaxCoordinate(0, new PartialCoordinates(null, y, z));
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
	public int getMinYAtX(int x) {
		return source.getMinCoordinate(1, new PartialCoordinates(x, null, null));
	}

	@Override
	public int getMaxYAtX(int x) {
		return source.getMaxCoordinate(1, new PartialCoordinates(x, null, null));
	}

	@Override
	public int getMinYAtZ(int z) {
		return source.getMinCoordinate(1, new PartialCoordinates(null, null, z));
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxCoordinate(1, new PartialCoordinates(null, null, z));
	}

	@Override
	public int getMinY(int x, int z) {
		return source.getMinCoordinate(1, new PartialCoordinates(x, null, z));
	}

	@Override
	public int getMaxY(int x, int z) {
		return source.getMaxCoordinate(1, new PartialCoordinates(x, null, z));
	}

	@Override
	public int getMinZ() {
		return source.getMinCoordinate(2);
	}

	@Override
	public int getMaxZ() {
		return source.getMaxCoordinate(2);
	}

	@Override
	public int getMinZAtX(int x) {
		return source.getMinCoordinate(2, new PartialCoordinates(x, null, null));
	}

	@Override
	public int getMaxZAtX(int x) {
		return source.getMaxCoordinate(2, new PartialCoordinates(x, null, null));
	}

	@Override
	public int getMinZAtY(int y) {
		return source.getMinCoordinate(2, new PartialCoordinates(null, y, null));
	}

	@Override
	public int getMaxZAtY(int y) {
		return source.getMaxCoordinate(2, new PartialCoordinates(null, y, null));
	}

	@Override
	public int getMinZ(int x, int y) {
		return source.getMinCoordinate(2, new PartialCoordinates(x, y, null));
	}

	@Override
	public int getMaxZ(int x, int y) {
		return source.getMaxCoordinate(2, new PartialCoordinates(x, y, null));
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
