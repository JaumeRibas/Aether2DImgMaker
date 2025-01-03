/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

public class SubModel2D<Source_Type extends Model2D> implements Model2D {
	
	protected Source_Type source;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	protected Integer absoluteMinX;
	protected Integer absoluteMaxX;
	protected Integer absoluteMinY;
	protected Integer absoluteMaxY;
	
	public SubModel2D(Source_Type source, Integer minX, Integer maxX, Integer minY, Integer maxY) {
		if (minX != null && maxX != null && minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be greater than max x.");
		}
		if (minY != null && maxY != null && minY > maxY) {
			throw new IllegalArgumentException("Min y cannot be greater than max y.");
		}
		this.source = source;
		if (!getActualBounds(minX, maxX, minY, maxY)) {
			throw new IllegalArgumentException("The subsection is out of bounds.");
		}
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
	}
	
	protected boolean getActualBounds(Integer minX, Integer maxX, Integer minY, Integer maxY) {
		boolean outOfBounds = false;
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		if (minX != null && minX > sourceMaxX || maxX != null && maxX < sourceMinX) {
			outOfBounds = true;
		} else {
			this.minX = minX == null ? sourceMinX : Math.max(minX, sourceMinX);
			this.maxX = maxX == null ? sourceMaxX : Math.min(maxX, sourceMaxX);
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
			if (minY != null && minY > maxYWithinBounds || maxY != null && maxY < minYWithinBounds) {
				outOfBounds = true;
			} else {
				this.minY = minY == null ? minYWithinBounds : Math.max(minY, minYWithinBounds);
				this.maxY = maxY == null ? maxYWithinBounds : Math.min(maxY, maxYWithinBounds);
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
	public String getXLabel() {
		return source.getXLabel();
	}
	
	@Override
	public String getYLabel() {
		return source.getYLabel();
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
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY)) {
			throw new UnsupportedOperationException("The subsection is out of bounds.");
		}
		return changed;
	}
	
	@Override
	public Boolean isChanged() {
		return source.isChanged();
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
		StringBuilder strCoordinateBounds = new StringBuilder();
		boolean anyNotNull = false;
		strCoordinateBounds.append("/");
		Integer minCoord = absoluteMinX;
		Integer maxCoord = absoluteMaxX;
		if (minCoord != null || maxCoord != null) {
			anyNotNull = true;
			strCoordinateBounds.append(getXLabel());
			if (minCoord == null) {
				strCoordinateBounds.append("(-inf");
			} else {
				strCoordinateBounds.append("[").append(minCoord);
			}
			strCoordinateBounds.append(",");
			if (maxCoord == null) {
				strCoordinateBounds.append("inf)");
			} else {
				strCoordinateBounds.append(maxCoord).append("]");
			}
		}
		minCoord = absoluteMinY;
		maxCoord = absoluteMaxY;
		if (minCoord != null || maxCoord != null) {
			if (anyNotNull) strCoordinateBounds.append("_");
			anyNotNull = true;
			strCoordinateBounds.append(getYLabel());
			if (minCoord == null) {
				strCoordinateBounds.append("(-inf");
			} else {
				strCoordinateBounds.append("[").append(minCoord);
			}
			strCoordinateBounds.append(",");
			if (maxCoord == null) {
				strCoordinateBounds.append("inf)");
			} else {
				strCoordinateBounds.append(maxCoord).append("]");
			}
		}
		return anyNotNull ? source.getSubfolderPath() + strCoordinateBounds.toString() : source.getSubfolderPath();
	}

	@Override
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}
	
}
