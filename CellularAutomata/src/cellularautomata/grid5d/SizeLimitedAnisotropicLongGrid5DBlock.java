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
package cellularautomata.grid5d;

import java.io.Serializable;

import cellularautomata.Constants;
import cellularautomata.Utils;

public class SizeLimitedAnisotropicLongGrid5DBlock implements LongGrid5D, AnisotropicGrid5DA, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4770835861364964176L;

	public static final int MIN_LENGTH = 2;

	public int maxV;
	public int minV;
	private AnisotropicLongGrid5DSlice[] slices;

	public SizeLimitedAnisotropicLongGrid5DBlock(int minV, long maxBytes) {
		this.minV = minV;
		int vLength = getMaxVLength(minV, maxBytes);
		if (vLength < 2) {
			throw new OutOfMemoryError("Grid block with min v of " + minV + " and mininmum length of " + MIN_LENGTH + " is bigger than size limit (" + maxBytes + " bytes).");
		}
		slices = new AnisotropicLongGrid5DSlice[vLength];
		maxV = minV + vLength - 1;
		for (int v = minV, i = 0; v <= maxV; v++, i++) {
			slices[i] = new AnisotropicLongGrid5DSlice(v);
		}
	}

	public void setValueAtPosition(int v, int w, int x, int y, int z, long initialValue) {
		slices[v - minV].setAtPosition(w, x, y, z, initialValue);			
	}

	@Override
	public long getFromPosition(int v, int w, int x, int y, int z) {
		if (slices == null) {
			throw new UnsupportedOperationException("The grid block is no longer available.");
		}
		return slices[v - minV].getFromPosition(w, x, y, z);
	}

	public void setSlice(int v, AnisotropicLongGrid5DSlice slice) {
		slices[v - minV] = slice;
	}
	
	public AnisotropicLongGrid5DSlice getSlice(int v) {
		return slices[v - minV];
	}
	
	private static int getMaxVLength(int minV, long maxBytes) {
		long size = Constants.ARRAY_SIZE_OVERHEAD;
		int vLength = 0;
		int v = minV;
		long roundedSize = Utils.roundUpToEightMultiple(size);
		while (roundedSize <= maxBytes) {
			size += AnisotropicLongGrid5DSlice.getSliceSize(v) + Integer.BYTES;
			roundedSize = Utils.roundUpToEightMultiple(size);
			v++;
			vLength++;
		}
		return vLength - 1;
	}

	@Override
	public int getMaxV() {
		return maxV;
	}

	@Override
	public int getMaxW() {
		return maxV;
	}

	@Override
	public int getMaxX() {
		return maxV;
	}

	@Override
	public int getMaxY() {
		return maxV;
	}

	@Override
	public int getMaxZ() {
		return maxV;
	}

	@Override
	public int getMinV() { return minV; }

	@Override
	public int getMinVAtW(int w) { return Math.max(w, minV); }

	@Override
	public int getMinVAtX(int x) { return Math.max(x, minV); }

	@Override
	public int getMinVAtY(int y) { return Math.max(y, minV); }

	@Override
	public int getMinVAtZ(int z) { return Math.max(z, minV); }

	@Override
	public int getMinVAtWX(int w, int x) { return Math.max(w, minV); }

	@Override
	public int getMinVAtWY(int w, int y) { return Math.max(w, minV); }

	@Override
	public int getMinVAtWZ(int w, int z) { return Math.max(w, minV); }

	@Override
	public int getMinVAtXY(int x, int y) { return Math.max(x, minV); }

	@Override
	public int getMinVAtXZ(int x, int z) { return Math.max(x, minV); }

	@Override
	public int getMinVAtYZ(int y, int z) { return Math.max(y, minV); }

	@Override
	public int getMinVAtWXY(int w, int x, int y) { return Math.max(w, minV); }

	@Override
	public int getMinVAtWXZ(int w, int x, int z) { return Math.max(w, minV); }

	@Override
	public int getMinVAtWYZ(int w, int y, int z) { return Math.max(w, minV); }

	@Override
	public int getMinVAtXYZ(int x, int y, int z) { return Math.max(x, minV); }

	@Override
	public int getMinV(int w, int x, int y, int z) { return Math.max(w, minV); }

	public void free() {
		slices = null;
	}
	
}