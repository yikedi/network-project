


import java.util.ArrayList;

// Lots of the action associated with handling a DNS query is processing
// the response. Although not required you might find the following skeleton of
// a DNSreponse helpful. The class below has bunch of instance data that typically needs to be 
// parsed from the response. If you decide to use this class keep in mind that it is just a 
// suggestion and feel free to add or delete methods to better suit your implementation as 
// well as instance variables.


public class DNSResponse {
    public int queryID;                  // this is for the response it must match the one in the request
    public int answerCount = 0;          // number of answers
    public boolean decoded = false;      // Was this response successfully decoded
    public int nsCount = 0;              // number of nscount response records
    public int additionalCount = 0;      // number of additional (alternate) response records
    public boolean authoritative = false;// Is this an authoritative record
    public int index = 0;                 // index number of response
    public int rcode;
    public String parentDNS_address;

    public ArrayList<ResponseRecord> answers = new ArrayList<ResponseRecord>();
    public ArrayList<ResponseRecord> name_servers = new ArrayList<ResponseRecord>();
    public ArrayList<ResponseRecord> additionals = new ArrayList<ResponseRecord>();
    public DNSResponse parent = null;

    // Note you will almost certainly need some additional instance variables.

    // When in trace mode you probably want to dump out all the relevant information in a response

    void dumpResponse() {

    }


    // The constructor: you may want to add additional parameters, but the two shown are 
    // probably the minimum that you need.


    //@byte[] data: data to parse
    //@int len: the length is used to find the start of the answer part
    //@String parentDNS_address: ip of the parent NS
    public DNSResponse(byte[] data, int len, String parentDNS_address) {
        this.parentDNS_address = parentDNS_address;
        parseResponse(data, len);
    }

    //This method parse response
    //@byte[] data: data to parse
    //@int len: the length is used to find the start of the answer part

    public void parseResponse(byte[] data, int len) {
        // The following are probably some of the things
        // you will need to do.

        // Extract the query ID
        queryID = Byte.toUnsignedInt(data[0]) * 256 + Byte.toUnsignedInt(data[1]);

        // Make sure the message is a query response and determine
        // if it is an authoritative response or note

        String temp = Integer.toBinaryString(Byte.toUnsignedInt(data[2]));
        char aa = temp.charAt(5);

        if (aa == 1) {
            authoritative = true;
        }

        rcode = data[3] << 28;
        rcode = rcode >>> 28;

        if (data[3] == 0) {
            decoded = true;
        }

        // determine answer count

        answerCount = Byte.toUnsignedInt(data[6]) * 256 + Byte.toUnsignedInt(data[7]);

        // determine NS Count

        nsCount = Byte.toUnsignedInt(data[8]) * 256 + Byte.toUnsignedInt(data[9]);


        // determine additional record count

        additionalCount = Byte.toUnsignedInt(data[10]) * 256 + Byte.toUnsignedInt(data[11]);


        // index at last byte of query name

        // Extract list of answers, name server, and additional information response


        index = index + len;


        if (answerCount > 0) {

            String FQDN = "";
            String type = "";
            int ttl = 0;
            String answer = "";
            String r_class = "";


            for (int i = 0; i < answerCount; i++) {
                FQDN = parse_FQDN(index, data);
                index = index + 3;                //the fourth byte of answer, type
                type = parse_type(index, data);
                index = index + 2;
                r_class = parse_class(index, data);
                index = index + 1;               // starting byte of ttl
                ttl = parse_ttl(index, data);
                index = index + 4;               //starting byte of rdata

                if (type.equals("A")) {
                    answer = parse_ip(index, data);
                } else if (type.equals("AAAA")) {
                    answer = parse_ipv6(index, data);
                } else if (type.equals("CNAME")) {
                    answer = parse_nsName(index, data);
                }

                index += get_length(index, data) + 2; //2 is for 2 bytes to specify length
                ResponseRecord responseRecord = new ResponseRecord(FQDN, answer, type, ttl, r_class);
                answers.add(responseRecord);
            }
        }

        if (nsCount > 0) {

            String name = "";
            String type = "";
            int ttl = 0;
            String nsName = "";
            String r_class = "";

            for (int i = 0; i < nsCount; i++) {

                name = parse_FQDN(index, data); //FQDN name for one answer count
                if (name.equals("")) {
                    index = index + 2;
                } else {
                    index = index + 3;
                }
                type = parse_type(index, data);
                index = index + 2;
                r_class = parse_class(index, data);
                index = index + 1;               // starting byte of ttl
                ttl = parse_ttl(index, data);
                index = index + 4;               //starting byte of rdata
                nsName = parse_nsName(index, data);
                index += get_length(index, data) + 2;

                ResponseRecord responseRecord = new ResponseRecord(name, nsName, type, ttl, r_class);
                name_servers.add(responseRecord);
            }
        }

        if (additionalCount > 0) {
            String name = "";
            String type = "";
            int ttl = 0;
            String r_class = "";
            ResponseRecord responseRecord_additional = null;

            for (int i = 0; i < additionalCount; i++) {


                name = parse_FQDN(index, data); //FQDN name for one answer count
                index = index + 3;                //the fourth byte of answer, type
                type = parse_type(index, data);
                index = index + 2;
                r_class = parse_class(index, data);
                index = index + 1;               // starting byte of ttl
                ttl = parse_ttl(index, data);
                index = index + 4;               //starting byte of rdata
                if (type.equals("A")) {

                    String ip = parse_ip(index, data);
                    responseRecord_additional = new ResponseRecord(name, ip, type, ttl, r_class);
                } else if (type.equals("CNAME")) {
                    String cname = parse_nsName(index, data);
                    responseRecord_additional = new ResponseRecord(name, cname, type, ttl, r_class);
                } else if (type.equals("AAAA")) {
                    String ipv6 = parse_ipv6(index, data);
                    responseRecord_additional = new ResponseRecord(name, ipv6, type, ttl, r_class);

                }
                index += get_length(index, data) + 2;
                additionals.add(responseRecord_additional);

            }
        }
    }


    //This method parse FQDN
    //@byte[] data: data to parse
    //@int index: the start index

    public String parse_FQDN(int index, byte data[]) {
        int offset_index = get_offset(index, data);
        StringBuilder name = new StringBuilder();

        if (Byte.toUnsignedInt(data[offset_index]) == 0) {
            return "";
        }

        while (Byte.toUnsignedInt(data[offset_index]) != 0) {

            int charnum = Byte.toUnsignedInt(data[offset_index]);
            char[] bytestochar = new char[charnum];

            for (int i = 0; i < charnum; i++) {
                bytestochar[i] = (char) data[i + 1 + offset_index];
            }

            offset_index = offset_index + charnum + 1;
            offset_index = get_offset(offset_index, data);
            name.append(bytestochar);
            name.append(".");

        }
        if (name.length() == 0) {
            System.out.println(offset_index);
            System.out.println(name.length() + " length");
        }
        return name.toString().substring(0, name.toString().length() - 1);
    }

    //This method parse type
    //@byte[] data: data to parse
    //@int index: the start index

    public String parse_type(int index, byte data[]) {
        String type;
        switch (data[index]) {
            case 0x01:
                type = "A";
                break;
            case 0x02:
                type = "NS";
                break;
            case 0x05:
                type = "CNAME";
                break;
            case 0x06:
                type = "SOA";
                break;
            case 0x1c:
                type = "AAAA";
                break;
            default:
                type = "unknown";
        }
        return type;
    }

    //This method parse class
    //@byte[] data: data to parse
    //@int index: the start index

    public String parse_class(int index, byte data[]) {
        String r_class = "unknown";

        if (data[index] == 0x01) {
            r_class = "IN";
        }
        return r_class;
    }

    //This method parses ttl
    //@byte[] data: data to parse
    //@int index: the start index

    public int parse_ttl(int index, byte data[]) {

        int first_byte = Byte.toUnsignedInt(data[index]) * 16777216;
        int second_byte = Byte.toUnsignedInt((data[index + 1])) * 65536;
        int third_byte = Byte.toUnsignedInt(data[index + 2]) * 256;
        int last_byte = Byte.toUnsignedInt(data[index + 3]);
        return first_byte + second_byte + third_byte + last_byte;
    }

    //This method parses ip in ipv4
    //@byte[] data: data to parse
    //@int index: the start index

    public String parse_ip(int index, byte data[]) {
        String ip = "";
        int rdata_length = get_length(index, data);
        index = index + 2;
        //char[] bytestochar = new char[rdata_length];
        int ip_part = 0;
        for (int i = index; i < index + rdata_length; i++) {
            ip_part = Byte.toUnsignedInt(data[i]);
            ip += ip_part;
            ip += ".";
        }
        return ip.substring(0, ip.length() - 1);
    }

    //This method parses name
    //@byte[] data: data to parse
    //@int index: the start index

    public String parse_nsName(int index, byte data[]) {

        index = index + 2;
        int offset_index = get_offset(index, data);

        StringBuilder name = new StringBuilder();

        while (Byte.toUnsignedInt(data[offset_index]) != 0) {

            int charnum = Byte.toUnsignedInt(data[offset_index]);

            char[] bytestochar = new char[charnum];

            for (int i = 0; i < charnum; i++) {
                bytestochar[i] = (char) data[offset_index + i + 1];
            }

            offset_index = offset_index + charnum + 1;
            offset_index = get_offset(offset_index, data);
            name.append(bytestochar);
            name.append(".");

        }

        return name.toString().substring(0, name.toString().length() - 1);
    }


    public class ResponseRecord {
        String name;
        String answer;
        String type;
        int ttl;
        String r_class;

        public ResponseRecord(String name, String answer, String type, int ttl, String r_class) {
            this.name = name;
            this.type = type;
            this.ttl = ttl;
            this.r_class = r_class;
            this.answer = answer;

        }

        //thie method is only used when debugging
        public void print_result() {
            System.out.println(this.name);
            System.out.println(this.answer);
            System.out.println(this.type);
            System.out.println(this.ttl);
            System.out.println(this.r_class);
            System.out.println();
        }


    }


    // this is a method used during the debugging process
    public void print_result() {


        System.out.println("queryID:  " + this.queryID);
        System.out.println("answerCount:  " + this.answerCount);
        System.out.println("decoded:  " + this.decoded);
        System.out.println("nsCount:  " + this.nsCount);
        System.out.println("additionalCount:  " + this.additionalCount);
        System.out.println("authoritative:  " + this.authoritative);
        System.out.println("rcode: " + this.rcode);
        System.out.println("parent IP: " + this.parentDNS_address);
        int i = 0;
        System.out.println("Answers:  ");
        System.out.println();
        for (ResponseRecord rr : answers) {
            System.out.println("answer " + i);
            rr.print_result();
            i++;
        }

        i = 0;
        System.out.println("Name servers: ");
        for (ResponseRecord rr : name_servers) {
            System.out.println("ns " + i);
            rr.print_result();
            i++;
        }

        i = 0;
        System.out.println("additionals: ");
        for (ResponseRecord rr : additionals) {
            System.out.println("additional: " + i);
            rr.print_result();
            i++;
        }
        System.out.println();
    }


    public void print_trace() {
        System.out.println("Response ID: " + queryID + " " + "Authoritative " + authoritative);
        System.out.println("  Answer" + answerCount);
        for (ResponseRecord rr : answers) {
            System.out.format("       %-30s %-10s %-4s %s\n", rr.name, rr.ttl, rr.type, rr.answer);
        }
        System.out.println("  Nameservers" + nsCount);
        for (ResponseRecord rr : name_servers) {
            System.out.format("       %-30s %-10s %-4s %s\n", rr.name, rr.ttl, rr.type, rr.answer);
        }
        System.out.println("  Additional Information" + additionalCount);
        for (ResponseRecord rr : additionals) {
            System.out.format("       %-30s %-10s %-4s %s\n", rr.name, rr.ttl, rr.type, rr.answer);
        }

    }

    //This method find the next index to look at
    //@byte[] data: data to parse
    //@int index: the start index
    public int get_offset(int index, byte[] data) {

        int offset_index;
        if (Byte.toUnsignedInt(data[index]) == 0) {
            return index;
        }

        String temp = Integer.toBinaryString(Byte.toUnsignedInt(data[index]) * 256 + Byte.toUnsignedInt(data[index + 1]));
        String start_bits = temp.substring(0, 2);
        String offset = temp.substring(2);
        if (start_bits.equals("11") && data[index] < 0) {
            offset_index = Integer.parseInt(offset, 2);
        } else {
            offset_index = index;
        }

        return offset_index;
    }

    //This method parses length
    //@byte[] data: data to parse
    //@int index: the start index
    public int get_length(int index, byte[] data) {
        return Byte.toUnsignedInt(data[index]) * 256 + Byte.toUnsignedInt(data[index + 1]);
    }

    //This method parses ip in ipv6
    //@byte[] data: data to parse
    //@int index: the start index
    public String parse_ipv6(int index, byte[] data) {
        String ip = "";
        int rdata_length = get_length(index, data);
        index = index + 2;
        int ip_part = 0;
        for (int i = index; i < index + rdata_length; i += 2) {
            ip_part = Byte.toUnsignedInt(data[i]) * 256 + Byte.toUnsignedInt(data[i + 1]);
            String hex = Integer.toHexString(ip_part);
            ip += hex;
            ip += ":";
        }
        return ip.substring(0, ip.length() - 1);
    }


}


