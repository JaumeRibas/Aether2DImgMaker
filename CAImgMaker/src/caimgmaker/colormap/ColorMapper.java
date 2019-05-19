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
package caimgmaker.colormap;

import cellularautomata.grid2D.Grid2D;
import cellularautomata.grid2D.IntGrid2D;
import cellularautomata.grid2D.LongGrid2D;
import cellularautomata.grid2D.ShortGrid2D;
import cellularautomata.grid2D.SymmetricGrid2D;
import cellularautomata.grid2D.SymmetricIntGrid2D;
import cellularautomata.grid2D.SymmetricLongGrid2D;
import cellularautomata.grid2D.SymmetricShortGrid2D;

public abstract class ColorMapper {
		
	public abstract ColorMappedLongGrid2D getMappedLongGrid(LongGrid2D grid, long minValue, long maxValue);
	public abstract ColorMappedIntGrid2D getMappedIntGrid(IntGrid2D grid, int minValue, int maxValue);
	public abstract ColorMappedShortGrid2D getMappedShortGrid(ShortGrid2D grid, short minValue, short maxValue);
	public abstract ColorMappedSymmetricLongGrid2D getMappedLongGrid(SymmetricLongGrid2D grid, long minValue, long maxValue);
	public abstract ColorMappedSymmetricIntGrid2D getMappedIntGrid(SymmetricIntGrid2D grid, int minValue, int maxValue);
	public abstract ColorMappedSymmetricShortGrid2D getMappedShortGrid(SymmetricShortGrid2D grid, short minValue, short maxValue);
	
	public ColorGrid2D getMappedGrid(Grid2D grid, long minValue, long maxValue) {
		ColorGrid2D mappedGrid = null;
		if (grid instanceof LongGrid2D) {
			mappedGrid = getMappedLongGrid((LongGrid2D)grid, minValue, maxValue);
		} else if (grid instanceof SymmetricLongGrid2D) {
			mappedGrid = getMappedLongGrid((SymmetricLongGrid2D)grid, minValue, maxValue);
		} else if (grid instanceof IntGrid2D) {
			mappedGrid = getMappedIntGrid((IntGrid2D)grid, (int)minValue, (int)maxValue);
		} else if (grid instanceof SymmetricIntGrid2D) {
			mappedGrid = getMappedIntGrid((SymmetricIntGrid2D)grid, (int)minValue, (int)maxValue);
		} else if (grid instanceof ShortGrid2D) {
			mappedGrid = getMappedShortGrid((ShortGrid2D)grid, (short)minValue, (short)maxValue);
		} else if (grid instanceof SymmetricShortGrid2D) {
			mappedGrid = getMappedShortGrid((SymmetricShortGrid2D)grid, (short)minValue, (short)maxValue);
		} else {
			throw new IllegalArgumentException(
					"Missing else if branch for Grid2D subtype " 
							+ grid.getClass().getSimpleName() + ".");
		}
		return mappedGrid;
	}
	
	public SymmetricColorGrid2D getMappedSymmetricGrid(SymmetricGrid2D grid, long minValue, long maxValue) {
		SymmetricColorGrid2D mappedGrid = null;
		if (grid instanceof SymmetricLongGrid2D) {
			mappedGrid = getMappedLongGrid((SymmetricLongGrid2D)grid, minValue, maxValue);
		} else if (grid instanceof SymmetricIntGrid2D) {
			mappedGrid = getMappedIntGrid((SymmetricIntGrid2D)grid, (int)minValue, (int)maxValue);
		} else if (grid instanceof SymmetricShortGrid2D) {
			mappedGrid = getMappedShortGrid((SymmetricShortGrid2D)grid, (short)minValue, (short)maxValue);
		} else {
			throw new IllegalArgumentException(
					"Missing else if branch for SymmetricGrid2D subtype " 
							+ grid.getClass().getSimpleName() + ".");
		}
		return mappedGrid;
	}
	
//	/**
//	 * Return the color mapper's name in a format that can be used in file names
//	 * 
//	 * @return the name
//	 */
//	public abstract String getName();
//	
//	/**
//	 * Return the color mapper's name and configuration as a folder and sub-folder(s) path.
//	 * For example: "<Name>/<backgroundValue>/<backgroundColor>"
//	 * 
//	 * @return the path
//	 */
//	public abstract String getSubFolderPath();
}
