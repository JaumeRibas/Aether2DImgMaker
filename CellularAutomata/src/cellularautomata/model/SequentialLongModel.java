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

import java.io.IOException;
import java.util.function.LongConsumer;

public interface SequentialLongModel extends Model, Iterable<Long> {
	
	/**
	 * Feeds every value of the region, in a consistent order, to a {@link LongConsumer}.
	 * @param consumer
	 * @throws IOException 
	 */
	void forEach(LongConsumer consumer) throws IOException;

	default long[] getMinAndMax() throws Exception {
		MinAndMaxLongConsumer consumer = new MinAndMaxLongConsumer();
		forEach(consumer);
		return new long[] {consumer.min, consumer.max};
	}
	
	default long getTotal() throws Exception {
		TotalLongConsumer consumer = new TotalLongConsumer();
		forEach(consumer);
		return consumer.total;
	}
	
	default long[] getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		if (isEven) {
			return getEvenPositionsMinAndMax();
		} else {
			return getOddPositionsMinAndMax();
		}
	}
	
	long[] getEvenPositionsMinAndMax() throws Exception;
	
	long[] getOddPositionsMinAndMax() throws Exception;
}
