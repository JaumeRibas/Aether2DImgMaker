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
package cellularautomata.model3d;

import java.io.Serializable;

import cellularautomata.Constants;
import cellularautomata.Utils;

public class SizeLimitedAnisotropicLongModel3DBlock implements LongModel3D, AnisotropicModel3DA, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6869953413944757177L;

	public static final int MIN_LENGTH = 2;

	public int maxX;
	public int minX;
	private AnisotropicLongModel3DSlice[] slices;

	public SizeLimitedAnisotropicLongModel3DBlock(int minX, long maxBytes) {
		this.minX = minX;
		int xLength = getMaxXLength(minX, maxBytes);
		if (xLength < 2) {
			throw new OutOfMemoryError("Grid block with min x of " + minX + " and mininmum length of " + MIN_LENGTH + " is bigger than size limit (" + maxBytes + " bytes).");
		}
		slices = new AnisotropicLongModel3DSlice[xLength];
		maxX = minX + xLength - 1;
		for (int x = minX, i = 0; x <= maxX; x++, i++) {
			slices[i] = new AnisotropicLongModel3DSlice(x);
		}
	}

	public void setValueAtPosition(int x, int y, int z, long initialValue) {
		slices[x - minX].setAtPosition(y, z, initialValue);			
	}

	@Override
	public long getFromPosition(int x, int y, int z) throws UnsupportedOperationException {
		if (slices == null) {
			throw new IllegalStateException("The grid block is no longer available.");
		}
		return slices[x - minX].getFromPosition(y, z);
	}

	public void setSlice(int x, AnisotropicLongModel3DSlice slice) {
		slices[x - minX] = slice;
	}
	
	public AnisotropicLongModel3DSlice getSlice(int x) {
		return slices[x - minX];
	}
	
	private static int getMaxXLength(int minX, long maxBytes) {
		long size = Constants.ARRAY_SIZE_OVERHEAD;
		int xLength = 0;
		int x = minX;
		long roundedSize = Utils.roundUpToEightMultiple(size);
		while (roundedSize <= maxBytes) {
			size += AnisotropicLongModel3DSlice.getSliceSize(x) + Integer.BYTES;
			roundedSize = Utils.roundUpToEightMultiple(size);
			x++;
			xLength++;
		}
		return xLength - 2; //subtract one more to leave room for the extra slice being computed
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
	public int getMaxY() {
		return maxX;
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
		return Math.max(y, minX);
	}

	public void free() {
		slices = null;
	}
	
}