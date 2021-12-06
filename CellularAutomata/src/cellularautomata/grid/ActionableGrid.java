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

import java.util.HashSet;
import java.util.Set;

import cellularautomata.grid.GridRegion;

//TODO try to make it more standard. Perhaps using the Iterable interface. 
/**
 * A grid region that can be processed progressively in chunks (blocks).
 * 
 * @author Jaume
 *
 * @param <G>
 */
public abstract class ActionableGrid<G extends GridRegion> {
	
	protected Set<GridProcessor<G>> processors;
	
	public void addProcessor(GridProcessor<G> processor) {
		if (processors == null) {
			processors = new HashSet<GridProcessor<G>>();
		}
		processors.add(processor);
		processor.addedToGrid(this);
	}
	
	public boolean removeProcessor(GridProcessor<G> processor) {
		if (processors != null) {
			if (processors.remove(processor)) {
				processor.removedFromGrid(this);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Triggers the processing of the grid by the added processors.
	 * 
	 * @throws Exception 
	 */
	public abstract void processGrid() throws Exception;
	
	/*
	//Possible alternative approach to ensure triggerBeforeProcessing and triggerAfterProcessing are called?
	public void processGrid() throws Exception {
		triggerBeforeProcessing();
		while (G block = getNextGridBlock() != null) {
			triggerProcessGridBlock(block);
		}
		triggerAfterProcessing();
	}
	
	protected abstract G getNextGridBlock() throws Exception;
	*/
	
	protected void triggerBeforeProcessing() throws Exception {
		if (processors != null) {
			for (GridProcessor<G> processor : processors) {
				processor.beforeProcessing();
			}
		}
	}
	
	protected void triggerProcessGridBlock(G gridBlock) throws Exception {
		if (processors != null) {
			for (GridProcessor<G> processor : processors) {
				processor.processGridBlock(gridBlock);
			}
		}
	}
	
	protected void triggerAfterProcessing() throws Exception {
		if (processors != null) {
			for (GridProcessor<G> processor : processors) {
				processor.afterProcessing();
			}
		}
	}
}
