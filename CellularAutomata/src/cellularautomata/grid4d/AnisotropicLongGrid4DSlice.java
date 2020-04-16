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

import java.io.Serializable;

import cellularautomata.grid.Constants;

public class AnisotropicLongGrid4DSlice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 594103704894753794L;

	private static final int PRIMITIVE_SIZE = Long.BYTES;

	private long[] data;
	
	public AnisotropicLongGrid4DSlice(int w) {
		data = new long[getIndex(w, w, w) + 1];
	}

	public void setValueAtPosition(int x, int y, int z, long value) {
		data[getIndex(x, y, z)] = value;	
	}
	
	public long getValueAtPosition(int x, int y, int z) {
		return data[getIndex(x, y, z)];
	}
	
	public void addValueAtPosition(int x, int y, int z, long value) {
		data[getIndex(x, y, z)] += value;
	}
	
	private static int getIndex(int x, int y, int z) {
		return (int) (getVolume(3, x) + getVolume(2, y) + z);
	}
	
	public static long getVolume(int dimension, int side) {
		if (dimension == 1) {
			return side;
		} else {
			int volume = 0;
			dimension--;
			for (int i = 1; i <= side; i++) {
				volume += getVolume(dimension, i);
			}
			return volume;
		}
	}
	
	public static long getSliceSize(int w) {
		long size = (getIndex(w, w, w) + 1) * PRIMITIVE_SIZE + Constants.ARRAY_SIZE_OVERHEAD;
		//round up to 8 multiple
		long reminder = size % 8;
		if (reminder > 0) {
			size += 8 - reminder;
		}
		return size;
	}
	
}
