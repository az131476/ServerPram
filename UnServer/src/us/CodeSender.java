package us;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class CodeSender {
	public boolean send(Socket socket, String msg) {
		try {
			PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
			pw.println(msg);
		} catch (IOException e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}
}
