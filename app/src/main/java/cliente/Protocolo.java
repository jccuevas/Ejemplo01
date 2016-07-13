package cliente;

public interface Protocolo {

	public static final int TCP_SERVICE_PORT = 6000;
	
	public static final String USER = "USER";
	public static final String PASS = "PASS";
	public static final String ECHO = "ECHO";
	public static final String QUIT = "QUIT";
	public static final String OK   = "OK";
	public static final String ERROR = "ERROR";
	public static final String CRLF = "\r\n";
	public static final String SP = " ";
	
	public static final int S_USER=0; 
	public static final int S_PASS=1;
	public static final int S_OPER=2; 
	
	public static final String USERNAME = "USER";
	public static final String PASSWORD = "12345";
}
