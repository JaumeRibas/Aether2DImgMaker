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
package cellularautomata.model4d;

import java.io.Serializable;

import cellularautomata.Constants;
import cellularautomata.Utils;

public class AnisotropicIntModel4DSlice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1166624610746587470L;

	private static final int PRIMITIVE_SIZE = Integer.BYTES;

	private int[][][] data;
	
	public AnisotropicIntModel4DSlice(int w) {
		data = Utils.buildAnisotropic3DIntArray(w + 1);
	}

	public void setAtPosition(int x, int y, int z, int value) {
		data[x][y][z] = value;	
	}
	
	public int getFromPosition(int x, int y, int z) {
		return data[x][y][z];
	}
	
	public void addToPosition(int x, int y, int z, int value) {
		data[x][y][z] += value;
	}
	
	public static long getSliceSize(int w) {
		int wPlusOne = w + 1;
		long size = Utils.roundUpToEightMultiple(wPlusOne * Integer.BYTES + Constants.ARRAY_SIZE_OVERHEAD);
		for (int i = 1; i <= wPlusOne; i++) {
			size += Utils.roundUpToEightMultiple(i * Integer.BYTES + Constants.ARRAY_SIZE_OVERHEAD);
			for (int j = 1; j <= i; j++) {
				size += Utils.roundUpToEightMultiple(j * PRIMITIVE_SIZE + Constants.ARRAY_SIZE_OVERHEAD);
			}
		}
		return size;
	}
	
}
