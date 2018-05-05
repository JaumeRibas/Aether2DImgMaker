/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2018 Jaume Ribas

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
package cellularautomata.grid;

public abstract class SymmetricLongGrid1D extends LongGrid1D implements SymmetricGrid1D {
	
	public long[] getMinAndMaxValue() {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = getValue(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getValue(x);
			if (value > maxValue)
				maxValue = value;
			if (value < minValue)
				minValue = value;
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long[] getMinAndMaxValue(long backgroundValue) {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX();
		long maxValue = getValue(minX), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			long value = getValue(x);
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
	public SymmetricLongGrid1D absoluteGrid() {
		return new AbsSymmetricLongGrid1D(this);
	}
}
