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

import cellularautomata.grid3d.AnisotropicBigIntGrid3DSlice;
import cellularautomata.grid3d.NumberGrid3D;
import cellularautomata.numbers.BigInt;

public class SizeLimitedAnisotropicBigIntGrid3DBlock implements NumberGrid3D<BigInt>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7030854790186368908L;

	public static final int MIN_LENGTH = 2;

	protected int maxX;
	protected int minX;
	private AnisotropicBigIntGrid3DSlice[] slices;

	public SizeLimitedAnisotropicBigIntGrid3DBlock(int minX, long maxVolume) {
		this.minX = minX;
		int xLength = getMaxXLength(minX, maxVolume);
		if (xLength < 2) {
			throw new OutOfMemoryError("Grid block with min x of " + minX + " and mininmum length of " + MIN_LENGTH + " exceeds the max allowed volume (" + maxVolume + ").");
		}
		slices = new AnisotropicBigIntGrid3DSlice[xLength];
		maxX = minX + xLength - 1;
		for (int x = minX, i = 0; x <= maxX; x++, i++) {
			slices[i] = new AnisotropicBigIntGrid3DSlice(x);
		}
	}

	protected void setValueAtPosition(int x, int y, int z, BigInt initialValue) {
		slices[x - minX].setAtPosition(y, z, initialValue);			
	}

	@Override
	public BigInt getFromPosition(int x, int y, int z) throws UnsupportedOperationException {
		if (slices == null) {
			throw new UnsupportedOperationException("The grid block is no longer available.");
		}
		return slices[x - minX].getFromPosition(y, z);
	}

	protected void setSlice(int x, AnisotropicBigIntGrid3DSlice slice) {
		slices[x - minX] = slice;
	}
	
	protected AnisotropicBigIntGrid3DSlice getSlice(int x) {
		return slices[x - minX];
	}
	
	private static int getMaxXLength(int minX, long maxVolume) {
		long volume = 0;
		int xLength = 0;
		int x = minX;
		while (volume <= maxVolume) {
			volume += AnisotropicBigIntGrid3DSlice.getSliceSize(x);
			x++;
			xLength++;
		}
		return xLength - 1;
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

	protected void free() {
		slices = null;
	}
	
}