package diktya;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import ithakimodem.*;

public class virtualModem {
	
	public void welcomeMessage(Modem modem) {
		char response;
		int k;
		for(;;) {
			try {
				k = modem.read();
				if (k == -1) {
					System.out.println("Connection closed.");
					break;
				}
				response=(char)k;
				System.out.print(response);
			}catch(Exception e) {
				System.out.println("Exception in writting method: " + e);
			}
		}
	}
	
 	public void echoPacket(Modem modem) {
 		String response = "";
 		String request = "E5443\r";
 		int k, numOfPackets=1;
 		long startTime=0, endTime=0, duration=0;
 		
 		try {
 			File textFile = new File("C:\\Users\\user\\Desktop\\Åñãáóßá äßêôõá\\ÔÑÉÔÇ_ÓÕÍÏÄÏÓ\\ECHO_DURATION.txt");
	        FileOutputStream os = new FileOutputStream(textFile);
	        Writer w = new OutputStreamWriter(os);
	        
	        File textFile2 = new File("C:\\Users\\user\\Desktop\\Åñãáóßá äßêôõá\\ÔÑÉÔÇ_ÓÕÍÏÄÏÓ\\ECHO.txt");
	        FileOutputStream os2 = new FileOutputStream(textFile2);
	        Writer w2 = new OutputStreamWriter(os2);
	        
 			//writting request code for the echoPackets
 			do{
 				response = "";
 				modem.write(request.getBytes());
 				System.out.println("Bytes writed succesfuly");
 				startTime = System.currentTimeMillis();
 				System.out.println("Current time in milliseconds: " + startTime);
 				//reading the response from the modem for the echoPackets
 				for(int x=1; ; x++) {
 					k = modem.read();
 					if (k == -1) {
 						System.out.println("Connection closed.");
 						break;
 					}
 					response+=(char)k;
 					if(response.indexOf("PSTOP") != -1) {
 						w2.write("\n");
 						System.out.println("Packet is here.");
 						System.out.println(response);
 						endTime = System.currentTimeMillis();
 						System.out.println("End time in milliseconds: " + endTime);
 						break;
 					}
 					if(x>=19 && x<=26) {
 						w2.write((int)k);
 					}
 				}
 				//w2.write(response + "\n");
 				long x = endTime - startTime;
 				w.write((int)x + "\n");
 				duration += x;
 				System.out.println("Duration of " + numOfPackets + " packet is " + x);
 				numOfPackets += 1;
 			}while(duration<300000);
 			w.write(numOfPackets);
 			w2.close();
 			w.close();
 		}catch(Exception e) {
 			System.out.println(e);
 		}
 	}
 	
 	public void image(Modem modem) {
 		String request = "M5976\r";
 		int response;
 		BufferedImage bufferedImage=null;
 		InputStream in=null;
 		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
 		try {
 	 		modem.write(request.getBytes()); 		
 	 		for(;;) {
 	 			response = modem.read();
 	 			if(response == -1) {
 	 				System.out.println("Connection clossed.");
 	 				break;
 	 			}
 	 			byteArray.write(response);
 	 			System.out.println(byteArray.size());
 	 		}
 	 		
 	 		//convert byte array output stream to byte array
 	 		byte[] c = byteArray.toByteArray();
 	 		in = new ByteArrayInputStream(c);
			bufferedImage = ImageIO.read(in);
			//saving the image in folder
			ImageIO.write(bufferedImage, "jpg", new File("C:\\Users\\user\\Desktop\\IMAGE_WITH ERRORS.jpg"));
			//convert buffered image to image icon
 		}catch(Exception e) {
 			System.out.println(e);
 		}
 		finally {
            // Close the file.
            try {
            	in.close();
            	byteArray.close();
            }
            catch (Exception e) {
                System.err.println(e);
            }
        }
 	} 	
 	
 	public void errorARQ(Modem modem) {
 		String resultACK = "Q8973\r";
 		String resultNACK = "R8757\r";
 		int[] fcs = new int[3];
 		int[] response = new int[16];
 		int numOfACK = 0;
 		int numOfNACK = 0;
 		int numNACK = 0;
 		long endTime=0;
 		int x=0;
 		String s = "";
 		
 		try {
 			File textFile = new File("C:\\Users\\user\\Desktop\\Åñãáóßá äßêôõá\\ÔÑÉÔÇ_ÓÕÍÏÄÏÓ\\ARQ_DURATION1.txt");
 			FileOutputStream is = new FileOutputStream(textFile);
            Writer w = new OutputStreamWriter(is);
	        
            File textFile2 = new File("C:\\Users\\user\\Desktop\\Åñãáóßá äßêôõá\\ÔÑÉÔÇ_ÓÕÍÏÄÏÓ\\ARQ_numOfNACKForEachPacket1.txt");
 			FileOutputStream is2 = new FileOutputStream(textFile2);
            Writer w2 = new OutputStreamWriter(is2);
            
            File textFile3 = new File("C:\\Users\\user\\Desktop\\Åñãáóßá äßêôõá\\ÔÑÉÔÇ_ÓÕÍÏÄÏÓ\\ARQ1.txt");
 			FileOutputStream is3 = new FileOutputStream(textFile3);
            Writer w3 = new OutputStreamWriter(is3);
            
 			//sending the request for first time
	 		modem.write(resultACK.getBytes());
	 		long startTime = System.currentTimeMillis();
	 		long duration = startTime + 300000;
 			do {
 				s="";
 				int responseElements=0;
 		 		int fcsElements=0;
 	 	 		for(int k=1; k<=58; k++) {
 	 	 			//reading the bytes
 	 	 			x=modem.read();
 	 	 			
 	 	 			//saving the <XXXXXXXXXXXXXXXX> message in the array: response[]
 	 	 			if(k>=32 && k <=47) {
 	 	 				response[responseElements] = x;
 	 	 				responseElements++;
 	 	 			}
 	 	 			//saving the FCS in the array: fcs[]
 	 	 			if(k>=50 && k<=52) {
 	 	 				/*convert the receiving byte in ACSII system 
 	 	 				 * which in this case represents the decimal number for FCS
 	 	 				 */
 	 	 				fcs[fcsElements] = x-48;
 	 	 				fcsElements++;
 	 	 			}
 	 	 			if(k>=19 && k<=26) {
 	 	 	 			s = s + (char)x;
 	 	 			}
 	 	 			System.out.print((char)x);
 	 	 		}
 	 	 		endTime = System.currentTimeMillis();
 	 	 		long dur = endTime - startTime;
 	 	 		if(x==-1) {
	 	 			System.out.println("Connection closed.");
	 	 			break;
	 	 		}
 	 	 		//calculating the FCS with the XOR operator
 	 	 		int fcs2=response[0];
 	 	 		for(int l=1; l<16; l++) {
 	 	 			fcs2 = fcs2^response[l];
 	 	 		}
 	 	 		/*converting the calculated FCS to numbers that 
 	 	 		 * will be able to be compared with each element of the array fcs[].
 	 	 		 * For example FCS=031. 
 	 	 		 * x1=031/100=0, x2=(031-0)/10=3, x3=031-0-(3*10)=1
 	 	 		 * NOTE: The division keeps the integer part of the result
 	 	 		 */
 	 	 		int x1 = fcs2/100;
 	 	 		int x2 = (fcs2-(x1*100))/10;
 	 	 		int x3 = fcs2-(x1*100)-(x2*10);

 	 	 		//compering the numbers x1,x2,x3 with the elements fcs[0], fcs[1], fcs[2]
 	 	 		if(x1 == fcs[0] && x2 == fcs[1] && x3 == fcs[2]) {
 	 	 			w.write((int)dur + "\n");
 	 	 			if(numNACK>=1) {
 	 	 				w2.write(numNACK + ", ");
 	 	 			}
 	 	 			w3.write(s + "\n");
 	 	 			numNACK=0;
 	 	 			numOfACK ++;
 	 	 			System.out.println("ACK.     Duration: " + dur + "     number of ACK: " + numOfACK);
 	 	 			modem.write(resultACK.getBytes());
 	 	 			startTime = System.currentTimeMillis();
 	 	 			continue;
 	 	 		}
 	 	 		else if (x1 != fcs[0] || x2 != fcs[1] || x3 != fcs[2]) {
 	 	 			numOfNACK ++;
 	 	 			numNACK++;
 	 	 			System.out.println("NACK.     Duration: " + dur + "     number of NACK: " + numOfNACK);
 	 	 			modem.write(resultNACK.getBytes());
 	 	 			continue;
 	 	 		}
 	 	 		
 			}while(System.currentTimeMillis() <= duration);
	 		System.out.println("Number of ACK: " + numOfACK);
	 	 	System.out.println("Number of NACK: " + numOfNACK);
	 	 	w.write("Number of ACK: " + numOfACK + "\nNumber of NACK: " + numOfNACK);
	 	 	w.close();
	 	 	w2.close();
	 	 	w3.close();
 		}catch(Exception e) {
 			System.out.println(e);
 		}
 		
 	}
 	
 	public void gps(Modem modem) {
 		String request = "P7496R=1100061\r";
 		int response;
 		
 		modem.write(request.getBytes());
 		try {
 			for(;;) {
 	 			response=modem.read();
 	 			if(response == -1) {
 	 				System.out.println("Connection closed.");
 	 				break;
 	 			}
 	 			System.out.print((char)response);
 	 		}
 		}catch(Exception e) {
 			System.out.println(e);
 		}
 		
 	}
 	
 	public void gpsImage(Modem modem) {
 		BufferedImage bufferedImage=null;
 		InputStream in=null;
 		ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
 		ImageIcon icon=null;
 		int response=0;
 		
 		String request1 = "P7496T=225737403734T=225736403735T=225734403737T=225733403738\r";
 		
 		modem.write(request1.getBytes());
 		
 		try {
 			for(;;) {
 	 			response = modem.read();
 	 			if(response==-1) {
 	 				System.out.println("Connection closed.");
 	 				break;
 	 			}
 	 			byteArray.write(response);
 		 		System.out.println(byteArray.size());
 	 		}
 			
 			//convert byte array output stream to byte array
 	 		byte[] c = byteArray.toByteArray();
 	 		in = new ByteArrayInputStream(c);
			bufferedImage = ImageIO.read(in);
			//saving the image in folder
			ImageIO.write(bufferedImage, "jpeg", new File("C:\\Users\\user\\Desktop\\GPS.jpeg"));
			//convert buffered image to image icon
			icon=new ImageIcon(bufferedImage);
			
 		}catch(Exception e) {
 			System.out.println(e);
 		}
 		finally {
            // Close the file.
            try {
            	in.close();
            	byteArray.close();
            }
            catch (Exception e) {
                System.err.println(e);
            }
        }
 	}
 	
 	public static void main(String[] param) { 		
 		Modem modem=new Modem();
 		modem.setSpeed(80000);
 		modem.setTimeout(4000);
 		modem.open("ithaki");
 		
 		System.out.println("Modem ithaki is now open.");
 		
 		virtualModem virtualModem=new virtualModem();
 		
 		//reading the welcome message
 		virtualModem.welcomeMessage(modem);
 		System.out.println();
 		
 		virtualModem.echoPacket(modem);
 		virtualModem.image(modem);
 		virtualModem.errorARQ(modem);
 		virtualModem.gps(modem);
 		virtualModem.gpsImage(modem);
 		
        
 		modem.close();//modem closes
 		System.out.println("Modem ithaki is now closed.");
 	}
}
