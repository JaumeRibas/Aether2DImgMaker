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

import cellularautomata.model2d.Model2D;

public class Model3DZCrossSection<Source_Type extends Model3D> implements Model2D {

	protected Source_Type source;
	protected int z;
	
	public Model3DZCrossSection(Source_Type source, int z) {
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.z = z;
	}
	
	@Override
	public String getXLabel() {
	    return source.getXLabel();
	}

	@Override
	public String getYLabel() {
	    return source.getYLabel();
	}

	@Override
	public int getMinX() {
	    return source.getMinXAtZ(this.z);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxXAtZ(this.z);
	}

	@Override
	public int getMinX(int y) {
	    return source.getMinX(y, this.z);
	}

	@Override
	public int getMaxX(int y) {
	    return source.getMaxX(y, this.z);
	}

	@Override
	public int getMinY() {
	    return source.getMinYAtZ(this.z);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxYAtZ(this.z);
	}

	@Override
	public int getMinY(int x) {
	    return source.getMinY(x, this.z);
	}

	@Override
	public int getMaxY(int x) {
	    return source.getMaxY(x, this.z);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (z > source.getMaxZ() || z < source.getMinZ()) {
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
		return source.getSubfolderPath() + "/" + source.getZLabel() + "=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}

}
