/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2020 Jaume Ribas

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
package cellularautomata.grid4d;

import cellularautomata.grid.ActionableGrid;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid3d.LongGrid3D;

public class ActionableLongGrid4DZCrossSectionProcessor extends ActionableGrid<GridProcessor<LongGrid3D>, LongGrid3D> implements GridProcessor<LongGrid4D> {

	private ActionableGrid<GridProcessor<LongGrid4D>, LongGrid4D> source;
	private int z;

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public ActionableLongGrid4DZCrossSectionProcessor(ActionableGrid<GridProcessor<LongGrid4D>, LongGrid4D> source, int z) {
		this.source = source;
		this.z = z;
	}

	@Override
	public void beforeProcessing() throws Exception {
		triggerBeforeProcessing();		
	}

	@Override
	public void afterProcessing() throws Exception {
		triggerAfterProcessing();		
	}

	@Override
	public void processGridBlock(LongGrid4D gridBlock) throws Exception {
		if (z >= gridBlock.getMinZ() && z <= gridBlock.getMaxZ()) {
			triggerProcessGridBlock(gridBlock.crossSectionAtZ(z));
		}
	}
	
	@Override
	public void processGrid() throws Exception {
		source.processGrid();
	}
	
}
