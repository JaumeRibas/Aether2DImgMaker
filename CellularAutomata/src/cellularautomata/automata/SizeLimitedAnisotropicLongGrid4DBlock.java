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
package cellularautomata.automata;

import java.io.Serializable;

import cellularautomata.grid.CAConstants;
import cellularautomata.grid4d.AnisotropicLongGrid4DSlice;
import cellularautomata.grid4d.LongGrid4D;

public class SizeLimitedAnisotropicLongGrid4DBlock implements LongGrid4D, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4770835861364964176L;

	public static final int MIN_LENGTH = 2;

	protected int maxW;
	protected int minW;
	private AnisotropicLongGrid4DSlice[] slices;

	public SizeLimitedAnisotropicLongGrid4DBlock(int minW, long maxBytes) {
		this.minW = minW;
		int wLength = getMaxWLength(minW, maxBytes);
		if (wLength < 2) {
			throw new OutOfMemoryError("Grid with min w of " + minW + " and mininmum length of " + MIN_LENGTH + " is bigger than size limit (" + maxBytes + " bytes).");
		}
		slices = new AnisotropicLongGrid4DSlice[wLength];
		maxW = minW + wLength - 1;
		for (int x = minW, i = 0; x <= maxW; x++, i++) {
			slices[i] = new AnisotropicLongGrid4DSlice(x);
		}
	}

	protected void setValueAtPosition(int w, int x, int y, int z, long initialValue) {
		slices[w - minW].setAtPosition(x, y, z, initialValue);			
	}

	@Override
	public long getFromPosition(int w, int x, int y, int z) {
		if (slices == null) {
			throw new UnsupportedOperationException("The grid block is no longer available.");
		}
		return slices[w - minW].getFromPosition(x, y, z);
	}

	protected void setSlice(int x, AnisotropicLongGrid4DSlice slice) {
		slices[x - minW] = slice;
	}
	
	protected AnisotropicLongGrid4DSlice getSlice(int w) {
		return slices[w - minW];
	}
	
	private int getMaxWLength(int minW, long maxBytes) {
		long size = CAConstants.ARRAY_SIZE_OVERHEAD;
		int wLength = 0;
		int w = minW;
		long roundedSize = Utils.roundUpToEightMultiple(size);
		while (roundedSize <= maxBytes) {
			size += AnisotropicLongGrid4DSlice.getSliceSize(w) + Long.BYTES;
			roundedSize = Utils.roundUpToEightMultiple(size);
			w++;
			wLength++;
		}
		return wLength - 1;
	}

	@Override
	public int getMinW() {
		return minW;
	}

	@Override
	public int getMaxW() {
		return maxW;
	}

	@Override
	public int getMinX() {
		return 0;
	}

	@Override
	public int getMaxX() {
		return maxW;
	}

	@Override
	public int getMinY() {
		return 0;
	}

	@Override
	public int getMaxY() {
		return maxW;
	}

	@Override
	public int getMinZ() {
		return 0;
	}

	@Override
	public int getMaxZ() {
		return maxW;
	}
	
	@Override
	public int getMinW(int x, int y, int z) {
		return Math.max(Math.max(Math.max(x, y), z), minW);
	}

	@Override
	public int getMaxW(int x, int y, int z) {
		return maxW;
	}

	public int getMinWAtZ(int z) {
		return Math.max(z, minW);
	}

	@Override
	public int getMinWAtXZ(int x, int z) {
		return Math.max(x, minW);
	}

	@Override
	public int getMinWAtYZ(int y, int z) {
		return Math.max(y, minW);
	}

	@Override
	public int getMaxWAtZ(int z) {
		return maxW;
	}

	@Override
	public int getMaxWAtXZ(int x, int z) {
		return maxW;
	}

	@Override
	public int getMaxWAtYZ(int y, int z) {
		return maxW;
	}

	@Override
	public int getMinXAtZ(int z) {
		return z;
	}

	@Override
	public int getMinXAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getMinXAtYZ(int y, int z) {
		return y;
	}

	@Override
	public int getMinX(int w, int y, int z) {
		return y;
	}

	@Override
	public int getMaxXAtZ(int z) {
		return maxW;
	}

	@Override
	public int getMaxXAtWZ(int w, int z) {
		return Math.min(maxW, w);
	}

	@Override
	public int getMaxXAtYZ(int y, int z) {
		return maxW;
	}

	@Override
	public int getMaxX(int w, int y, int z) {
		return Math.min(maxW, w);
	}

	@Override
	public int getMinYAtZ(int z) {
		return z;
	}

	@Override
	public int getMaxYAtWZ(int w, int z) {
		return Math.min(maxW, w);
	}

	@Override
	public int getMinYAtXZ(int x, int z) {
		return z;
	}

	@Override
	public int getMinY(int w, int x, int z) {
		return z;
	}

	@Override
	public int getMaxYAtZ(int z) {
		return maxW;
	}

	@Override
	public int getMinYAtWZ(int w, int z) {
		return z;
	}

	@Override
	public int getMaxYAtXZ(int x, int z) {
		return Math.min(maxW, x);
	}

	@Override
	public int getMaxY(int w, int x, int z) {
		return Math.min(maxW, x);
	}

	protected void free() {
		slices = null;
	}
	
}