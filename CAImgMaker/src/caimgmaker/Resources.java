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
package caimgmaker;
//TODO translations
public final class Resources {
	
	public static final String USE_HELP_MESSAGE = "Use -help to view the list of available parameters and their accepted values.";
	public static final String UNKNOWN_IMG_GEN_MODE_MESSAGE = "Unrecognized image generation mode.";
	public static final String UNSUPPORTED_MODEL_SECTION_MESSAGE_FORMAT = "It is currently not supported to generate images form a model section of type %s.%n";
	public static final String UNSUPPORTED_DIMENSION_MESSAGE_FORMAT = "Currently it is only supported to generate images from a model section with dimension two or three (found %d). Use the -coordinate-filters parameter or a -grid with two or three dimensions.%n";

	public static final String GRID_NOT_SUPPORTED_MESSAGE_FORMAT = "The %s model is currently not supported with this type of grid.%n";
	public static final String GRID_TYPE_NEEDED_IN_ORDER_TO_RESTORE_MESSAGE_FORMAT = "You need to specify the grid type of the backup you are trying to restore.%n";
	public static final String INITIAL_CONFIG_NOT_SUPPORTED_MESSAGE_FORMAT = "The %s model is currently not supported with the selected initial configuration.%n";
	public static final String SINGLE_SOURCE_OUT_OF_RANGE_MESSAGE_FORMAT = "The single source value is out of the currently supported range for this model: [%d, %d].%n";
	public static final String MIN_MAX_OUT_OF_RANGE_MESSAGE_FORMAT = "The min/max values are out of the currently supported range for this model: [%d, %d].%n";
	public static final String INITIAL_CONFIG_NEEDED_MESSAGE_FORMAT = "The %s model needs and initial configuration.%n";
	public static final String MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_MODEL_MESSAGE_FORMAT = "The %s model is currently not supported with the -memory-safe parameter.%n";
	public static final String MEMORY_SAFE_NOT_SUPPORTED_FOR_THIS_INITIAL_CONFIG_MESSAGE_FORMAT = "The %s model is currently not supported with the -memory-safe parameter and the selected initial configuration.%n";
	public static final String IMG_GEN_MODE_NOT_SUPPORTED_MESSAGE_FORMAT = "The selected image generation mode is currently not supported for the %s model.%n";
	public static final String MEMORY_SAFE_NOT_SUPPORTED_WITH_THESE_PARAMS_MESSAGE_FORMAT = "The -memory-safe parameter is currently not supported with the other given parameters.%n";
	public static final String INITIAL_CONFIG_NOT_SUPPORTED_WITH_THESE_PARAMS_MESSAGE_FORMAT = "The initial configuration is currently not supported with the other given parameters.%n";
	public static final String BACKUP_COULD_NOT_BE_RESTORED_MESSAGE_FORMAT = "The backup could not be resored.%n";
	
	private Resources() { }
	
}
