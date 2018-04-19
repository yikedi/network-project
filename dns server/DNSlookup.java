

import java.io.IOException;
import java.net.*;

/**
 *
 */

/**
 * @author Donald Acton
 *         This example is adapted from Kurose & Ross
 *         Feel free to modify and rearrange code as you see fit
 */

public class DNSlookup {

    static final int MIN_PERMITTED_ARGUMENT_COUNT = 2;
    static final int MAX_PERMITTED_ARGUMENT_COUNT = 3;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        DNSResponse response; // Just to force compilation
        int argCount = args.length;
        boolean tracingOn = false;
        String rootNameServer;
        String name;
        String option = "A";

        if (argCount < MIN_PERMITTED_ARGUMENT_COUNT || argCount > MAX_PERMITTED_ARGUMENT_COUNT) {
            usage();
            return;
        }

        rootNameServer = args[0];
        name = args[1];
        if (argCount == 3) {  // option provided
            if (args[2].equals("-t"))
                tracingOn = true;
            else if (args[2].equals("-6")) {
                option = "AAAA";
            } else if (args[2].equals("-t6")) {
                tracingOn = true;
                option = "AAAA";
            } else { // option present but wasn't valid option
                usage();
                return;
            }
        }

        DatagramSocket serverSocket = new DatagramSocket();
        serverSocket.setSoTimeout(5000);

        try {
            response = get_DNSResponse(name, serverSocket, rootNameServer, option, rootNameServer, 0,tracingOn);

            if (response.answers.size() > 0) {

                String answer = "";
                int ttl = 0;
                for (int i = 0; i < response.answers.size(); i++) {
                    if (response.answers.get(i).type.equals(option)) {
                        answer = response.answers.get(i).answer;
                        ttl = response.answers.get(i).ttl;
                    }
                }
                System.out.println(name+" "+ttl+"  "+option+" "+answer);
            }

        } catch (Exception e) {
            if (e instanceof My_exception) {
                String message = e.getMessage();
                print_error_message(message, option, name);
            } else {
                print_error_message("-4", option, name);
            }

        }


    }

    private static void usage() {
        System.out.println("Usage: java -jar DNSlookup.jar rootDNS name [-6|-t|t6]");
        System.out.println("   where");
        System.out.println("       rootDNS - the IP address (in dotted form) of the root");
        System.out.println("                 DNS server you are to start your search at");
        System.out.println("       name    - fully qualified domain name to lookup");
        System.out.println("       -6      - return an IPV6 address");
        System.out.println("       -t      - trace the queries made and responses received");
        System.out.println("       -t6     - trace the queries made, responses received and return an IPV6 address");
    }


    // This method encode the query from the name given in the command line and return a byte array that contain the query content
    // @String option: query type  i.e "A","AAAA"
    //@String name: the name to search

    public static byte[] encodeQuery(String name, String option) {
        int queryID = (int) (Math.random() * 65535);

        byte[] query = new byte[512];
        byte[] id = new byte[]{
                (byte) ((queryID & 0x0000FF00) >> 8),
                (byte) (queryID & 0x000000FF)
        };

        query[0] = id[0];
        query[1] = id[1];
        query[5] = 1;

        String[] parts = name.split("\\.");
        int index = 12;

        for (String part : parts) {
            int len = part.length();
            query[index] = (byte) len;
            for (int i = 0; i < len; i++) {
                index++;
                query[index] = part.getBytes()[i];
            }
            index++;
        }
        query[index] = 0;
        index = index + 2;
        if (option.equals("AAAA")) {
            query[index] = 28;
        } else {
            query[index] = 1;
        }

        index = index + 2;
        query[index] = 1;

        byte[] final_query = new byte[index + 1];
        for (int i = 0; i < final_query.length; i++) {
            final_query[i] = query[i];
        }

        return final_query;
    }

    //This method send the query and receive a packet that contains the response on a socket and then return it. Print the query information if tracingOn is true.
    //@DatagramSocket serverSocket: the socket used to send and receive packet
    //@String name :name to search
    //@String option: query type
    //@String rootNameServer: rootName server IP address
    //@boolean tracingOn: Do tracing or not

    public static DatagramPacket send_receive_packet(DatagramSocket serverSocket, String name,String option, String rootNameServer, boolean tracingOn) throws IOException, My_exception {

        byte []request=encodeQuery(name,option);
        DatagramPacket packet = null;
        boolean need_loop = true;

        InetAddress rootIp=InetAddress.getByName(rootNameServer);
        int i = 0;

        while (need_loop) {
            packet = new DatagramPacket(request, request.length, rootIp, 53);

            try {
                if (tracingOn){
                    // print_query
                    //get query_id
                    int query_id = Byte.toUnsignedInt(request[0])*256+Byte.toUnsignedInt(request[1]);

                    System.out.println();
                    System.out.println();
                    System.out.println("Query ID     "+query_id+" "+ name+ " "+option+" --> "+rootNameServer);

                }
            serverSocket.send(packet);
            byte[] buf = new byte[1024];

            packet = new DatagramPacket(buf, buf.length);

            //configure datagram socket to timeout if no response is received within 5s

                serverSocket.receive(packet);
                need_loop = false;
            } catch (SocketTimeoutException e) {
                System.out.println("time out sending packet again");
                need_loop = true;
                i++;
                if (i > 1) {
                    throw new My_exception("-2");
                }
            }

        }
        return packet;
    }


    // This method sends a query on serverSocket and parse the response and return it.
    // @String name: name to search
    // @String option query type
    // @DatagramSocket serverSocket: the socket used to send and receive packet
    // @String name_server_ip: the ip address of the NS to send query to
    // @String parentIp: the ip of previous NS
    // @boolean tracingOn: do tracing or not
    public static DNSResponse get_DNSResponse2(String name, String option, DatagramSocket serverSocket, String name_server_ip, String parentIp,boolean tracingOn) throws IOException, My_exception {
        DatagramPacket packet = send_receive_packet(serverSocket, name,option, name_server_ip,tracingOn);
        byte[] data_received = packet.getData();
        DNSResponse response=new DNSResponse(data_received, name.length() + 18, parentIp);
        if (tracingOn){
            // print_trace_response
           response.print_trace();
        }
        return response;
    }

    //This method recursively find the ip address of the name given.
    // @String name: name to search
    // @String option query type
    // @DatagramSocket serverSocket: the socket used to send and receive packet
    // @String name_server_ip: the ip address of the NS to send query to
    // @String rootDNS: the ip of the rootDNS
    // @boolean tracingOn: do tracing or not
    // @int counter: number of queries sent

    public static DNSResponse get_DNSResponse(String name, DatagramSocket serverSocket, String name_server_ip, String option, String rootDNS, int counter,boolean tracingOn) throws IOException, My_exception {

        DNSResponse response = get_DNSResponse2(name, option, serverSocket, name_server_ip, name_server_ip,tracingOn);

        counter++;
        String parentIp;

        do {
            if (response.answerCount > 0) {

                // show answer
                boolean check_next = true;
                for (int i = 0; i < response.answers.size(); i++) {
                    if (response.answers.get(i).type.equals(option)) {
                        //response.print_result();
                        check_next = false;
                        break;
                    }
                    check_next = true;
                }
                if (check_next) {
                    // keep searching on CNAME
                    String cname = response.answers.get(0).answer;
                    DNSResponse parent = response.parent;
                    parent.additionals.remove(0);

                    response = get_DNSResponse(cname, serverSocket, rootDNS, option, rootDNS, counter,tracingOn);
                }
                break;
            } else if (response.rcode == 3) {
                throw new My_exception("-1");
            } else if (response.rcode == 5) {
                throw new My_exception("-4");
            } else if (response.additionals.size() > 0) {

                parentIp = name_server_ip;
                for (int i = 0; i < response.additionals.size(); i++) {

                    if (response.additionals.get(i).type.equals("A")) {
                        name_server_ip = response.additionals.get(i).answer;
                        response.additionals.remove(i);
                        break;
                    }
                }

                if (name_server_ip != null) {
                    DNSResponse sub_response = get_DNSResponse2(name, option, serverSocket, name_server_ip, parentIp,tracingOn);
                    counter++;
                    sub_response.parent = response;
                    response = sub_response;
                }

            } else if (response.name_servers.size() > 0) {
                if (response.name_servers.get(0).type.equals("SOA")) {
                    throw new My_exception("-6");
                }
                String server_name = response.name_servers.get(0).answer;
                response.name_servers.remove(0);
                String parentDNS_address = response.parentDNS_address;
                DNSResponse name_response = get_DNSResponse(server_name, serverSocket, parentDNS_address, "A", rootDNS, counter,tracingOn);
                if (name_response.answers.size() > 0) {
                    name_server_ip = name_response.answers.get(0).answer;
                    response = get_DNSResponse(name, serverSocket, name_server_ip, option, rootDNS, counter,tracingOn);
                }
            }
        } while (counter < 31);

        if (counter > 30) {
            throw new My_exception("-3");
        }

        return response;
    }

    public static void print_error_message(String code, String option, String name) {
        String recordValue;
        if (option.equals("A")) {
            recordValue = "0.0.0.0";
        } else {
            recordValue = "0:0:0:0:0:0:0:0";
        }
        System.out.println(name+" "+code+"  "+option+" "+recordValue);
    }
}

class My_exception extends Exception {

    My_exception(String message) {
        super(message);
    }
}


