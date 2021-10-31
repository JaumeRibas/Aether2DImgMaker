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
package caimgmaker.args;

import com.beust.jcommander.IStringConverter;

public class GridConverter implements IStringConverter<GridOptionValue> {
	@Override
	public GridOptionValue convert(String strValue) {
		GridOptionValue value = null;
		if (strValue != null) {
			String[] parts = strValue.split("d_");
			if (parts[1].equals("infinite")) {
				value = new GridOptionValue(Integer.parseInt(parts[0]));
			} else {
				value = new GridOptionValue(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
			}
		}
		return value;
	}
}