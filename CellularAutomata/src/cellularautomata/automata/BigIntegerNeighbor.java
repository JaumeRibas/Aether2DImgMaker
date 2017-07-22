/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
    Copyright (C) 2017 Jaume Ribas

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

import java.math.BigInteger;

public class BigIntegerNeighbor {
	
	private BigInteger value;
	private byte direction;
	
	public BigIntegerNeighbor(byte direction, BigInteger value) {
		this.value = value;
		this.direction = direction;
	}
	
	public BigInteger getValue() {
		return value;
	}
	
	public byte getDirection() {
		return direction;
	}
	
	@Override
	public String toString() {
		return "{ value: " + value + ", direction: " + direction + " }";
	}
}