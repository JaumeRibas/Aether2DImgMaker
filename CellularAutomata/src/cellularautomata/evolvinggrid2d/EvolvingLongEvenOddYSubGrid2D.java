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
package cellularautomata.evolvinggrid2d;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid2d.LongEvenOddYSubGrid2D;

public class EvolvingLongEvenOddYSubGrid2D extends LongEvenOddYSubGrid2D<EvolvingLongGrid2D> implements EvolvingLongGrid2D {
	
	private boolean originalIsEven;
	
	public EvolvingLongEvenOddYSubGrid2D(EvolvingLongGrid2D source, boolean isEven) {
		super(source, isEven);
		originalIsEven = isEven;
		if (source.getStep() % 2 != 0) {
			this.isEven = !isEven;
		}
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		isEven = !isEven;
		int minY = source.getMinY(), maxY = source.getMaxY();
		if (minY == maxY && minY%2 == 0 != isEven) {
			throw new UnsupportedOperationException("Y range has no " + (isEven? "even" : "odd") + " coordinate.");
		}
		updateBounds();
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_" + (originalIsEven? "even" : "odd") + "_y";
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/" + (originalIsEven? "even" : "odd") + "_y";
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
