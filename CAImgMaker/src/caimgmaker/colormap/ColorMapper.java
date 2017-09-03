/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
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
package caimgmaker.colormap;

import cellularautomata.grid.Grid2D;
import cellularautomata.grid.IntGrid2D;
import cellularautomata.grid.LongGrid2D;
import cellularautomata.grid.ShortGrid2D;
import cellularautomata.grid.SymmetricGrid2D;
import cellularautomata.grid.SymmetricIntGrid2D;
import cellularautomata.grid.SymmetricLongGrid2D;
import cellularautomata.grid.SymmetricShortGrid2D;

public abstract class ColorMapper {
		
	protected abstract ColorGrid2D getMappedGrid(LongGrid2D grid, long minValue, long maxValue);
	protected abstract ColorGrid2D getMappedGrid(IntGrid2D grid, int minValue, int maxValue);
	protected abstract ColorGrid2D getMappedGrid(ShortGrid2D grid, short minValue, short maxValue);
	protected abstract SymmetricColorGrid2D getMappedGrid(SymmetricLongGrid2D grid, long minValue, long maxValue);
	protected abstract SymmetricColorGrid2D getMappedGrid(SymmetricIntGrid2D grid, int minValue, int maxValue);
	protected abstract SymmetricColorGrid2D getMappedGrid(SymmetricShortGrid2D grid, short minValue, short maxValue);
	
	public ColorGrid2D getMappedGrid(Grid2D grid, long minValue, long maxValue) {
		ColorGrid2D mappedGrid = null;
		if (grid instanceof LongGrid2D) {
			mappedGrid = getMappedGrid((LongGrid2D)grid, minValue, maxValue);
		} else if (grid instanceof SymmetricLongGrid2D) {
			mappedGrid = getMappedGrid((SymmetricLongGrid2D)grid, minValue, maxValue);
		} else if (grid instanceof IntGrid2D) {
			mappedGrid = getMappedGrid((IntGrid2D)grid, (int)minValue, (int)maxValue);
		} else if (grid instanceof SymmetricIntGrid2D) {
			mappedGrid = getMappedGrid((SymmetricIntGrid2D)grid, (int)minValue, (int)maxValue);
		} else if (grid instanceof ShortGrid2D) {
			mappedGrid = getMappedGrid((ShortGrid2D)grid, (short)minValue, (short)maxValue);
		} else if (grid instanceof SymmetricShortGrid2D) {
			mappedGrid = getMappedGrid((SymmetricShortGrid2D)grid, (short)minValue, (short)maxValue);
		} else {
			throw new IllegalArgumentException(
					"Missing else if branch for Grid2D subtype " 
							+ grid.getClass().getSimpleName() + " (my fault).");
		}
		return mappedGrid;
	}
	
	public SymmetricColorGrid2D getMappedSymmetricGrid(SymmetricGrid2D grid, long minValue, long maxValue) {
		SymmetricColorGrid2D mappedGrid = null;
		if (grid instanceof SymmetricLongGrid2D) {
			mappedGrid = getMappedGrid((SymmetricLongGrid2D)grid, minValue, maxValue);
		} else if (grid instanceof SymmetricIntGrid2D) {
			mappedGrid = getMappedGrid((SymmetricIntGrid2D)grid, (int)minValue, (int)maxValue);
		} else if (grid instanceof SymmetricShortGrid2D) {
			mappedGrid = getMappedGrid((SymmetricShortGrid2D)grid, (short)minValue, (short)maxValue);
		} else {
			throw new IllegalArgumentException(
					"Missing else if branch for SymmetricGrid2D subtype " 
							+ grid.getClass().getSimpleName() + " (my fault).");
		}
		return mappedGrid;
	}
}
