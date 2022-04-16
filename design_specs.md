# Design Specs

## Servers and Coordinator


### ServerHandler
Manages all of the RPC calls that come in from the coordinator or clients. Heavily utilizes its `ServerManager` or `Coordinator` field depending on what type of server it is (ordinary or coordinator).

#### RPC Functions defined in Serverhandler
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

### ServerManager
This is a field used by the ServerHandler and Coordinator. This object manages all of the read and write requests that the server receives. 

### Coordinator
This field is used by the `ServerHandler` if its a coordinator server. This handles everything the Coordinator of the system does. It receives requests and then builds read or write quorums. For reads, it scans the servers of the quorum and returns the file that contains the highest version number. For writes, it updates the specific file that each server of the quorum has. This class has a `Servermanager` field that keeps track of its own server duties. If this is the case, the `ServerHandler` that created this `Coordinator` keeps its own `ServerManager` field null since the coordinator uses it instead. Coordiantor makes use of a `SemHelper` class to utilize semaphores for each file to ensure mutual exclusivity.

### SemHelper
This class takes in an int which is the number of files that need semaphores. It creates an array of semaphores where the indices correspond to each file. This class is used by the coordinator to manage a semaphore for each file it is responsible for. This class has functions that the `Coordinator` can use to wait for and signal semaphores for each file. This allows the coordinator to ensure mutual exclusivity for the files and proper ordering since the semaphore is defined to keep its queue in FIFO order.












## Server & Coordinator Logging

The servers log each read() and write() call that it receives. The coordinator makes read calls to the servers in order to build a quorum as well as find the most updated version of a file in order to execute a write request. Each write call is done to update a file for that server. Therefore there are more read log messages than write ones. This way, the user can look at the reads and writes and see that they are properly giving the most updated values.

The coordinator logs each read() and write() call that comes in from a client. It logs the file of the request, the server that the value was found or the servers that the value was updated on, and the answer (version number for read and status for write). These are all designated with a 'COORDINATOR:' at the beginning of the line. The server that acts as the coordinator has both these coordinator and server log statements.






***** OLD STUFF *****

# RPC Calls
Encapsulated in ServerComm class.

# Coordinator
* Commandline argument that indicates if this server is a coordinator. In start up if isCoordinator tell handler to build Coordinator object.
* Maintains a queue of requests.

# Server
In order for a server to join they must contact the coordinator and receive an OK.

# File System

## File
Each file has two fields: name and version.

# Client
Needs to time itself.

# Questions 
Are Servers supposed to start with all of the same files? yes

What is returned by read operations? version number

Is this UI supposed to be dynamically updated? query at any point mainly at the end of a set of reads and writes

How to lock files?

2. "If there are insufficient up-to-date copies within the quorum, the write operation should be applied to the ones with latest version numbers and the outdated copies should be replaced with this latest copy."
* What does insufficient mean here? Every thing in write quorom gets updated to latest version
* What is being written? In 3 it updates version numbers.


3. "After performing the write operation the version numbers should be incremented,
and the completion should be notified back to the requesting client. The
remaining replicas (outside of the quorum) can then be updated in the background."
* How to update the others in the background? Update them lazily not necessarilly inline.
* All of the others need to be updated?
