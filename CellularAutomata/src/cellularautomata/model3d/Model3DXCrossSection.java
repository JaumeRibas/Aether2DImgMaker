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
package cellularautomata.model3d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.model2d.Model2D;

public class Model3DXCrossSection<G extends Model3D> implements Model2D {

	protected G source;
	protected int x;
	
	public Model3DXCrossSection(G source, int x) {
		if (x > source.getMaxX() || x < source.getMinX()) {
			throw new IllegalArgumentException("X coordinate is out of bounds.");
		}
		this.source = source;
		this.x = x;
	}

	@Override
	public int getMinX() {
		return source.getMinZAtX(x);
	}
	
	@Override
	public int getMinX(int y) {
		return source.getMinZ(x, y);
	}
	
	@Override
	public int getMaxX() {
		return source.getMaxZAtX(x);
	}
	
	@Override
	public int getMaxX(int y) {
		return source.getMaxZ(x, y);
	}
	
	@Override
	public int getMinY() {
		return source.getMinYAtX(x);
	}
	
	@Override
	public int getMinY(int x) {
		return source.getMinY(this.x, x);
	}
	
	@Override
	public int getMaxY() {
		return source.getMaxYAtX(x);
	}
	
	@Override
	public int getMaxY(int x) {
		return source.getMaxY(this.x, x);
	}
	
	@Override
	public String getXLabel() {
		return source.getYLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getZLabel();
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (x > source.getMaxX() || x < source.getMinX()) {
			throw new UnsupportedOperationException("X coordinate is out of bounds.");
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
		return source.getSubfolderPath() + "/" + source.getXLabel() + "=" + x;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
