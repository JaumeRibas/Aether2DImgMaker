/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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
package caimgmaker.args;

import cellularautomata.numbers.BigInt;

public class InitialConfigParameterValue {	
	
	public InitialConfigType type;
	public BigInt singleSource;
	public BigInt min;
	public BigInt max;
	public int side;
	
	public InitialConfigParameterValue(BigInt singleSource) {
		this.type = InitialConfigType.SINGLE_SOURCE;
		this.singleSource = singleSource;
	}
	
	public InitialConfigParameterValue(int side, BigInt min, BigInt max) {
		this.type = InitialConfigType.RANDOM_REGION;
		if (min.compareTo(max) > 0) {
			BigInt swp = min;
			min = max;
			max = swp;
		}
		this.min = min;
		this.max = max;
		this.side = side;
	}
	
	@Override
	public String toString() {
		String result;
		switch (type) {
		case SINGLE_SOURCE:
			result = singleSource.toString();
			break;
		default:
			result = "random-region_" + side + "_" + min + "_" + max;
		}
		return result;
	}
	
	public static enum InitialConfigType {
		SINGLE_SOURCE, RANDOM_REGION
	}
}
