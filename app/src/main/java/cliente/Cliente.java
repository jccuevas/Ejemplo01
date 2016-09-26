package cliente;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Cliente implements Protocolo, Runnable {

	private Socket mSocket = null;

	private String mAddress = null;
	public void run() {
		String exit = "";
		System.out.println("CLIENTE> Bienvenido");
		do {
			try {
				int status = S_USER;
				boolean exitNow = false;
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));// Entrada
																							// de
																							// usuario
				System.out.print("CLIENTE> Indroduzca la dirección destino");
				System.out.print("[ENTER para dirección por defecto]> ");
				String address = br.readLine();
				if (address.length() == 0) {// Si el usuario no introduce una
											// direcci�n se copia localhost
					address = "localhost";
				}
				mSocket = new Socket(address, TCP_SERVICE_PORT);
				if (mSocket != null) {
					String input = "";
					String output = "";

					// Conexión con la entrada y salida del socket
					DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
					BufferedReader inputStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

					// Se lee el mensaje de bienvenida del servidor
					input = inputStream.readLine();// Permite leer los datos
													// hasta un CRLF
					System.out.println("CLIENTE [RECIBIDO]> " + input);

					do {
						switch (status) {
						case S_USER:// Estado USER
							System.out.print("CLIENTE Introduzca el usuario> ");
							String user = br.readLine();// Se lee el nombre de
														// usuario
							if (user.length() == 0) {// Si no se escribe nada se
														// envía QUIT
								output = QUIT + CRLF;
								exitNow = true;
							} else
								output = USER + SP + user + CRLF;// Comando USER
							break;
						case S_PASS:// Estado PASS
							System.out.print("CLIENTE Introduzca la clave> ");
							String pass = br.readLine();// Se lee la clave
							if (pass.length() == 0) {// Si no se escribe nada se
														// envía QUIT
								output = QUIT + CRLF;
								exitNow = true;
							} else
								output = PASS + SP + pass + CRLF;// Comando
																	// PASS
							break;
						case S_OPER:// Estado OPERACIÓN
							System.out.print("CLIENTE Introduzca la cadena a enviar> ");
							String echo = br.readLine();// Se lee el mensaje de
														// eco
							if (echo.length() == 0) {// Si no se escribe nada se
														// envía QUIT
								output = QUIT + CRLF;
								exitNow = true;
							} else
								output = ECHO + SP + echo + CRLF;// Comando ECHO
							break;

						}
						outputStream.write(output.getBytes());// Se envIan los datos
						outputStream.flush(); // Se fuerza el envIo de todos los datos
										// pendientes

						input = inputStream.readLine();// Se lee la respuesta
														// recibida del servidor
						System.out.println("CLIENTE [RECIBIDO]> " + input);
						// Se comprueba la respuesta del servidor para cambiar de estado o no
						switch (status) {
						case S_USER:
							if (input.startsWith(OK))
								status++;
							break;
						case S_PASS:
							if (input.startsWith(OK))
								status++;
							else
								status = S_USER;
							break;

						}
					} while (!exitNow);

					mSocket.close();// Cierre del socket
				}
			} catch (UnknownHostException e) {// Control de errores basado en
												// excepciones
				System.err.println("CLIENTE [ERROR]> " + e.getMessage());
			} catch (SocketException e) {
				System.err.println("CLIENTE [ERROR]> " + e.getMessage());
			} catch (IOException e) {
				System.err.println("CLIENTE [ERROR]> " + e.getMessage());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));// Entrada
																						// de
																						// usuario
			System.out.print("CLIENTE ¿Volver a realizar una conexión? [S/N]> ");
			try {
				exit = br.readLine();
			} catch (IOException e) {
				exit = "N";
			}
		} while (!exit.equalsIgnoreCase("N"));
		System.out.println("CLIENTE> ¡Pase un buen día!");
	}
}