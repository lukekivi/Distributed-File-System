# Design Specs
## File System

Files are named using ids which are ints. Each file is created with an incrementing id thats in the range of 0 to n-1 where n is the number of files that each server is to contain.

### File
A file is a thrift defined object that is made up of an `id:int` and a `version:int`. The id is used to identify the file and the version field is used to figure out how up-to-date the file currently is.
### Folder
A folder is a thrift defined object that contains a `serverId:int` and a `List<File>` . Folders are used in order for the servers to send their current file layout to the coordinator as well as show the client.

## RPC Calls
Encapsulated in ServerComm class.
All functions defined inside the `ServerHandler` class
#### RPC Functions 
* WriteResponse  ClientWrite(int  fileId): 
	* Client calls this to  contact a random server. The server handles the client's write request

* WriteResponse  ServerWrite(int  fileId)
	* A server calls this onto the coordinator of the system, the coordinator handles the client's write request

* WriteResponse  CoordWrite(File  file)
	* The coordinator calls this onto servers of the system, the server executes the write call on the certain file and returns a status

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

## Coordinator
This field is used by the `ServerHandler` if it's a coordinator server. This handles everything the Coordinator of the system does. It is multi-threaded so it can work on multiple requests at once. Only one thread can work on each file at once, however. We use semaphores to ensure the mutual exclusion of the files access.
* It receives client requests from the servers and then builds read or write quorums. It does this by randomly selecting servers from the server list. It uses the read/write qourum number specified in the config file to determine how big the quorum should be.
* For reads, it scans the servers of the quorum and returns the most recent file, this is the file with the largest version number.
* For writes, it scans the write quorum servers for the the file that's most up-to-date. It then takes that file, updates it, then sends it to each of the quorum servers and updates each of their instances of the file. 

This class has a `Servermanager` field that keeps track of its own server duties. If this is the case, the `ServerHandler` that created this `Coordinator` keeps its own `ServerManager` field null since the coordinator uses it instead. Coordiantor makes use of a `SemHelper` class to utilize semaphores for each file to ensure mutual exclusivity. 

## SemHelper
This class's constructor takes in an int which is the number of files that need semaphores. It creates an array of semaphores where the indices correspond to each file (e.g file 2's semaphore would be `sems[2]`. This class is used by the coordinator to manage a semaphore for each file it is responsible for. This class has functions that the `Coordinator` can use to wait for and signal semaphores for each file. When a thread waits for a file's semaphore, it goes to sleep and is placed in a queue. Then whenever a thread signals that semaphore, the next-up sleeping thread in the semaphore's queue is awakened and continues. Once a thread finishes its operation, it will signal the semaphore (giving up the lock). This allows the coordinator to ensure mutual exclusivity for the files and proper ordering since the semaphore is defined to keep its queue in FIFO order.

## Server
### Server Summary
* Each server will be contacted by a client with requests. The server will send these requests to the coordinator (unless it is the coordinator, in which case it will handle them themselves). Once it receives a result from the coordinator for a request, it will send it back to the client
* Each server can also be contacted by the coordinator. In these instances, the server will either read one of its files for the coordinator or it will apply a write call to one of its files in which case the file version number updates
* The coordinator can also contact a server to request the structure of the server (returns a folder which is a  thrift-defined object that contains all of the file objects)


### ServerHandler
Manages all of the RPC calls that come in from the coordinator or clients. Heavily utilizes its `ServerManager` or `Coordinator` field depending on what type of server it is (ordinary or coordinator).

### ServerManager
This is a field used by the ServerHandler and Coordinator. This object manages all of the read and write requests that the server receives. 

## Server & Coordinator Logging

The servers log each read() and write() call that it receives. The server logs are all designated with a 'COORDINATOR:' at the beginning of the line. The coordinator makes read calls to the servers in order to find the most updated version of a file so it can execute a write request. Each write call is done to update a file for that server. Therefore there are more read log messages than write ones. This way, the user can look at the reads and writes and see that they are properly giving the most updated values.

The coordinator logs each read() and write() call that comes in from a client. It logs the file of the request, the server that the value was found or the servers that the value was updated on, and the answer (version number for read and status for write). These are all designated with a 'COORDINATOR:' at the beginning of the line. The server that acts as the coordinator has both these coordinator and server log statements.

# Client
Needs to time itself.

