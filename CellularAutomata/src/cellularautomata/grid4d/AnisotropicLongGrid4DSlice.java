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

import java.io.Serializable;

import cellularautomata.automata.Utils;
import cellularautomata.grid.CAConstants;

public class AnisotropicLongGrid4DSlice implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4048142974007302517L;

	private static final int PRIMITIVE_SIZE = Long.BYTES;

	private long[][][] data;
	
	public AnisotropicLongGrid4DSlice(int w) {
		data = Utils.buildAnisotropic3DLongArray(w + 1);
	}

	public void setValueAtPosition(int x, int y, int z, long value) {
		data[x][y][z] = value;	
	}
	
	public long getValueAtPosition(int x, int y, int z) {
		return data[x][y][z];
	}
	
	public void addValueAtPosition(int x, int y, int z, long value) {
		data[x][y][z] += value;
	}
	
	public static long getSliceSize(int w) {
		int wPlusOne = w + 1;
		long size = Utils.roundUpToEightMultiple(wPlusOne * Integer.BYTES + CAConstants.ARRAY_SIZE_OVERHEAD);
		for (int i = 1; i <= wPlusOne; i++) {
			size += Utils.roundUpToEightMultiple(i * Integer.BYTES + CAConstants.ARRAY_SIZE_OVERHEAD);
			for (int j = 1; j <= i; j++) {
				size += Utils.roundUpToEightMultiple(j * PRIMITIVE_SIZE + CAConstants.ARRAY_SIZE_OVERHEAD);
			}
		}
		return size;
	}
	
}
