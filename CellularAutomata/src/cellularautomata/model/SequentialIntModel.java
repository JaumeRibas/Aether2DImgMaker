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

import java.util.function.IntConsumer;

public interface SequentialIntModel extends Model, Iterable<Integer> {
	
	/**
	 * Feeds every value of the region, in a consistent order, to an {@link IntConsumer}.
	 * @param consumer
	 */
	void forEach(IntConsumer consumer);

	default int[] getMinAndMax() throws Exception {
		MinAndMaxIntConsumer consumer = new MinAndMaxIntConsumer();
		forEach(consumer);
		return new int[] {consumer.min, consumer.max};
	}
	
	default int getTotal() throws Exception {
		TotalIntConsumer consumer = new TotalIntConsumer();
		forEach(consumer);
		return consumer.total;
	}
	
	default int[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		if (isEven) {
			return getEvenPositionsMinAndMax();
		} else {
			return getOddPositionsMinAndMax();
		}
	}
	
	int[] getEvenPositionsMinAndMax() throws Exception;
	
	int[] getOddPositionsMinAndMax() throws Exception;
	
}
