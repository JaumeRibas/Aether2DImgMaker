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
package cellularautomata.model1d;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SubModel1D<Source_Type extends Model1D> implements Model1D {
	
	protected Source_Type source;
	protected int minX;
	protected int maxX;
	protected Integer absoluteMinX;
	protected Integer absoluteMaxX;
	
	public SubModel1D(Source_Type source, Integer minX, Integer maxX) {
		if (minX != null && maxX != null && minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be greater than max x.");
		}
		this.source = source;
		if (!getActualBounds(minX, maxX)) {
			throw new IllegalArgumentException("Subsection is out of bounds.");
		}
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
	}
	
	protected boolean getActualBounds(Integer minX, Integer maxX) {
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		if (minX == null) {
			this.minX = sourceMinX;
		} else {
			int intMinX = minX;
			if (intMinX > sourceMaxX) 
				return false;
			this.minX = Math.max(intMinX, sourceMinX);
		}
		if (maxX == null) {
			this.maxX = sourceMaxX;
		} else {
			int intMaxX = maxX;
			if (intMaxX < sourceMinX) 
				return false;
			this.maxX = Math.min(intMaxX, sourceMaxX);
		}
		return true;
	}

	@Override
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX)) {
			throw new UnsupportedOperationException("Subsection is out of bounds.");
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
		if (absoluteMinX != null || absoluteMaxX != null) {
			StringBuilder strCoordinateBounds = new StringBuilder();
			strCoordinateBounds.append("/");
			strCoordinateBounds.append(getXLabel());
			if (absoluteMinX == null) {
				strCoordinateBounds.append("(-inf");
			} else {
				strCoordinateBounds.append("[").append(absoluteMinX);
			}
			strCoordinateBounds.append(",");
			if (absoluteMaxX == null) {
				strCoordinateBounds.append("inf)");
			} else {
				strCoordinateBounds.append(absoluteMaxX).append("]");
			}
			return source.getSubfolderPath() + strCoordinateBounds.toString();
		}
		return source.getSubfolderPath();		
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
