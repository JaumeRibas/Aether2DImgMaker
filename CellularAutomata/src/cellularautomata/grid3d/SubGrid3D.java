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
package cellularautomata.grid3d;

public class SubGrid3D<G extends Grid3D> implements Grid3D {
	
	protected G source;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	
	public SubGrid3D(G source, int minX, int maxX, int minY, 
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
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
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
	public int getMinX() {
		return minX;
	}

	@Override
	public int getMaxX() {
		return maxX;
	}

	@Override
	public int getMinY() {
		return minY;
	}

	@Override
	public int getMaxY() {
		return maxY;
	}
	
	@Override
	public int getMinZ() {
		return minZ;
	}

	@Override
	public int getMaxZ() {
		return maxZ;
	}
	
	//TODO add missing methods

}
