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

public class CoordianteConverter implements IStringConverter<CoordinateOptionValue> {
	@Override
	public CoordinateOptionValue convert(String strValue) {
		CoordinateOptionValue value = null;
		if (strValue != null) {
			if (strValue.matches("^-?\\d+$")) {
				value = new CoordinateOptionValue(Integer.parseInt(strValue));
			} else if (strValue.matches("^[v-z]$")) {
				value = new CoordinateOptionValue(strValue.toLowerCase(), 0);
			} else if (strValue.matches("^\\[-?\\d+,-?\\d+\\]$")) {
				strValue = strValue.substring(1, strValue.length() - 1);
				String[] minMax = strValue.split(",");
				value = new CoordinateOptionValue(new int[] { Integer.parseInt(minMax[0]), Integer.parseInt(minMax[1]) });
			} else {
				value = new CoordinateOptionValue(strValue.substring(0, 0).toLowerCase(), Integer.parseInt(strValue.substring(1)));
			}
		}
		return value;
	}
}