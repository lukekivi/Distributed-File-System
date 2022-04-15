# Distributed File System
**Created by:** \
Lucas Kivi - kivix019 \
Charles Droege - droeg022


## Description
A mock distributed file system. Files consist of an id and a version. Clients can read file version numbers and write to files. Writing to a file is simply incrementing the version number. Clients communicate with the system via a random server which serves as the ambassador. The system is lead by a coordinator server which performs regular server duties on top of its coordination effort. Each client transaction goes from the client's ambassador server to the coordinator server. The coordinator then assembles a quorum of servers, performs the transaction, and then returns the results to the ambassador server who then returns to the client.


## Implementation
#### Consistency
This is a distributed file system that is to maintain ordering based sequential consistency. In order to achieve we use a coordinator to choose the order of transactions. All transactions are enqueued at the coordinator and then carried out in FIFO order. 

#### Quorums
In order to reduce the number of transactions required to maintain read and write consistency form the client's perspective we have implemented *Gifford's Quorum Based Protocol*. This queries a subset of servers for writes and requires a subset of servers to agree on writes in order to be consistent. So long as the quorum sizes adhere to these rules sequential consistency will be intact:

Q<sub>R</sub> is the reading quorum, Q<sub>W</sub> - is the writing quorum, and N is the number of servers in the system.
* Q<sub>W</sub> > N/2
* Q<sub>R</sub> + Q<sub>W</sub> > N


## Output
All output is redirected to a log in the `Distributed-File-System/app/log` directory. Server logs are enumerated based on their configuration file like so `server1.txt`.

Client logs are of the form `clientLog_<N>.txt`. `<N>` is the next available log number. If `clientLog_1.txt` exists then the next client created will have log `clientLog_2.txt`. The log designated to a given client is announced to the terminal upon starting the client. Like so:
```
All output directed to: log/clientLog_0.txt
```

# Running
In order to run our distributed file distributed you must do a few things:
* Setup your environment
* Familiarize yourself with and setup the configuration documents `machines.txt` and `config.txt`.
* Understand how to issue commands to a client
* Start the system
* Run clients


## Setup
1. Make sure you have a current JDK, JRE, and `Thrift` installed.
2. Download the application. If you need a fresh version you can find it here:
	```
    git clone https://github.com/lukekivi/Distributed-File-System.git
    ```
3. Set the required environment variables:
	```
    export THRIFT_COMPILER_PATH=/<absolute path to thrift compiler>
	export THRIFT_LIB_PATH=/<absolute path to thrift java libs>
    ```
4. If you want to run tests using our ssh scripts you will want to set these environment variables as well. The `DFS_USERNAME` should be the username you use to ssh into the machines you will be using.
	```
 	export PROJ_PATH=/<absolute path to>/Distributed-File-System/app
	export DFS_USERNAME=<username>
    ```
5. Here is an example script for setting up the environment variables.
	```
    export THRIFT_COMPILER_PATH=/project/kivix019/thrift-0.15.0/compiler/cpp/thrift
	export THRIFT_LIB_PATH=/project/kivix019/thrift-0.15.0/lib/java/build/libs
	export PROJ_PATH=/project/kivix019/Distributed-File-System/app
	export DFS_USERNAME=kivix019
    ```


## Configuration
### `app/config/config.txt`
---
This file contains foundational information about the system you want to setup. It is the source of truth for four things: how many servers are supposed to be, write quorum size, read quorum size, and the number of files in the system.

Here is a sample `config.txt`:
```
servers 7
read 6
write 4
files 10
```
1. `servers 7` - there should be 7 servers in this system
2. `read 6` - the read quorum should be composed of 6 servers
3. `write 4` - the write quorum should be composed of 4 servers
4. `files 10` - ten files should exist in each server. IDs will be 0-9.

You may modify these entries to your liking but just be sure you do a few things:
* Follow the quorum constraints in the **Quorums** section above. 
* Make sure the number of servers you choose matches the number of servers you provide in the `machines.txt` file (details below) and the number of servers you ultimately start.

### `app/config/machines.txt`
---
Here is where you declare the hostnames or ip addresses, port numbers, and IDs of each server and the coordinator.

Here is a sample `machines.txt` file:
```
server csel-kh1250-11.cselabs.umn.edu 9033 1
server csel-kh1250-12.cselabs.umn.edu 9033 2
server csel-kh1250-13.cselabs.umn.edu 9033 3
server csel-kh1250-14.cselabs.umn.edu 9033 4
server csel-kh1250-15.cselabs.umn.edu 9033 5
server csel-kh1250-16.cselabs.umn.edu 9033 6
coordinator csel-kh1250-17.cselabs.umn.edu 9033 7
```
The format is like this:
```
<server-type> <hostname/ip> <port> <id>
```
* `server-type` - can only be `server` or `coordinator`. There has to be one and only one coordinator in any functioning app.
* `hostname/ip` - this, in tandem with the port num, is how the server will be found by other participants in the system. There must be a server running on that machine for the app to function.
* `port` - the port the server is listening on. The server running on the related `hostname/ip` will also use this value for setup.
* `id` - the unique integer ID the server uses in the system.

**Remember: the number of servers declared in this file must match the number of servers declared in the `config.txt` file**

## Commands
There are four commands a client may issue to the file system. All output is directed to the given client's log. Read more about logging in the **Output** section above.

* **READ**  the version number of a file with **integer** id `n`.
	```
	read n
	```

* **Write** or essentially increment the version of the file with **integer** id `n`.
	``` 
	write n
	```

* **Print** the structure of the entire distributed file system. This includes each server and their files.
	```
	print
	```

* **Check** the consensus version of each file. It also prints the number of servers that agreed with that value along side the current write quorum size.
	```
	check
	```

	**Example Output**
	```
	File [3]: 
		-      Version: 90
		- confirmed by: 4/4 servers
	```

### `commands.txt`
