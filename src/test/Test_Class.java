package test;

public class Test_Class extends Test_Abstract_Class {

	private String specific_string;

	public Test_Class() {
		this.specific_string = "specific_string";
	}

	@Override
	public void print() {
		System.out.println(this.specific_string);
	}

}
