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
package cellularautomata.grid3D;

import cellularautomata.grid2D.ShortGrid2D;
import cellularautomata.grid2D.SymmetricShortGrid2D;

public interface SymmetricShortGrid3D extends ShortGrid3D, SymmetricGrid3D {

	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	short getValueAtNonSymmetricPosition(int x, int y, int z);

	default short[] getMinAndMaxValue() {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		short maxValue = getValueAtNonSymmetricPosition(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					short value = getValueAtNonSymmetricPosition(x, y, z);
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	default short[] getMinAndMaxValueExcluding(short backgroundValue) {
		int maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		short maxValue = getValueAtNonSymmetricPosition(minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					short value = getValueAtNonSymmetricPosition(x, y, z);
					if (value != backgroundValue) {
						if (value > maxValue)
							maxValue = value;
						if (value < minValue)
							minValue = value;
					}
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	@Override
	default SymmetricShortGrid3D absoluteGrid() {
		return new AbsSymmetricShortGrid3D(this);
	}
	
	default SymmetricShortGrid2D crossSectionAtZ(int z) {
		return new SymmetricShortGrid3DZCrossSection(this, z);
	}
	
	default ShortGrid2D projectedSurface(short backgroundValue) {
		return new SymmetricShortGrid3DProjectedSurface(this, backgroundValue);
	}
}
