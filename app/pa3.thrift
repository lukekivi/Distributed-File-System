namespace java pa3

/**
 * App status
 * -   SUCCESS: everything went ok
 * -     ERROR: something went wrong
 * - NOT_FOUND: the file was not found
 */
enum Status {
    SUCCESS = 0,
    ERROR = 1,
    NOT_FOUND = 2
}


/**
 * File data structure
 */
struct File {
    1: i32 id,
    2: i32 version
}


/**
 * Folder is used to pass files via RPC
 */
struct Folder {
    1: list<File> files
}


/**
 * Response to a read request
 */
struct ReadResponse {
    1: File file,
    2: Status status,
    3: string msg
}

/**
 * Response to a write request
 */
struct WriteResponse {
    1: Status status,
    2: string msg
}


/**
 * Response with an individual server's folder
 */
struct FolderResponse {
    1: Folder folders,
    2: Status status,
    3: string msg
}


/**
 * Response with structure of entire distributed file system
 */
struct StructResponse {
    1: list<Folder> folders,
    2: Status status,
    3: string msg
}


/**
 * Server is responsible for Coordinator tasks as well
 */
service Server {
    WriteResponse ClientWrite(1: i32 fileId),
    WriteResponse ServerWrite(1: i32 fileId),
    WriteResponse CoordWrite(1: i32 fileId),
    ReadResponse ClientRead(1: i32 fileId),
    ReadResponse ServerRead(1: i32 fileId),
    ReadResponse CoordRead(1: i32 fileId),
    StructResponse ClientGetStruct(),
    StructResponse ServerGetStruct(),
    FolderResponse CoordGetFolder()
}