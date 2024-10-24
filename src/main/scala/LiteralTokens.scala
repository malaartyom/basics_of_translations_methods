import Boolean.BOOLEAN
import Integer.INTEGER
import Integer.INT_SUFFIX
import Runes.RUNE
import StringLiteral.STRING
import syspro.tm.lexer.BuiltInType

case object LiteralTokens {

  def isBoolean(s: String): Boolean = BOOLEAN.matches(s)

  def isInteger(s: String): Boolean = INTEGER.matches(s)

  def isRune(s: String): Boolean = RUNE.matches(s)

  def isString(s: String): Boolean = STRING.matches(s)

  def isSuffix(s: String): Boolean = INT_SUFFIX.matches(s)

  def hasSuffix(s: String): Boolean  = INT_SUFFIX.matches(s.slice(s.length - 3, s.length))

  def getSuffix(s: String, hasSuffix: Boolean): BuiltInType = {
    if (!hasSuffix) return BuiltInType.INT64
    val suffix = s.slice(s.length - 3, s.length)
    suffix match {
      case "i32" => BuiltInType.INT32
      case "i64" => BuiltInType.INT64
      case "u32" => BuiltInType.UINT32
      case "u64" => BuiltInType.UINT64}
  }

  def getInt(s: String, hasSuffix: Boolean): Long =
    if (!hasSuffix) {
      return s.toLong
    }
    s.slice(0, s.length - 3).toLong

  def getBoolean(s: String): Boolean =
    s match {
      case "false" => false
      case "true" => true
    }
}
