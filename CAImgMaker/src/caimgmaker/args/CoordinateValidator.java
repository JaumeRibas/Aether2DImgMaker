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
package caimgmaker.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class CoordinateValidator implements IParameterValidator {
	public void validate(String name, String value)
			throws ParameterException {
		if (!value.matches("^-?\\d+|\\[-?\\d+,-?\\d+\\]|[v-z]([+-]\\d+)?$")) {
			throw new ParameterException("The value of " + name + " has an incorrect format.");
		}
	}
}