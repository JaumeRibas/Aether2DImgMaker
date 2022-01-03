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
package cellularautomata.model4d;

import java.io.Serializable;

import cellularautomata.Utils;
import cellularautomata.numbers.BigInt;

public class AnisotropicBigIntModel4DSlice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2700268974732088977L;
	
	private BigInt[][][] data;
	
	public AnisotropicBigIntModel4DSlice(int w) {
		data = Utils.buildAnisotropic3DBigIntArray(w + 1);
	}

	public void setAtPosition(int x, int y, int z, BigInt value) {
		data[x][y][z] = value;	
	}
	
	public BigInt getFromPosition(int x, int y, int z) {
		return data[x][y][z];
	}
	
	public void addToPosition(int x, int y, int z, BigInt value) {
		data[x][y][z] = data[x][y][z].add(value);
	}
	
}
