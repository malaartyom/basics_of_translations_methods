package ParserImplementation
import syspro.tm.lexer.Token
import syspro.tm.parser.{AnySyntaxKind, Diagnostic, DiagnosticInfo, ParseResult, SyntaxNode, TextSpan}

import java.util
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*




class MyParseResult(rootKind: AnySyntaxKind, rootToken: Token = null) extends ParseResult {
  private val rootNode: MySyntaxNode = MySyntaxNode(rootKind, rootToken)
  private val invalid_ranges: ListBuffer[TextSpan] = ListBuffer[TextSpan]()
  private val diagnostic: ListBuffer[Diagnostic] = ListBuffer[Diagnostic]()

  override def root(): MySyntaxNode = rootNode

  override def invalidRanges(): util.Collection[TextSpan] = invalid_ranges.asJavaCollection

  override def diagnostics(): util.Collection[Diagnostic] = diagnostic.asJavaCollection
  
  def addToRoot(mySyntaxNode: SyntaxNode): ListBuffer[SyntaxNode] = rootNode.add(mySyntaxNode)
  
  def addInvalidRange(start: Int, end: Int): ListBuffer[TextSpan] =
    println(TextSpan.fromBounds(start , end))
    invalid_ranges.append(TextSpan.fromBounds(start , end))
  
  def addInvalidRange(textSpan: TextSpan): ListBuffer[TextSpan] = invalid_ranges.append(textSpan)
  
  def addDiagnostic(textSpan: TextSpan, code: Int) =
    val diag = Diagnostic(DiagnosticInfo(MyErrorCode(code), null), textSpan, List[Diagnostic]().asJava)  
    diagnostic.append(diag)
  
}
