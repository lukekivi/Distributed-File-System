namespace java pa3;

/**
 * Request type
 */
enum RequestType {
    SEND = 0,
    READ = 1
}

/**
 * Request data structure
 */
struct Request {
    1: RequestType type,
    2: string file
}

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
 * Response to a read request
 */
struct ReadResponse {
    1: 
}

/**
 * Response to a write request
 */
struct WriteResponse {
    1: Status status,
    2: string msg
}

/**
 * Data structure to represent server info
 */
struct ServerInfo {
    1: string ip,
    2: i32 port
}