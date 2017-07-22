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
package cellularautomata.grid;

public abstract class SymmetricLongGrid3D extends LongGrid3D implements SymmetricGrid3D {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	public abstract long getNonSymmetricValueAt(int x, int y, int z);

	public long[] getMinAndMaxValue() {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		long maxValue = getNonSymmetricValueAt(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					long value = getNonSymmetricValueAt(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long[] getMinAndMaxValue(long backgroundValue) {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		long maxValue = getNonSymmetricValueAt(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					long value = getNonSymmetricValueAt(x, y, z);
					if (value != backgroundValue) {
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	@Override
	public SymmetricLongGrid3D absoluteGrid() {
		return new AbsSymmetricLongGrid3D(this);
	}
}
