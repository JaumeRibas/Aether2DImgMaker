/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2022 Jaume Ribas

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

import cellularautomata.model.ActionableModelTransformerProcessor;
import cellularautomata.model2d.LongModel2D;
import cellularautomata.model2d.ObjectModel2D;

public class ActionableLongModel2DColorMapperProcessor 
	extends ActionableModelTransformerProcessor<LongModel2D, ObjectModel2D<Color>> {
	
	protected ColorMapper colorMapper;
	protected long minValue;
	protected long maxValue;
	
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
	
	public ActionableLongModel2DColorMapperProcessor(ColorMapper colorMapper, long minValue, long maxValue) {
		this.colorMapper = colorMapper;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	@Override
	protected ObjectModel2D<Color> transformModelBlock(LongModel2D gridBlock) {
		return colorMapper.getMappedModel(gridBlock, minValue, maxValue);
	}

}
