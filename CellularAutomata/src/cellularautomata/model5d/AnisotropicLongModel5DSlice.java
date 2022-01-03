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
package cellularautomata.model5d;

import java.io.Serializable;

import cellularautomata.Constants;
import cellularautomata.Utils;

public class AnisotropicLongModel5DSlice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7583600562904238979L;

	private static final int PRIMITIVE_SIZE = Long.BYTES;

	private long[][][][] data;
	
	public AnisotropicLongModel5DSlice(int v) {
		data = Utils.buildAnisotropic4DLongArray(v + 1);
	}

	public void setAtPosition(int v, int x, int y, int z, long value) {
		data[v][x][y][z] = value;	
	}
	
	public long getFromPosition(int v, int x, int y, int z) {
		return data[v][x][y][z];
	}
	
	public void addToPosition(int v, int x, int y, int z, long value) {
		data[v][x][y][z] += value;
	}
	
	public static long getSliceSize(int v) {
		int vPlusOne = v + 1;
		long size = Utils.roundUpToEightMultiple(vPlusOne * Integer.BYTES + Constants.ARRAY_SIZE_OVERHEAD);
		for (int i = 1; i <= vPlusOne; i++) {
			size += Utils.roundUpToEightMultiple(i * Integer.BYTES + Constants.ARRAY_SIZE_OVERHEAD);
			for (int j = 1; j <= i; j++) {
				size += Utils.roundUpToEightMultiple(j * Integer.BYTES + Constants.ARRAY_SIZE_OVERHEAD);
				for (int k = 1; k <= j; k++) {
					size += Utils.roundUpToEightMultiple(k * PRIMITIVE_SIZE + Constants.ARRAY_SIZE_OVERHEAD);
				}
			}
		}
		return size;
	}
	
}
