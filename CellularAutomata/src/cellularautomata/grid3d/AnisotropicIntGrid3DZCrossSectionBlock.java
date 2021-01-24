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

import java.io.Serializable;

import cellularautomata.grid2d.IntGrid2D;

public class AnisotropicIntGrid3DZCrossSectionBlock implements Serializable, IntGrid2D {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7718067014725567373L;

	private int[] data;
	
	private int z;
	private int maxX;
	private int minX;
	
	public AnisotropicIntGrid3DZCrossSectionBlock(IntGrid3D block, int z) throws Exception {
		if (z < 0) {
			throw new IllegalArgumentException("Z cannot be smaller than 0.");
		}
		if (z < block.getMinZ() || z > block.getMaxZ()) {
			throw new IllegalArgumentException("Z is outside grid block bounds");
		}
		this.z = z;
		minX = Math.max(z, block.getMinX());
		maxX = block.getMaxX();
		data = new int[getArea(maxX + 1, maxX + 1)];
		int i = 0;
		for (int x = minX; x <= maxX; x++) {
			for (int y = z; y <= x; y++) {
				data[i] = block.getFromPosition(x, y, z);
				i++;
			}
		}
	}
	
	@Override
	public int getFromPosition(int x, int y) {
		return data[getIndex(x, y)];
	}
	
	private int getIndex(int x, int y) {
		int xSectionY = y - z;
		int index = getArea(x, x) + xSectionY;
		return index;
	}	
	
	private int getArea(int xSize, int ySize) {
		int blockXSize = xSize - minX;
		int blockYSize = ySize;
		int xSectionXSize = blockXSize;
		int xSectionYSize = blockYSize - z;
		int area;
		if (xSectionXSize%2 == 0) {
			area = xSectionXSize/2 * (xSectionYSize + xSectionYSize - xSectionXSize + 1);
		} else {
			area = xSectionXSize/2 * (xSectionYSize + xSectionYSize - xSectionXSize + 2) + xSectionYSize - xSectionXSize + 1;
		}
		return area;
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
		//Min y matches cross section z
		return z;
	}

	@Override
	public int getMaxY() {
		//Max y matches max x
		return maxX;
	}

	@Override
	public int getMinX(int y) {
		return Math.max(y, minX);
	}
	
	@Override
	public int getMaxX(int y) {
		return maxX;
	}
	
	@Override
	public int getMinY(int x) {
		//Min y matches cross section z
		return z;
	}
	
	@Override
	public int getMaxY(int x) {
		//Max y matches x
		return x;
	}

}
