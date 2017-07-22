function generateComparisonLogic(methods, originalElments, elemIndex, elems) {
	var result = "";
	var nextElemIndex = elemIndex + 1;
	var index = elems.indexOf(originalElments[elemIndex]);
	var isLastElem = elemIndex == originalElments.length - 1;
	var elem = elems[index];
	var methodName = getMethodName(elems);
	var methodInvocation = methodName + '(newGrid, value, ' + originalElments.join(", ") + ', "DIMMENSIONS PLACEHOLDER");';
	result += "protected long " + methodName + "(long" + getArrBrackets(originalElments.length/2) + " newGrid, long value, long " 
		+ originalElments.join(", long ") + ", string dimmensionsPlaceholder) {\n";
	if (index > 0) {
		var indexMinusOne = index - 1;
		var candidates = elems[indexMinusOne];
		var isArrCandidates = typeof candidates !== "string";
		if (!isArrCandidates) {
			var candidate = candidates;
		} else {
			var candidate = candidates[0];
		}
		result += "\tif (" + elem + " < " + candidate + ") {\n";
		
		if (!isLastElem) {
			result += "\t\tvalue = " + generateComparisonLogic(methods, originalElments, nextElemIndex, copyElements(elems));
		} else {
			result += getInnerLogic(elems, 2);
		}
		
		result += "\n" + "\t} else if (" + elem + " > " + candidate + ") {\n";
		
		var elemsCp = copyElements(elems);
		elemsCp[index] = elemsCp[indexMinusOne];
		elemsCp[indexMinusOne] = elem;
		result += "\t\tvalue = " + generateComparisonLogic(methods, originalElments, elemIndex, elemsCp);
		
		result += "\n" + "\t} else {//" + elem + " == " + candidate + "\n";
		
		if (isArrCandidates) {
			candidates.push(elem);
		} else {
			elems[indexMinusOne] = [elems[indexMinusOne], elem];
		}
		elems.splice(index,1);
		if (!isLastElem) {
			result += "\t\tvalue = " + generateComparisonLogic(methods, originalElments, nextElemIndex, elems);
		} else {
			result += getInnerLogic(elems, 2);
		}
		
		result += "\n\t}";
	} else {
		var candidate = "value";
		result += "\tif (" + elem + " < " + candidate + ") {\n";
		
		if (!isLastElem) {
			result += "\t\tvalue = " + generateComparisonLogic(methods, originalElments, nextElemIndex, copyElements(elems));
		} else {
			result += getInnerLogic(elems, 2);
		}
		
		if (elems.length > 1) {
			result += "\n\t} else {//" + elem + " >= " + candidate + "\n";
			
			elems.splice(index,1);
			if (!isLastElem) {
				result += "\t\tvalue = " + generateComparisonLogic(methods, originalElments, nextElemIndex, elems);
			} else {
				result += getInnerLogic(elems, 2);
			}
		}
		
		result += "\n\t}";
	}
	result += "\n\treturn value;";
	result += "\n" + "}";
	methods.splice(0,0,result);
	return methodInvocation;
}

function getArrBrackets(count) {
	var brackets = "";
	for (var i = 0; i < count; i++) {
		brackets += "[]";
	}
	return brackets;
}

function getMethodName(elems) {
	if (!window.methodNameCounter) {
		window.methodNameCounter = 1;
	}
	var counter = window.methodNameCounter;
	window.methodNameCounter++;
	return "sharingLogicMethod" + counter;
}

function ind(level) {
	var indentation = "";
	for (var i = 0; i < level; i++) {
		indentation += "\t";
	}
	return indentation;
}

function copyElements(original) {
	var newArr = [];
	for (var i = 0; i < original.length; i++) {
		var elem = original[i];
		if (typeof elem === "string") {
			newArr.push(elem);
		} else {
			newArr.push(copyElements(elem));
		}
	}
	return newArr;
}

function countFrom(elems, index) {
	var count = elems.length - index;
	for (var i = index; i < elems.length; i++) {
		var elem = elems[i];
		if (typeof elem !== "string") {
			count += elem.length - 1;
		}
	}
	return count;
}

function getElementsFrom(elems, index) {
	var subElements = [];
	for (var i = index; i < elems.length; i++) {
		var elem = elems[i];
		if (typeof elem !== "string") {
			for (var j = 0; j < elem.length; j++) {
				subElements.push(elem[j]);
			}
		} else {
			subElements.push(elem);
		}
	}
	return subElements;
}

function getInnerLogic(elems, indent) {
	var indentation = ind(indent);
	var lb = "\n" + indentation;
	var result = indentation + "// " + getElementsComparison(elems) + lb;
	result += "long toShare, share;" + lb;
	result += "int shareCount;";
	for (var i = 0; i < elems.length; i++) {
		var el = elems[i];
		if (typeof el !== "string") {
			el = el[0];
		}
		result += lb + "toShare = value - " + el + ";" + lb;
		result += "shareCount = " + (countFrom(elems, i) + 1) + ";" + lb;
		result += "share = toShare/shareCount;" + lb;
		result += "if (share != 0) {" + lb;
		result += "	changed = true;" + lb;
		result += "	value = value - toShare + toShare%shareCount + share;" + lb;
		var subElements = getElementsFrom(elems, i);
		for (var j = 0; j < subElements.length; j++) {
			var subEl = subElements[j];
			result += "	add" + subEl[0].toUpperCase() + subEl.substr(1) + "(newGrid, \"DIMMENSIONS PLACEHOLDER\", share);" + lb;
		}
		result += "}";
	}
	return result;
}

function getElementsComparison(elems) {
	var result = "value > ";
	var elem = elems[0];
	if (typeof elem === "string") {
		result += elem;
	} else {
		result += elem[0]; 
		for (var i = 1; i < elem.length; i++) {
			var el = elem[i];
			result += " = " + el;
		}
	}
	for (var i = 1; i < elems.length; i++) {
		result += " > ";
		var elem = elems[i];
		if (typeof elem === "string") {
			result += elem;
		} else {
			result += elem[0]; 
			for (var j = 1; j < elem.length; j++) {
				var el = elem[j];
				result += " = " + el;
			}
		}
	}
	return result;
}


























