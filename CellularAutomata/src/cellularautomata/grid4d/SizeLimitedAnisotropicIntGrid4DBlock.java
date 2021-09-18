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

import cellularautomata.Constants;
import cellularautomata.Utils;

public class SizeLimitedAnisotropicIntGrid4DBlock implements IntGrid4D, AnisotropicGrid4DA, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -848573187277376184L;

	public static final int MIN_LENGTH = 2;

	public int maxW;
	public int minW;
	private AnisotropicIntGrid4DSlice[] slices;

	public SizeLimitedAnisotropicIntGrid4DBlock(int minW, long maxBytes) {
		this.minW = minW;
		int wLength = getMaxWLength(minW, maxBytes);
		if (wLength < 2) {
			throw new OutOfMemoryError("Grid block with min w of " + minW + " and mininmum length of " + MIN_LENGTH + " is bigger than size limit (" + maxBytes + " bytes).");
		}
		slices = new AnisotropicIntGrid4DSlice[wLength];
		maxW = minW + wLength - 1;
		for (int w = minW, i = 0; w <= maxW; w++, i++) {
			slices[i] = new AnisotropicIntGrid4DSlice(w);
		}
	}

	public void setValueAtPosition(int w, int x, int y, int z, int initialValue) {
		slices[w - minW].setAtPosition(x, y, z, initialValue);			
	}

	@Override
	public int getFromPosition(int w, int x, int y, int z) {
		if (slices == null) {
			throw new UnsupportedOperationException("The grid block is no longer available.");
		}
		return slices[w - minW].getFromPosition(x, y, z);
	}

	public void setSlice(int w, AnisotropicIntGrid4DSlice slice) {
		slices[w - minW] = slice;
	}
	
	public AnisotropicIntGrid4DSlice getSlice(int w) {
		return slices[w - minW];
	}
	
	private static int getMaxWLength(int minW, long maxBytes) {
		long size = Constants.ARRAY_SIZE_OVERHEAD;
		int wLength = 0;
		int w = minW;
		long roundedSize = Utils.roundUpToEightMultiple(size);
		while (roundedSize <= maxBytes) {
			size += AnisotropicIntGrid4DSlice.getSliceSize(w) + Integer.BYTES;
			roundedSize = Utils.roundUpToEightMultiple(size);
			w++;
			wLength++;
		}
		return wLength - 2; //subtract one more to leave room for the extra slice being computed
	}

	@Override
	public int getMinW() {
		return minW;
	}
	
	@Override
	public int getMinW(int x, int y, int z) {
		return Math.max(x, minW);
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
	public int getMinWAtX(int x) { 
		return Math.max(x, minW); 
	}

	@Override
	public int getMinWAtY(int y) { 
		return Math.max(y, minW); 
	}

	@Override
	public int getMinWAtXY(int x, int y) { 
		return Math.max(x, minW); 
	}

	@Override
	public int getMaxW() {
		return maxW;
	}

	@Override
	public int getMaxX() {
		return maxW;
	}

	@Override
	public int getMaxY() {
		return maxW;
	}

	@Override
	public int getMaxZ() {
		return maxW;
	}

	public void free() {
		slices = null;
	}
	
}