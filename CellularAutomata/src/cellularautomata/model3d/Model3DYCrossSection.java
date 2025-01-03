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

public class Model3DYCrossSection<Source_Type extends Model3D> implements Model2D {

	protected Source_Type source;
	protected int y;
	
	public Model3DYCrossSection(Source_Type source, int y) {
		if (y > source.getMaxY() || y < source.getMinY()) {
			throw new IllegalArgumentException("The cross section is out of bounds.");
		}
		this.source = source;
		this.y = y;
	}
	
	@Override
	public String getXLabel() {
	    return source.getXLabel();
	}

	@Override
	public String getYLabel() {
	    return source.getZLabel();
	}

	@Override
	public int getMinX() {
	    return source.getMinXAtY(this.y);
	}

	@Override
	public int getMaxX() {
	    return source.getMaxXAtY(this.y);
	}

	@Override
	public int getMinX(int y) {
	    return source.getMinX(this.y, y);
	}

	@Override
	public int getMaxX(int y) {
	    return source.getMaxX(this.y, y);
	}

	@Override
	public int getMinY() {
	    return source.getMinZAtY(this.y);
	}

	@Override
	public int getMaxY() {
	    return source.getMaxZAtY(this.y);
	}

	@Override
	public int getMinY(int x) {
	    return source.getMinZ(x, this.y);
	}

	@Override
	public int getMaxY(int x) {
	    return source.getMaxZ(x, this.y);
	}

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (y > source.getMaxY() || y < source.getMinY()) {
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
		return source.getSubfolderPath() + "/" + source.getYLabel() + "=" + y;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}

}
