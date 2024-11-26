import LexerImplementation.Tokenizer
import ParserImplementation.MySyntaxNode

import syspro.tm.lexer.{IdentifierToken, SymbolToken, Symbol, Keyword}
import syspro.tm.parser.{SyntaxKind, AnySyntaxKind}
import scala.jdk.CollectionConverters.*


class ParserTests extends munit.FunSuite {
  test("General Parser Tests") {
    val t = Tokenizer()
    val s = "class Identifier0<T <: U & V, G <: X & Y>  <: Object0"
    val myParser = ParserImplementation.MyParser()
    val r = myParser.parse(s)
    val firstTypeDef = r.root().slot(0).slot(0)

    assertEquals(r.root().kind(), SyntaxKind.SOURCE_TEXT)
    assertEquals(firstTypeDef.kind, SyntaxKind.TYPE_DEFINITION)
    assertEquals(firstTypeDef.slot(0).kind(), Keyword.CLASS)
    assertEquals(firstTypeDef.slot(1).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(firstTypeDef.slot(2).kind(), Symbol.LESS_THAN)
    assertEquals(firstTypeDef.slot(3).kind(), SyntaxKind.SEPARATED_LIST)

    val sepList = firstTypeDef.slot(3)
    assertEquals(sepList.slot(0).kind(), SyntaxKind.TYPE_PARAMETER_DEFINITION)

    val firstTypeParamDef = sepList.slot(0)

    assertEquals(firstTypeParamDef.slot(0).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(firstTypeParamDef.slot(1).kind(), SyntaxKind.TYPE_BOUND)

    val firstTypeBound = firstTypeParamDef.slot(1)

    assertEquals(firstTypeBound.slot(0).kind(), Symbol.BOUND)
    assertEquals(firstTypeBound.slot(1).kind(), SyntaxKind.SEPARATED_LIST)


    val separatedListTypeBound = firstTypeBound.slot(1)

    assertEquals(separatedListTypeBound.slot(0).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(separatedListTypeBound.slot(1).kind(), Symbol.AMPERSAND)
    assertEquals(separatedListTypeBound.slot(2).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)

    //    val sepList1 = firstTypeDef.slot(1)
    //
    //    assertEquals(sepList1.slot(0).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)


    assertEquals(sepList.slot(1).kind(), Symbol.COMMA)
    assertEquals(sepList.slot(2).kind(), SyntaxKind.TYPE_PARAMETER_DEFINITION)

    assertEquals(firstTypeDef.slot(4).kind(), Symbol.GREATER_THAN)
    assertEquals(firstTypeDef.slot(5).kind(), SyntaxKind.TYPE_BOUND)


  }

  test("NameExpression") {

    val t = Tokenizer()
    val s = "name0< ?name1, name2>"
    val myParser = ParserImplementation.MyParser()
    val r = t.lex(s)
    println()
    val nameExpr = myParser.matchNameExpression(r.asScala.toVector)

    assertEquals(nameExpr.kind(), SyntaxKind.GENERIC_NAME_EXPRESSION)

    assertEquals(nameExpr.slot(0).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(nameExpr.slot(1).kind(), Symbol.LESS_THAN)
    assertEquals(nameExpr.slot(2).kind(), SyntaxKind.SEPARATED_LIST)

    val sepList = nameExpr.slot(2)
    assertEquals(sepList.slot(0).kind(), SyntaxKind.OPTION_NAME_EXPRESSION)
    assertEquals(sepList.slot(1).kind(), Symbol.COMMA)
    assertEquals(sepList.slot(2).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)


    assertEquals(nameExpr.slot(3).kind(), Symbol.GREATER_THAN)

  }

  test("Func Definition") {
    val t = Tokenizer()
    val s = "override abstract virtual native def f()\n  break"
    val tokens = t.lex(s)


    val p = ParserImplementation.MyParser()
    val r = p.matchFuncDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.FUNCTION_DEFINITION)
    // TODO: Tests for defenitions
  }

  test("Statements") {

  }
}

class TypeParamDefinitionTests extends munit.FunSuite {
  test("Simple TypeParamDefinitionTests") {
    val t = Tokenizer()
    val s = "func"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchTypeParamDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.TYPE_PARAMETER_DEFINITION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), SyntaxKind.IDENTIFIER)


  }

  test("Basic TypeParamDefinitionTests") {
    val t = Tokenizer()
    val s = "T <: U & V & G"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchTypeParamDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.TYPE_PARAMETER_DEFINITION)
    assertEquals(r.slotCount(), 2)
    assertEquals(r.slot(0).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(r.slot(1).kind(), SyntaxKind.TYPE_BOUND)

    val typeBound = r.slot(1)

    assertEquals(typeBound.slotCount(), 2)
    assertEquals(typeBound.slot(0).kind(), Symbol.BOUND)
    assertEquals(typeBound.slot(1).kind(), SyntaxKind.SEPARATED_LIST)

    val sepList = typeBound.slot(1)

    assertEquals(sepList.slotCount(), 5)
    assertEquals(sepList.slot(0).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(sepList.slot(1).kind(), Symbol.AMPERSAND)
    assertEquals(sepList.slot(2).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(sepList.slot(3).kind(), Symbol.AMPERSAND)
    assertEquals(sepList.slot(4).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
  }

  test("Complicated TypeParamDefinitionTests") {
    val t = Tokenizer()
    val s = "T <: U<X, Y> & ?V & G"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchTypeParamDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.TYPE_PARAMETER_DEFINITION)
    assertEquals(r.slotCount(), 2)
    assertEquals(r.slot(0).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(r.slot(1).kind(), SyntaxKind.TYPE_BOUND)

    val typeBound = r.slot(1)

    assertEquals(typeBound.slotCount(), 2)
    assertEquals(typeBound.slot(0).kind(), Symbol.BOUND)
    assertEquals(typeBound.slot(1).kind(), SyntaxKind.SEPARATED_LIST)

    val sepList = typeBound.slot(1)

    assertEquals(sepList.slotCount(), 5)
    assertEquals(sepList.slot(0).kind(), SyntaxKind.GENERIC_NAME_EXPRESSION)
    assertEquals(sepList.slot(1).kind(), Symbol.AMPERSAND)
    assertEquals(sepList.slot(2).kind(), SyntaxKind.OPTION_NAME_EXPRESSION)
    assertEquals(sepList.slot(3).kind(), Symbol.AMPERSAND)
    assertEquals(sepList.slot(4).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
  }

}

class ParamDefinitionTests extends munit.FunSuite {
  test("ParamDefinitionTest") {

    val t = Tokenizer()
    val s = "function: name0< ?name1, name2, ?name3>"
    val tokens = t.lex(s)


    val p = ParserImplementation.MyParser()
    val r = p.matchParamDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.PARAMETER_DEFINITION)
    assertEquals(r.slotCount(), 3)
    assertEquals(r.slot(0).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(r.slot(1).kind(), Symbol.COLON)
    assertEquals(r.slot(2).kind(), SyntaxKind.GENERIC_NAME_EXPRESSION)

  }
}


class FunctionDefinitionsTests extends munit.FunSuite {
  test("without specifiers") {
    val t = Tokenizer()
    val s = "def f()\n  break"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchFuncDef(tokens.asScala.toVector)

  }
  test("Simple Definition") {
    val t = Tokenizer()
    val s = "override abstract virtual native def f()\n  break"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchFuncDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.FUNCTION_DEFINITION)
    assertEquals(r.slotCount(), 11)
    assertEquals(r.slot(0).kind(), Keyword.OVERRIDE)
    assertEquals(r.slot(1).kind(), Keyword.ABSTRACT)
    assertEquals(r.slot(2).kind(), Keyword.VIRTUAL)
    assertEquals(r.slot(3).kind(), Keyword.NATIVE)
    assertEquals(r.slot(4).kind(), Keyword.DEF)
    assertEquals(r.slot(5).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(r.slot(6).kind(), Symbol.OPEN_PAREN)
    assertEquals(r.slot(7).kind(), Symbol.CLOSE_PAREN)
    assertEquals(r.slot(8).kind(), SyntaxKind.INDENT)
    assertEquals(r.slot(9).kind(), SyntaxKind.LIST)
    assertEquals(r.slot(10).kind(), SyntaxKind.DEDENT)

    // TODO: Tests for defenitions
  }


  test("Complicated Definition") {
    val t = Tokenizer()
    val s = "override abstract virtual native def f(x: name0, y: ?name1, z: name3<T>): name5<V> \n  break\n  continue\n  "
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchFuncDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.FUNCTION_DEFINITION)
    assertEquals(r.slotCount(), 14)
    assertEquals(r.slot(0).kind(), Keyword.OVERRIDE)
    assertEquals(r.slot(1).kind(), Keyword.ABSTRACT)
    assertEquals(r.slot(2).kind(), Keyword.VIRTUAL)
    assertEquals(r.slot(3).kind(), Keyword.NATIVE)
    assertEquals(r.slot(4).kind(), Keyword.DEF)
    assertEquals(r.slot(5).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(r.slot(6).kind(), Symbol.OPEN_PAREN)
    assertEquals(r.slot(7).kind(), SyntaxKind.SEPARATED_LIST)
    assertEquals(r.slot(8).kind(), Symbol.CLOSE_PAREN)
    assertEquals(r.slot(9).kind(), Symbol.COLON)
    assertEquals(r.slot(10).kind(), SyntaxKind.GENERIC_NAME_EXPRESSION)
    assertEquals(r.slot(11).kind(), SyntaxKind.INDENT)
    assertEquals(r.slot(12).kind(), SyntaxKind.LIST)
    assertEquals(r.slot(13).kind(), SyntaxKind.DEDENT)
  }
}

class VariableDefinitionTests extends munit.FunSuite {
  test("VarDef (1) ") {
    val t = Tokenizer()
    val s = "var x"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchVariableDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.VARIABLE_DEFINITION)
    assertEquals(r.slotCount(), 2)
    assertEquals(r.slot(0).kind(), Keyword.VAR)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER)

  }

  test("VarDef (2) ") {
    val t = Tokenizer()
    val s = "val x"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchVariableDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.VARIABLE_DEFINITION)
    assertEquals(r.slotCount(), 2)
    assertEquals(r.slot(0).kind(), Keyword.VAL)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER)

  }

  test("VarDef (3) ") {
    val t = Tokenizer()
    val s = "val x: name<T> =  name0"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchVariableDef(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.VARIABLE_DEFINITION)
    assertEquals(r.slotCount(), 6)
    assertEquals(r.slot(0).kind(), Keyword.VAL)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER)
    assertEquals(r.slot(2).kind(), Symbol.COLON)
    assertEquals(r.slot(3).kind(), SyntaxKind.GENERIC_NAME_EXPRESSION)
    assertEquals(r.slot(4).kind(), Symbol.EQUALS)
    assertEquals(r.slot(5).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)


  }
}

class StatementTests extends munit.FunSuite {
  test("break") {

    val t = Tokenizer()
    val s = "break"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.slotCount(), 1)
    assertEquals(r.kind(), SyntaxKind.BREAK_STATEMENT)
    assertEquals(r.slot(0).kind(), Keyword.BREAK)

  }

  test("continue") {

    val t = Tokenizer()
    val s = "continue"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.slotCount(), 1)
    assertEquals(r.kind(), SyntaxKind.CONTINUE_STATEMENT)
    assertEquals(r.slot(0).kind(), Keyword.CONTINUE)

  }


  test("return") {
    val t = Tokenizer()
    val s = "return"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.slotCount(), 1)
    assertEquals(r.kind(), SyntaxKind.RETURN_STATEMENT)
    assertEquals(r.slot(0).kind(), Keyword.RETURN)
  }

  test("return x") {
    val t = Tokenizer()
    val s = "return x"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.slotCount(), 2)
    assertEquals(r.kind(), SyntaxKind.RETURN_STATEMENT)
    assertEquals(r.slot(0).kind(), Keyword.RETURN)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
  }

  test("expression statement") {
    val t = Tokenizer()
    val s = "identifier"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.slotCount(), 1)
    assertEquals(r.kind(), SyntaxKind.EXPRESSION_STATEMENT)
    assertEquals(r.slot(0).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
  }

  test("assignment statement") {

    val t = Tokenizer()
    val s = "name0 = name1<T>"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)
    assertEquals(r.kind(), SyntaxKind.ASSIGNMENT_STATEMENT)
    assertEquals(r.slotCount(), 3)
    assertEquals(r.slot(0).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(r.slot(1).kind(), Symbol.EQUALS)
    assertEquals(r.slot(2).kind(), SyntaxKind.GENERIC_NAME_EXPRESSION)
  }

  test("variable definition statement") {

    val t = Tokenizer()
    val s = "var x = name1"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.VARIABLE_DEFINITION_STATEMENT)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), SyntaxKind.VARIABLE_DEFINITION)


  }

  test("if statement") {
    val t = Tokenizer()
    val s = "if x\n  break\n  continue\n  return\nelse\n  break\n  continue\n  return"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.IF_STATEMENT)
    assertEquals(r.slotCount(), 9)
    assertEquals(r.slot(0).kind(), Keyword.IF)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(r.slot(2).kind(), SyntaxKind.INDENT)
    assertEquals(r.slot(3).kind(), SyntaxKind.LIST)
    assertEquals(r.slot(4).kind(), SyntaxKind.DEDENT)
    assertEquals(r.slot(5).kind(), Keyword.ELSE)
    assertEquals(r.slot(6).kind(), SyntaxKind.INDENT)
    assertEquals(r.slot(7).kind(), SyntaxKind.LIST)
    assertEquals(r.slot(8).kind(), SyntaxKind.DEDENT)
  }

  test("while statement") {
    val t = Tokenizer()
    val s = "while x\n  break\n  continue\n  return\n"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.slotCount(), 5)
    assertEquals(r.slot(0).kind(), Keyword.WHILE)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(r.slot(2).kind(), SyntaxKind.INDENT)
    assertEquals(r.slot(3).kind(), SyntaxKind.LIST)
    assertEquals(r.slot(4).kind(), SyntaxKind.DEDENT)

  }

  test("for statement") {

    val t = Tokenizer()
    val s = "for x in a\n  break\n  continue\n  return\n"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchStatement(tokens.asScala.toVector)

    assertEquals(r.slotCount(), 7)
    assertEquals(r.slot(0).kind(), Keyword.FOR)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(r.slot(2).kind(), Keyword.IN)
    assertEquals(r.slot(3).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(r.slot(4).kind(), SyntaxKind.INDENT)
    assertEquals(r.slot(5).kind(), SyntaxKind.LIST)
    assertEquals(r.slot(6).kind(), SyntaxKind.DEDENT)


  }
}

class PrimaryTests extends munit.FunSuite {
  test("this") {

    val t = Tokenizer()
    val s = "this"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.THIS_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), Keyword.THIS)

  }

  test("super") {

    val t = Tokenizer()
    val s = "super"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.SUPER_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), Keyword.SUPER)

  }

  test("null") {

    val t = Tokenizer()
    val s = "null"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.NULL_LITERAL_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), Keyword.NULL)

  }

  test("boolean true") {
    val t = Tokenizer()
    val s = "true"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.TRUE_LITERAL_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), SyntaxKind.BOOLEAN)

  }

  test("boolean false") {
    val t = Tokenizer()
    val s = "false"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.FALSE_LITERAL_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), SyntaxKind.BOOLEAN)

  }
  test("rune") {
    val t = Tokenizer()
    val s = "'a'"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.RUNE_LITERAL_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), SyntaxKind.RUNE)

  }

  test("string") {
    val t = Tokenizer()
    val s = """"xyz""""
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.STRING_LITERAL_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), SyntaxKind.STRING)
  }

  test("int") {
    val t = Tokenizer()
    val s = "42"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.INTEGER_LITERAL_EXPRESSION)
    assertEquals(r.slotCount(), 1)
    assertEquals(r.slot(0).kind(), SyntaxKind.INTEGER)
  }

  test("parenthesized expression") {
    val t = Tokenizer()
    val s = "(name)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.PARENTHESIZED_EXPRESSION)
    assertEquals(r.slotCount(), 3)
    assertEquals(r.slot(0).kind(), Symbol.OPEN_PAREN)
    assertEquals(r.slot(1).kind(), SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(r.slot(2).kind(), Symbol.CLOSE_PAREN)

  }

  test("invocation expression") {
    val t = Tokenizer()
    val s = "this(name0, name1)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.INVOCATION_EXPRESSION)
    assertEquals(r.slotCount(), 4)
    assertEquals(r.slot(0).kind(), SyntaxKind.THIS_EXPRESSION)
    assertEquals(r.slot(1).kind(), Symbol.OPEN_PAREN)
    assertEquals(r.slot(2).kind(), SyntaxKind.SEPARATED_LIST)

    val sepList = r.slot(2)

    assertEquals(sepList.slotCount(), 3)
    assertEquals(sepList.slot(0).kind, SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(sepList.slot(1).kind, Symbol.COMMA)
    assertEquals(sepList.slot(2).kind, SyntaxKind.IDENTIFIER_NAME_EXPRESSION)

    assertEquals(r.slot(3).kind(), Symbol.CLOSE_PAREN)

  }

  test("index expression") {
    val t = Tokenizer()
    val s = "this[12]"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.INDEX_EXPRESSION)
    assertEquals(r.slotCount(), 4)
    assertEquals(r.slot(0).kind(), SyntaxKind.THIS_EXPRESSION)
    assertEquals(r.slot(1).kind(), Symbol.OPEN_BRACKET)
    assertEquals(r.slot(2).kind(), SyntaxKind.INTEGER_LITERAL_EXPRESSION)
    assertEquals(r.slot(3).kind(), Symbol.CLOSE_BRACKET)

  }

  test("member access expression") {

    val t = Tokenizer()
    val s = "this.a"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(r.slotCount(), 3)
    assertEquals(r.slot(0).kind(), SyntaxKind.THIS_EXPRESSION)
    assertEquals(r.slot(1).kind(), Symbol.DOT)
    assertEquals(r.slot(2).kind(), SyntaxKind.IDENTIFIER)
  }

  test("this.a.b") {
    val t = Tokenizer()
    val s = "this.a.b"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind(), SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(r.slotCount(), 3)

  }

  test("Primary this[32].A") {
    val t = Tokenizer()
    val s = "this[32].A"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val r = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(r.kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(r.slot(0).kind, SyntaxKind.INDEX_EXPRESSION)
    assertEquals(r.slot(1).kind, Symbol.DOT)
    assertEquals(r.slot(2).kind, SyntaxKind.IDENTIFIER)
    assertEquals(r.slot(0).slot(0).kind, SyntaxKind.THIS_EXPRESSION)
    assertEquals(r.slot(0).slot(1).kind, Symbol.OPEN_BRACKET)
    assertEquals(r.slot(0).slot(2).kind, SyntaxKind.INTEGER_LITERAL_EXPRESSION)
    assertEquals(r.slot(0).slot(3).kind, Symbol.CLOSE_BRACKET)
  }

  test("Primary this[32].A(23)") {
    val t = Tokenizer()
    val s = "this[32].A(23)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)
    assertEquals(result.kind, SyntaxKind.INVOCATION_EXPRESSION)
    assertEquals(result.slot(0).kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(result.slot(0).slot(0).kind, SyntaxKind.INDEX_EXPRESSION)
    assertEquals(result.slot(0).slot(0).slot(0).kind, SyntaxKind.THIS_EXPRESSION)
  }

  test("Primary this[32].A(23, b)") {
    val t = Tokenizer()
    val s = "this[32].A(23, b)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)
    assertEquals(result.kind, SyntaxKind.INVOCATION_EXPRESSION)
    assertEquals(result.slot(0).kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(result.slot(0).slot(0).kind, SyntaxKind.INDEX_EXPRESSION)
    assertEquals(result.slot(0).slot(0).slot(0).kind, SyntaxKind.THIS_EXPRESSION)

  }


  test("Primary a.b[20](30)") {
    val t = Tokenizer()
    val s = " a.b[20](30)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(result.kind, SyntaxKind.INVOCATION_EXPRESSION)
    assertEquals(result.slot(0).kind, SyntaxKind.INDEX_EXPRESSION)
    assertEquals(result.slot(0).slot(0).kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(result.slot(0).slot(0).slot(0).kind, SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
  }

  test("Primary a.b[20](30) 32") {
    val t = Tokenizer()
    val s = "a.b[20](30) 32"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)
    assertEquals(result.kind, SyntaxKind.INVOCATION_EXPRESSION)
    assertEquals(result.slot(0).kind, SyntaxKind.INDEX_EXPRESSION)
    assertEquals(result.slot(0).slot(0).kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(result.slot(0).slot(0).slot(0).kind, SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
  }

  test("Primary (asd)") {
    val t = Tokenizer()
    val s = "(asd)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)
    assertEquals(result.kind, SyntaxKind.PARENTHESIZED_EXPRESSION)
    assertEquals(result.slot(0).kind, Symbol.OPEN_PAREN)
    assertEquals(result.slot(1).kind, SyntaxKind.IDENTIFIER_NAME_EXPRESSION)
    assertEquals(result.slot(2).kind, Symbol.CLOSE_PAREN)
  }

  test("Primary (asd).b") {
    val t = Tokenizer()
    val s = "(asd).b"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)
    assertEquals(result.kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(result.slot(0).kind, SyntaxKind.PARENTHESIZED_EXPRESSION)
  }

  test("Primary ?a.b[20](30)") {
    val t = Tokenizer()
    val s = "?a.b[20](30)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(result.kind, SyntaxKind.INVOCATION_EXPRESSION)
    assertEquals(result.slot(0).kind, SyntaxKind.INDEX_EXPRESSION)
    assertEquals(result.slot(0).slot(0).kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(result.slot(0).slot(0).slot(0).kind, SyntaxKind.OPTION_NAME_EXPRESSION)
  }

  test("Primary a<T>.b[20](30)") {
    val t = Tokenizer()
    val s = "a<T>.b[20](30)"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchPrimary(tokens.asScala.toVector)

    assertEquals(result.kind, SyntaxKind.INVOCATION_EXPRESSION)
    assertEquals(result.slot(0).kind, SyntaxKind.INDEX_EXPRESSION)
    assertEquals(result.slot(0).slot(0).kind, SyntaxKind.MEMBER_ACCESS_EXPRESSION)
    assertEquals(result.slot(0).slot(0).slot(0).kind, SyntaxKind.GENERIC_NAME_EXPRESSION)
  }

}


class ExpressionTest extends munit.FunSuite {
  test("+12") {

    val t = Tokenizer()
    val s = "12 + 12"
    val tokens = t.lex(s)

    val p = ParserImplementation.MyParser()
    val result = p.matchExpression(tokens.asScala.toVector)

  }
}

class BaseTests extends munit.FunSuite {
  test("Base test 2 ") {
    val t = Tokenizer()
    val s = "class \uD835\uDEA8\u00AD\uD800\uDF41\n    def nameImplicit(): String\n        return \"\uD835\uDEA8\u00AD\uD800\uDF41\"\n    def nameExͯplicit(): String\n        return \"\\U+1D6A8\\U+00AD\\U+10341\"\n    def letterImplicit(): Rune\n        return '\uD835\uDEA8'\n    def letterExͯplicit(): Rune\n        return '\\U+1D6A8'\n    def number\uFEFFValue(): Int64\n        return 90\n    def numberImplicit(): Rune\n        return '\uD800\uDF41'\n    def numberExͯplicit(): Rune\n        return '\\U+10341'"
    val tokens = t.lex(s)

    println(tokens)

    val p = ParserImplementation.MyParser()
    val result = p.parse(s)

  }

//  test("Base test 3") {
//    val t = Tokenizer()
//    val s = "class Indent1\n   def notMultipleOf2(): Boolean\n      return true"
//    val tokens = t.lex(s)
//
//    println(tokens)
//
//    val p = ParserImplementation.MyParser()
//    val result = p.parse(s)
//
//  }
}