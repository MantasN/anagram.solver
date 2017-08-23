package helpers

import java.security.MessageDigest

object Cryptography {
  def getMD5Hash(value: String) = {
    MessageDigest
      .getInstance("MD5")
      .digest(value.getBytes())
      .map(0xFF & _)
      .map("%02x".format(_))
      .mkString
  }
}
