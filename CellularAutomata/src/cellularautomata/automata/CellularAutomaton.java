/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017 Jaume Ribas

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
package cellularautomata.automata;

import java.util.concurrent.ExecutionException;

public interface CellularAutomaton {	
		
	/**
	 * Computes the next step of the automaton and returns whether
	 * or not the state changed. 
	 *  
	 * @return true if the state changed or false otherwise
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public boolean nextStep() throws InterruptedException, ExecutionException;
	
	/**
	 * Returns the current step
	 * 
	 * @return the current step
	 */
	public long getCurrentStep();
}
