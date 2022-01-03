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
package cellularautomata.model;

public abstract class ActionableModelTransformerProcessor<G1 extends Model, G2 extends Model> 
	extends ActionableModel<G2> implements ModelProcessor<G1> {

	protected ActionableModel<G1> source;
	
	@Override
	public void addedToModel(ActionableModel<G1> grid) {
		if (this.source == null) {
			this.source = grid;
		} else {
			throw new UnsupportedOperationException("This processor does not support being added to more than one grid at the same time.");
		}
	}
	
	@Override
	public void removedFromModel(ActionableModel<G1> grid) {
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
	
	protected abstract G2 transformModelBlock(G1 gridBlock);

	@Override
	public void processModelBlock(G1 gridBlock) throws Exception {
		G2 transformedBlock = transformModelBlock(gridBlock);
		if (transformedBlock != null) {
			triggerProcessModelBlock(transformedBlock);
		}
	}
	
	@Override
	public void processModel() throws Exception {
		if (this.source == null) {
			throw new UnsupportedOperationException("This instance is not added to any grid.");
		} else {
			source.processModel();
		}
	}
	
}
