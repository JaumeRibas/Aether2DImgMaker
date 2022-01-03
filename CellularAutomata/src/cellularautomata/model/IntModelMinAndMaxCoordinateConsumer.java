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
package cellularautomata.model;

import java.util.function.Consumer;

public class IntModelMinAndMaxCoordinateConsumer implements Consumer<Coordinates> {
	public int min = Integer.MAX_VALUE;
	public int max = Integer.MIN_VALUE;
	private IntModel region;
	
	public IntModelMinAndMaxCoordinateConsumer(IntModel region) {
		this.region = region;
	}
	
	@Override
	public void accept(Coordinates coordinates) {
		int value;
		try {
			value = region.getFromPosition(coordinates);
			if (value > max)
				max = value;
			if (value < min)
				min = value;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
