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

import org.apache.commons.math3.FieldElement;

import cellularautomata.MinAndMax;
import cellularautomata.MinAndMaxConsumer;
import cellularautomata.TotalConsumer;

public interface NumericModel<Number_Type extends FieldElement<Number_Type> & Comparable<Number_Type>> extends ObjectModel<Number_Type> {

	default MinAndMax<Number_Type> getMinAndMax() throws Exception {
		MinAndMaxConsumer<Number_Type> consumer = new MinAndMaxConsumer<Number_Type>();
		forEach(consumer);
		return consumer.getMinAndMaxValue();
	}
	
	default Number_Type getTotal() throws Exception {
		TotalConsumer<Number_Type> consumer = new TotalConsumer<Number_Type>();
		forEach(consumer);
		return consumer.getTotal();
	}
	
	default MinAndMax<Number_Type> getEvenOddPositionsMinAndMax(boolean isEven) throws Exception {
		if (isEven) {
			return getEvenPositionsMinAndMax();
		} else {
			return getOddPositionsMinAndMax();
		}
	}
	
	default MinAndMax<Number_Type> getEvenPositionsMinAndMax() throws Exception {
		MinAndMaxConsumer<Number_Type> consumer = new MinAndMaxConsumer<Number_Type>();
		forEachAtEvenPosition(consumer);
		return consumer.getMinAndMaxValue();
	}
	
	default MinAndMax<Number_Type> getOddPositionsMinAndMax() throws Exception {
		MinAndMaxConsumer<Number_Type> consumer = new MinAndMaxConsumer<Number_Type>();
		forEachAtOddPosition(consumer);
		return consumer.getMinAndMaxValue();
	}
	
}
