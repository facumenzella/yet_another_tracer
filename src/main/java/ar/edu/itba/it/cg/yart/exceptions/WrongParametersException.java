package ar.edu.itba.it.cg.yart.exceptions;

@SuppressWarnings("serial")
public class WrongParametersException extends RuntimeException{
	
	public WrongParametersException(final String message) {
		super(message);
	}
}
