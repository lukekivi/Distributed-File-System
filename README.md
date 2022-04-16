# Distributed File System

**Created by:** \

Lucas Kivi - kivix019 \

Charles Droege - droeg022

  
  

## Description

A mock distributed file system. Files consist of an id and a version. Clients can read file version numbers and write to files. Writing to a file is simply incrementing the version number. Clients communicate with the system via a random server which serves as the ambassador. The system is lead by a coordinator server which performs regular server duties on top of its coordination effort. Each client transaction goes from the client's ambassador server to the coordinator server. The coordinator then assembles a quorum of servers, performs the transaction, and then returns the results to the ambassador server who then returns to the client.

  
  

## Implementation

##### Consistency

The coordinator utilizes a semaphore for each file in order to maintain mutual exclusivity. This way, each request for either a read or write of a file is received in order. If no process posseses the file's lock, the next file up in the queue is given access. Once it finishes its operation, it gives the lock to the next process waiting. This way, the coordinator can be multithreaded and conduct operations on different files at the same time.

##### Quorums

In order to reduce the number of transactions required to maintain read and write consistency form the client's perspective we have implemented *Gifford's Quorum Based Protocol*. This queries a subset of servers for writes and requires a subset of servers to agree on writes in order to be consistent. So long as the quorum sizes adhere to these rules sequential consistency will be intact:

  

Q<sub>R</sub> is the reading quorum, Q<sub>W</sub> - is the writing quorum, and N is the number of servers in the system.

* Q<sub>W</sub> > N/2

* Q<sub>R</sub> + Q<sub>W</sub> > N

The servers that are selected to be a part of the quorums are randomly selected.


## Output

All output is redirected to a log in the `Distributed-File-System/app/log` directory. Server logs are enumerated based on their configuration file like so `server1.txt`. The coordinator will be one of these servers and has logging messages for both the server side and coordinator side of its implementation.

  

Client logs are of the form `clientLog_<N>.txt`. `<N>` is the next available log number. If `clientLog_1.txt` exists then the next client created will have log `clientLog_2.txt`. The log designated to a given client is announced to the terminal upon starting the client. Like so:

```
All output directed to: log/clientLog_0.txt
```
The servers log each read() and write() call that it receives. The coordinator makes read calls to the servers in order to build a quorum as well as find the most updated version of a file in order to execute a write request. Each write call is done to update a file for that server. Therefore there are more read log messages than write ones. This way, the user can look at the reads and writes and see that they are properly giving the most updated values.

The coordinator logs each read() and write() call that comes in from a client. It logs the file of the request, the server that the value was found or the servers that the value was updated on, and the answer (version number for read and status for write). These are all designated with a 'COORDINATOR:' at the beginning of the line. The server that acts as the coordinator has both these coordinator and server log statements.
  

# Running

In order to run our distributed file distributed you must do a few things:

* Setup your environment

* Familiarize yourself with and setup the configuration documents `machines.txt` and `config.txt`.

* Understand how to issue commands to a client

* Start the system
  
  

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

Here is an example script for setting up the environment variables.

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

1.  `servers 7` - there should be 7 servers in this system
2.  `read 6` - the read quorum should be composed of 6 servers
3.  `write 4` - the write quorum should be composed of 4 servers
4.  `files 10` - ten files should exist in each server. IDs will be 0-9.


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

*  `server-type` - can only be `server` or `coordinator`. There has to be one and only one coordinator in any functioning app.

*  `hostname/ip` - this, in tandem with the port num, is how the server will be found by other participants in the system. There must be a server running on that machine for the app to function.

*  `port` - the port the server is listening on. The server running on the related `hostname/ip` will also use this value for setup.

*  `id` - the unique integer ID the server uses in the system.
  

**Remember: the number of servers declared in this file must match the number of servers declared in the `config.txt` file**

  

## Commands

There are four commands a client may issue to the file system. All output is directed to the given client's log. Read more about logging in the **Output** section above.

*  **READ** the version number of a file with **integer** id `n`.
	```
	read n
	```
    
*  **Write** or essentially increment the version of the file with **integer** id `n`.
	```
	write n
	```
  
*  **Print** the structure of the entire distributed file system. This includes each server and their files.
	```
	print
	```
 
*  **Check** the consensus version of each file. It also prints the number of servers that agreed with that value along side the current write quorum size.
	```
	check
	```
	**Example Output**
	```
	File [3]:
	- Version: 90
	- confirmed by: 4/4 servers
	```
  
### `app/commands/commands.txt`
---
Within the `commands.txt` file you may list the commands you want to give to the system via a client. This file is located with the `commands` directory which contains a few other command files. These other command files are used in some of the tests we have provided. Please only edit `commands.txt` and it will be used by the `ant client` command. The `ant` targets and other tests will be detailed next.
```
app
 |-- commands
 |   |-- commands.txt           <-- edit me
 |   |-- commandsCheck.txt      <-- don't edit
 |   |-- commandsMixed.txt      <-- don't edit
 |   |-- commandsReadHeavy.txt  <-- don't edit
 |   |-- commandsWriteHeavy.txt <-- don't edit
```


## Start the System
There are three ways to do this:
1. Automated Tests
2. Automated system start up with custom clients
3. Customized

### Automated Tests
Check out the `ssh` directory. 
```
Distributed-File-Server
|-- app
|-- ssh
|   |-- custom
|   |-- mixed        <-- test
|   |-- readHeavy    <-- test
|   |-- writeHeavy   <-- test
```
Within you will find four directories, each containing a `ssh_commands.sh` and a `ssh_cleanup.sh` script. The automated tests are the last 3 directories. They will each start 7 servers, 3 transaction performing clients, and then a 4th client that checks the final values.
* `mixed` - transacting clients run 250 reads and 250 writes each
* `readHeavy` - transacting clients perform 500 reads each
* `writeHeavy` - transacting clients perform 500 writes each

These scripts are setup for UMN machines but you could replace the machine names with any machine you desire to use. Be sure to follow these steps.

**Steps**
1. Follow the **Setup** section near the top of this document. Complete all steps.
2. Setup your test ssh scripts with the machines you will use. If you have access to UMN machines do not change anything.
3. Make sure `machines.txt` uses the same machines you will start in the `ssh_commands.sh`
4. Make sure `config.txt` is setup correctly. (see **Configuration** section for help with these two documents) You may want to alter quorum sizes but make sure they follow the **Quorum** guidelines as well.
5. Run command `source ssh_commands.sh` on the test directory entry of you choosing.
6. Wait for the final check client to complete. View the logs in `app/log`. For details about output see the **Output** section.
7. Run `source ssh_cleanup.sh` to cleanup processes on all machines you used.

### Automated System Startup & Custom Clients
Within `ssh/custom` there are two more ssh scripts. These scripts startup and cleanup a set of 7 servers. They do not run any clients. From here you can follow the `ant` commands detailed in **Customized** below. This will allow you to run as many clients as you want with any custom command sets.

**Steps**
1. Follow the **Setup** section near the top of this document. Complete all steps.
2. Setup your test ssh scripts with the machines you will use. If you have access to UMN machines do not change anything.
3. Make sure `machines.txt` uses the same machines you will start in the `ssh_commands.sh`
4. Make sure `config.txt` is setup correctly. (see **Configuration** section for help with these two documents) You may want to alter quorum sizes but make sure they follow the **Quorum** guidelines as well.
5. Run command `source ssh_commands.sh` within `ssh/custom`.
6. Wait for the all servers to start. You will see their logs in `app/log`.
7. Modify `commands.txt` to contain the command you want run.
8. Run `ant client` within `app`.
9. Repeat. Also feel free to run any other client command detailed below. 
8. When you are done, run `source ssh_cleanup.sh` to cleanup processes on all server machines you used.

## Customized
You can be responsible for starting all machines! All entities configure themselves with the details of `machines.txt` and `config.txt`. There are a few build commands you can use.

* `ant server` - starts a server 

* `ant client` - run a client with the `commands.txt` command set.

* `ant clientReadHeavy` - run client who performs 500 random reads

* `ant clientWriteHeavy` - run client who performs 500 random writes

* `ant clientMixed` - run client who performs 250 random reads and 250 random writes

* `ant checkClient` - run client that performs `check` command


Make sure you know what you are doing! Read the **Setup**, **Configuration**, and **Commands** sections.

## Example Run of Automated Server and Custom Clients


## Design Details

### Servers and Coordinator


#### ServerHandler
Manages all of the RPC calls that come in from the coordinator or clients. Heavily utilizes its `ServerManager` or `Coordinator` field depending on what type of server it is (ordinary or coordinator).

##### RPC Functions defined in Serverhandler
* WriteResponse  ClientWrite(int  fileId): 
	* Client calls this to  contact a random server. The server handles the client's write request

* WriteResponse  ServerWrite(int  fileId)
	* A server calls this onto the coordinator of the system, the coordinator handles the client's write request

* WriteResponse  CoordWrite(File  file)
	* The coordinator calls this onto servers of the system, the server executes the write call the certain file and returns a status

* ReadResponse  ClientRead(int  fileId)
	* Client calls this to contact a random server. The server handles the client's read request

* ReadResponse  ServerRead(int  fileId)
	* A server calls this onto the coordinator of the system, the coordinator handles the client's read request

* ReadResponse  CoordRead(int  fileId)
	* The coordinator calls this onto servers of the system, the server executes the read call on the specific file and returns the file.

* StructResponse  ClientGetStruct()
	*  Client calls this to contact a random server. The server handles the client's get structure request

* StructResponse  ServerGetStruct()
	* A server calls this onto the coordinator of the system, the coordinator handles the client's get structure request

* FolderResponse  CoordGetFolder()
	* The coordinator calls this onto servers of the system, the server gives the coordinator its folder structure which contains all of its file objects.

#### ServerManager
This is a field used by the ServerHandler and Coordinator. This object manages all of the read and write requests that the server receives. 

#### Coordinator
This field is used by the `ServerHandler` if its a coordinator server. This handles everything the Coordinator of the system does. It receives requests and then builds read or write quorums. For reads, it scans the servers of the quorum and returns the file that contains the highest version number. For writes, it updates the specific file that each server of the quorum has. This class has a `Servermanager` field that keeps track of its own server duties. If this is the case, the `ServerHandler` that created this `Coordinator` keeps its own `ServerManager` field null since the coordinator uses it instead. Coordiantor makes use of a `SemHelper` class to utilize semaphores for each file to ensure mutual exclusivity.

#### SemHelper
This class takes in an int which is the number of files that need semaphores. It creates an array of semaphores where the indices correspond to each file. This class is used by the coordinator to manage a semaphore for each file it is responsible for. This class has functions that the `Coordinator` can use to wait for and signal semaphores for each file. This allows the coordinator to ensure mutual exclusivity for the files and proper ordering since the semaphore is defined to keep its queue in FIFO order.


