/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2019 Jaume Ribas

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
package cellularautomata.grid1d;

public interface SymmetricLongGrid1D extends LongGrid1D, SymmetricGrid1D {
	
	long getValueAtNonSymmetricPosition(int x);
	
	default long[] getMinAndMaxValue() {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = getValueAtPosition(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getValueAtPosition(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return new long[]{ minValue, maxValue };
	}
	
	default long[] getMinAndMaxValueExcluding(long backgroundValue) {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = getValueAtPosition(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getValueAtPosition(x);
			if (value != backgroundValue) {
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;	
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	default SymmetricLongGrid1D absoluteGrid() {
		return new AbsSymmetricLongGrid1D(this);
	}
}
