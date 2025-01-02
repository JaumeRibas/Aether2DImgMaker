/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2025 Jaume Ribas

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

public class GridConverter implements IStringConverter<GridParameterValue> {
	
	@Override
	public GridParameterValue convert(String strValue) {
		GridParameterValue value = null;
		if (strValue != null) {
			String[] parts = strValue.toLowerCase().split("_");
			int dimension = Integer.parseInt(parts[0].substring(0, parts[0].length() - 1));
			if (parts.length == 1 || parts[1].equals("infinite")) {
				value = new GridParameterValue(dimension);
			} else {
				value = new GridParameterValue(dimension, Integer.parseInt(parts[1]));
			}
		}
		return value;
	}
	
}