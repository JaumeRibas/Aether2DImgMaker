/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2021 Jaume Ribas

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

import cellularautomata.grid.ActionableGrid;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.LongGrid2D;
import cellularautomata.grid2d.ObjectGrid2D;

public class ActionableLongGrid2DColorMapperProcessor extends ActionableGrid<GridProcessor<ObjectGrid2D<Color>>, ObjectGrid2D<Color>> 
	implements GridProcessor<LongGrid2D> {
	
	private ColorMapper colorMapper;
	private ActionableGrid<GridProcessor<LongGrid2D>, LongGrid2D> source;
	private long minValue;
	private long maxValue;
	
	public void setMinValue(long value) {
		minValue = value;
	}
	
	public void setMaxValue(long value) {
		maxValue = value;
	}

	public long getMaxValue() {
		return maxValue;
	}

	public long getMinValue() {
		return minValue;
	}
	
	public ActionableLongGrid2DColorMapperProcessor(ActionableGrid<GridProcessor<LongGrid2D>, LongGrid2D> source, 
			ColorMapper colorMapper, long minValue, long maxValue) {
		this.source = source;
		this.colorMapper = colorMapper;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	public void beforeProcessing() throws Exception {
		triggerBeforeProcessing();
	}

	@Override
	public void processGridBlock(LongGrid2D gridBlock) throws Exception {
		triggerProcessGridBlock(colorMapper.getMappedGrid(gridBlock, minValue, maxValue));
	}

	@Override
	public void afterProcessing() throws Exception {
		triggerAfterProcessing();
	}

	@Override
	public void processGrid() throws Exception {
		source.processGrid();
	}

}
