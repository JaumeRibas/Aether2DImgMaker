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
package cellularautomata.evolvinggrid5d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.evolvinggrid2d.EvolvingIntGrid2D;
import cellularautomata.grid5d.IntGrid5DXYZCrossSection;

public class EvolvingIntGrid5DXYZCrossSection extends IntGrid5DXYZCrossSection<EvolvingIntGrid5D> implements EvolvingIntGrid2D {

	public EvolvingIntGrid5DXYZCrossSection(EvolvingIntGrid5D source, int x, int y, int z) {
		super(source, x, y, z);
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (z > source.getMaxZ() || z < source.getMinZ()) {
			throw new IllegalArgumentException("Z coordinate outside of grid bounds.");
		} else if (y > source.getMaxYAtZ(z) || y < source.getMinYAtZ(z)) {
			throw new IllegalArgumentException("Y coordinate outside of grid bounds.");
		} else if (x > source.getMaxXAtYZ(y, z) || x < source.getMinXAtYZ(y, z)) {
			throw new IllegalArgumentException("X coordinate outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_x=" + x + "_y=" + y + "_z=" + z;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/x=" + x + "_y=" + y + "_z=" + z;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}