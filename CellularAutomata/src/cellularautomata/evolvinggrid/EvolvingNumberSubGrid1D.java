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
package cellularautomata.evolvinggrid;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid1d.NumberSubGrid1D;

public class EvolvingNumberSubGrid1D<T extends FieldElement<T> & Comparable<T>> 
	extends NumberSubGrid1D<T, EvolvingNumberGrid1D<T>> implements EvolvingNumberGrid1D<T> {

	protected int absoluteMinX;
	protected int absoluteMaxX;
	
	public EvolvingNumberSubGrid1D(EvolvingNumberGrid1D<T> source, int minX, int maxX) {
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
