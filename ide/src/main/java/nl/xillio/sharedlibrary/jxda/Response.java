package nl.xillio.sharedlibrary.jxda;

public class Response {

	public int responseCode;
	public String responseMessage;
	public String content;

	@Override
	public String toString() {

		String code = new Integer(responseCode).toString();

		return "Response: " + code + " " + responseMessage + "\nContent:  " + content;
	}
}
