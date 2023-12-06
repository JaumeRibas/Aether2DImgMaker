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

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import caimgmaker.AetherImgMaker;

public class ImgNameValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value)
			throws ParameterException {
		int index = value.indexOf('/');
		if (index != -1) {
			throw new ParameterException(String.format(AetherImgMaker.messages.getString("illegal-char-at-index-format"), '/', index, value));
		}
		index = value.indexOf('\\');
		if (index != -1) {
			throw new ParameterException(String.format(AetherImgMaker.messages.getString("illegal-char-at-index-format"), '\\', index, value));
		}
		value += ".";//so that trailing spaces don't cause the next validation to fail
		try {
			Paths.get(value);
		} catch (InvalidPathException  ex) {
			throw new ParameterException(ex.getLocalizedMessage());
		}
	}
	
}