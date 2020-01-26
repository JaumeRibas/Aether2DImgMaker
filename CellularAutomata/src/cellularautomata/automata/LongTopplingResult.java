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
package cellularautomata.automata;

public class LongTopplingResult {
	
	private long remainingCenterValue;
	private long[] valuesSentToNeighbors;
	
	public LongTopplingResult(long remainingCenterValue, long[] valuesSentToNeighbors) {
		this.remainingCenterValue = remainingCenterValue;
		this.valuesSentToNeighbors = valuesSentToNeighbors;
	}
	
	public long getRemainingCenterValue() {
		return remainingCenterValue;
	}
	
	public long[] getValuesSentToNeighbors() {
		return valuesSentToNeighbors;
	}
	
}
