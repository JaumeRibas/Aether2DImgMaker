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

import cellularautomata.grid4d.AnisotropicBigIntGrid4DSlice;
import cellularautomata.grid4d.NumberGrid4D;
import cellularautomata.numbers.BigInt;

public class SizeLimitedAnisotropicBigIntGrid4DBlock implements NumberGrid4D<BigInt>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2133691026396797912L;

	public static final int MIN_LENGTH = 2;

	protected int maxW;
	protected int minW;
	private AnisotropicBigIntGrid4DSlice[] slices;

	public SizeLimitedAnisotropicBigIntGrid4DBlock(int minW, int side) {
		this.minW = minW;
		if (side < 2) {
			throw new IllegalArgumentException("Passed side is smaller than minimum (" + MIN_LENGTH + ")");
		}
		slices = new AnisotropicBigIntGrid4DSlice[side];
		maxW = minW + side - 1;
		for (int x = minW, i = 0; x <= maxW; x++, i++) {
			slices[i] = new AnisotropicBigIntGrid4DSlice(x);
		}
	}

	protected void setValueAtPosition(int w, int x, int y, int z, BigInt initialValue) {
		slices[w - minW].setAtPosition(x, y, z, initialValue);			
	}

	@Override
	public BigInt getFromPosition(int w, int x, int y, int z) {
		if (slices == null) {
			throw new UnsupportedOperationException("The grid block is no longer available.");
		}
		return slices[w - minW].getFromPosition(x, y, z);
	}

	protected void setSlice(int x, AnisotropicBigIntGrid4DSlice slice) {
		slices[x - minW] = slice;
	}
	
	protected AnisotropicBigIntGrid4DSlice getSlice(int w) {
		return slices[w - minW];
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

	@Override
	public int getMinYAtWX(int w, int x) {
		return 0;
	}

	@Override
	public int getMaxYAtWX(int w, int x) {
		return Math.min(getMaxY(), x);
	}

	@Override
	public int getMinZ(int w, int x, int y) {
		return 0;
	}

	@Override
	public int getMaxZ(int w, int x, int y) {
		return Math.min(getMaxZ(), y);
	}
	
	@Override
	public int getMinXAtW(int w) {
		return 0;
	}
	
	@Override
	public int getMaxXAtW(int w) {
		return Math.min(getMaxX(), w);
	}
	
	@Override
	public int getMinYAtW(int w) {
		return 0;
	}

	@Override
	public int getMaxYAtW(int w) {
		return Math.min(getMaxY(), w);
	}

	@Override
	public int getMinZAtW(int w) {
		return 0;
	}

	@Override
	public int getMaxZAtW(int w) {
		return Math.min(getMaxZ(), w);
	}

	protected void free() {
		slices = null;
	}
	
}