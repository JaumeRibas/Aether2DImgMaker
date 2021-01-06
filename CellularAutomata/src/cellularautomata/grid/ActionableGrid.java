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

import cellularautomata.grid.Grid;

//TODO try to make it more standard. Perhaps using the Iterable interface. 
/**
 * A grid that can be processed progressively in chunks (blocks).
 * The cuts between blocks are perpendicular to the x-axis.
 * The blocks are processed in order from the one at the lower x end until the one at the greater x end.
 * 
 * @author Jaume
 *
 * @param <P>
 * @param <G>
 */
public abstract class ActionableGrid<P extends GridProcessor<G>, G extends Grid> {
	
	protected Set<P> processors;
	
	public void addProcessor(P processor) {
		if (processors == null) {
			processors = new HashSet<P>();
		}
		processors.add(processor);
	}
	
	public boolean removeProcessor(P processor) {
		if (processors != null) {
			return processors.remove(processor);
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
			for (P processor : processors) {
				processor.beforeProcessing();
			}
		}
	}
	
	protected void triggerProcessGridBlock(G gridBlock) throws Exception {
		if (processors != null) {
			for (P processor : processors) {
				processor.processGridBlock(gridBlock);
			}
		}
	}
	
	protected void triggerAfterProcessing() throws Exception {
		if (processors != null) {
			for (P processor : processors) {
				processor.afterProcessing();
			}
		}
	}
}
