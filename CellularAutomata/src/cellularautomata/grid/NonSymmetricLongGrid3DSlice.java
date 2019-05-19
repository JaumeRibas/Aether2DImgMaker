/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid;//TODO move to grid3D package and make app to convert serialized backups

import java.io.Serializable;

import cellularautomata.grid.Constants;

public class NonSymmetricLongGrid3DSlice implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3523533483749359105L;

	private static final int PRIMITIVE_SIZE = Long.BYTES;

	private long[] data;
	
	public NonSymmetricLongGrid3DSlice(int x) {
		data = new long[getIndex(x, x) + 1];
	}

	public void setValueAtPosition(int y, int z, long value) {
		data[getIndex(y, z)] = value;	
	}
	
	public long getValueAtPosition(int y, int z) {
		return data[getIndex(y, z)];
	}
	
	public void addValueAtPosition(int y, int z, long value) {
		data[getIndex(y, z)] += value;
	}
	
	private static int getIndex(int y, int z) {
		if (y%2 == 0) {
			return (y + 1) * y/2 + z;
		} else {
			return y * (y/2 + 1) + z;
		}
	}
	
	public static long getSliceSize(int x) {
		long size = (getIndex(x, x) + 1) * PRIMITIVE_SIZE + Constants.ARRAY_SIZE_OVERHEAD;
		//round up to 8 multiple
		long reminder = size % 8;
		if (reminder > 0) {
			size += 8 - reminder;
		}
		return size;
	}
	
}
