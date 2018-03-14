package us;

public class CodeSend {
	public boolean send(ASokcetType aSokcetType, String code) {
		SocketServer.socketServerHashMap.get(aSokcetType).sendSocketMsg(code);
		return true;
	}
}
