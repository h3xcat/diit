/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package invisibleinktoolkit;

import invisibleinktoolkit.algorithms.*;
import invisibleinktoolkit.filters.*;
import invisibleinktoolkit.stego.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.HashMap; 
import java.util.Map; 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.lang.StringBuffer;

public class CommandLineInterface{    
    public static void main(final String[] args){
        if (args.length < 2){
            printHelp();
            System.exit(1);
        }else{
            Map<String, String> params = new HashMap<String, String>();
            params.put("action", args[0]);
            params.put("algorithm", args[1]);
            
            fillDefaults(params);

            for (int i = 2; i < args.length; i++) {
                String fullparam = args[i].toLowerCase();
                String param = fullparam.substring(2);
                
                switch(fullparam){
                    case "--password":
                    case "--startbits":
                    case "--endbits":
                    case "--moveaway":
                    case "--initshots":
                    case "--shotsincrease":
                    case "--shotrange":
                    case "--filter":
                    case "--filterStart":
                    case "--filterEnd":
                    case "--infile":
                    case "--outfile":
                    case "--msg":
                        params.put(param, args[i+1]);
                        i++; // Skip over next argument
                        break;

                    case "--matchlsb":
                    case "--debug":
                        params.put(param, "1");
                        break;
                    case "--help":
                        printHelp();
                        System.exit(0);
                    default:
                        System.err.println("Unknown parameter: "+fullparam);
                        System.exit(1);
                }
            }

            decode(params);
        }
    }
    
    private static void printHelp(){
        System.out.println("USAGE: diit-cli <action> <algorithm> [<param>...]");
        
        System.out.println("\nAction:");
        System.out.println("\tdecode");
        System.out.println("\tencode");
        
        
        System.out.println("\nAlgorithms:");
        System.out.println("\tHideSeek");
        System.out.println("\tFilterFirst");
        System.out.println("\tDynamicFilterFirst");
        System.out.println("\tDynamicBattleSteg");
        System.out.println("\tBlindHide");
        System.out.println("\tBattleSteg");

        System.out.println("\nParamaters:");
        System.out.println("\t--password <str>");
        System.out.println("\t--infile <path>");
        System.out.println("\t--outfile <path>");
        System.out.println("\t--msg <path>");

        System.out.println("\nAlgorithm Paramaters:");
        System.out.println("\t--startbits <num>");
        System.out.println("\t--endbits <num>");
        System.out.println("\t--matchlsb");
        System.out.println("\t--moveaway <num>");
        System.out.println("\t--initshots <num>");
        System.out.println("\t--shotsincrease <num>");
        System.out.println("\t--shotrange <num>");
        System.out.println("\t--filter <filter>");

        System.out.println("\nFilters:");
        System.out.println("\tLaplace");
        System.out.println("\tPrewitt");
        System.out.println("\tSobel");

        System.out.println("\nFilter Paramaters:");
        System.out.println("\t--filterStart <num>");
        System.out.println("\t--filterEnd <num>");
    }
    
    private static void fillDefaults(Map<String, String> params){
        
        params.put("password",      "");
        params.put("moveaway",      "-1");
        params.put("initshots",     "-1");
        params.put("shotsincrease", "-1");
        params.put("shotrange",     "-1");
        params.put("filterStart",   "-1");
        params.put("filterEnd",     "-1");

        switch(params.get("algorithm").toLowerCase()){
            case "hideseek":
                params.put("algorithm",     "HideSeek");
                break;

            case "filterfirst":
                params.put("algorithm",     "FilterFirst");
                params.put("moveaway",      "10");
                params.put("initshots",     "5");
                params.put("shotsincrease", "2");
                params.put("shotrange",     "5");
                params.put("filter",        "Laplace");
                params.put("filterStart",   "1");
                params.put("filterEnd",     "8");
                break;

            case "dynamicfilterfirst":
                params.put("algorithm",     "DynamicFilterFirst");
                params.put("moveaway",      "10");
                params.put("initshots",     "5");
                params.put("shotsincrease", "2");
                params.put("shotrange",     "5");
                params.put("filter",        "Laplace");
                params.put("filterStart",   "1");
                params.put("filterEnd",     "8");
                break;

            case "dynamicbattlesteg":
                params.put("algorithm",     "DynamicBattleSteg");
                params.put("moveaway",      "10");
                params.put("initshots",     "5");
                params.put("shotsincrease", "2");
                params.put("shotrange",     "1");
                params.put("filter",        "Laplace");
                params.put("filterStart",   "1");
                params.put("filterEnd",     "8");
                break;
                
            
            case "blindhide":
                params.put("algorithm",     "BlindHide");
                break;

            case "battlesteg":
                params.put("algorithm",     "BattleSteg");
                params.put("moveaway",      "10");
                params.put("initshots",     "5");
                params.put("shotsincrease", "2");
                params.put("shotrange",     "1");
                params.put("filter",        "Laplace");
                params.put("filterStart",   "1");
                params.put("filterEnd",     "8");
                break;
            
            default:
                System.err.println("Invalid algorithm");
                System.exit(1);
        }
    }

    
    /**
     * Converts a byte string to a String.
     *
     * @param b The bytestring string to convert.
     * @return The string as a String.
     */
    private static String toHexString ( byte[] bytestring )
    {
        StringBuffer sb = new StringBuffer( bytestring.length * 2 );
        for ( int i = 0; i < bytestring.length; i++ )
        {
            // look up high nibble character
            sb.append( hexChar [( bytestring[i] & 0xf0 ) >>> 4] );
            
            // look up low nibble character
            sb.append( hexChar [bytestring[i] & 0x0f] );
        }
        return sb.toString();
    }
    /**
     * An array of hexadecimal characters.
     */
    private static char[] hexChar = {
        '0' , '1' , '2' , '3' ,
        '4' , '5' , '6' , '7' ,
        '8' , '9' , 'a' , 'b' ,
        'c' , 'd' , 'e' , 'f'};

    /**
     * Gets the password currently residing in the password field.
     * <P>
     * This does some calculations to return the password as a long value.
     * The function first gets the password value in the box, converts it
     * to a byte string.  This byte string is then digested into a MD5 value.
     * The byte string of the digest is converted back into a string, which
     * is then reduced to 15 characters.  Then the MD5 string is finally
     * converted to a long value.  This computation is done to make the 
     * password as unique as possible, and not necessarily short.
     *
     * @return A numerical version of the password.
     */
    private static long getPassword( String pass ){
        try {
            byte[] passbytes = pass.getBytes();
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(passbytes);
            byte[] md5sum = digest.digest();
            String smd5sum = toHexString(md5sum);
            smd5sum = smd5sum.substring(0,15);
            return Long.parseLong(smd5sum, 16);
        } catch (Exception e) {
            return 0;
        }
    }

    public static void decode(Map<String, String> params){
        String algorithm = params.get("algorithm");
        
        try{
            String outPath = "./.decode.tmp";

            StegoAlgorithm alg = (StegoAlgorithm) Class.forName("invisibleinktoolkit.algorithms." + algorithm).newInstance();


            int startbits = alg.getStartBits();
            int endbits = alg.getEndBits();
            int moveaway = Integer.parseInt(params.get("moveaway"));
            int initshots = Integer.parseInt(params.get("initshots"));
            int shotsincrease = Integer.parseInt(params.get("shotsincrease"));
            int shotrange = Integer.parseInt(params.get("shotrange"));
            String filter = params.get("filter");
            int filterStart = Integer.parseInt(params.get("filterStart"));
            int filterEnd = Integer.parseInt(params.get("filterEnd"));
            String password = params.get("password");
            boolean matchlsb = alg.getMatch();

            if(params.containsKey("startbits"))
            {
                startbits = Integer.parseInt(params.get("startbits"));
                alg.setStartBits(startbits);
            }

            if(params.containsKey("endbits"))
            {
                endbits = Integer.parseInt(params.get("endbits"));
                alg.setEndBits(endbits);
            }

            if(params.containsKey("matchlsb"))
            {
                matchlsb = true;
                alg.setMatch(matchlsb);
            }




            Filter filterObj = null;
            if(filter != null)
            {
                try{
                    filterObj = (Filter)Class.forName("invisibleinktoolkit.filters." + filter).newInstance();
                    filterObj.setStartRange(filterStart);
                    filterObj.setEndRange(filterEnd);
                }catch(Exception exp){
                    exp.printStackTrace();
                    System.exit(1);
                }
            }

            if(params.containsKey("debug"))
            {
                System.err.println("algorithm:\t\t\t" + algorithm);
                System.err.println("password:\t\t" + password + " (" + getPassword(password) + ")");
                System.err.println("startbits:\t\t\t" + startbits);
                System.err.println("endbits:\t\t\t" + endbits);
                System.err.println("matchlsb:\t\t\t" + matchlsb);
                System.err.println("moveaway:\t\t" + moveaway);
                System.err.println("initshots:\t\t" + initshots);
                System.err.println("shotsincrease:\t\t" + shotsincrease);
                System.err.println("shotrange:\t\t" + shotrange);
                System.err.println("filter:\t\t\t" + filter);
                System.err.println("filterStart:\t\t" + filterStart);
                System.err.println("filterEnd:\t\t" + filterEnd);
            }

            switch(algorithm){
                case "HideSeek":
                case "BlindHide":
                    break;
        
                case "DynamicFilterFirst":
                case "FilterFirst":
                    Filterable falg = (Filterable)alg;
                    falg.setFilter(filterObj);
                    break;

                case "DynamicBattleSteg":
                case "BattleSteg":

                    BattleSteg balg = (BattleSteg)alg;
                    balg.setFilter(filterObj);    
                    balg.setMoveAway(moveaway);
                    balg.setRange(shotrange);
                    balg.setIncreaseShots(shotsincrease);
                    balg.setInitialShots(initshots);
                    break;
            }
            
            OutputStream ostream = null;
            InputStream istream = null;
                
            try {

                if (params.containsKey("infile"))
                    istream = new FileInputStream(params.get("infile"));
                else
                    istream = System.in;

                if (params.containsKey("outfile"))
                    ostream = new FileOutputStream(params.get("outfile"));
                else

                    ostream = new PrintStream(System.out);


                StegoImage img = new StegoImage(ImageIO.read(istream));

                if (params.get("action").equals("decode")){
                    alg.decode(img, getPassword(password), ostream);
                }else if(params.get("action").equals("encode")) {
                    CoverImage cimg = new CoverImage(istream);
                    InsertableMessage msg = new InsertableMessage(params.get("msg"));
                    alg.encode(msg, cimg, getPassword(password));
                }
                istream.close();
                ostream.close();
                
                System.err.println();


                System.exit(0);
            }
            catch (NoMessageException e)
            {
                if(istream!=null)
                    istream.close();
                
                if(ostream!=null)
                    ostream.close();
                
                
                System.err.println("No message");
                System.exit(1);
            }
                
        }
        catch (IOException exp)
        {
            exp.printStackTrace();
            System.exit(1);
        }
        catch(Exception exp){
            exp.printStackTrace();
            System.exit(1);
        }
    }
}