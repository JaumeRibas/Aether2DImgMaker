/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

public class SubModel4D<Source_Type extends Model4D> implements Model4D {

	protected Source_Type source;
	protected int minW;
	protected int maxW;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	protected int minZ;
	protected int maxZ;
	protected Integer absoluteMinW;
	protected Integer absoluteMaxW;
	protected Integer absoluteMinX;
	protected Integer absoluteMaxX;
	protected Integer absoluteMinY;
	protected Integer absoluteMaxY;
	protected Integer absoluteMinZ;
	protected Integer absoluteMaxZ;

	public SubModel4D(Source_Type source, Integer minW, Integer maxW, Integer minX, 
			Integer maxX, Integer minY, Integer maxY, Integer minZ, Integer maxZ) {
		if (minW != null && maxW != null && minW > maxW) {
			throw new IllegalArgumentException("Min w cannot be greater than max w.");
		}
		if (minX != null && maxX != null && minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be greater than max x.");
		}
		if (minY != null && maxY != null && minY > maxY) {
			throw new IllegalArgumentException("Min y cannot be greater than max y.");
		}
		if (minZ != null && maxZ != null && minZ > maxZ) {
			throw new IllegalArgumentException("Min z cannot be greater than max z.");
		}
		this.source = source;
		if (!getActualBounds(minW, maxW, minX, maxX, minY, maxY, minZ, maxZ))
			throw new IllegalArgumentException("The subsection is out of bounds.");
		this.absoluteMaxW = maxW;
		this.absoluteMinW = minW;
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
		this.absoluteMaxZ = maxZ;
		this.absoluteMinZ = minZ;
	}
	
	protected boolean getActualBounds(Integer minW, Integer maxW, Integer minX, Integer maxX, Integer minY, 
			Integer maxY, Integer minZ, Integer maxZ) {
		int sourceMinW = source.getMinW();
		int sourceMaxW = source.getMaxW();
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		int sourceMinY = source.getMinY();
		int sourceMaxY = source.getMaxY();
		int sourceMinZ = source.getMinZ();
		int sourceMaxZ = source.getMaxZ();
		//TODO validate that passed bounds are within local bounds
		if (minW == null) {
			this.minW = sourceMinW;
		} else {
			int intMinW = minW;
			if (intMinW > sourceMaxW) 
				return false;
			this.minW = Math.max(intMinW, sourceMinW);
		}
		if (maxW == null) {
			this.maxW = sourceMaxW;
		} else {
			int intMaxW = maxW;
			if (intMaxW < sourceMinW) 
				return false;
			this.maxW = Math.min(intMaxW, sourceMaxW);
		}
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
		if (minY == null) {
			this.minY = sourceMinY;
		} else {
			int intMinY = minY;
			if (intMinY > sourceMaxY) 
				return false;
			this.minY = Math.max(intMinY, sourceMinY);
		}
		if (maxY == null) {
			this.maxY = sourceMaxY;
		} else {
			int intMaxY = maxY;
			if (intMaxY < sourceMinY) 
				return false;
			this.maxY = Math.min(intMaxY, sourceMaxY);
		}
		if (minZ == null) {
			this.minZ = sourceMinZ;
		} else {
			int intMinZ = minZ;
			if (intMinZ > sourceMaxZ) 
				return false;
			this.minZ = Math.max(intMinZ, sourceMinZ);
		}
		if (maxZ == null) {
			this.maxZ = sourceMaxZ;
		} else {
			int intMaxZ = maxZ;
			if (intMaxZ < sourceMinZ) 
				return false;
			this.maxZ = Math.min(intMaxZ, sourceMaxZ);
		}
		return true;
	}
	
	@Override
	public String getWLabel() {
		return source.getWLabel();
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
	public String getZLabel() {
		return source.getZLabel();
	}

	@Override
	public int getMinW() { return minW; }

	@Override
	public int getMaxW() { return maxW; }

	@Override
	public int getMinWAtX(int x) { return Math.max(minW, source.getMinWAtX(x)); }

	@Override
	public int getMaxWAtX(int x) { return Math.min(maxW, source.getMaxWAtX(x)); }

	@Override
	public int getMinWAtY(int y) { return Math.max(minW, source.getMinWAtY(y)); }

	@Override
	public int getMaxWAtY(int y) { return Math.min(maxW, source.getMaxWAtY(y)); }

	@Override
	public int getMinWAtZ(int z) { return Math.max(minW, source.getMinWAtZ(z)); }

	@Override
	public int getMaxWAtZ(int z) { return Math.min(maxW, source.getMaxWAtZ(z)); }

	@Override
	public int getMinWAtXY(int x, int y) { return Math.max(minW, source.getMinWAtXY(x, y)); }

	@Override
	public int getMaxWAtXY(int x, int y) { return Math.min(maxW, source.getMaxWAtXY(x, y)); }

	@Override
	public int getMinWAtXZ(int x, int z) { return Math.max(minW, source.getMinWAtXZ(x, z)); }

	@Override
	public int getMaxWAtXZ(int x, int z) { return Math.min(maxW, source.getMaxWAtXZ(x, z)); }

	@Override
	public int getMinWAtYZ(int y, int z) { return Math.max(minW, source.getMinWAtYZ(y, z)); }

	@Override
	public int getMaxWAtYZ(int y, int z) { return Math.min(maxW, source.getMaxWAtYZ(y, z)); }

	@Override
	public int getMinW(int x, int y, int z) { return Math.max(minW, source.getMinW(x, y, z)); }

	@Override
	public int getMaxW(int x, int y, int z) { return Math.min(maxW, source.getMaxW(x, y, z)); }

	@Override
	public int getMinX() { return minX; }

	@Override
	public int getMaxX() { return maxX; }

	@Override
	public int getMinXAtW(int w) { return Math.max(minX, source.getMinXAtW(w)); }

	@Override
	public int getMaxXAtW(int w) { return Math.min(maxX, source.getMaxXAtW(w)); }

	@Override
	public int getMinXAtY(int y) { return Math.max(minX, source.getMinXAtY(y)); }

	@Override
	public int getMaxXAtY(int y) { return Math.min(maxX, source.getMaxXAtY(y)); }

	@Override
	public int getMinXAtZ(int z) { return Math.max(minX, source.getMinXAtZ(z)); }

	@Override
	public int getMaxXAtZ(int z) { return Math.min(maxX, source.getMaxXAtZ(z)); }

	@Override
	public int getMinXAtWY(int w, int y) { return Math.max(minX, source.getMinXAtWY(w, y)); }

	@Override
	public int getMaxXAtWY(int w, int y) { return Math.min(maxX, source.getMaxXAtWY(w, y)); }

	@Override
	public int getMinXAtWZ(int w, int z) { return Math.max(minX, source.getMinXAtWZ(w, z)); }

	@Override
	public int getMaxXAtWZ(int w, int z) { return Math.min(maxX, source.getMaxXAtWZ(w, z)); }

	@Override
	public int getMinXAtYZ(int y, int z) { return Math.max(minX, source.getMinXAtYZ(y, z)); }

	@Override
	public int getMaxXAtYZ(int y, int z) { return Math.min(maxX, source.getMaxXAtYZ(y, z)); }

	@Override
	public int getMinX(int w, int y, int z) { return Math.max(minX, source.getMinX(w, y, z)); }

	@Override
	public int getMaxX(int w, int y, int z) { return Math.min(maxX, source.getMaxX(w, y, z)); }

	@Override
	public int getMinY() { return minY; }

	@Override
	public int getMaxY() { return maxY; }

	@Override
	public int getMinYAtW(int w) { return Math.max(minY, source.getMinYAtW(w)); }

	@Override
	public int getMaxYAtW(int w) { return Math.min(maxY, source.getMaxYAtW(w)); }

	@Override
	public int getMinYAtX(int x) { return Math.max(minY, source.getMinYAtX(x)); }

	@Override
	public int getMaxYAtX(int x) { return Math.min(maxY, source.getMaxYAtX(x)); }

	@Override
	public int getMinYAtZ(int z) { return Math.max(minY, source.getMinYAtZ(z)); }

	@Override
	public int getMaxYAtZ(int z) { return Math.min(maxY, source.getMaxYAtZ(z)); }

	@Override
	public int getMinYAtWX(int w, int x) { return Math.max(minY, source.getMinYAtWX(w, x)); }

	@Override
	public int getMaxYAtWX(int w, int x) { return Math.min(maxY, source.getMaxYAtWX(w, x)); }

	@Override
	public int getMinYAtWZ(int w, int z) { return Math.max(minY, source.getMinYAtWZ(w, z)); }

	@Override
	public int getMaxYAtWZ(int w, int z) { return Math.min(maxY, source.getMaxYAtWZ(w, z)); }

	@Override
	public int getMinYAtXZ(int x, int z) { return Math.max(minY, source.getMinYAtXZ(x, z)); }

	@Override
	public int getMaxYAtXZ(int x, int z) { return Math.min(maxY, source.getMaxYAtXZ(x, z)); }

	@Override
	public int getMinY(int w, int x, int z) { return Math.max(minY, source.getMinY(w, x, z)); }

	@Override
	public int getMaxY(int w, int x, int z) { return Math.min(maxY, source.getMaxY(w, x, z)); }

	@Override
	public int getMinZ() { return minZ; }

	@Override
	public int getMaxZ() { return maxZ; }

	@Override
	public int getMinZAtW(int w) { return Math.max(minZ, source.getMinZAtW(w)); }

	@Override
	public int getMaxZAtW(int w) { return Math.min(maxZ, source.getMaxZAtW(w)); }

	@Override
	public int getMinZAtX(int x) { return Math.max(minZ, source.getMinZAtX(x)); }

	@Override
	public int getMaxZAtX(int x) { return Math.min(maxZ, source.getMaxZAtX(x)); }

	@Override
	public int getMinZAtY(int y) { return Math.max(minZ, source.getMinZAtY(y)); }

	@Override
	public int getMaxZAtY(int y) { return Math.min(maxZ, source.getMaxZAtY(y)); }

	@Override
	public int getMinZAtWX(int w, int x) { return Math.max(minZ, source.getMinZAtWX(w, x)); }

	@Override
	public int getMaxZAtWX(int w, int x) { return Math.min(maxZ, source.getMaxZAtWX(w, x)); }

	@Override
	public int getMinZAtWY(int w, int y) { return Math.max(minZ, source.getMinZAtWY(w, y)); }

	@Override
	public int getMaxZAtWY(int w, int y) { return Math.min(maxZ, source.getMaxZAtWY(w, y)); }

	@Override
	public int getMinZAtXY(int x, int y) { return Math.max(minZ, source.getMinZAtXY(x, y)); }

	@Override
	public int getMaxZAtXY(int x, int y) { return Math.min(maxZ, source.getMaxZAtXY(x, y)); }

	@Override
	public int getMinZ(int w, int x, int y) { return Math.max(minZ, source.getMinZ(w, x, y)); }

	@Override
	public int getMaxZ(int w, int x, int y) { return Math.min(maxZ, source.getMaxZ(w, x, y)); }

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinW, absoluteMaxW, absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY, absoluteMinZ, absoluteMaxZ)) {
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
		Integer minCoord = absoluteMinW;
		Integer maxCoord = absoluteMaxW;
		if (minCoord != null || maxCoord != null) {
			anyNotNull = true;
			strCoordinateBounds.append(getWLabel());
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
		minCoord = absoluteMinX;
		maxCoord = absoluteMaxX;
		if (minCoord != null || maxCoord != null) {
			if (anyNotNull) strCoordinateBounds.append("_");
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
		minCoord = absoluteMinZ;
		maxCoord = absoluteMaxZ;
		if (minCoord != null || maxCoord != null) {
			if (anyNotNull) strCoordinateBounds.append("_");
			anyNotNull = true;
			strCoordinateBounds.append(getZLabel());
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
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
