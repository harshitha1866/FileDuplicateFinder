import java.util.*;
import java.nio.file.*;
import java.io.IOException;
public class Main{
    public static void main(String[] args) throws IOException
    {
        Scanner sc = new Scanner(System.in);
        String given_path = sc.nextLine();
        Path path = Paths.get(given_path);
        if(Files.exists(path)){
            Files.walkFileTree(path, new FileVisitorClass());
            FileVisitorClass fvc = new FileVisitorClass();
            fvc.printDuplicateFolders();
        }
        sc.close();
    }
}