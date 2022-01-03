/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model2d.Model2D;

public class Model3DYCrossSection<G extends Model3D> implements Model2D {

	protected G source;
	protected int y;
	
	public Model3DYCrossSection(G source, int y) {
		if (y > source.getMaxY() || y < source.getMinY()) {
			throw new IllegalArgumentException("Y coordinate is out of bounds.");
		}
		this.source = source;
		this.y = y;
	}

	@Override
	public int getMinX() {
		return source.getMinXAtY(y);
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinX(this.y, y);
	}
	
	@Override
	public int getMaxX() {
		return source.getMaxXAtY(y);
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxX(this.y, y);
	}
	
	@Override
	public int getMinY() {
		return source.getMinZAtY(y);
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinZ(x, y);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxZAtY(y);
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxZ(x, y);
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
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (y > source.getMaxY() || y < source.getMinY()) {
			throw new UnsupportedOperationException("Y coordinate is out of bounds.");
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
		return source.getSubfolderPath() + "/" + source.getYLabel() + "=" + y;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
