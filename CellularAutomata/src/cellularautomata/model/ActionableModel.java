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

import java.util.HashSet;
import java.util.Set;

import cellularautomata.model.Model;

//TODO try to make it more standard. Perhaps using the Iterable interface. 
/**
 * <p>A grid region that can be processed progressively in chunks (blocks).</p>
 * <p>The cuts between blocks are perpendicular to the first axis.
 * The first axis is the x-axis for dimension 1 to 3, the w-axis for dimension 4, the v-axis for dimension 5 and so on.</p>
 * <p>The blocks are processed in order from minimum to maximum coordinate.</p>
 * 
 * @author Jaume
 *
 * @param <G>
 */
public abstract class ActionableModel<G extends Model> {
	
	protected Set<ModelProcessor<G>> processors;
	
	public void addProcessor(ModelProcessor<G> processor) {
		if (processors == null) {
			processors = new HashSet<ModelProcessor<G>>();
		}
		processors.add(processor);
		processor.addedToModel(this);
	}
	
	public boolean removeProcessor(ModelProcessor<G> processor) {
		if (processors != null) {
			if (processors.remove(processor)) {
				processor.removedFromModel(this);
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
	public abstract void processModel() throws Exception;
	
	/*
	//Possible alternative approach to ensure triggerBeforeProcessing and triggerAfterProcessing are called?
	public void processModel() throws Exception {
		triggerBeforeProcessing();
		while (G block = getNextModelBlock() != null) {
			triggerProcessModelBlock(block);
		}
		triggerAfterProcessing();
	}
	
	protected abstract G getNextModelBlock() throws Exception;
	*/
	
	protected void triggerBeforeProcessing() throws Exception {
		if (processors != null) {
			for (ModelProcessor<G> processor : processors) {
				processor.beforeProcessing();
			}
		}
	}
	
	protected void triggerProcessModelBlock(G gridBlock) throws Exception {
		if (processors != null) {
			for (ModelProcessor<G> processor : processors) {
				processor.processModelBlock(gridBlock);
			}
		}
	}
	
	protected void triggerAfterProcessing() throws Exception {
		if (processors != null) {
			for (ModelProcessor<G> processor : processors) {
				processor.afterProcessing();
			}
		}
	}
}
