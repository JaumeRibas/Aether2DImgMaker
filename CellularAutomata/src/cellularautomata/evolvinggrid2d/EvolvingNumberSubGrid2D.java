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

import org.apache.commons.math3.FieldElement;

import cellularautomata.grid2d.NumberSubGrid2D;

public class EvolvingNumberSubGrid2D<T extends FieldElement<T> & Comparable<T>> extends NumberSubGrid2D<T, EvolvingNumberGrid2D<T>> implements EvolvingNumberGrid2D<T> {

	protected int absoluteMinX;
	protected int absoluteMaxX;
	protected int absoluteMinY;
	protected int absoluteMaxY;
	
	public EvolvingNumberSubGrid2D(EvolvingNumberGrid2D<T> source, int minX, int maxX, int minY, int maxY) {
		super(source, minX, maxX, minY, maxY);
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY)) {
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
		return source.getName() + "_minX=" + absoluteMinX + "_maxX=" + absoluteMaxX 
				+ "_minY=" + absoluteMinY + "_maxY=" + absoluteMaxY;
	}

	@Override
	public String getSubFolderPath() {
		return source.getSubFolderPath() + "/minX=" + absoluteMinX + "_maxX=" + absoluteMaxX 
				+ "_minY=" + absoluteMinY + "_maxY=" + absoluteMaxY;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
