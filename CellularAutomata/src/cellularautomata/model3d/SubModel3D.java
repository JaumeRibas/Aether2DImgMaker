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
package cellularautomata.model3d;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SubModel3D<G extends Model3D> implements Model3D {
	
	protected G source;
	protected int minX;
	protected int maxX;
	protected int minY;
	protected int maxY;
	protected int minZ;
	protected int maxZ;
	protected int absoluteMinX;
	protected int absoluteMaxX;
	protected int absoluteMinY;
	protected int absoluteMaxY;
	protected int absoluteMinZ;
	protected int absoluteMaxZ;
	
	protected SubModel3D() {	}
	
	public SubModel3D(G source, int minX, int maxX, int minY, 
			int maxY, int minZ, int maxZ) {
		if (minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be bigger than max x.");
		}
		if (minY > maxY) {
			throw new IllegalArgumentException("Min y cannot be bigger than max y.");
		}
		if (minZ > maxZ) {
			throw new IllegalArgumentException("Min z cannot be bigger than max z.");
		}
		this.source = source;
		if (!getActualBounds(minX, maxX, minY, maxY, minZ, maxZ))
			throw new IllegalArgumentException("Subsection is out of bounds.");
		this.absoluteMaxX = maxX;
		this.absoluteMinX = minX;
		this.absoluteMaxY = maxY;
		this.absoluteMinY = minY;
		this.absoluteMaxZ = maxZ;
		this.absoluteMinZ = minZ;
	}
	
	protected boolean getActualBounds(int minX, int maxX, int minY, 
			int maxY, int minZ, int maxZ) {
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		int sourceMinY = source.getMinY();
		int sourceMaxY = source.getMaxY();
		int sourceMinZ = source.getMinZ();
		int sourceMaxZ = source.getMaxZ();
		if (minX > sourceMaxX || maxX < sourceMinX
				|| minY > sourceMaxY || maxY < sourceMinY
				|| minZ > sourceMaxZ || maxZ < sourceMinZ) {
			return false;
		} else {
			//TODO validate that passed bounds are within local bounds
			this.minX = Math.max(minX, sourceMinX);
			this.maxX = Math.min(maxX, sourceMaxX);
			this.minY = Math.max(minY, sourceMinY);
			this.maxY = Math.min(maxY, sourceMaxY);
			this.minZ = Math.max(minZ, sourceMinZ);
			this.maxZ = Math.min(maxZ, sourceMaxZ);
			return true;
		}
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
	public boolean nextStep() throws Exception {
		boolean changed = source.nextStep();
		if (!getActualBounds(absoluteMinX, absoluteMaxX, absoluteMinY, absoluteMaxY, absoluteMinZ, absoluteMaxZ)) {
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
				+ "_" + absoluteMinY + "<=" + source.getYLabel() + "<=" + absoluteMaxY 
				+ "_" + absoluteMinZ + "<=" + source.getZLabel() + "<=" + absoluteMaxZ;
	}

	@Override
	public void backUp(String backupPath, String backupName) throws FileNotFoundException, IOException {
		source.backUp(backupPath, backupName);
	}

}
