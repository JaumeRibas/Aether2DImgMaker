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
package cellularautomata.automata.siv;

import cellularautomata.automata.IsotropicLongSandpileRules;
import cellularautomata.automata.LongTopplingResult;

public class SpreadIntegerValueRules extends IsotropicLongSandpileRules {

	@Override
	public String getName() {
		return "SpreadIntegerValue";
	}

	@Override
	protected LongTopplingResult topplePosition(long centerValue, long[] neighborValues) {
		int divider = neighborValues.length + 1;
		long valueSentToNeighbors = centerValue/divider;
		long remainingCenterValue = valueSentToNeighbors + centerValue%divider;
		long[] valuesSentToNeighbors = new long[neighborValues.length];
		if (valueSentToNeighbors != 0) {
			for (int i = 0; i < valuesSentToNeighbors.length; i++) {
				valuesSentToNeighbors[i] = valueSentToNeighbors;
			}
		}
		return new LongTopplingResult(remainingCenterValue, valuesSentToNeighbors);
	}

}
