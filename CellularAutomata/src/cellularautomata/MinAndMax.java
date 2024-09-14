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

public class MinAndMax<Object_Type> {

	private final Object_Type min;
	private final Object_Type max;
	
	public MinAndMax(Object_Type min, Object_Type max) {
		this.min = min;
		this.max = max;
	}
	
	public Object_Type getMin() {
		return min;
	}
	
	public Object_Type getMax() {
		return max;
	}
	
	@Override
	public String toString() {
		return "min: " + min + ", max: " + max;
	}
}
