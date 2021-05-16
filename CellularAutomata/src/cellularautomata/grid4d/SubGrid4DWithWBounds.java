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
package cellularautomata.grid4d;

public class SubGrid4DWithWBounds<G extends Grid4D> implements Grid4D {
	
	protected G source;
	private int minW;
	private int maxW;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	
	public SubGrid4DWithWBounds(G source, int minW, int maxW) {
		if (minW > maxW) {
			throw new IllegalArgumentException("Min w cannot be bigger than max w.");
		}
		int sourceMinW = source.getMinW();
		int sourceMaxW = source.getMaxW();
		if (minW > sourceMaxW || maxW < sourceMinW) {
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		}
		this.source = source;
		this.minW = Math.max(minW, sourceMinW);
		this.maxW = Math.min(maxW, sourceMaxW);
		minX = source.getMinXAtW(this.minW);
		maxX = source.getMaxXAtW(this.minW);
		minY = source.getMinYAtW(this.minW);
		maxY = source.getMaxYAtW(this.minW);
		minZ = source.getMinZAtW(this.minW);
		maxZ = source.getMaxZAtW(this.minW);
		for (int w = this.minW + 1; w <= this.maxW; w++) {
			int localMinX = source.getMinXAtW(w);
			if (localMinX < minX) {
				minX = localMinX;
			}
			int localMaxX = source.getMaxXAtW(w);
			if (localMaxX > maxX) {
				maxX = localMaxX;
			}
			int localMinY = source.getMinYAtW(w);
			if (localMinY < minY) {
				minY = localMinY;
			}
			int localMaxY = source.getMaxYAtW(w);
			if (localMaxY > maxY) {
				maxY = localMaxY;
			}
			int localMinZ = source.getMinZAtW(w);
			if (localMinZ < minZ) {
				minZ = localMinZ;
			}
			int localMaxZ = source.getMaxZAtW(w);
			if (localMaxZ > maxZ) {
				maxZ = localMaxZ;
			}
		}
	}

	@Override
	public int getMinW() {
		return minW;
	}

	@Override
	public int getMinW(int x, int y, int z) {
		return Math.max(minW, source.getMinW(x, y, z));
	}

	@Override
	public int getMaxW(int x, int y, int z) {
		return Math.min(maxW, source.getMaxW(x, y, z));
	}

	@Override
	public int getMaxW() {
		return maxW;
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

	@Override
	public int getMinWAtZ(int z) {
		return source.getMinWAtZ(z);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
		return source.getMinWAtXZ(x, z);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
		return source.getMinWAtYZ(y, z);
	}

	@Override
	public int getMaxWAtZ(int z) {
		return source.getMaxWAtZ(z);
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
		return source.getMaxWAtXZ(x, z);
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
		return source.getMaxWAtYZ(y, z);
	}

	@Override
	public int getMinXAtZ(int z) {
		return source.getMinXAtZ(z);
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
		return source.getMinXAtWZ(w, z);
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
		return source.getMinXAtYZ(y, z);
	}

	@Override
	public int getMinX(int w, int y, int z) {
		return source.getMinX(w, y, z);
	}

	@Override
	public int getMaxXAtZ(int z) {
		return source.getMaxXAtZ(z);
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
		return source.getMaxXAtWZ(w, z);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
		return source.getMaxXAtYZ(y, z);
	}

	@Override
	public int getMaxX(int w, int y, int z) {
		return source.getMaxX(w, y, z);
	}

	@Override
	public int getMinYAtZ(int z) {
		return source.getMinYAtZ(z);
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
		return source.getMaxYAtWZ(w, z);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
		return source.getMinYAtXZ(x, z);
	}

	@Override
	public int getMinY(int w, int x, int z) {
		return source.getMinY(w, x, z);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return source.getMaxYAtZ(z);
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
		return source.getMinYAtWZ(w, z);
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
		return source.getMaxYAtXZ(x, z);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
		return source.getMaxY(w, x, z);
	}

	@Override
	public int getMinYAtWX(int w, int x) {
		return source.getMinYAtWX(w, x);
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
		return source.getMaxYAtWX(w, x);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
		return source.getMinZ(w, x, y);
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
		return source.getMaxZ(w, x, y);
	}

	@Override
	public int getMinXAtW(int w) {
		return source.getMinXAtW(w);
	}
	
	@Override
	public int getMaxXAtW(int w) {
		return source.getMaxXAtW(w);
	}

	@Override
	public int getMinYAtW(int w) {
		return source.getMinYAtW(w);
	}
	
	@Override
	public int getMaxYAtW(int w) {
		return source.getMaxYAtW(w);
	}
	
	@Override
	public int getMinZAtW(int w) {
		return source.getMinZAtW(w);
	}
	
	@Override
	public int getMaxZAtW(int w) {
		return source.getMaxZAtW(w);
	}

}
