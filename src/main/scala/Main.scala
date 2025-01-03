package LexerImplementation

import ParserImplementation.MyParser
import LexerImplementation.Processors.UnicodeProcessor
import syspro.tm.WebServer
import syspro.tm.lexer.Token
import syspro.tm.lexer.TestMode
import syspro.tm.lexer.TestLineTerminators.{CarriageReturnLineFeed, LineFeed, Mixed, Native}

object Main {
  def main(args: Array[String]): Unit = {
    val lexer = Tokenizer()
    var test = TestMode()

    //    test = test.repeated(true)
    //    test = test.strict(true)
    //    test = test.parallel(true)
    //    test = test.shuffled(true)
    //    test = test.forceLineTerminators(Mixed)
        syspro.tm.Tasks.Lexer.registerSolution(lexer, test)
    
    

    WebServer.start()
    val parser = MyParser()
    syspro.tm.Tasks.Parser.registerSolution(parser)
    WebServer.waitForWebServerExit()

  }

  private def printTokens(l: java.util.List[Token]): Unit = {
    var i: Int = 0
    (0 until l.size())
      .foreach(i =>
        print(i.toString + " " +
          l.get(i).toString + " " + l.get(i).start.toString + " " + l.get(i).end.toString + " " + l.get(i).leadingTriviaLength.toString + " " + l.get(i).trailingTriviaLength + "\n")
      )
  }
}

