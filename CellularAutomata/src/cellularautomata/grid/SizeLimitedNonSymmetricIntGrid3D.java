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
package cellularautomata.grid;

import java.io.Serializable;

public class SizeLimitedNonSymmetricIntGrid3D extends IntGrid3D implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2299868984790671684L;

	public static final int MIN_LENGTH = 2;

	public int maxX;
	public int minX;
	private NonSymmetricIntGrid3DSlice[] slices;

	public SizeLimitedNonSymmetricIntGrid3D(int minX, long maxBytes) {
		this.minX = minX;
		int blockLength = getMaxBlockLength(minX, maxBytes);
		if (blockLength < 2) {
			throw new OutOfMemoryError("Grid with minX=" + minX + " and mininmum length of " + MIN_LENGTH + " is bigger than size limit (" + maxBytes + " bytes).");
		}
		slices = new NonSymmetricIntGrid3DSlice[blockLength];
		maxX = minX + blockLength - 1;
		for (int x = minX, i = 0; x <= maxX; x++, i++) {
			slices[i] = new NonSymmetricIntGrid3DSlice(x);
		}
	}

	public void setValue(int x, int y, int z, int initialValue) {
		slices[x - minX].setValue(y, z, initialValue);			
	}

	@Override
	public int getValue(int x, int y, int z) {
		return slices[x - minX].getValue(y, z);
	}

	public void setSlice(int x, NonSymmetricIntGrid3DSlice slice) {
		slices[x - minX] = slice;
	}
	
	private int getMaxBlockLength(int minX, long maxBytes) {
		long blockSize = Constants.ARRAY_SIZE_OVERHEAD;
		int blockLength = 0;
		int x = minX;
		//round up to 8 multiple
		long reminder = blockSize % 8;
		long roundedBlockSize = reminder > 0 ? blockSize + 8 - reminder: blockSize;
		while (roundedBlockSize <= maxBytes) {
			blockSize += NonSymmetricIntGrid3DSlice.getSliceSize(x) + Integer.BYTES;
			reminder = blockSize % 8;
			roundedBlockSize = reminder > 0 ? blockSize + 8 - reminder: blockSize;
			x++;
			blockLength++;
		}
		return blockLength - 1;
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
		return 0;
	}
	
	@Override
	public int getMaxY() {
		return maxX;
	}
	
	@Override
	public int getMinZ() {
		return 0;
	}
	
	@Override
	public int getMaxZ() {
		return maxX;
	}
	
	@Override
	public int getMinXAtY(int y) {
		return Math.max(y, minX);
	}

	@Override
	public int getMinXAtZ(int z) {
		return Math.max(z, minX);
	}

	@Override
	public int getMinX(int y, int z) {
		return Math.max(Math.max(y, z), minX);
	}

	@Override
	public int getMaxXAtY(int y) {
		return maxX;
	}

	@Override
	public int getMaxXAtZ(int z) {
		return maxX;
	}

	@Override
	public int getMaxX(int y, int z) {
		return maxX;
	}

	@Override
	public int getMinYAtX(int x) {
		return 0;
	}

	@Override
	public int getMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getMinY(int x, int z) {
		return z;
	}

	@Override
	public int getMaxYAtX(int x) {
		return Math.min(maxX, x);
	}

	@Override
	public int getMaxYAtZ(int z) {
		return maxX;
	}

	@Override
	public int getMaxY(int x, int z) {
		return Math.min(maxX, x);
	}

	@Override
	public int getMinZAtX(int x) {
		return 0;
	}

	@Override
	public int getMinZAtY(int y) {
		return 0;
	}

	@Override
	public int getMinZ(int x, int y) {
		return 0;
	}

	@Override
	public int getMaxZAtX(int x) {
		return Math.min(maxX, x);
	}

	@Override
	public int getMaxZAtY(int y) {
		return Math.min(maxX, y);
	}

	@Override
	public int getMaxZ(int x, int y) {
		return Math.min(maxX, y);
	}
	
}