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
package cellularautomata.model2d;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SubModel2D<G extends Model2D> implements Model2D {
	
	protected G source;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	protected int absoluteMinX;
	protected int absoluteMaxX;
	protected int absoluteMinY;
	protected int absoluteMaxY;
	
	public SubModel2D(G source, int minX, int maxX, int minY, int maxY) {
		if (minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be bigger than max x.");
		}
		if (minY > maxY) {
			throw new IllegalArgumentException("Min y cannot be bigger than max y.");
		}
		this.source = source;
		if (!getActualBounds(minX, maxX, minY, maxY)) {
			throw new IllegalArgumentException("Subsection is out of bounds.");
		}
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
	}
	
	protected boolean getActualBounds(int minX, int maxX, int minY, int maxY) {
		boolean outOfBounds = false;
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		if (minX > sourceMaxX || maxX < sourceMinX) {
			outOfBounds = true;
		} else {
			this.minX = Math.max(minX, sourceMinX);
			this.maxX = Math.min(maxX, sourceMaxX);
			int minYWithinBounds = source.getMinY(this.minX);
			int maxYWithinBounds = source.getMaxY(this.minX);
			for (int x = this.minX + 1; x <= this.maxX; x++) {
				int localMinY = source.getMinY(x);
				if (localMinY < minYWithinBounds) {
					minYWithinBounds = localMinY;
				}
				int localMaxY = source.getMaxY(x);
				if (localMaxY > maxYWithinBounds) {
					maxYWithinBounds = localMaxY;
				}
			}
			if (minY > maxYWithinBounds || maxY < minYWithinBounds) {
				outOfBounds = true;
			} else {
				this.minY = Math.max(minY, minYWithinBounds);
				this.maxY = Math.min(maxY, maxYWithinBounds);
				int minXWithinBounds = source.getMinX(this.minY);
				int maxXWithinBounds = source.getMaxX(this.minY);
				for (int y = this.minY + 1; y <= this.maxY; y++) {
					int localMinX = source.getMinX(y);
					if (localMinX < minXWithinBounds) {
						minXWithinBounds = localMinX;
					}
					int localMaxX = source.getMaxX(y);
					if (localMaxX > maxXWithinBounds) {
						maxXWithinBounds = localMaxX;
					}
				}
				this.minX = Math.max(this.minX, minXWithinBounds);
				this.maxX = Math.min(this.maxX, maxXWithinBounds);
			}
		}
		return !outOfBounds;
	}

	@Override
	public int getMinX() {
		return minX;
	}
	
	@Override
	public int getMinX(int y) {
		return Math.max(minX, source.getMinX(y));
	}

	@Override
	public int getMaxX() {
		return maxX;
	}
	
	@Override
	public int getMaxX(int y) {
		return Math.min(maxX, source.getMaxX(y));
	}

	@Override
	public int getMinY() {
		return minY;
	}
	
	@Override
	public int getMinY(int x) {
		return Math.max(minY, source.getMinY(x));
	}

	@Override
	public int getMaxY() {
		return maxY;
	}
	
	@Override
	public int getMaxY(int x) {
		return Math.min(maxY, source.getMaxY(x));
	}

	@Override
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY)) {
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
		return source.getSubfolderPath() + "/" + absoluteMinX + "<=" + source.getXLabel() + "<=" + absoluteMaxX 
				+ "_" + absoluteMinY + "<=" + source.getYLabel() + "<=" + absoluteMaxY;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}
	
}
