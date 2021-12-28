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

import cellularautomata.grid3d.Grid3DXYDiagonalCrossSection;
import cellularautomata.model2d.Model2D;

public class Model3DXYDiagonalCrossSection<M extends Model3D> extends Grid3DXYDiagonalCrossSection<M> implements Model2D {

	public Model3DXYDiagonalCrossSection(M source, int yOffsetFromX) {
		super(source, yOffsetFromX);
	}
	
	@Override
	public String getXLabel() {
		return source.getZLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getXLabel();
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getBounds()) {
			throw new UnsupportedOperationException("Cross section is out of bounds.");
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
		String path = source.getSubfolderPath() + "/" + source.getYLabel() + "=" + source.getXLabel();
		if (yOffsetFromX < 0) {
			path += yOffsetFromX;
		} else if (yOffsetFromX > 0) {
			path += "+" + yOffsetFromX;
		}
		return path;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
