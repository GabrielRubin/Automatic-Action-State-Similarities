package competition.richmario;
import java.io.*;
import java.net.*;
import java.io.IOException;


public class AutoEncoderCommunicator {

    private OutputStreamWriter out;
    private BufferedReader in;

    private DatagramSocket m_udpSocket;
    private InetAddress m_address;

    public AutoEncoderCommunicator() {

        try {
            //InetAddress localhost = InetAddress.getByName("localhost");
            //Socket      s         = new Socket(localhost, 9000);
            //s.setTcpNoDelay(true);
//
            //out = new OutputStreamWriter(s.getOutputStream());
            //in  = new BufferedReader(new InputStreamReader(s.getInputStream()));

            m_udpSocket = new DatagramSocket(9001);
            m_address = InetAddress.getByName("localhost");

        } catch (Exception e) {
            System.err.println("Ooops!");
            e.printStackTrace();
        }
    }

    public double[] GetSimilarities(String state1, String states) {

        String message = state1 + "\n" + state1 + "\n" + states;

        try {
            //System.out.println("Sending message " + message + " to Python program...");
            out.write(message);
            out.flush();
            //System.out.println("Getting response from Python program...");
            String answer = in.readLine();
            //System.out.println("Got: " + answer);
            int retries = 0;
            while(answer == "" && retries < 10) {

                answer = in.readLine();
                retries++;
            }

            String[] allValues = answer.split(";");
            double[] allValuesParsed = new double[allValues.length];
            for(int i = 0; i < allValues.length; i++) {
                double value = 0.0;
                if(!allValues[i].equals(""))
                    value = Double.parseDouble(allValues[i]);
                value /= 0.5;
                if(value > 1.0)
                    value = 1.0;
                allValuesParsed[i] = 1.0 - value;
            }

            return allValuesParsed;
        } catch (Exception e) {
            System.err.println("Ooops!");
            e.printStackTrace();
        }

        return new double[0];
    }

    public double[] GetEmbeddings(String state1, String state2) {

        String message = state1 + "\n" + state1 + "\n" + state2 + "\n";

        try {
            //System.out.println("Sending message " + message + " to Python program...");
            out.write(message);
            out.flush();
            //System.out.println("Getting response from Python program...");
            String answer = in.readLine();
            //System.out.println("Got: " + answer);
            int retries = 0;
            while(answer == "" && retries < 10) {

                answer = in.readLine();
                retries++;
            }

            String[] allValues = answer.split(";");
            double[] allValuesParsed = new double[allValues.length];

            for(int i = 0; i < allValues.length; i++) {
                double value = 0.0;
                if(!allValues[i].equals(""))
                    value = Double.parseDouble(allValues[i]);
                allValuesParsed[i] = value;
            }

            return allValuesParsed;

        } catch (Exception e) {
            System.err.println("Ooops!");
            e.printStackTrace();
        }

        return new double[0];
    }

    public double[] GetEmbeddingsUDP(String state1, String state2) {

        String message = state1 + "\n" + state1 + "\n" + state2 + "\n";

        byte[] buffer = message.getBytes();
        byte[] receiveBuffer = new byte[65535];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, m_address, 9000);

        try {
            m_udpSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DatagramPacket receive = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        try {
            m_udpSocket.receive(receive);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String response          = new String(receiveBuffer);
        String[] allValues       = response.split(";");
        double[] allValuesParsed = new double[allValues.length];

        for(int i = 0; i < allValues.length; i++) {
            double value = 0.0;
            if(!allValues[i].equals(""))
                value = Double.parseDouble(allValues[i]);
            allValuesParsed[i] = value;
        }

        return allValuesParsed;

    }

    public void SendMsg(String msg) {

        try {
            //System.out.println("Sending message " + msg + " to Python program...");
            out.write(msg);
            out.flush();
            //System.out.println("Getting response from Python program...");
            String answer = in.readLine();
            //System.out.println("Got: " + answer);
        } catch (Exception e) {
            //System.err.println("Ooops!");
            e.printStackTrace();
        }
    }
}
