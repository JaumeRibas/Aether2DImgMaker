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

public enum ImageGenerationMode {	
	
	NORMAL ("normal"),
	SPLIT_BY_COORDINATE_PARITY ("split-by-coordinate-parity"),
	EVEN_COORDINATES_ONLY ("even-coordinates-only"),
	ODD_COORDINATES_ONLY ("odd-coordinates-only"),
	//TODO missing modes
	TOPPLING_ALTERNATION_VIOLATIONS ("toppling-alternation-violations");
	
	public static final String FRIENDLY_VALUES_LIST = "'normal', 'split-by-coordinate-parity', 'even-coordinates-only', 'odd-coordinates-only' and 'toppling-alternation-violations'";
	
	private final String name;

	ImageGenerationMode(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public static ImageGenerationMode getByName(String name) {
		ImageGenerationMode result = null;
		ImageGenerationMode[] values = values();
		for (int i = 0; i != values.length && result == null; i++) {
			ImageGenerationMode currentValue = values[i];
			if (currentValue.name.equals(name)) {
				result = currentValue;
			}
		}
		return result;
	}

}
