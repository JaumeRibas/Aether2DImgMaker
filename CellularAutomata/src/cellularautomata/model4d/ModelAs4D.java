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
package cellularautomata.model4d;

import cellularautomata.model.Model;
import cellularautomata.PartialCoordinates;

public class ModelAs4D<Source_Type extends Model> implements Model4D {

	protected final Source_Type source;
	
	public ModelAs4D(Source_Type source) {
		this.source = source;
		int dimension = source.getGridDimension();
		if (dimension != 4) {
			throw new IllegalArgumentException("Model's grid dimension (" + dimension + ") must be 4.");
		}
	}
	
	@Override
	public String getWLabel() {
		return source.getAxisLabel(0);
	}
	
	@Override
	public String getXLabel() {
		return source.getAxisLabel(1);
	}
	
	@Override
	public String getYLabel() {
		return source.getAxisLabel(2);
	}
	
	@Override
	public String getZLabel() {
		return source.getAxisLabel(3);
	}
	
	@Override
	public int getMinW() {
	    return source.getMinCoordinate(0);
	}

	@Override
	public int getMaxW() {
	    return source.getMaxCoordinate(0);
	}

	@Override
	public int getMinWAtX(int x) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, x, null, null));
	}

	@Override
	public int getMaxWAtX(int x) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, x, null, null));
	}

	@Override
	public int getMinWAtY(int y) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, y, null));
	}

	@Override
	public int getMaxWAtY(int y) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, y, null));
	}

	@Override
	public int getMinWAtZ(int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, null, z));
	}

	@Override
	public int getMaxWAtZ(int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, null, z));
	}

	@Override
	public int getMinWAtXY(int x, int y) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, x, y, null));
	}

	@Override
	public int getMaxWAtXY(int x, int y) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, x, y, null));
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, x, null, z));
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, x, null, z));
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, null, y, z));
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, null, y, z));
	}

	@Override
	public int getMinW(int x, int y, int z) {
	    return source.getMinCoordinate(0, new PartialCoordinates(null, x, y, z));
	}

	@Override
	public int getMaxW(int x, int y, int z) {
	    return source.getMaxCoordinate(0, new PartialCoordinates(null, x, y, z));
	}

	@Override
	public int getMinX() {
	    return source.getMinCoordinate(1);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxCoordinate(1);
	}

	@Override
	public int getMinXAtW(int w) {
	    return source.getMinCoordinate(1, new PartialCoordinates(w, null, null, null));
	}

	@Override
	public int getMaxXAtW(int w) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(w, null, null, null));
	}

	@Override
	public int getMinXAtY(int y) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, y, null));
	}

	@Override
	public int getMaxXAtY(int y) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, y, null));
	}

	@Override
	public int getMinXAtZ(int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, null, z));
	}

	@Override
	public int getMaxXAtZ(int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, null, z));
	}

	@Override
	public int getMinXAtWY(int w, int y) {
	    return source.getMinCoordinate(1, new PartialCoordinates(w, null, y, null));
	}

	@Override
	public int getMaxXAtWY(int w, int y) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(w, null, y, null));
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(w, null, null, z));
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(w, null, null, z));
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(null, null, y, z));
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(null, null, y, z));
	}

	@Override
	public int getMinX(int w, int y, int z) {
	    return source.getMinCoordinate(1, new PartialCoordinates(w, null, y, z));
	}

	@Override
	public int getMaxX(int w, int y, int z) {
	    return source.getMaxCoordinate(1, new PartialCoordinates(w, null, y, z));
	}

	@Override
	public int getMinY() {
	    return source.getMinCoordinate(2);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxCoordinate(2);
	}

	@Override
	public int getMinYAtW(int w) {
	    return source.getMinCoordinate(2, new PartialCoordinates(w, null, null, null));
	}

	@Override
	public int getMaxYAtW(int w) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(w, null, null, null));
	}

	@Override
	public int getMinYAtX(int x) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, x, null, null));
	}

	@Override
	public int getMaxYAtX(int x) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, x, null, null));
	}

	@Override
	public int getMinYAtZ(int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, null, null, z));
	}

	@Override
	public int getMaxYAtZ(int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, null, null, z));
	}

	@Override
	public int getMinYAtWX(int w, int x) {
	    return source.getMinCoordinate(2, new PartialCoordinates(w, x, null, null));
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(w, x, null, null));
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(w, null, null, z));
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(w, null, null, z));
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(null, x, null, z));
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(null, x, null, z));
	}

	@Override
	public int getMinY(int w, int x, int z) {
	    return source.getMinCoordinate(2, new PartialCoordinates(w, x, null, z));
	}

	@Override
	public int getMaxY(int w, int x, int z) {
	    return source.getMaxCoordinate(2, new PartialCoordinates(w, x, null, z));
	}

	@Override
	public int getMinZ() {
	    return source.getMinCoordinate(3);
	}

	@Override
	public int getMaxZ() {
	    return source.getMaxCoordinate(3);
	}

	@Override
	public int getMinZAtW(int w) {
	    return source.getMinCoordinate(3, new PartialCoordinates(w, null, null, null));
	}

	@Override
	public int getMaxZAtW(int w) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(w, null, null, null));
	}

	@Override
	public int getMinZAtX(int x) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, x, null, null));
	}

	@Override
	public int getMaxZAtX(int x) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, x, null, null));
	}

	@Override
	public int getMinZAtY(int y) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, null, y, null));
	}

	@Override
	public int getMaxZAtY(int y) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, null, y, null));
	}

	@Override
	public int getMinZAtWX(int w, int x) {
	    return source.getMinCoordinate(3, new PartialCoordinates(w, x, null, null));
	}

	@Override
	public int getMaxZAtWX(int w, int x) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(w, x, null, null));
	}

	@Override
	public int getMinZAtWY(int w, int y) {
	    return source.getMinCoordinate(3, new PartialCoordinates(w, null, y, null));
	}

	@Override
	public int getMaxZAtWY(int w, int y) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(w, null, y, null));
	}

	@Override
	public int getMinZAtXY(int x, int y) {
	    return source.getMinCoordinate(3, new PartialCoordinates(null, x, y, null));
	}

	@Override
	public int getMaxZAtXY(int x, int y) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(null, x, y, null));
	}

	@Override
	public int getMinZ(int w, int x, int y) {
	    return source.getMinCoordinate(3, new PartialCoordinates(w, x, y, null));
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
	    return source.getMaxCoordinate(3, new PartialCoordinates(w, x, y, null));
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
