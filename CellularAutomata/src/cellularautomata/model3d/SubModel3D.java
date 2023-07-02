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
package cellularautomata.model3d;

public class SubModel3D<Source_Type extends Model3D> implements Model3D {
	
	protected Source_Type source;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	protected int minZ;
	protected int maxZ;
	protected Integer absoluteMinX;
	protected Integer absoluteMaxX;
	protected Integer absoluteMinY;
	protected Integer absoluteMaxY;
	protected Integer absoluteMinZ;
	protected Integer absoluteMaxZ;
	
	public SubModel3D(Source_Type source, Integer minX, Integer maxX, Integer minY, 
			Integer maxY, Integer minZ, Integer maxZ) {
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
		if (!getActualBounds(minX, maxX, minY, maxY, minZ, maxZ))
			throw new IllegalArgumentException("The subsection is out of bounds.");
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
		this.absoluteMaxZ = maxZ;
		this.absoluteMinZ = minZ;
	}
	
	protected boolean getActualBounds(Integer minX, Integer maxX, Integer minY, 
			Integer maxY, Integer minZ, Integer maxZ) {
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		int sourceMinY = source.getMinY();
		int sourceMaxY = source.getMaxY();
		int sourceMinZ = source.getMinZ();
		int sourceMaxZ = source.getMaxZ();
		//TODO validate that passed bounds are within local bounds
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
	public int getMinX() { return minX; }

	@Override
	public int getMaxX() { return maxX; }

	@Override
	public int getMinXAtY(int y) { return Math.max(minX, source.getMinXAtY(y)); }

	@Override
	public int getMaxXAtY(int y) { return Math.min(maxX, source.getMaxXAtY(y)); }

	@Override
	public int getMinXAtZ(int z) { return Math.max(minX, source.getMinXAtZ(z)); }

	@Override
	public int getMaxXAtZ(int z) { return Math.min(maxX, source.getMaxXAtZ(z)); }

	@Override
	public int getMinX(int y, int z) { return Math.max(minX, source.getMinX(y, z)); }

	@Override
	public int getMaxX(int y, int z) { return Math.min(maxX, source.getMaxX(y, z)); }

	@Override
	public int getMinY() { return minY; }

	@Override
	public int getMaxY() { return maxY; }

	@Override
	public int getMinYAtX(int x) { return Math.max(minY, source.getMinYAtX(x)); }

	@Override
	public int getMaxYAtX(int x) { return Math.min(maxY, source.getMaxYAtX(x)); }

	@Override
	public int getMinYAtZ(int z) { return Math.max(minY, source.getMinYAtZ(z)); }

	@Override
	public int getMaxYAtZ(int z) { return Math.min(maxY, source.getMaxYAtZ(z)); }

	@Override
	public int getMinY(int x, int z) { return Math.max(minY, source.getMinY(x, z)); }

	@Override
	public int getMaxY(int x, int z) { return Math.min(maxY, source.getMaxY(x, z)); }

	@Override
	public int getMinZ() { return minZ; }

	@Override
	public int getMaxZ() { return maxZ; }

	@Override
	public int getMinZAtX(int x) { return Math.max(minZ, source.getMinZAtX(x)); }

	@Override
	public int getMaxZAtX(int x) { return Math.min(maxZ, source.getMaxZAtX(x)); }

	@Override
	public int getMinZAtY(int y) { return Math.max(minZ, source.getMinZAtY(y)); }

	@Override
	public int getMaxZAtY(int y) { return Math.min(maxZ, source.getMaxZAtY(y)); }

	@Override
	public int getMinZ(int x, int y) { return Math.max(minZ, source.getMinZ(x, y)); }

	@Override
	public int getMaxZ(int x, int y) { return Math.min(maxZ, source.getMaxZ(x, y)); }

	@Override
	public Boolean nextStep() throws Exception {
		Boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY, absoluteMinZ, absoluteMaxZ)) {
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
	public void backUp(String backupPath, String backupName) throws Exception {
		source.backUp(backupPath, backupName);
	}

}
