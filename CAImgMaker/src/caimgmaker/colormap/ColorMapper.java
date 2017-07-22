/* CellularSharing2DImgMaker -- console app to generate images from the Cellular Sharing 2D cellular automaton
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

import cellularautomata.grid.BigIntegerGrid2D;
import cellularautomata.grid.Grid2D;
import cellularautomata.grid.LongGrid2D;
import cellularautomata.grid.SymmetricBigIntegerGrid2D;
import cellularautomata.grid.SymmetricGrid2D;
import cellularautomata.grid.SymmetricLongGrid2D;

public abstract class ColorMapper {
		
	protected abstract ColorGrid2D getMappedGrid(LongGrid2D grid);
	protected abstract ColorGrid2D getMappedGrid(BigIntegerGrid2D grid);
	protected abstract SymmetricColorGrid2D getMappedGrid(SymmetricLongGrid2D grid);
	protected abstract SymmetricColorGrid2D getMappedGrid(SymmetricBigIntegerGrid2D grid);
	
	public ColorGrid2D getMappedGrid(Grid2D grid) {
		ColorGrid2D mappedGrid = null;
		if (grid instanceof LongGrid2D) {
			mappedGrid = getMappedGrid((LongGrid2D)grid);
		} else if (grid instanceof BigIntegerGrid2D) {
			mappedGrid = getMappedGrid((BigIntegerGrid2D)grid);
		} else if (grid instanceof SymmetricLongGrid2D) {
			mappedGrid = getMappedGrid((SymmetricLongGrid2D)grid);
		} else if (grid instanceof SymmetricBigIntegerGrid2D) {
			mappedGrid = getMappedGrid((SymmetricBigIntegerGrid2D)grid);
		} else {
			throw new IllegalArgumentException(
					"Missing else if branch for Grid2D subtype " 
							+ grid.getClass().getSimpleName() + " (my fault).");
		}
		return mappedGrid;
	}
	
	public SymmetricColorGrid2D getMappedSymmetricGrid(SymmetricGrid2D grid) {
		SymmetricColorGrid2D mappedGrid = null;
		if (grid instanceof SymmetricLongGrid2D) {
			mappedGrid = getMappedGrid((SymmetricLongGrid2D)grid);
		} else if (grid instanceof SymmetricBigIntegerGrid2D) {
			mappedGrid = getMappedGrid((SymmetricBigIntegerGrid2D)grid);
		} else {
			throw new IllegalArgumentException(
					"Missing else if branch for SymmetricGrid2D subtype " 
							+ grid.getClass().getSimpleName() + " (my fault).");
		}
		return mappedGrid;
	}
}
