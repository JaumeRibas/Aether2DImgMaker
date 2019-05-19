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
package cellularautomata.grid4D;

import cellularautomata.grid2D.SymmetricIntGrid2D;
import cellularautomata.grid3D.IntGrid3D;

public interface SymmetricIntGrid4D extends IntGrid4D, SymmetricGrid4D {

	/**
	 * Returns the value at a given position
	 * 
	 * @param w the position on the w-coordinate
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 */
	int getValueAtNonSymmetricPosition(int w, int x, int y, int z);

	default int[] getMinAndMaxValue() {
		int maxW = getNonSymmetricMaxW(), minW = getNonSymmetricMinW(), 
				maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		int maxValue = getValueAtNonSymmetricPosition(minW, minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int w = minW; w <= maxW; w++) {
						int value = getValueAtNonSymmetricPosition(w, x, y, z);
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
	
	default int[] getMinAndMaxValueExcluding(int backgroundValue) {
		int maxW = getNonSymmetricMaxW(), minW = getNonSymmetricMinW(), 
				maxX = getNonSymmetricMaxX(), minX = getNonSymmetricMinX(), 
				maxY = getNonSymmetricMaxY(), minY = getNonSymmetricMinY(),
				maxZ = getMaxZ(), minZ = getNonSymmetricMinZ();
		int maxValue = getValueAtNonSymmetricPosition(minW, minX, minY, minZ), minValue = maxValue;
		for (int z = minZ; z <= maxZ; z++) {
			for (int y = minY; y <= maxY; y++) {
				for (int x = minX; x <= maxX; x++) {
					for (int w = minW; w <= maxW; w++) {
						int value = getValueAtNonSymmetricPosition(w, x, y, z);
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
		return new int[]{ minValue, maxValue };
	}
	
	@Override
	default SymmetricIntGrid4D absoluteGrid() {
		return new AbsSymmetricIntGrid4D(this);
	}
	
	default SymmetricIntGrid2D crossSection(int y, int z) {
		return new SymmetricIntGrid4DCrossSection(this, y, z);
	}
	
	default IntGrid3D projected3DEdge(int backgroundValue) {
		return new SymmetricIntGrid4DProjected3DEdge(this, backgroundValue);
	}
}
