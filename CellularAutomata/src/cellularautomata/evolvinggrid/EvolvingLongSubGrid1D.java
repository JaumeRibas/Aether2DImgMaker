/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import cellularautomata.grid1d.LongSubGrid1D;

public class EvolvingLongSubGrid1D<G extends EvolvingLongGrid1D> extends LongSubGrid1D<G> implements EvolvingLongGrid1D {
	
	protected int absoluteMinX;
	protected int absoluteMaxX;
	
	public EvolvingLongSubGrid1D(G source, int minX, int maxX) {
		super(source, minX, maxX);
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX)) {
			throw new UnsupportedOperationException("Sub-grid bounds outside of grid bounds.");
		}
		return changed;
	}

	@Override
	public long getStep() {
		return source.getStep();
	}

	@Override
	public String getName() {
		return source.getName() + "_minX=" + absoluteMinX + "_maxX=" + absoluteMaxX;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/minX=" + absoluteMinX + "_maxX=" + absoluteMaxX;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}
	
}
