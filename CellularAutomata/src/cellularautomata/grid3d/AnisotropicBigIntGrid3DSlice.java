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
    aBigInt with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package cellularautomata.grid3d;

import java.io.Serializable;

import cellularautomata.numbers.BigInt;

public class AnisotropicBigIntGrid3DSlice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3071067782682941418L;

	private BigInt[] data;
	
	public AnisotropicBigIntGrid3DSlice(int x) {
		data = new BigInt[getIndex(x, x) + 1];
		for (int i = 0; i < data.length; i++) {
			data[i] = BigInt.ZERO;
		}
	}

	public void setAtPosition(int y, int z, BigInt value) {
		data[getIndex(y, z)] = value;	
	}
	
	public BigInt getFromPosition(int y, int z) {
		return data[getIndex(y, z)];
	}
	
	public void addToPosition(int y, int z, BigInt value) {
		int index = getIndex(y, z);
		data[index] = data[index].add(value);
	}
	
	private static int getIndex(int y, int z) {
		return (int)(Math.pow(y, 2) - y)/2 + y + z;
	}
	
}
