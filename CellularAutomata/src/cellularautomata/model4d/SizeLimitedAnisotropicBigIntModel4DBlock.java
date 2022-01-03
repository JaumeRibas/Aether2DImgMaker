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

import cellularautomata.numbers.BigInt;

public class SizeLimitedAnisotropicBigIntModel4DBlock implements NumericModel4D<BigInt>, AnisotropicModel4DA, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2133691026396797912L;

	public static final int MIN_LENGTH = 2;

	public int maxW;
	public int minW;
	private AnisotropicBigIntModel4DSlice[] slices;

	public SizeLimitedAnisotropicBigIntModel4DBlock(int minW, int side) {
		this.minW = minW;
		if (side < 2) {
			throw new IllegalArgumentException("Passed side is smaller than minimum (" + MIN_LENGTH + ")");
		}
		slices = new AnisotropicBigIntModel4DSlice[side];
		maxW = minW + side - 1;
		for (int w = minW, i = 0; w <= maxW; w++, i++) {
			slices[i] = new AnisotropicBigIntModel4DSlice(w);
		}
	}

	public void setValueAtPosition(int w, int x, int y, int z, BigInt initialValue) {
		slices[w - minW].setAtPosition(x, y, z, initialValue);			
	}

	@Override
	public BigInt getFromPosition(int w, int x, int y, int z) {
		if (slices == null) {
			throw new IllegalStateException("The grid block is no longer available.");
		}
		return slices[w - minW].getFromPosition(x, y, z);
	}

	public void setSlice(int w, AnisotropicBigIntModel4DSlice slice) {
		slices[w - minW] = slice;
	}
	
	protected AnisotropicBigIntModel4DSlice getSlice(int w) {
		return slices[w - minW];
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