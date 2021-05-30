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
package cellularautomata.grid;

public abstract class ActionableGridTransformerProcessor<G1 extends Grid, G2 extends Grid> 
	extends ActionableGrid<G2> implements GridProcessor<G1> {

	protected ActionableGrid<G1> source;
	
	@Override
	public void addedToGrid(ActionableGrid<G1> grid) {
		if (this.source == null) {
			this.source = grid;
		} else {
			throw new UnsupportedOperationException("This processor does not support being added to more than one grid at the same time.");
		}
	}
	
	@Override
	public void removedFromGrid(ActionableGrid<G1> grid) {
		if (this.source == grid) {
			this.source = null;
		}
	}

	@Override
	public void beforeProcessing() throws Exception {
		triggerBeforeProcessing();		
	}

	@Override
	public void afterProcessing() throws Exception {
		triggerAfterProcessing();		
	}
	
	protected abstract G2 transformGridBlock(G1 gridBlock);

	@Override
	public void processGridBlock(G1 gridBlock) throws Exception {
		G2 transformedBlock = transformGridBlock(gridBlock);
		if (transformedBlock != null) {
			triggerProcessGridBlock(transformedBlock);
		}
	}
	
	@Override
	public void processGrid() throws Exception {
		if (this.source == null) {
			throw new UnsupportedOperationException("This instance is not added to any grid.");
		} else {
			source.processGrid();
		}
	}
	
}
