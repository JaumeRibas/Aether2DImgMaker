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

import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;

public class CoordinateFiltersValidatorAndConverter implements IParameterValidator, IStringConverter<CoordinateFilters> {
	
	@Override
	public void validate(String name, String value)
			throws ParameterException {
		String[] filters = value.split(";");
		for (int i = 0; i < filters.length; i++) {
			if (!filters[i].matches("(?i)^(x\\d+=[+-]?(\\d+|x\\d+([+-]\\d+)?)|x\\d+[<>][+-]?\\d+)$")) {
				throw new ParameterException("The coordinate filter at position " + (i+1) + " has an invalid format.");
			}
		}
	}

	private static final String MESSAGE_FORMAT_PREFIX = "Invalid coordinate filter at position %d. ";
	private static final String INVALID_COORD_INDEX_MESSAGE_FORMAT = MESSAGE_FORMAT_PREFIX + "The coordinate index must be between one and the dimension, both included.";
	private static final String COORD_RELATIVE_TO_ITSELF_MESSAGE_FORMAT = MESSAGE_FORMAT_PREFIX + "The coordinate cannot be set relative to itself.";
	private static final String INVALID_RANGE_MESSAGE_FORMAT = MESSAGE_FORMAT_PREFIX + "Invalid resulting range %d < x%d < %d.";
	private static final String REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT = MESSAGE_FORMAT_PREFIX + "The filter is either redundant or incompatible with one or a combination of preceding filters. Some possible causes are: incorrect coordinate indexes, repeated filters, too many greater/less than filters on the same set of interconnected coordinates, redundant interconnections of coordinates, etc.";

	@Override
	public CoordinateFilters convert(String parameterValue) {
		String[] strFilters = parameterValue.split(";");
		Pattern coordIndexPattern = Pattern.compile("(?<=x)\\d+", Pattern.CASE_INSENSITIVE);
		CoordinateFilters resultingFilters = new CoordinateFilters();
		for (int i = 0, filterPosition = 1; i < strFilters.length; i = filterPosition, filterPosition++) {
			String strFilter = strFilters[i];
			Matcher matcher = coordIndexPattern.matcher(strFilter);
			matcher.find();
			int coordinate = Integer.parseInt(matcher.group()) - 1;
			if (coordinate < 0) {
				throw new ParameterException(String.format(INVALID_COORD_INDEX_MESSAGE_FORMAT, filterPosition));
			}
			int indexOfOperator = matcher.end();
			char operatorCharacter = strFilter.charAt(indexOfOperator);
			switch (operatorCharacter) {
			case '=':
				if (matcher.find())
				{
					int referenceCoordinate = Integer.parseInt(matcher.group()) - 1;
					boolean isCoordinateOpposite = strFilter.charAt(indexOfOperator + 1) == '-';
					if (referenceCoordinate < 0) {
						throw new ParameterException(String.format(INVALID_COORD_INDEX_MESSAGE_FORMAT, filterPosition));
					}
					if (coordinate == referenceCoordinate) {
						throw new ParameterException(String.format(COORD_RELATIVE_TO_ITSELF_MESSAGE_FORMAT, filterPosition));
					}	
					int offset = 0;
					int endOfMatch = matcher.end();
					if (endOfMatch != strFilter.length()) {
						offset = Integer.parseInt(strFilter.substring(endOfMatch));
					}			
					validateAndAddRelativeFilter(filterPosition, resultingFilters, coordinate, referenceCoordinate, isCoordinateOpposite, offset);
				} else {
					int value = Integer.parseInt(strFilter.substring(indexOfOperator + 1));
					validateAndAddAbsoluteFilter(filterPosition, resultingFilters, coordinate, value);
				}	
				break;
			case '<':
				int max = Integer.parseInt(strFilter.substring(indexOfOperator + 1)) - 1;
				validateAndAddMaxFilter(filterPosition, resultingFilters, coordinate, max);
				break;
			case '>':
				int min = Integer.parseInt(strFilter.substring(indexOfOperator + 1)) + 1;
				validateAndAddMinFilter(filterPosition, resultingFilters, coordinate, min);
				break;
			}
		}
		resultingFilters.relativeFilterGroups.sort(new Comparator<SortedMap<Integer, int[]>>() {

			@Override
			public int compare(SortedMap<Integer, int[]> o1, SortedMap<Integer, int[]> o2) {
				return o1.keySet().iterator().next() - o2.keySet().iterator().next();
			}
			
		});
		return resultingFilters;
	}

	private static void validateAndAddRelativeFilter(int userProvidedPosition, CoordinateFilters filters, int axis, int referenceAxis, boolean isOpposite, int offset) {
		int signum = isOpposite ? -1 : 1;
		if (filters.absoluteFilters.containsKey(axis)) {
			int absoluteValue = signum*(filters.absoluteFilters.get(axis) - offset);
			validateAndAddAbsoluteFilter(userProvidedPosition, filters, referenceAxis, absoluteValue);
		} else if (filters.absoluteFilters.containsKey(referenceAxis)) {
			int absoluteValue = signum*filters.absoluteFilters.get(referenceAxis) + offset;
			validateAndAddAbsoluteFilterWithoutCheck(userProvidedPosition, filters, axis, absoluteValue);
		} else {
			if (referenceAxis > axis) {
				int swap = axis;
				axis = referenceAxis;
				referenceAxis = swap;
				if (!isOpposite) {
					offset = -offset;
				}
			}			
			SortedMap<Integer, int[]> axisFilterGroup = getFilterGroupContainingAxis(filters.relativeFilterGroups, axis);
			SortedMap<Integer, int[]> referenceAxisFilterGroup = getFilterGroupContainingAxis(filters.relativeFilterGroups, referenceAxis);
			if (axisFilterGroup == null) {
				if (referenceAxisFilterGroup == null) {
					//add new group of relative filters
					combineMinMaxFilters(userProvidedPosition, filters.minMaxFilters, referenceAxis, axis, isOpposite, offset);
					SortedMap<Integer, int[]> newFilterGroup = new TreeMap<Integer, int[]>();
					newFilterGroup.put(referenceAxis, new int[] { 1, 0 });
					newFilterGroup.put(axis, new int[] { signum, offset });
					filters.relativeFilterGroups.add(newFilterGroup);
				} else {
					//add axis to existing group without checking if the group needs adjusting
					//	change offset and signum to be relative to the smallest axis in the group
					int[] filter = referenceAxisFilterGroup.get(referenceAxis);
					if (isOpposite) {
						offset -= filter[1];
					} else {
						offset += filter[1];
					}
					if (filter[0] == -1) {
						isOpposite = !isOpposite;
						signum = -signum;				
					}
					int smallestAxisInGroup = referenceAxisFilterGroup.keySet().iterator().next();
					combineMinMaxFilters(userProvidedPosition, filters.minMaxFilters, smallestAxisInGroup, axis, isOpposite, offset);
					referenceAxisFilterGroup.put(axis, new int[] { signum, offset });
				}
			} else if (referenceAxisFilterGroup == null) {
				//add referenceAxis to existing group checking if the group needs adjusting
				//	change offset and signum to be relative to the smallest axis in the group
				int[] axisFilter = axisFilterGroup.get(axis);
				if (isOpposite) {
					offset -= axisFilter[1];
				} else {
					offset = axisFilter[1] - offset;
				}
				if (axisFilter[0] == -1) {
					isOpposite = !isOpposite;
					signum = -signum;				
				}
				int smallestAxisInGroup = axisFilterGroup.keySet().iterator().next();
				if (referenceAxis < smallestAxisInGroup) {
					//group needs adjusting
					for (Integer axisInGroup : axisFilterGroup.keySet()) {
						int[] filter = axisFilterGroup.get(axisInGroup);
						filter[0] *= signum;
						filter[1] -= filter[0]*offset;
					}
					axisFilterGroup.put(referenceAxis, new int[] { 1, 0 });
					int[] previousSmallestAxisInGroupFilter = axisFilterGroup.get(smallestAxisInGroup);
					combineMinMaxFilters(userProvidedPosition, filters.minMaxFilters, referenceAxis, smallestAxisInGroup, previousSmallestAxisInGroupFilter[0] == -1, previousSmallestAxisInGroupFilter[1]);
				} else {
					//no adjusting needed
					combineMinMaxFilters(userProvidedPosition, filters.minMaxFilters, smallestAxisInGroup, referenceAxis, isOpposite, offset);
					axisFilterGroup.put(referenceAxis, new int[] { signum, offset });
				}
			} else if (axisFilterGroup == referenceAxisFilterGroup) {
				throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
			} else {
				//merge groups
				int[] axisFilter = axisFilterGroup.get(axis);
				int[] referenceAxisFiler = referenceAxisFilterGroup.get(referenceAxis);
				int smallestAxisInAxisFilterGroup = axisFilterGroup.keySet().iterator().next();
				int smallestAxisInReferenceAxisFilterGroup = referenceAxisFilterGroup.keySet().iterator().next();
				SortedMap<Integer, int[]> groupToKeep, groupToRemove;
				int smallestAxisInGroupToKeep, smallestAxisInGroupToRemove;
				if (smallestAxisInAxisFilterGroup < smallestAxisInReferenceAxisFilterGroup) {
					groupToKeep = axisFilterGroup;
					groupToRemove = referenceAxisFilterGroup;
					smallestAxisInGroupToKeep = smallestAxisInAxisFilterGroup;
					smallestAxisInGroupToRemove = smallestAxisInReferenceAxisFilterGroup;
					offset = signum*referenceAxisFiler[0]*(axisFilter[1] - signum*referenceAxisFiler[1] - offset);
				} else {
					groupToKeep = axisFilterGroup;
					groupToRemove = referenceAxisFilterGroup;
					smallestAxisInGroupToKeep = smallestAxisInAxisFilterGroup;
					smallestAxisInGroupToRemove = smallestAxisInReferenceAxisFilterGroup;
					offset = axisFilter[0]*(signum*referenceAxisFiler[1] + offset - axisFilter[1]);
				}
				signum *= axisFilter[0]*referenceAxisFiler[0];
				for (Integer axisInGroup : groupToRemove.keySet()) {
					int[] filter = groupToRemove.get(axisInGroup);
					filter[0] *= signum;
					filter[1] -= filter[0]*offset;
				}
				groupToKeep.putAll(groupToRemove);
				filters.relativeFilterGroups.remove(groupToRemove);
				int[] smallestAxisInGroupToRemoveFilter = groupToKeep.get(smallestAxisInGroupToRemove);
				combineMinMaxFilters(userProvidedPosition, filters.minMaxFilters, smallestAxisInGroupToKeep, smallestAxisInGroupToRemove, smallestAxisInGroupToRemoveFilter[0] == -1, smallestAxisInGroupToRemoveFilter[1]);
			}
		}		
	}
	
	private static void combineMinMaxFilters(int userProvidedPosition, SortedMap<Integer, Integer[]> minMaxFilters, int targetAxis, int otherAxis, boolean isOpposite, int offset) {
		Integer[] targetAxisMinMaxFilters = minMaxFilters.get(targetAxis);
		Integer[] otherAxisMinMaxFilters = minMaxFilters.get(otherAxis);
		if (targetAxisMinMaxFilters == null) {
			if (otherAxisMinMaxFilters != null) {
				targetAxisMinMaxFilters = new Integer[2];
				Integer min = otherAxisMinMaxFilters[0];
				Integer max = otherAxisMinMaxFilters[1];
				if (isOpposite) {
					if (min != null) {
						targetAxisMinMaxFilters[1] = offset - min;
					}
					if (max != null) {
						targetAxisMinMaxFilters[0] = offset - max;
					}
				} else {
					if (min != null) {
						targetAxisMinMaxFilters[0] = min - offset;
					}
					if (max != null) {
						targetAxisMinMaxFilters[1] = max - offset;
					}								
				}
				minMaxFilters.remove(otherAxis);	
				minMaxFilters.put(targetAxis, targetAxisMinMaxFilters);
			}
		} else if (otherAxisMinMaxFilters != null) {
			if (!combineMinMaxFilters(targetAxisMinMaxFilters, otherAxisMinMaxFilters, isOpposite, offset)) {
				throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
			}
			minMaxFilters.remove(otherAxis);
		}
	}
	
	private static boolean combineMinMaxFilters(Integer[] targetFilters, Integer[] otherFilters, boolean isOpposite, int offset) {
		Integer targetMin = targetFilters[0];
		Integer targetMax = targetFilters[1];
		Integer otherMin = otherFilters[0];
		Integer otherMax = otherFilters[1];
		if (isOpposite) {
			if (otherMin != null) {
				if (targetMax != null) {
					return false;
				}
				targetFilters[1] = offset - otherMin;
			}
			if (otherMax != null) {
				if (targetMin != null) {
					return false;
				}
				targetFilters[0] = offset - otherMax;
			}
		} else {
			if (otherMin != null) {
				if (targetMin != null) {
					return false;
				}
				targetFilters[0] = otherMin - offset;
			}
			if (otherMax != null) {
				if (targetMax != null) {
					return false;
				}
				targetFilters[1] = otherMax - offset;
			}
		}
		return true;
	}
	
	private static void validateAndAddAbsoluteFilter(int userProvidedPosition, CoordinateFilters filters, int axis, int value) {
		if (filters.absoluteFilters.containsKey(axis) || filters.minMaxFilters.containsKey(axis)) {
			throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
		}
		validateAndAddAbsoluteFilterWithoutCheck(userProvidedPosition, filters, axis, value);
	}
	
	private static void validateAndAddAbsoluteFilterWithoutCheck(int userProvidedPosition, CoordinateFilters filters, int axis, int value) {
		SortedMap<Integer, int[]> filterGroup = getFilterGroupContainingAxis(filters.relativeFilterGroups, axis);
		if (filterGroup == null) {
			filters.absoluteFilters.put(axis, value);
		} else {
			int smallestAxisInGroup = filterGroup.keySet().iterator().next();
			if (filters.minMaxFilters.containsKey(smallestAxisInGroup)) {
				throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
			}
			convertToAbsoluteFilters(filters.absoluteFilters, filterGroup, axis, value);
			filters.relativeFilterGroups.remove(filterGroup);
		}
	}
	
	private static SortedMap<Integer, int[]> getFilterGroupContainingAxis(List<SortedMap<Integer, int[]>> filterGroups, int axis) {
		SortedMap<Integer, int[]> result = null;
		boolean notFound = true;
		for (int i = filterGroups.size() - 1; i != -1 && notFound; i--) {
			SortedMap<Integer, int[]> filterGroup = filterGroups.get(i);
			if (filterGroup.containsKey(axis)) {
				notFound = false;
				result = filterGroup;
			}
		}
		return result;
	}
	
	private static void convertToAbsoluteFilters(SortedMap<Integer, Integer> absoluteFilters, SortedMap<Integer, int[]> filterGroup, int axis, int value) {
		int[] relativeFilter = filterGroup.get(axis);
		int smallestAxisValue = relativeFilter[0]*(value - relativeFilter[1]);
		for (Integer axisInGroup : filterGroup.keySet()) {
			relativeFilter = filterGroup.get(axisInGroup);
			absoluteFilters.put(axisInGroup, relativeFilter[0]*smallestAxisValue + relativeFilter[1]);
		}
	}

	private static void validateAndAddMinFilter(int userProvidedPosition, CoordinateFilters filters, int axis, int min) {
		Integer[] existingMinMaxFilters = filters.minMaxFilters.get(axis);
		if (existingMinMaxFilters == null) {
			if (filters.absoluteFilters.containsKey(axis)) {
				throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
			}
			SortedMap<Integer, int[]> filterGroup = getFilterGroupContainingAxis(filters.relativeFilterGroups, axis);
			if (filterGroup == null) {
				filters.minMaxFilters.put(axis, new Integer[] { min, null });
			} else {
				int smallestAxisInGroup = filterGroup.keySet().iterator().next();
				if (axis == smallestAxisInGroup) {
					filters.minMaxFilters.put(axis, new Integer[] { min, null });
				} else {
					int[] relativeFilter = filterGroup.get(axis);
					axis = smallestAxisInGroup;
					existingMinMaxFilters = filters.minMaxFilters.get(axis);
					int signum = relativeFilter[0];
					if (signum == 1) {
						min = min - relativeFilter[1];
						if (existingMinMaxFilters == null) {
							filters.minMaxFilters.put(axis, new Integer[] { min, null });
						} else {
							if (existingMinMaxFilters[0] != null) {
								throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
							}
							int existingMax = existingMinMaxFilters[1];//It can be assumed it's not null
							if (existingMax < min) {
								throw new ParameterException(String.format(INVALID_RANGE_MESSAGE_FORMAT, userProvidedPosition, min - 1, axis + 1, existingMax + 1));
							}
							if (existingMax == min) {
								filters.minMaxFilters.remove(axis);
								convertToAbsoluteFilters(filters.absoluteFilters, filterGroup, axis, min);
								filters.relativeFilterGroups.remove(filterGroup);
							} else {
								existingMinMaxFilters[0] = min;
							}
						}
					} else {
						int max = relativeFilter[1] - min;
						if (existingMinMaxFilters == null) {
							filters.minMaxFilters.put(axis, new Integer[] { null, max });
						} else {
							if (existingMinMaxFilters[1] != null) {
								throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
							}
							int existingMin = existingMinMaxFilters[0];//It can be assumed it's not null
							if (existingMin > max) {
								throw new ParameterException(String.format(INVALID_RANGE_MESSAGE_FORMAT, userProvidedPosition, existingMin - 1, axis + 1, max + 1));
							}
							if (existingMin == max) {
								filters.minMaxFilters.remove(axis);
								convertToAbsoluteFilters(filters.absoluteFilters, filterGroup, axis, max);
								filters.relativeFilterGroups.remove(filterGroup);
							} else {
								existingMinMaxFilters[1] = max;
							}
						}
					}
				}
			}
		} else {
			if (existingMinMaxFilters[0] != null) {
				throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
			}
			int existingMax = existingMinMaxFilters[1];//It can be assumed it's not null
			if (existingMax < min) {
				throw new ParameterException(String.format(INVALID_RANGE_MESSAGE_FORMAT, userProvidedPosition, min - 1, axis + 1, existingMax + 1));
			}
			if (existingMax == min) {
				filters.minMaxFilters.remove(axis);
				validateAndAddAbsoluteFilter(userProvidedPosition, filters, axis, min);
			} else {
				existingMinMaxFilters[0] = min;
			}
		}
	}

	private static void validateAndAddMaxFilter(int userProvidedPosition, CoordinateFilters filters, int axis, int max) {
		Integer[] existingMinMaxFilters = filters.minMaxFilters.get(axis);
		if (existingMinMaxFilters == null) {
			if (filters.absoluteFilters.containsKey(axis)) {
				throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
			}
			SortedMap<Integer, int[]> filterGroup = getFilterGroupContainingAxis(filters.relativeFilterGroups, axis);
			if (filterGroup == null) {
				filters.minMaxFilters.put(axis, new Integer[] { null, max });
			} else {
				int smallestAxisInGroup = filterGroup.keySet().iterator().next();
				if (axis == smallestAxisInGroup) {
					filters.minMaxFilters.put(axis, new Integer[] { null, max });
				} else {
					int[] relativeFilter = filterGroup.get(axis);
					axis = smallestAxisInGroup;
					existingMinMaxFilters = filters.minMaxFilters.get(axis);
					int signum = relativeFilter[0];
					if (signum == 1) {
						max = max - relativeFilter[1];
						if (existingMinMaxFilters == null) {
							filters.minMaxFilters.put(axis, new Integer[] { null, max });
						} else {
							if (existingMinMaxFilters[1] != null) {
								throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
							}
							int existingMin = existingMinMaxFilters[0];//It can be assumed it's not null
							if (existingMin > max) {
								throw new ParameterException(String.format(INVALID_RANGE_MESSAGE_FORMAT, userProvidedPosition, existingMin - 1, axis + 1, max + 1));
							}
							if (existingMin == max) {
								filters.minMaxFilters.remove(axis);
								convertToAbsoluteFilters(filters.absoluteFilters, filterGroup, axis, max);
								filters.relativeFilterGroups.remove(filterGroup);
							} else {
								existingMinMaxFilters[1] = max;
							}
						}
					} else {
						int min = relativeFilter[1] - max;
						if (existingMinMaxFilters == null) {
							filters.minMaxFilters.put(axis, new Integer[] { min, null });
						} else {
							if (existingMinMaxFilters[0] != null) {
								throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
							}
							int existingMax = existingMinMaxFilters[1];//It can be assumed it's not null
							if (existingMax < min) {
								throw new ParameterException(String.format(INVALID_RANGE_MESSAGE_FORMAT, userProvidedPosition, min - 1, axis + 1, existingMax + 1));
							}
							if (existingMax == min) {
								filters.minMaxFilters.remove(axis);
								convertToAbsoluteFilters(filters.absoluteFilters, filterGroup, axis, min);
								filters.relativeFilterGroups.remove(filterGroup);
							} else {
								existingMinMaxFilters[0] = min;
							}
						}
					}
				}
			}
		} else {
			if (existingMinMaxFilters[1] != null) {
				throw new ParameterException(String.format(REDUNDANT_OR_INCOMPATIBLE_FILTER_MESSAGE_FORMAT, userProvidedPosition));
			}
			int existingMin = existingMinMaxFilters[0];//It can be assumed it's not null
			if (existingMin > max) {
				throw new ParameterException(String.format(INVALID_RANGE_MESSAGE_FORMAT, userProvidedPosition, existingMin - 1, axis + 1, max + 1));
			}
			if (existingMin == max) {
				filters.minMaxFilters.remove(axis);
				validateAndAddAbsoluteFilter(userProvidedPosition, filters, axis, max);
			} else {
				existingMinMaxFilters[1] = max;
			}
		}
	}
	
}