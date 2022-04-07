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
## Folder
Folders maintain a list of `File`s


# Client
Needs to time itself.

# Questions 
Are Servers to supposed to start with all of the same files?

What is returned by read operations?

Is this UI supposed to be dynamically updated?

How to lock files?

2. "If there are insufficient up-to-date copies within the quorum, the write operation should be applied to the ones with latest version numbers and the outdated copies should be replaced with this latest copy."
* What does insufficient mean here?
* What is being written? In 3 it updates version numbers.


3. "After performing the write operation the version numbers should be incremented,
and the completion should be notified back to the requesting client. The
remaining replicas (outside of the quorum) can then be updated in the background."
* How to update the others in the background?
* All of the others need to be updated?