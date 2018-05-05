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

public abstract class SymmetricIntGrid3D extends IntGrid3D implements SymmetricGrid3D, ProcessableGrid<IntGrid3DProcessor> {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	public abstract int getNonSymmetricValue(int x, int y, int z) throws Exception;

	public int[] getMinAndMaxValue() throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxYAtX(minX), minY = getNonSymmetricMinYAtX(minX),
				maxZ = getNonSymmetricMaxZ(minX, minY), minZ = getNonSymmetricMinZ(minX, minY);
		int maxValue = getNonSymmetricValue(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinYAtX(x);
			maxY = getNonSymmetricMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getNonSymmetricMinZ(x, y);
				maxZ = getNonSymmetricMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getNonSymmetricValue(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	public int[] getMinAndMaxValue(int backgroundValue) throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxYAtX(minX), minY = getNonSymmetricMinYAtX(minX),
				maxZ = getNonSymmetricMaxZ(minX, minY), minZ = getNonSymmetricMinZ(minX, minY);
		int maxValue = getNonSymmetricValue(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinYAtX(x);
			maxY = getNonSymmetricMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getNonSymmetricMinZ(x, y);
				maxZ = getNonSymmetricMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getNonSymmetricValue(x, y, z);
					if (value != backgroundValue) {
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	public SymmetricIntGrid3D absoluteGrid() {
		return new AbsSymmetricIntGrid3D(this);
	}
	
	public SymmetricIntGrid2D crossSection(int z) {
		return new SymmetricIntGrid3DCrossSection(this, z);
	}
	
	public IntGrid2D projectedSurface(int backgroundValue) {
		return new SymmetricIntGrid3DProjectedSurface(this, backgroundValue);
	}
}
