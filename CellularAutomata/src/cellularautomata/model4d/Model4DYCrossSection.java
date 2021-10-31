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
package cellularautomata.model4d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid4d.Grid4DYCrossSection;
import cellularautomata.model3d.Model3D;

public class Model4DYCrossSection<M extends Model4D> extends Grid4DYCrossSection<M> implements Model3D {

	public Model4DYCrossSection(M source, int y) {
		super(source, y);
	}
	
	@Override
	public String getXLabel() {
		return source.getWLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getXLabel();
	}
	
	@Override
	public String getZLabel() {
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