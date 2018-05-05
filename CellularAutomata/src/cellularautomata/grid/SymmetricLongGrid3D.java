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

public abstract class SymmetricLongGrid3D extends LongGrid3D implements SymmetricGrid3D {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	public abstract long getNonSymmetricValue(int x, int y, int z) throws Exception;

	public long[] getMinAndMaxValue() throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxYAtX(minX), minY = getNonSymmetricMinYAtX(minX),
				maxZ = getNonSymmetricMaxZ(minX, minY), minZ = getNonSymmetricMinZ(minX, minY);
		long maxValue = getNonSymmetricValue(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinYAtX(x);
			maxY = getNonSymmetricMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getNonSymmetricMinZ(x, y);
				maxZ = getNonSymmetricMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					long value = getNonSymmetricValue(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long[] getMinAndMaxValue(int backgroundValue) throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxYAtX(minX), minY = getNonSymmetricMinYAtX(minX),
				maxZ = getNonSymmetricMaxZ(minX, minY), minZ = getNonSymmetricMinZ(minX, minY);
		long maxValue = getNonSymmetricValue(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinYAtX(x);
			maxY = getNonSymmetricMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getNonSymmetricMinZ(x, y);
				maxZ = getNonSymmetricMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					long value = getNonSymmetricValue(x, y, z);
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
	
	public SymmetricLongGrid2D crossSection(int z) {
		return new SymmetricLongGrid3DCrossSection(this, z);
	}
	
	public LongGrid2D projectedSurface(long backgroundValue) {
		return new SymmetricLongGrid3DProjectedSurface(this, backgroundValue);
	}
}
