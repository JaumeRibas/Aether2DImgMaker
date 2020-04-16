/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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

public class ShortSubGrid4D implements ShortGrid4D {
	
	private ShortGrid4D source;
	private int minW;
	private int maxW;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int minZ;
	private int maxZ;
	
	public ShortSubGrid4D(ShortGrid4D source, int minW, int maxW, int minX, 
			int maxX, int minY, int maxY, int minZ, int maxZ) {
		if (minW > maxW) {
			throw new IllegalArgumentException("Min w cannot be bigger than max w.");
		}
		if (minX > maxX) {
			throw new IllegalArgumentException("Min x cannot be bigger than max x.");
		}
		if (minY > maxY) {
			throw new IllegalArgumentException("Min y cannot be bigger than max y.");
		}
		if (minZ > maxZ) {
			throw new IllegalArgumentException("Min z cannot be bigger than max z.");
		}
		int sourceMinW = source.getMinW();
		int sourceMaxW = source.getMaxW();
		int sourceMinX = source.getMinX();
		int sourceMaxX = source.getMaxX();
		int sourceMinY = source.getMinY();
		int sourceMaxY = source.getMaxY();
		int sourceMinZ = source.getMinZ();
		int sourceMaxZ = source.getMaxZ();
		//TODO validate that passed bounds are within source bounds
		this.source = source;
		this.minW = Math.max(minW, sourceMinW);
		this.maxW = Math.min(maxW, sourceMaxW);
		this.minX = Math.max(minX, sourceMinX);
		this.maxX = Math.min(maxX, sourceMaxX);
		this.minY = Math.max(minY, sourceMinY);
		this.maxY = Math.min(maxY, sourceMaxY);
		this.minZ = Math.max(minZ, sourceMinZ);
		this.maxZ = Math.min(maxZ, sourceMaxZ);
	}

	@Override
	public short getValueAtPosition(int w, int x, int y, int z) throws Exception {
		return source.getValueAtPosition(w, x, y, z);
	}
	
	@Override
	public int getMinW() {
		return minW;
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
	
	//TODO add missing methods

}
