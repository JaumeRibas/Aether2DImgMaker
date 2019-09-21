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
package cellularautomata.grid3d;

import cellularautomata.grid2d.IntGrid2D;
import cellularautomata.grid2d.SymmetricIntGrid2D;

public interface SymmetricIntGrid3D extends IntGrid3D, SymmetricGrid3D {
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	int getValueAtNonSymmetricPosition(int x, int y, int z) throws Exception;

	default int[] getMinAndMaxValue() throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxYAtX(minX), minY = getNonSymmetricMinYAtX(minX),
				maxZ = getNonSymmetricMaxZ(minX, minY), minZ = getNonSymmetricMinZ(minX, minY);
		int maxValue = getValueAtNonSymmetricPosition(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinYAtX(x);
			maxY = getNonSymmetricMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getNonSymmetricMinZ(x, y);
				maxZ = getNonSymmetricMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getValueAtNonSymmetricPosition(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new int[]{ minValue, maxValue };
	}
	
	default int[] getMinAndMaxValueExcluding(int backgroundValue) throws Exception {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxYAtX(minX), minY = getNonSymmetricMinYAtX(minX),
				maxZ = getNonSymmetricMaxZ(minX, minY), minZ = getNonSymmetricMinZ(minX, minY);
		int maxValue = getValueAtNonSymmetricPosition(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getNonSymmetricMinYAtX(x);
			maxY = getNonSymmetricMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getNonSymmetricMinZ(x, y);
				maxZ = getNonSymmetricMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getValueAtNonSymmetricPosition(x, y, z);
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
	default SymmetricIntGrid3D absoluteGrid() {
		return new AbsSymmetricIntGrid3D(this);
	}
	
	default SymmetricIntGrid2D crossSectionAtZ(int z) {
		return new SymmetricIntGrid3DZCrossSection(this, z);
	}
	
	default IntGrid2D projectedSurface(int backgroundValue) {
		return new SymmetricIntGrid3DProjectedSurface(this, backgroundValue);
	}
}
