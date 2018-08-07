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

public abstract class LongGrid2D implements Grid2D, LongGrid, ProcessableGrid<LongGrid2DProcessor> {
	
	protected Set<LongGrid2DProcessor> processors;
	
	/**
	 * Returns the value at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the value at (x,y)
	 * @throws Exception 
	 */
	public abstract long getValueAtPosition(int x, int y) throws Exception;
	
	public long[] getMinAndMaxValue() throws Exception {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY();
		long maxValue = getValueAtPosition(minX, minY), minValue = maxValue;
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				long value = getValueAtPosition(x, y);
				if (value > maxValue)
					maxValue = value;
				if (value < minValue)
					minValue = value;
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	/**
	 * Get min and max values excluding a backgroundValue
	 * 
	 * @param backgroundValue
	 * @return
	 * @throws Exception 
	 */
	public long[] getMinAndMaxValue(long backgroundValue) throws Exception {
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY();
		long maxValue = getValueAtPosition(minX, minY), minValue = maxValue;
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				long value = getValueAtPosition(x, y);
				if (value != backgroundValue) {
					if (value > maxValue)
						maxValue = value;
					if (value < minValue)
						minValue = value;
				}
			}
		}
		return new long[]{ minValue, maxValue };
	}
	
	public long getTotalValue() throws Exception {
		long total = 0;
		int maxX = getMaxX(), minX = getMinX(), 
				maxY = getMaxY(), minY = getMinY();
		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				total += getValueAtPosition(x, y);
			}	
		}
		return total;
	}
	
	public long getMaxAbsoluteValue() throws Exception {
		long maxAbsoluteValue;
		long[] minAndMax = getMinAndMaxValue();
		if (minAndMax[0] < 0) {
			minAndMax[0] = Math.abs(minAndMax[0]);
			maxAbsoluteValue = Math.max(minAndMax[0], minAndMax[1]);
		} else {
			maxAbsoluteValue = minAndMax[1];
		}
		return maxAbsoluteValue;
	}
	
	public LongGrid2D absoluteGrid() {
		return new AbsLongGrid2D(this);
	}
	
	public LongGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return subGrid(minX, maxX, minY, maxY, false);
	}
	
	public LongGrid2D subGrid(int minX, int maxX, int minY, int maxY, boolean allowOverflow) {
		if (!allowOverflow && (minX < getMinX() || minX > getMaxX() 
				|| maxX < getMinX() || maxX > getMaxX()
				|| minY < getMinY() || minY > getMaxY() 
				|| maxY < getMinY() || maxY > getMaxY()))
			throw new IllegalArgumentException("Sub-grid bounds outside of grid bounds.");
		if (minX > maxX || minY > maxY)
			throw new IllegalArgumentException("Transposed bounds. Check argument order.");
		return new LongSubGrid2D(this, minX, maxX, minY, maxY);
	}
	
	@Override
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		triggerProcessGridBlock(this);
		triggerAfterProcessing();
	}
	
	@Override
	public void addProcessor(LongGrid2DProcessor processor) {
		if (processors == null) {
			processors = new HashSet<LongGrid2DProcessor>();
		}
		processors.add(processor);
	}
	
	@Override
	public boolean removeProcessor(LongGrid2DProcessor processor) {
		if (processors != null) {
			return processors.remove(processor);
		}
		return false;
	}
	
	protected void triggerBeforeProcessing() throws Exception {
		if (processors != null) {
			for (LongGrid2DProcessor processor : processors) {
				processor.beforeProcessing();
			}
		}
	}
	
	protected void triggerProcessGridBlock(LongGrid2D gridBlock) throws Exception {
		if (processors != null) {
			for (LongGrid2DProcessor processor : processors) {
				processor.processGridBlock(gridBlock);
			}
		}
	}
	
	protected void triggerAfterProcessing() throws Exception {
		if (processors != null) {
			for (LongGrid2DProcessor processor : processors) {
				processor.afterProcessing();
			}
		}
	}
}
