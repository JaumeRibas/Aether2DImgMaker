package caimgmaker.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

// copied from https://jcommander.org/
public class PositiveIntegerValidator implements IParameterValidator {
	public void validate(String name, String value)
			throws ParameterException {
		int n = Integer.parseInt(value);
		if (n < 0) {
			throw new ParameterException("Parameter " + name + " should be positive (found " + value +")");
		}
	}
}