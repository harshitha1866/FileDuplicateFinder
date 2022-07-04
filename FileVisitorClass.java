import java.io.*;
import java.math.BigInteger;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.*;
import java.util.*;

public class FileVisitorClass implements FileVisitor<Path>{
    // file_hash to store hash values of files
    // dir_hash to store hash values of folders
    HashMap<BigInteger, Path> file_hash = new HashMap<>();
    static HashMap<String, BigInteger> dir_hash = new HashMap<>();

    public void printDuplicateFolders(){
        // to list the keys with same values
        Map<BigInteger, ArrayList<String>> reverseMap = new HashMap<>();

        for (Map.Entry<String, BigInteger> entry : dir_hash.entrySet()) {
            if (!reverseMap.containsKey(entry.getValue())) {
                reverseMap.put(entry.getValue(), new ArrayList<>());
            }
            ArrayList<String> keys = reverseMap.get(entry.getValue());
            keys.add(entry.getKey());
            reverseMap.put(entry.getValue(), keys);
        }
        //to print duplicate folders
        for (Map.Entry<BigInteger, ArrayList<String>> entry : reverseMap.entrySet())
        {
            if(entry.getValue().size()>1)
                for(String s : entry.getValue())
                    System.out.println("Duplicate folder : "+s);
        }
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes br) throws IOException
    {
        // to avoid Recycle bin
        if(dir.toString().contains("$Recycle.Bin") || dir.toString().contains("$RECYCLE.BIN"))
        return FileVisitResult.SKIP_SUBTREE;
        dir_hash.put(dir.toString(), BigInteger.valueOf(0));
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes br) throws IOException
    {
        try{
            //to read file to byteArray
            ByteArrayFileChannel reader = new ByteArrayFileChannel(file.toString(), 1024);
            while(reader.read()!=-1);
            byte[] fileData = reader.getArray();
            reader.close();
            // checksum 
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] msg;
            if(fileData==null){
                //readAllBytes
                msg = md.digest();
            }
            else{
                msg = md.digest(fileData);
            }
            
            BigInteger bignum = new BigInteger(1, msg);
            String pathName = file.toString();
            int dirIndex = pathName.lastIndexOf('\\');
            String dirPath=pathName;
            if(dirIndex==-1)
                dirPath = pathName;
            else
                dirPath = pathName.substring(0, dirIndex);
                
            dir_hash.put(dirPath, dir_hash.get(dirPath).add(bignum));

            //to print duplicate files in each folder
            if(file_hash.containsKey(bignum)){
                System.out.println("Duplicate File--> "+ file);
                System.out.println("Copy of --> "+file_hash.get(bignum)+"\n");
            }
            else{
                file_hash.put(bignum, file);
            }
        }
        catch(NoSuchAlgorithmException e){
            System.out.println("Failed to hash...");
        }
        catch(NullPointerException exe){
            System.out.println("NullPointer Exception..");
        }
        catch(FileNotFoundException fe){
            System.out.println("Couldn't access the file..");
        }
        //System.out.println(file_hash);
        return FileVisitResult.CONTINUE;
     }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException
    {
        System.out.println("Failed to Visit : "+ file);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException
    {
        // to find its parent directory
        String pathName = dir.toString();
        int dirIndex = pathName.lastIndexOf('\\');
        String dirPath=pathName;
        if(dirIndex==-1)
            dirPath = pathName;
        else
            dirPath = pathName.substring(0, dirIndex);
        // add the big integer values if not null
        if(dir_hash.get(dir.toString())!=null && dir_hash.get(dirPath)!=null)
            dir_hash.put(dirPath, dir_hash.get(dirPath).add(dir_hash.get(dir.toString())));
        
        return FileVisitResult.CONTINUE;
    }
}
