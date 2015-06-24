/**
 * 
 */
package nl.xillio.sharedlibrary.license;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import nl.xillio.sharedlibrary.license.License.LicenseType;
import nl.xillio.sharedlibrary.license.License.SoftwareModule;

import org.apache.commons.io.FileUtils;

/**
 * @author Marijn van der Zaag
 *
 */
public class LicenseGenerator {
    String privatekeypath;
    private Scanner s = new Scanner(System.in);
	
	public LicenseGenerator(String privatekeypath) {
	    this.privatekeypath = privatekeypath;
	}
	
	public void start() {
	    boolean more = true;
	    
	    while(more) {
    	    // 1. Load & verify private key
            System.out.println("Step 1/5: Loading private key...");
            
            PrivateKey key = (PrivateKey) loadPrivateKey(privatekeypath);
            if(key == null)
                return;
            
            
            // 2. Ask user for input and create license
            System.out.println("\nStep 2/5: Enter license information");
            License license = createLicense();
            
            
            // 3. Sign license
            System.out.println("\nStep 3/5: Generating license...");
            license.Sign(key);
            
            // 4. Output license
            System.out.println("\nStep 4/5: Preview license");
            System.out.println("------------------------------------------------------");
            System.out.println(license);
            System.out.println("\n\n------------------------------------------------------");
            
            // 5. Save license
            System.out.println("\nStep 5/5: Saving license...");
            saveLicense(license);
            
            // 6. Add more?
            System.out.print("\nCreate another license? [y/n] ");
            String t = "";
            while(!(t = s.nextLine()).matches("[yn]"));
            if (!t.equals("y"))
                more = false;
	    }
	    
	    
	    System.out.println("\nAll done.");
	}
	
	private PrivateKey loadPrivateKey(String privatekeypath) {
	    File f= new File(privatekeypath);
	    if(!f.exists() || !f.isFile()) {
	        System.out.println("Private key file not found: " + privatekeypath);
	        return null;
	    }
	    
        byte[] privateKeyBytes = null;
        try {
            privateKeyBytes = FileUtils.readFileToByteArray(f);
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(privateKeyBytes);            
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey key = kf.generatePrivate(pkcs8);
                        
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	
	private License createLicense() {       
	    
	    
	    // 1. License type
	    System.out.println("Select license type:\n  1) Internal\n  2) Developer\n  3) Production");
        LicenseType licensetype = null;
	    String t = "";
	    while(!(t = s.nextLine()).matches("[123]"));
	    if (t.equals("1"))
	        licensetype = LicenseType.INTERNAL;
	    else if (t.equals("2"))
	        licensetype = LicenseType.DEVELOPER;
	    else if (t.equals("3"))
	        licensetype = LicenseType.PRODUCTION;
    	
	    
	    // 2. Company name
	    String companyname = "";
	    if (licensetype == LicenseType.INTERNAL) {
	        System.out.println("\nCompany name: Xillio");
	        companyname = "Xillio";
	    } else {
            System.out.print("\nCompany name: ");
             companyname = s.nextLine();
	    }
        
        
        // 3. Company name
        System.out.print("\nContact name: ");
        String contactname = s.nextLine();
        
        // 4. Company name
        System.out.print("\nContact email: ");
        String contactemail = s.nextLine();
        
        // 5. Expiration date
        System.out.print("\nExpiration date in format yyyy-mm-dd: ");
        String expdatestring = s.nextLine();
        while (!isValidDate(expdatestring)) {
            System.out.print("\nIncorrectly formatted date. Please try again (yyyy-mm-dd): ");
            expdatestring = s.nextLine();
        }
        String expirationdate = expdatestring;
        
        // 6. Modules
        List<SoftwareModule> modules= new LinkedList<>();
        modules.add(SoftwareModule.RUNTIME);
        if(licensetype == LicenseType.INTERNAL) {
            modules.add(SoftwareModule.IDE);
            modules.add(SoftwareModule.SERVER);
        } else if (licensetype == LicenseType.DEVELOPER) {
            modules.add(SoftwareModule.IDE);
        } else if (licensetype == LicenseType.PRODUCTION) {
            System.out.print("\nEnable software module 'IDE'? [y/n] ");
            while(!(t = s.nextLine()).matches("[yn]"));
            if (t.equals("y"))
                modules.add(SoftwareModule.IDE);
            
            System.out.print("\nEnable software module 'Server'? [y/n] ");
            while(!(t = s.nextLine()).matches("[yn]"));
            if (t.equals("y"))
                modules.add(SoftwareModule.SERVER);
        }
        
        ///////////////////////////////////////////
        String xml = "<?xml version=\"1.0\"?>"
+"<license vendor=\"Xillio\" version=\"2.0\">"
+"    <details>"
+"        <licensetype>" + (licensetype == LicenseType.DEVELOPER ? "Developer" : (licensetype == LicenseType.INTERNAL ? "Internal" : "Production")) + "</licensetype>"
+"        <company>"+companyname+"</company>"
+"        <contactname>"+contactname+"</contactname>"
+"        <contactemail>"+contactemail+"</contactemail>"
+"        <dateissued>"+dateFormat.format(new Date())+"</dateissued>"
+"        <dateexpires>"+expirationdate+"</dateexpires>"
+"        <modules>"
+(modules.contains(SoftwareModule.RUNTIME) ? "            <module>Runtime</module>" : "")
+(modules.contains(SoftwareModule.IDE) ? "            <module>IDE</module>" : "")
+(modules.contains(SoftwareModule.SERVER) ? "            <module>Server</module>" : "")
+"        </modules>"
+"    </details>"
+"    <signature><![CDATA[]]></signature>"
+" </license>";
        
        
        return new License(xml);
	}
	
	private void saveLicense(License license) {
	    int i = 1;
        String filename = "License - " + license.getLicenseName();
        File f = new File(filename + ".xml");
        while((f).exists()) {
            f = new File(filename + "(" + i++ + ").xml");
        }
        String xml = license.toString();
        
        try {
            PrintStream out = new PrintStream(new FileOutputStream(f, false), true, "UTF-8");
            out.append(xml);
            out.flush();
            out.close();
            
            System.out.println("License written to file: " + f.getPath());
        } catch (Exception e) {}
	}
		
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private boolean isValidDate(String date) {
	    try {
    	    Date d = dateFormat.parse(date);
    	    if(d.after(new Date()))
    	        return true;
	    } catch(Exception e) {}
	    return false;
	}
}



