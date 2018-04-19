/*
** server.c -- a stream socket server demo
*/

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <arpa/inet.h>
#include <sys/wait.h>
#include <signal.h>
#include <ctype.h>
#include <ifaddrs.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/time.h>
#include "usage.h"

#include "dir.h"
#include <dirent.h>

#define BACKLOG 10     // how many pending connections queue will hold
//done

void sigchld_handler(int s) {
    // waitpid() might overwrite errno, so we save and restore it:
    int saved_errno = errno;

    while (waitpid(-1, NULL, WNOHANG) > 0);

    errno = saved_errno;
}


//this method send the message to a given socket
//@ int socket: the socket to send message to
//@ message: the message to send
void send_message(int socket, char *message) {

    char message2[500];
    if (message != NULL) {
        strcpy(message2, message);
    }
    strcat(message2, "\r\n");
    if (send(socket, message2, strlen(message2), 0) == -1)
        perror("send");
    printf("Server sent: %s", message2);
}

//this method send the receive a message from a given socket
//@ int socket: the socket to receive message from
//@ buf: buffer to hold the message
//@ len message length
void recv_message(int socket, char *buf, int len) {
    int client_message;

    if ((client_message = recv(socket, buf, len, 0)) == -1) {
        perror("recv");
        exit(1);
    }
}

// get sockaddr, IPv4 or IPv6:
void *get_in_addr(struct sockaddr *sa) {
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in *) sa)->sin_addr);
    }

    return &(((struct sockaddr_in6 *) sa)->sin6_addr);
}

// parse the client command
// @input: the input to parse
// @command1: the first command to set
// @command2: the second command to set
void parse_command(char *input, char *command1, char *command2) {

    if (input == NULL) {
        command1 = NULL;
        command2 = NULL;
        return;
    }


    char *temp;
    temp = strtok(input, "\n");  // get rid of \n in the end
    temp = strtok(temp, "\r");  // get rid of \r in the end

    char *token;
    if (temp != NULL) {
        token = strtok(temp, " ");      // set command1
        strcpy(command1, token);
    }

    token = strtok(NULL, " ");
    if (token != NULL) {
        strcpy(command2, token);        // set command2

    }


}

// this method accept the connect from a client
//@sockfd: the socket set up to accept the client
// @return new_fd an integer points to the file descriptor
int accept_connection(int sockfd) {
    int new_fd;
    char s[INET6_ADDRSTRLEN];
    struct sockaddr_storage their_addr;
    socklen_t sin_size;

    while (1) {  // main accept() loop

        new_fd = accept(sockfd, (struct sockaddr *) &their_addr, &sin_size);

        if (new_fd == -1) {
            perror("accept");
            continue;
        }

        inet_ntop(their_addr.ss_family,
                  get_in_addr((struct sockaddr *) &their_addr),
                  s, sizeof s);

        printf("Server: got connection from '%s'\n", s);
        break;
    }

    return new_fd;
}

// set up a socket for clients to connect
//@ port: port to connect
//@ return sockfd an integer points to the socket
int setup_socket(char *port) {
    int sockfd=-1, new_fd;  // listen on sock_fd, new connection on new_fd
    struct addrinfo hints, *servinfo, *p;
    struct sockaddr_storage their_addr; // connector's address information
    socklen_t sin_size;
    struct sigaction sa;
    int yes=1;
    char s[INET6_ADDRSTRLEN];
    int rv;

    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE; // use my IP

    if ((rv = getaddrinfo(NULL, port, &hints, &servinfo)) != 0) {
        fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
        return -1;
    }

    // loop through all the results and bind to the first we can
    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sockfd = socket(p->ai_family, p->ai_socktype,
                             p->ai_protocol)) == -1) {
            perror("server: socket");
            continue;
        }

        if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &yes,
                       sizeof(int)) == -1) {
            perror("setsockopt");
            exit(1);
        }

        if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1) {
            close(sockfd);
            perror("server: bind");
            continue;
        }

        break;
    }

    freeaddrinfo(servinfo); // all done with this structure

    if (p == NULL)  {
        fprintf(stderr, "server: failed to bind\n");
        exit(1);
    }

    if (listen(sockfd, BACKLOG) == -1) {
        perror("listen");
        exit(1);
    }

    sa.sa_handler = sigchld_handler; // reap all dead processes
    sigemptyset(&sa.sa_mask);
    sa.sa_flags = SA_RESTART;
    if (sigaction(SIGCHLD, &sa, NULL) == -1) {
        perror("sigaction");
        exit(1);
    }

    printf("server: waiting for connections...\n");
    return sockfd;

}

// do pasv commmand
//@ command_socket: socket to send and receive message during the process
//@return pasv_socket: an integer points to the pasv_socket if not successful return -1
int do_pasv(int command_socket) {

    // figure out the new port

    struct ifaddrs *addrs;
    if (getifaddrs(&addrs) == -1) {
        perror("getifaddrs");
        exit(EXIT_FAILURE);
    }
    struct ifaddrs *tmp = addrs;
    char *ip = NULL;

    // part of this code is taken from stack overflow

    while (tmp) {

        if (tmp->ifa_addr && tmp->ifa_addr->sa_family == AF_INET) {
            struct sockaddr_in *pAddr = (struct sockaddr_in *) tmp->ifa_addr;                   // loop through and find the ip address

            if (strcmp(tmp->ifa_name, "en0") == 0 || strcmp(tmp->ifa_name, "em1") == 0) {
                ip = inet_ntoa(pAddr->sin_addr);
                char *delimeter;
                while (1) {
                    delimeter = strstr(ip, ".");
                    if (delimeter != NULL) {
                        strncpy (delimeter, ",", 1);
                    } else {
                        break;
                    }
                }


                break;
            }

        }

        tmp = tmp->ifa_next;
    }

    int temp_socket = 0;
    int i = 5;
    while (!temp_socket) {
        char temp_ip[100];
        strcpy(temp_ip, ip);
        char port[100] = ",20,";

        char num[15];
        sprintf(num, "%d", i);   // number to string
        strcat(port, num);

        strcat(temp_ip, port);
        printf("%s\n", temp_ip);
        int port_num = 5120 + i;

        char num2[15];
        sprintf(num2, "%d", port_num);   // number to string

        temp_socket = setup_socket(num2);
        if (temp_socket) {
            //  sample output 227 Entering Passive Mode (198,162,33,28,126,59)
            char message_out[100] = "227 Entering Passive Mode (";
            strcat(message_out, temp_ip);
            strcat(message_out, ")");               //concat the output
            send_message(command_socket, message_out);
        }
        i++;
    }

    struct timeval timeout;
    fd_set read_fds;
    timeout.tv_sec = 30;
    timeout.tv_usec = 0;                        // set the timeout for accept  20s
    FD_ZERO(&read_fds);
    FD_SET(temp_socket, &read_fds);

    int rc = select(temp_socket + 1, &read_fds, NULL, NULL, &timeout);
    int pasv_socket = -1;
    if (rc > 0) {
        pasv_socket = accept_connection(temp_socket);          // process accept
        printf("%s\n", "Connection established");
    } else {
        if (rc == 0) {
            send_message(command_socket, "503 timeout");       //timeout
            close(pasv_socket);
            return -1;
        } else {
            send_message(command_socket, "503 unknown error");      //some other error
            close(pasv_socket);
            return -1;
        }

    }


    return pasv_socket;
}


//A loop that keep processing commands of clients return at QUIT command
//@ command_socket: the socket to send and receive commands during the process

int provide_service(int command_socket) {

    int login = 0;
    int pasv_socket = -1;
    int max_path = 100;
    char *start_dir;
    char dir_buf[max_path];
    start_dir = getcwd(dir_buf, max_path);  // get the starting dir

    // the main loop
    while (1) {

        int len = 1000;
        char *input = malloc(len * sizeof(char));

        printf("%s\n", "Server: waiting for commands..");
        recv_message(command_socket, input, len);

        char **buf = (char **) malloc(3 * sizeof(char *));
        int i = 0;
        for (i; i < 3; i++) {
            buf[i] = malloc(
                    100 * sizeof(char));                //malloc space for commands and initialize commmands as ""
            strcpy(buf[i], "");
        }


        parse_command(input, buf[0], buf[1]);
        printf("Server received command: %s\n", buf[0]);
        printf("input1 is %s\n", buf[1]);

        //TODO: implement if else according to spec

        i = 0;
        char *command0 = buf[0];
        char c;
        while (command0[i]) {
            c = toupper(command0[i]);   // command1 to uppercase
            command0[i] = c;
            i++;
        }


        if (strcmp(buf[0], "USER") == 0) {
            if (strcmp(buf[1], "cs317") == 0) {
                login = 1;
                send_message(command_socket, "230");            // login in ok send 230

            } else {
                send_message(command_socket, "530 this server is cs317 only");
                continue;
            }

        } else if (strcmp(buf[0], "") == 0) {              // Press enter is ok
            continue;
        } else if (strcmp(buf[0], "QUIT") == 0) {

            if (strcmp(buf[1], "") != 0) {
                send_message(command_socket, "503 wrong number of arguments");
                continue;
            }
            send_message(command_socket, "221 quit success\n");
            return 0;

        } else {
            if (!login) {
                send_message(command_socket, "530 Please login with USER");         // check for login in
                continue;
            }

            if (strcmp(buf[0], "CDUP") == 0) {

                if (strcmp(buf[1], "") != 0) {
                    send_message(command_socket, "503 wrong number of arguments");
                    continue;
                }

                char *current_dir;
                char temp[max_path];
                current_dir = getcwd(temp, max_path);

                if (strcmp(current_dir, start_dir) == 0) {
                    send_message(command_socket, "550 fail to open");
                } else {
                    int ret = chdir("..");
                    if (ret)
                        send_message(command_socket, "550 fail to open");
                    else {
                        send_message(command_socket, "250 Directory successfully changed");
                    }
                }

            } else if (strcmp(buf[0], "TYPE") == 0) {

                if (strcmp(buf[1], "A") == 0) {
                    send_message(command_socket, "200 Switching to ASCII mode");
                } else if ((strcmp(buf[1], "I") == 0)) {
                    send_message(command_socket, "200 Switching to Binary mode");
                } else {
                    send_message(command_socket, "500 Unrecognised TYPE command");
                }
            } else if (strcmp(buf[0], "MODE") == 0) {


                if (strcmp(buf[1], "S") == 0) {
                    send_message(command_socket, "200 Mode Set to S");
                } else {
                    send_message(command_socket, "504 Bad Mode command");
                }

            } else if (strcmp(buf[0], "STRU") == 0) {

                if (strcmp(buf[1], "F") == 0) {
                    send_message(command_socket, "200 Structure set to F");
                } else {
                    send_message(command_socket, "504 Bad STRU command");
                }

            } else if (strcmp(buf[0], "RETR") == 0) {


                if (pasv_socket == -1) {
                    send_message(command_socket, "425 Use PASV first");
                    continue;
                }

                char *file_name;
                int fd, copy;
                off_t offset;
                struct stat filestat;
                file_name = buf[1];
                // open file given filename
                fd = open(file_name, O_RDONLY);
                if (fd == -1) {
                    send_message(command_socket, "503 unable to open");
                }
                // get the size of the file
                fstat(fd, &filestat);
                printf("size is: %lld\n", filestat.st_size);
                // copy file using send file

                char *file_buffer = malloc(filestat.st_size);

                int r = read(fd, file_buffer, filestat.st_size);
                printf("read is:%d\n ", r);

                strcat(file_buffer, "\r\n");
                send_message(command_socket, "150");
                copy = send(pasv_socket, file_buffer, filestat.st_size, 0);
                printf("copy is: %d\n", copy);

                if (copy != 0) {
                    send_message(command_socket, "226");            // success close pasv_socket and set it to -1
                    close(pasv_socket);
                    pasv_socket = -1;
                } else {
                    send_message(command_socket, "500 retrieve failed");
                    close(pasv_socket);
                    pasv_socket = -1;
                }


            } else if (strcmp(buf[0], "PASV") == 0) {

                if (strcmp(buf[1], "") != 0) {
                    send_message(command_socket, "503 wrong number of arguments");
                    continue;
                }

                pasv_socket = do_pasv(command_socket);


            } else if (strcmp(buf[0], "NLST") == 0) {

                if (strcmp(buf[1], "") != 0) {
                    send_message(command_socket, "503 wrong number of arguments");
                    continue;
                } else {
                    if (pasv_socket == -1) {
                        send_message(command_socket, "425 Use PASV first");
                    } else {
                        printf("Printed %d directory entries\n", listFiles(pasv_socket, "."));
                        send_message(command_socket, "150 Here comes the directory listing");
                        send_message(command_socket, "226 Directory send OK");
                        close(pasv_socket);
                        pasv_socket = -1;
                    }
                }

            } else if (strcmp(buf[0], "CWD") == 0) {

                if (strstr(buf[1], "./") == buf[1] || strstr(buf[1], "../") != NULL) {
                    char *message = "550 fail to open";                                     //check if input is valid
                    send_message(command_socket, message);
                } else {

                    int ret = chdir(buf[1]);
                    if (ret)
                        send_message(command_socket, "550 fail to open");
                    else {
                        send_message(command_socket, "250 Directory successfully changed");
                    }
                }
            } else {
                send_message(command_socket, "503 invalid command");
            }

        }

        free(input);
        free(buf);
        input = NULL;
    }
}

int main(int argc, char **argv) {

    //main loop when one client logout wait for the next one. Only handle one client at a time
    if (argc != 2) {
        usage(argv[0]);
        return -1;
    }

    while (1) {
        int main_socket = setup_socket(argv[1]);
        if (main_socket==-1){
            break;
        }
        int command_socket = accept_connection(main_socket);
        send_message(command_socket, "220");

        while (1) {
            provide_service(command_socket);
            break;
        }
        close(command_socket);
        close(main_socket);

    }

    return 0;
}