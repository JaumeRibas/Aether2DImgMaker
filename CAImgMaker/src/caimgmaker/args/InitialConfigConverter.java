/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2023 Jaume Ribas

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

import cellularautomata.numbers.BigInt;

public class InitialConfigConverter implements IStringConverter<InitialConfigParameterValue> {
	
	@Override
	public InitialConfigParameterValue convert(String strValue) {
		new InitialConfigValidator().validate("Default", strValue);//There's a bug in the currently used version of JCommander that causes the main parameter to be converted before being validated
		InitialConfigParameterValue value = null;
		if (strValue != null) {
			String[] parts = strValue.toLowerCase().split("_");
			if (parts.length == 1) {
				value = new InitialConfigParameterValue(new BigInt(strValue));
			} else if (parts[0].equals("single-source")) {
				value = new InitialConfigParameterValue(new BigInt(parts[1]));
			} else {
				value = new InitialConfigParameterValue(Integer.parseInt(parts[1]), new BigInt(parts[2]), new BigInt(parts[3]));
			}
		}
		return value;
	}
	
}