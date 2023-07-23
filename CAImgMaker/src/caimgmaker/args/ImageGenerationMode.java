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

import java.util.Arrays;

/**
 * Accepted values of the -image-generation-mode console app parameter. Each has a unique name and can have one or more unique aliases. The names and aliases are in kebab-case.
 * 
 * @author Jaume
 *
 */
public enum ImageGenerationMode {	
	
	NORMAL ("normal"),
	SPLIT_COORDINATE_PARITY ("split-coordinate-parity", "split-coord-parity", "scp"),
	EVEN_COORDINATES_ONLY ("even-coordinates-only", "even-coords-only", "eco"),
	ODD_COORDINATES_ONLY ("odd-coordinates-only", "odd-coords-only", "oco"),
	//TODO missing modes? or should the app figure out when to use even-y even-x, etc. based on coordinate filters?
	TOPPLING_ALTERNATION_COMPLIANCE ("toppling-alternation-compliance", "tac");
	
	private final String name;
	private final String[] aliases;

	ImageGenerationMode(String name, String... aliases) {
		this.name = name;
		this.aliases = aliases;
	}

	public String getName() {
		return name;
	}
	
	public String[] getAliases() {
		return aliases;
	}
	
	public boolean is(String nameOrAlias) {
		return name.equals(nameOrAlias) || Arrays.asList(aliases).contains(nameOrAlias);
	}
	
	@Override
	public String toString() {
		return name;
	}	
	
	public static ImageGenerationMode getByNameOrAlias(String nameOrAlias) {
		ImageGenerationMode result = null;
		ImageGenerationMode[] values = values();
		for (int i = 0; i != values.length && result == null; i++) {
			ImageGenerationMode currentValue = values[i];
			if (currentValue.is(nameOrAlias)) {
				result = currentValue;
			}
		}
		return result;
	}

}
