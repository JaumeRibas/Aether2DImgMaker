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
package cellularautomata.grid3d;

import java.io.Serializable;

import cellularautomata.grid.ActionableGrid;
import cellularautomata.grid.GridProcessor;
import cellularautomata.grid2d.IntGrid2D;

public class ActionableSymmetricIntGrid3DZCrossSectionCopy extends ActionableGrid<GridProcessor<IntGrid2D>, IntGrid2D> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2151281544490932379L;
	
	private NonsymmetricIntGrid3DZCrossSectionBlock[] blocks;
	private int z;

	public int getZ() {
		return z;
	}

	public ActionableSymmetricIntGrid3DZCrossSectionCopy(NonsymmetricIntGrid3DZCrossSectionBlock[] blocks, int z) {
		this.blocks = blocks;
		this.z = z;
	}

	@Override
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		for (NonsymmetricIntGrid3DZCrossSectionBlock block : blocks) {
			triggerProcessGridBlock(block);
		}
		triggerAfterProcessing();
	}

}
