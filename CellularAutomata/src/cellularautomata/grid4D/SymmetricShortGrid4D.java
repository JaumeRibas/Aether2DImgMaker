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
package cellularautomata.grid4D;

import cellularautomata.grid2D.SymmetricShortGrid2D;
import cellularautomata.grid3D.ShortGrid3D;

public interface SymmetricShortGrid4D extends ShortGrid4D, SymmetricGrid4D {

	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-coordinate
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	short getValueAtNonSymmetricPosition(int w, int x, int y, int z);

	default short[] getMinAndMaxValue() {
		int maxW = getNonSymmetricMaxW(), minW = getNonSymmetricMinW(), 
				maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		short maxValue = getValueAtNonSymmetricPosition(minW, minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int w = minW; w <= maxW; w++) {
						short value = getValueAtNonSymmetricPosition(w, x, y, z);
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
	
	default short[] getMinAndMaxValueExcluding(short backgroundValue) {
		int maxW = getNonSymmetricMaxW(), minW = getNonSymmetricMinW(), 
				maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		short maxValue = getValueAtNonSymmetricPosition(minW, minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int w = minW; w <= maxW; w++) {
						short value = getValueAtNonSymmetricPosition(w, x, y, z);
						if (value != backgroundValue) {
							if (value > maxValue)
								maxValue = value;
							if (value < minValue)
								minValue = value;
						}
					}
				}
			}
		}
		return new short[]{ minValue, maxValue };
	}
	
	@Override
	default SymmetricShortGrid4D absoluteGrid() {
		return new AbsSymmetricShortGrid4D(this);
	}
	
	default SymmetricShortGrid2D crossSection(int y, int z) {
		return new SymmetricShortGrid4DCrossSection(this, y, z);
	}
	
	default ShortGrid3D projected3DEdge(short backgroundValue) {
		return new SymmetricShortGrid4DProjected3DEdge(this, backgroundValue);
	}
}
