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

import java.io.IOException;
import java.util.function.LongConsumer;

public interface LongGrid extends Grid, Iterable<Long> {

	default long[] getMinAndMax() throws Exception {
		MinMaxConsumer action = new MinMaxConsumer() ;
		forEach(action);
		return new long[] { action.min, action.max };
	}
	
	long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception;
	
	default long getTotal() throws Exception {
		TotalConsumer action = new TotalConsumer() ;
		forEach(action);
		return action.total;
	}
	
	/**
	 * Executes an action for each element of the grid in a consistent order.
	 * 
	 * @param action an action to execute for each element
	 * @throws IOException
	 */
	default void forEach(LongConsumer action) throws IOException {
		for (long value : this) {
			action.accept(value);
		}
	}
	
	static class MinMaxConsumer implements LongConsumer {
		
		public long min = Long.MAX_VALUE;
		public long max = Long.MIN_VALUE;
		
		@Override
		public void accept(long value) {
			if (value < min) {
				min = value;
			}
			if (value > max) {
				max = value;
			}
		}
	}
	
	static class TotalConsumer implements LongConsumer {
		
		public long total = 0;
		
		@Override
		public void accept(long value) {
			total += value;
		}
	}
}
