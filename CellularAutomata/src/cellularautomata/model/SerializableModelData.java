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
package cellularautomata.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cellularautomata.numbers.BigInt;

/**
 * Serializable data to back up and restore a model
 * 
 * @author Jaume
 *
 */
public class SerializableModelData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//KEYS
	/** Key for the model. The value being one the {@link Models} */
	public static final int MODEL = 0;
	/** Key for the model's step. The value being a {@link Long} object */
	public static final int STEP = 1;
	/** Key for a {@link Boolean} object representing whether or not the model's configuration changed from the previous step to the current one */
	public static final int CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP = 2;
	/** Key for the grid. See also {@link this#GRID_TYPE} and {@#link this#GRID_IMPLEMENTATION_TYPE} */
	public static final int GRID = 3;
	/** Key for the grid's type. The value being one of the {@link GridTypes} */
	public static final int GRID_TYPE = 4;
	/** Key for the grid's implementation type. The value being one of the {@link GridImplementationTypes} */
	public static final int GRID_IMPLEMENTATION_TYPE = 5;
	/** Key for the model's initial configuration. See also {@link this#INITIAL_CONFIGURATION_TYPE} and {@#link this#INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE} */
	public static final int INITIAL_CONFIGURATION = 6;
	/** Key for the model's initial configuration type. The value being one of the {@link InitialConfigurationTypes} */
	public static final int INITIAL_CONFIGURATION_TYPE = 7;
	/** Key for the model's initial configuration implementation type. The value being one of the {@link InitialConfigurationImplementationTypes} */
	public static final int INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE = 8;
	/** Key for the model's coordinate bounds. See also {@#link CoordinateBoundsImplementationTypes} */
	public static final int COORDINATE_BOUNDS = 9;
	/** Key for the model's coordinate bounds implementation type. The value being one of the {@link CoordinateBoundsImplementationTypes} */
	public static final int COORDINATE_BOUNDS_IMPLEMENTATION_TYPE = 10;
	/** Key for the model's toppling alternation compliance data. See also {@link this#TOPPLING_ALTERNATION_COMPLIANCE_TYPE} and {@#link this#TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE} */
	public static final int TOPPLING_ALTERNATION_COMPLIANCE = 11;
	/** Key for the model's toppling alternation compliance implementation type. The value being one of the {@link GridImplementationTypes} */
	public static final int TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE = 12;
	/** Key for the name of the folder for the model's initial configuration. The value being a {@link String} object */
	public static final int INITIAL_CONFIGURATION_FOLDER_NAME = 13;
	/*...*/
	
	//MODELS AND TYPES
	public static final class Models {
		
		public static final Integer AETHER = 0;
		public static final Integer SPREAD_INTEGER_VALUE = 1;
		public static final Integer ABELIAN_SANDPILE = 2;
		/*...*/
		
		private Models() { }
	}
	
	public static final class GridTypes {

		/** An infinite 1D grid */
		public static final Integer INFINITE_1D = 0;
		/** A regular infinite square grid */
		public static final Integer INFINITE_SQUARE = 1;
		/** A regular infinite 3D grid */
		public static final Integer REGULAR_INFINITE_3D = 2;
		/** A regular bounded 1D grid */
		public static final Integer REGULAR_BOUNDED_1D = 3;
		/** A regular infinite 4D grid */
		public static final Integer REGULAR_INFINITE_4D = 4;
		/** A regular infinite 5D grid */
		public static final Integer REGULAR_INFINITE_5D = 5;
		/*...*/
		
		private GridTypes() { }
	}
	
	public static final class GridImplementationTypes {
		
		/** {@code int} primitive array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_INT_ARRAY_1 = 0;
		/** {@code long} primitive array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1 = 1;
		/** {@link BigInt} array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_BIG_INT_ARRAY_1 = 2;
		/** {@code boolean} primitive array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1 = 3;
		/** {@code BigFraction} array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_BIG_FRACTION_ARRAY_1 = 4;
		/*...*/
		
		private GridImplementationTypes() { }
	}
	
	public static final class InitialConfigurationTypes {
		
		public static final Integer SINGLE_SOURCE_AT_ORIGIN = 0;
		/*...*/
		
		private InitialConfigurationTypes() { }
	}
	
	public static final class InitialConfigurationImplementationTypes {
		/** An {@link Integer} object */
		public static final Integer INTEGER = 0;
		/** A {@link Long} object */
		public static final Integer LONG = 1;
		/** A {@link BigInt} object */
		public static final Integer BIG_INT = 2;
		/** An {@link Boolean} object */
		public static final Integer BOOLEAN = 3;
		/*...*/
		
		private InitialConfigurationImplementationTypes() { }
	}
	
	public static final class CoordinateBoundsImplementationTypes {
		
		public static final Integer BOUNDS_REACHED_BOOLEAN = 0;
		public static final Integer MAX_COORDINATE_INTEGER = 1;
		/*...*/
		
		private CoordinateBoundsImplementationTypes() { }
	}
	
	private final Map<Integer, Object> data = new HashMap<Integer, Object>();
	
	public boolean contains(Integer key) {
		return data.containsKey(key);
	}
	
	public Object get(Integer key) {
		return data.get(key);
	}
	
	public void put(Integer key, Object value) {
		data.put(key, value);
	}

}
