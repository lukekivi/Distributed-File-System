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

# Running

In order to run our distributed file distributed you must do a few things:

* Setup your environment

* Familiarize yourself with and setup the configuration documents `machines.txt` and `config.txt`

* Understand how to issue commands to a client

* Start the system
  
  

## Setup

1. Make sure you have access to UMN lab machines.
2. Make sure you have [key-based-authentication](https://cse.umn.edu/cseit/self-help-guides/secure-shell-ssh) setup on lab machines. You should set it up so there is no password entry required.  
3. Make sure you have a current JDK, JRE, and `Thrift` installed.

4. Download the application. If you need a fresh version you can find it here:

```
git clone https://github.com/lukekivi/Distributed-File-System.git
```

5. Set the required environment variables:

```
export THRIFT_COMPILER_PATH=/<absolute path to thrift compiler>
export THRIFT_LIB_PATH=/<absolute path to thrift java libs>
```

6. If you want to run tests using our ssh scripts you will want to set these environment variables as well. The `DFS_USERNAME` should be the username you use to ssh into the machines you will be using.

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
These files relieve users of most startup logic. You build the requirements of the system in these two files and you are responsible for starting the machines to fulfill those requirements. Since configuration is automated, you can simply navigate to the machines you have declared and run `ant server`. The build targets will be detailed in the next section.

### `app/config/config.txt`
---

This file contains foundational information about the system you want to setup. It is the source of truth for four things: how many servers there are supposed to be, write quorum size, read quorum size, and the number of files in the system.

  

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

Here is where you declare the hostnames or ip addresses, port numbers, and IDs of each server and the coordinator. This is the source of truth for which machines must be acting as servers, and which server is the coordinator. The server setup on the machine with the `coordinator` label will set itself up as a coordinator.

  

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


## Build Targets
We automated entity start-ups for ease-of-use. You can simply follow the **Configuration** guidelines and run these build commands. However, the one trade off is that you cannot run servers on the same machine with different port numbers. There must be one machine per server. Clients can all be run on the same machine.

Navigate to the `app` directory to run these commands:

* `ant server` - starts a server (servers self-setup as coordinators based on the `machine.txt` file)

* `ant client` - run a client with the `commands.txt` command set.

* `ant clientReadHeavy` - run client who performs 500 random reads

* `ant clientWriteHeavy` - run client who performs 500 random writes

* `ant clientMixed` - run client who performs 250 random reads and 250 random writes

* `ant checkClient` - run client that performs `check` command


*Note: there are automated tests and system startup scripts we provide. They are detailed at the end of this document.*

### Server Steps
1. Declare the total number of servers you will start in `config.txt`
2. Declare quorum sizes and number of files in `config.txt`
3. Declare server-type, the machine you will use, an available port number, and the unique server ID in `machines.txt`
4. ssh to or otherwise access the machine you just declared
5. Be sure this machine has the up-to-date config files.
6. Navigate to `Distributed-File-System/app`
7. Run `ant server` 
8. Check terminal for any initial errors
9. Check logs for runtime output

### Client Steps
1. Modify `commands.txt` to contain the commands you want to run or just use the default command sets we provide. (read **Commands** section below)
2. Be sure all servers are running
3. Be sure the machine you are working on contains the up-to-date `config.txt` and `machines.txt`
4. Navigate to `Distributed-File-System/app`
5. Run `ant client` (or another client target if you are using the provided command sets)
6. Note the log file name printed to terminal
7. View the log file for runtime output

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
Within the `commands.txt` file you may list the commands you want to give to the system via `ant client`. This file is located within the `commands` directory which contains a few other command files. These other command files are used in the tests we have provided. Please only edit `commands.txt`.
```
app
 |-- commands
 |   |-- commands.txt           <-- edit me
 |   |-- commandsCheck.txt      <-- don't edit
 |   |-- commandsMixed.txt      <-- don't edit
 |   |-- commandsReadHeavy.txt  <-- don't edit
 |   |-- commandsWriteHeavy.txt <-- don't edit
```


## Automated System Startup
There are two options:
1. Automated tests
2. Automated system start up with custom clients

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
Within you will find four directories, each containing a `ssh_commands.sh` and a `ssh_cleanup.sh` script. The automated tests are the last 3 directories. They will each start 7 servers, 3 transaction performing clients, and then a 4th client that checks the final versions of files.

**Tests**
* `mixed` - transacting clients run 250 reads and 250 writes each
* `readHeavy` - transacting clients perform 500 reads each
* `writeHeavy` - transacting clients perform 500 writes each

**Example line from `ssh_commands.sh`**
```
ssh -f $DFS_USERNAME@csel-kh1250-11.cselabs.umn.edu "cd $PROJ_PATH; ant server"
```
It uses the environment variables you setup in the **Setup** section and the machines must be matched by the ones you declare in `machines.txt`. `-f` allows the ssh call to be run in the background which means destroying processes might be difficult. This is why we provide `ssh_cleanup.sh`.

**Steps**
1. Follow the **Setup** section near the top of this document. Complete all steps. The UMN machines have shared memory so all machines will be able to see the same config docs.
2. Setup your test ssh scripts with the machines you will use. You can likely leave the script as is.
3. Make sure `machines.txt` uses the same machines you will start in the `ssh_commands.sh`
4. Make sure `config.txt` is setup correctly. You may want to alter quorum sizes.
5. Optionally delete logs from `app/log` to reduce clutter
6. Run command `source ssh_commands.sh` on the test directory entry of you choosing.
7. Wait for the final check client to complete. View the logs in `app/log`. For details about output see the **Output** section.
8. Run `source ssh_cleanup.sh` to cleanup processes on all machines you used.
9. In between you may want to clean up the client logs. Server logs will be replaced with new ones but client logs will build up as they are named sequentially.

### Automated System Startup & Custom Clients
Within `ssh/custom` there are two more ssh scripts. These scripts startup and cleanup a set of 7 servers. They do not run any clients. From here you can follow the `ant` commands detailed in **Customized** below. This will allow you to run as many clients as you want with any custom command sets.

**Steps**
1. Follow the **Setup** section near the top of this document. Complete all steps.
2. Setup your test ssh scripts with the machines you will use. If you have access to UMN machines do not change anything.
3. Make sure `machines.txt` uses the same machines you will start in the `ssh_commands.sh`
4. Make sure `config.txt` is setup correctly. You may want to alter quorum sizes.
5. Optionally delete logs from `app/log` to reduce clutter
6. Run command `source ssh_commands.sh` within `ssh/custom`.
7. Wait for the all servers to start. You will see their logs in `app/log`.
8. Navigate to `app` directory
9. Modify `commands.txt` to contain the command you want run.
10. Run `ant client` (or any provided ant client target)
11. Repeat to your hearts content.
12. When you are done, run `source ssh_cleanup.sh` to cleanup processes on all server machines you used.


