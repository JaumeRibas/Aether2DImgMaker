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
package cellularautomata;

import java.util.function.LongConsumer;

public class MinAndMaxLongConsumer implements LongConsumer {

	private long[] minAndMax;	
	
	@Override
	public void accept(long value) {
		if (minAndMax == null) {
			minAndMax = new long[]{value, value};
		} else {
			if (value < minAndMax[0]) minAndMax[0] = value;
			if (value > minAndMax[1]) minAndMax[1] = value;
		}
	}
	
	public long[] getMinAndMaxValue() {
		return minAndMax;
	}
	
}
