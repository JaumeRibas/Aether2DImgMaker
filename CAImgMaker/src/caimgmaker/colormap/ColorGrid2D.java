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
package caimgmaker.colormap;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

import cellularautomata.grid.Grid2D;
import cellularautomata.grid.ProcessableGrid;

public abstract class ColorGrid2D implements Grid2D, ProcessableGrid<ColorGrid2DProcessor> {
	
	protected Set<ColorGrid2DProcessor> processors;
	
	/**
	 * Returns the color at a given position
	 * 
	 * @param x the position on the x-coordinate
	 * @param y the position on the y-coordinate
	 * @return the {@link Color} at (x,y)
	 * @throws Exception 
	 */
	public abstract Color getColorAtPosition(int x, int y) throws Exception;
	
	@Override
	public ColorGrid2D subGrid(int minX, int maxX, int minY, int maxY) {
		return new ColorSubGrid2D(this, minX, maxX, minY, maxY);
	}	
	
	@Override
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		triggerProcessGridBlock(this);
		triggerAfterProcessing();
	}
	
	@Override
	public void addProcessor(ColorGrid2DProcessor processor) {
		if (processors == null) {
			processors = new HashSet<ColorGrid2DProcessor>();
		}
		processors.add(processor);
	}
	
	@Override
	public boolean removeProcessor(ColorGrid2DProcessor processor) {
		if (processors != null) {
			return processors.remove(processor);
		}
		return false;
	}
	
	protected void triggerBeforeProcessing() throws Exception {
		if (processors != null) {
			for (ColorGrid2DProcessor processor : processors) {
				processor.beforeProcessing();
			}
		}
	}
	
	protected void triggerProcessGridBlock(ColorGrid2D gridBlock) throws Exception {
		if (processors != null) {
			for (ColorGrid2DProcessor processor : processors) {
				processor.processGridBlock(gridBlock);
			}
		}
	}
	
	protected void triggerAfterProcessing() throws Exception {
		if (processors != null) {
			for (ColorGrid2DProcessor processor : processors) {
				processor.afterProcessing();
			}
		}
	}
}
