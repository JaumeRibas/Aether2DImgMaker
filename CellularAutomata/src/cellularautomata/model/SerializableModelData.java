/* Aether2DImgMaker -- console app to generate images of the Aether cellular automaton in 2D
    Copyright (C) 2017-2024 Jaume Ribas

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
	/** Key for the version of the data format. The value being an {@link Integer} object */
	private static final int version = 14;
	/** Key for the model. The value being one the {@link Models} */
	public static final int MODEL = 0;
	/** Key for the model's step. The value being a {@link Long} object */
	public static final int STEP = 1;
	/** Key for a {@link Boolean} object representing whether or not the model's configuration changed from the previous step to the current one */
	public static final int CONFIGURATION_CHANGED_FROM_PREVIOUS_STEP = 2;
	/** Key for the grid. See also {@link #GRID_TYPE} and {@link #GRID_IMPLEMENTATION_TYPE} */
	public static final int GRID = 3;
	/** Key for the grid's type. The value being one of the {@link GridTypes} */
	public static final int GRID_TYPE = 4;
	/** Key for the grid's dimension. The value being an {@link Integer} object  */
	public static final int GRID_DIMENSION = 15;
	/** Key for the grid's implementation type. The value being one of the {@link GridImplementationTypes} */
	public static final int GRID_IMPLEMENTATION_TYPE = 5;
	/** Key for the model's initial configuration. See also {@link #INITIAL_CONFIGURATION_TYPE} and {@link #INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE} */
	public static final int INITIAL_CONFIGURATION = 6;
	/** Key for the model's initial configuration type. The value being one of the {@link InitialConfigurationTypes} */
	public static final int INITIAL_CONFIGURATION_TYPE = 7;
	/** Key for the model's initial configuration implementation type. The value being one of the {@link InitialConfigurationImplementationTypes} */
	public static final int INITIAL_CONFIGURATION_IMPLEMENTATION_TYPE = 8;
	/** Key for the model's coordinate bounds. See also {@link CoordinateBoundsImplementationTypes} */
	public static final int COORDINATE_BOUNDS = 9;
	/** Key for the model's coordinate bounds implementation type. The value being one of the {@link CoordinateBoundsImplementationTypes} */
	public static final int COORDINATE_BOUNDS_IMPLEMENTATION_TYPE = 10;
	/** Key for the model's toppling alternation compliance data. See also {@link #TOPPLING_ALTERNATION_COMPLIANCE_TYPE} and {@link #TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE} */
	public static final int TOPPLING_ALTERNATION_COMPLIANCE = 11;
	/** Key for the model's toppling alternation compliance implementation type. The value being one of the {@link GridImplementationTypes} */
	public static final int TOPPLING_ALTERNATION_COMPLIANCE_IMPLEMENTATION_TYPE = 12;
	/** Key for the name of the folder for the model's initial configuration. The value being a {@link String} object */
	public static final int INITIAL_CONFIGURATION_FOLDER_NAME = 13;
	/*...*/
	
	//MODELS AND TYPES
	// It might have been better not to repeat values across sections to prevent errors when comparing
	public static final class Models {
		
		public static final Integer AETHER = 0;
		public static final Integer SUNFLOWER = 1;
		public static final Integer ABELIAN_SANDPILE = 2;
		/*...*/
		
		private Models() { }
	}
	
	public static final class GridTypes {

		/** 
		 * An infinite 1D grid 
		 * 
		 * @deprecated Use {@link #INFINITE_REGULAR} instead.  
		 */
		@Deprecated
		private static final Integer INFINITE_1D = 0;
		/** 
		 * A regular infinite square grid
		 * 
		 * @deprecated Use {@link #INFINITE_REGULAR} instead.  
		 */
		@Deprecated
		private static final Integer INFINITE_SQUARE = 1;
		/** 
		 * A regular infinite 3D grid
		 * 
		 * @deprecated Use {@link #INFINITE_REGULAR} instead.  
		 */
		@Deprecated
		private static final Integer REGULAR_INFINITE_3D = 2;
		/** 
		 * A regular infinite 4D grid
		 * 
		 * @deprecated Use {@link #INFINITE_REGULAR} instead.  
		 */
		@Deprecated
		private static final Integer REGULAR_INFINITE_4D = 4;
		/** 
		 * A regular infinite 5D grid
		 * 
		 * @deprecated Use {@link #INFINITE_REGULAR} instead.  
		 */
		@SuppressWarnings("unused")
		@Deprecated
		private static final Integer REGULAR_INFINITE_5D = 5;
		/** An infinite regular grid */
		public static final Integer INFINITE_REGULAR = 6;
		/** A bounded regular grid */
		public static final Integer BOUNDED_REGULAR = 7;
		/*...*/
		
		private GridTypes() { }
	}
	
	public static final class GridImplementationTypes {
		
		/** An {@code int} primitive array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_INT_ARRAY_1 = 0;
		/** A {@code long} primitive array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_LONG_PRIMITIVE_ARRAY_1 = 1;
		/** A {@link BigInt} array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_BIG_INT_ARRAY_1 = 2;
		/** A {@code boolean} primitive array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_BOOLEAN_PRIMITIVE_ARRAY_1 = 3;
		/** A {@link BigFraction} array of as many dimensions as the grid, holding an asymmetric section of an isotropic hypercube with a center cell. This center cell is also the origin. The array's indexes match the coordinates' absolute values. */
		public static final Integer ANYSOTROPIC_BIG_FRACTION_ARRAY_1 = 4;
		/** An {@link AnisotropicIntArray} object */
		public static final Integer ANYSOTROPIC_INT_ARRAY_CLASS_INSTANCE = 5;
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
		/** A {@link Boolean} object */
		public static final Integer BOOLEAN = 3;
		/** A length 2 1D {@code int} array containing the value at the origin at index 0 and the background value at index 1 */
		public static final Integer ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_INT_ARRAY = 4;
		/** A length 2 1D {@code long} array containing the value at the origin at index 0 and the background value at index 1 */
		public static final Integer ORIGIN_AND_BACKGROUND_VALUES_AS_LENGTH_2_LONG_PRIMITIVE_ARRAY = 5;
		/*...*/
		
		private InitialConfigurationImplementationTypes() { }
	}
	
	public static final class CoordinateBoundsImplementationTypes {
		
		public static final Integer BOUNDS_REACHED_BOOLEAN = 0;
		public static final Integer MAX_COORDINATE_INTEGER = 1;
		/*...*/
		
		private CoordinateBoundsImplementationTypes() { }
	}
	
	/**
	 * Updates an old instance to the latest format. If the instance is already up to date, the same instance is returned unchanged.
	 * 
	 * @param serializableData the instance to be updated
	 * @return an up to date instance
	 */
	public static final SerializableModelData updateDataFormat(SerializableModelData serializableData) {
		if (!serializableData.contains(version)) {
			//version 0
			Integer gridType = (Integer) serializableData.get(GRID_TYPE);
			int gridDimension;
			if (GridTypes.INFINITE_1D.equals(gridType)) {
				gridDimension = 1;
			} else if (GridTypes.INFINITE_SQUARE.equals(gridType)) {
				gridDimension = 2;
			} else if (GridTypes.REGULAR_INFINITE_3D.equals(gridType)) {
				gridDimension = 3;
			} else if (GridTypes.REGULAR_INFINITE_4D.equals(gridType)) {
				gridDimension = 4;
			} else { //GridTypes.REGULAR_INFINITE_5D.equals(gridType)
				gridDimension = 5;
			}
			serializableData.put(GRID_DIMENSION, gridDimension);
			serializableData.put(GRID_TYPE, GridTypes.INFINITE_REGULAR);//only infinite regular grids where used
		}
		return serializableData;
	}
	
	private final Map<Integer, Object> data = new HashMap<Integer, Object>();
	
	public SerializableModelData() {
		data.put(version, 1);
	}
	
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
