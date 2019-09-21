/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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
package cellularautomata.grid2d;

public class ArrayIntGrid2D implements IntGrid2D {

	private int[][] data;
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;
	private int[] minYAtX;
	
	public ArrayIntGrid2D(int[][] data, int minX, int[] minYAtX) {
		if (data.length == 0) {
			throw new IllegalArgumentException("Data array cannot be empty.");
		}
		if (minYAtX.length == 0) {
			throw new IllegalArgumentException("minYAtX must have at least one value.");
		}
		for (int i = 0; i < data.length; i++) {
			if (data[i].length == 0) {
				throw new IllegalArgumentException("Data array cannot have empty columns.");
			}
		}
		//TODO validate that there aren't any regions inside bounds but outside of the array
		this.data = data;
		this.minX = minX;
		this.maxX = minX + data.length - 1;
		this.minYAtX = minYAtX;
		//min and max Y
		minY = minYAtX[0];
		maxY = minYAtX[0];
		for (int i = 1; i < minYAtX.length; i++) {
			if (minYAtX[i] < minY) {
				minY = minYAtX[i]; 
			} else if (minYAtX[i] > maxY) {
				maxY = minYAtX[i]; 
			}
		}
	}
	
	private int getMinYAtIndex(int index) {
		int minYAtCurrentX;
		if (index > minYAtX.length) {
			minYAtCurrentX = minYAtX[minYAtX.length - 1];
		} else {
			minYAtCurrentX = minYAtX[index];
		}
		return minYAtCurrentX;
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
	
	public int getMinX(int y) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	public int getMaxX(int y) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	@Override
	public int getMinY(int x) {
		int i = x - minX;
		return getMinYAtIndex(i);
	}
	
	@Override
	public int getMaxY(int x) {
		int i = x - minX;
		return data[i].length + getMinYAtIndex(i) - 1;
	}

	@Override
	public int getValueAtPosition(int x, int y) throws Exception {
		int i = x - minX;
		return data[i][y - getMinYAtIndex(i)];
	}

}
