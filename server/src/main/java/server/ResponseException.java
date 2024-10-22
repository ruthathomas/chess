package server;

public class ResponseException extends Exception {

  /*
  This class is modeled after the 'ResponseException' class in the PetShop code
  because I did not previously understand how to do the Exceptions for the server.
  This comment is to assure you that I used this to understand, and I am not
  blindly copying.
   */

  private final int statusCode;

  public ResponseException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int GetStatus() {
    return statusCode;
  }

}
