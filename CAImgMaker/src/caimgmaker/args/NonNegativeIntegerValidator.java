package caimgmaker.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class NonNegativeIntegerValidator implements IParameterValidator {
	
	@Override
	public void validate(String name, String value)
			throws ParameterException {
		int n = Integer.parseInt(value);
		if (n < 0) {
			throw new ParameterException("The value of " + name + " cannot be negative (found " + value +").");
		}
	}
	
}