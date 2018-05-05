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

import java.util.HashSet;
import java.util.Set;

public abstract class IntGrid3D implements Grid3D, IntGrid, ProcessableGrid<IntGrid3DProcessor> {
	
	protected Set<IntGrid3DProcessor> processors;
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @param z the position on the z-coordinate
	 * @return the value at (x,y,z)
	 * @throws Exception 
	 */
	public abstract int getValue(int x, int y, int z) throws Exception;

	public int[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxYAtX(minX), minY = getMinYAtX(minX),
				maxZ = getMaxZ(minX, minY), minZ = getMinZ(minX, minY);
		int maxValue = getValue(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getValue(x, y, z);
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
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxYAtX(minX), minY = getMinYAtX(minX),
				maxZ = getMaxZ(minX, minY), minZ = getMinZ(minX, minY);
		int maxValue = getValue(minX, minY, minZ), minValue = maxValue;
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					int value = getValue(x, y, z);
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
	
	public int getTotalValue() throws Exception {
		int total = 0;
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxYAtX(minX), minY = getMinYAtX(minX),
				maxZ = getMaxZ(minX, minY), minZ = getMinZ(minX, minY);
		for (int x = minX; x <= maxX; x++) {
			minY = getMinYAtX(x);
			maxY = getMaxYAtX(x);
			for (int y = minY; y <= maxY; y++) {
				minZ = getMinZ(x, y);
				maxZ = getMaxZ(x, y);
				for (int z = minZ; z <= maxZ; z++) {
					total += getValue(x, y, z);
				}
			}
		}
		return total;
	}
	
	public int getMaxAbsoluteValue() throws Exception {
		int maxAbsoluteValue;
		int[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = Math.abs(minAndMax[0]);
			maxAbsoluteValue = Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	public IntGrid3D absoluteGrid() {
		return new AbsIntGrid3D(this);
	}
	
	public IntGrid3D subGrid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
		if (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()
				|| minZ < getMinZ() || minZ > getMaxZ() 
				|| maxZ < getMinZ() || maxZ > getMaxZ())
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY || minZ > maxZ)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new IntSubGrid3D(this, minX, maxX, minY, maxY, minZ, maxZ);
	}
	
	public IntGrid2D crossSection(int z) {
		return new IntGrid3DCrossSection(this, z);
	}
	
	public IntGrid2D projectedSurfaceMaxX(int backgroundValue) {
		return new IntGrid3DProjectedSurfaceMaxX(this, backgroundValue);
	}
	
	@Override
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		triggerProcessGridBlock(this);
		triggerAfterProcessing();
	}
	
	@Override
	public void addProcessor(IntGrid3DProcessor processor) {
		if (processors == null) {
			processors = new HashSet<IntGrid3DProcessor>();
		}
		processors.add(processor);
	}
	
	@Override
	public boolean removeProcessor(IntGrid3DProcessor processor) {
		if (processors != null) {
			return processors.remove(processor);
		}
		return false;
	}
	
	protected void triggerBeforeProcessing() throws Exception {
		if (processors != null) {
			for (IntGrid3DProcessor processor : processors) {
				processor.beforeProcessing();
			}
		}
	}
	
	protected void triggerProcessGridBlock(IntGrid3D gridBlock) throws Exception {
		if (processors != null) {
			for (IntGrid3DProcessor processor : processors) {
				processor.processGridBlock(gridBlock);
			}
		}
	}
	
	protected void triggerAfterProcessing() throws Exception {
		if (processors != null) {
			for (IntGrid3DProcessor processor : processors) {
				processor.afterProcessing();
			}
		}
	}
}
