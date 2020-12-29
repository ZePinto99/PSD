import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Data {
    final public String roomString;
    final public String message;

    public Data(String roomCode, String message){
        this.roomString = roomCode;
        this.message = message;
    }

    public byte[] encode(){
        byte[] roomCode = roomCodeFrom(roomString);
        ByteBuffer buf = ByteBuffer.allocate(roomCode.length + 4 + message.length());
        buf.put(roomCode);

        buf.putInt(message.length());
        buf.put(message.getBytes());

        return buf.array();
    }

    public static Data decode(byte[] data){
        ByteBuffer buf = ByteBuffer.wrap(data);
        int roomCodeLen = buf.getInt();
        byte[] room = new byte[roomCodeLen];
        buf.get(room);
        int messageLen = buf.getInt();
        byte[] message = new byte[messageLen];
        buf.get(message);
        return new Data(new String(room, StandardCharsets.UTF_8), new String(message, StandardCharsets.UTF_8));
    }

    public static byte[] roomCodeFrom(String roomString){
        ByteBuffer buf = ByteBuffer.allocate(4 + roomString.length());
        buf.putInt(roomString.length());
        buf.put(roomString.getBytes());
        return buf.array();
    }
}
