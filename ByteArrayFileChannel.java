import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteArrayFileChannel {
    private FileInputStream input;
    private int arraysize;
    private ByteBuffer buff;
    private byte[] array;
    public ByteArrayFileChannel(String file, int arraysize) throws IOException{
        this.input = new FileInputStream(file);
        this.arraysize = arraysize;
        this.buff = ByteBuffer.allocate(this.arraysize);
    }

    public int read() throws IOException, FileNotFoundException {
        FileChannel file_Channel = input.getChannel();
        int bytes = file_Channel.read(buff);
        if(bytes!=-1){
            array = new byte[bytes];
            buff.flip();
            buff.get(array);
            buff.clear();
            return bytes;
        }
        return -1;
    }

    public byte[] getArray() {
        return array;
    }

    public void close() throws IOException {
        input.close();
        array = null;
    }

}
