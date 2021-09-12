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
package cellularautomata.evolvinggrid3d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.evolvinggrid2d.EvolvingLongGrid2D;
import cellularautomata.grid3d.LongGrid3DXYDiagonalCrossSection;

public class EvolvingLongGrid3DXYDiagonalCrossSection extends LongGrid3DXYDiagonalCrossSection<EvolvingLongGrid3D> implements EvolvingLongGrid2D {

	public EvolvingLongGrid3DXYDiagonalCrossSection(EvolvingLongGrid3D source, int yOffsetFromX) {
		super(source, yOffsetFromX);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getBounds()) {
			throw new UnsupportedOperationException("Cross section outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		String name = source.getName() + "_y=x";
		if (yOffsetFromX < 0) {
			name += yOffsetFromX;
		} else if (yOffsetFromX > 0) {
			name += "+" + yOffsetFromX;
		}
		return name;
	}

	@Override
	public String getSubFolderPath() {
		String path = source.getSubFolderPath() + "/y=x";
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
