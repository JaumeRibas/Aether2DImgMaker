/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
package cellularautomata;

import java.util.function.Consumer;

public class MinAndMaxConsumer<Comparable_Type extends Comparable<Comparable_Type>> implements Consumer<Comparable_Type> {

	private Comparable_Type min;
	private Comparable_Type max;
	
	@Override
	public void accept(Comparable_Type value) {
		if (value != null) {
			if (min == null) {
				min = value;
				max = value;
			} else if (value.compareTo(min) < 0) {
				min = value;
			} else if (value.compareTo(max) > 0) {
				max = value;
			}
		}
	}
	
	public MinAndMax<Comparable_Type> getMinAndMaxValue() {
		return min == null? null : new MinAndMax<Comparable_Type>(min, max);
	}
	
}
