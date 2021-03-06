package org.timekeeper


import JavaScriptLexer
import JavaScriptParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.timekeeper.grammar.*
import org.timekeeper.grammar.ConvertTreeVisitor
import org.timekeeper.utils.TEST_PATH
import org.timekeeper.utils.UPDATE_TREE
import java.io.File

fun main(args: Array<String>) {
//    val convertVisitor = ConvertTreeVisitor()
//    val str = "x = [1, 2, , 3, 4]"
//    val tree = Initialization().createLexerAndParser(str)
//    val n = tree?.accept(convertVisitor)
//    n?.printParentNode("")

    if (UPDATE_TREE) {
        Initialization().updateTrees()
    }
}

class Initialization() {
    private val convertVisitor = ConvertTreeVisitor()
    var version = "0.0.2"

    fun updateTrees() {
        File(TEST_PATH).walkTopDown().forEach {
            if (it.isFile.and(".js".toRegex().containsMatchIn(it.name))) {
                val data = it.readText()
                val saveFile = File("${it.parent}\\tree_${it.name.subSequence(0, it.name.length - 2)}.res")
                saveFile.delete()
                val parser = Initialization().createLexerAndParser(data)
                val result = parser?.accept(convertVisitor)
                //saveFile.appendText("testing system\n")
                //saveFile.appendText("version: $version\n")
                // saveFile.appendText("${if(it.readLines().isEmpty()) 1 else it.readLines().size}\n")
                //saveFile.appendText("test_case = ${data}\n")
                result?.printParentNode("", saveFile)
            }
        }
    }

    fun createLexerAndParser(str: String): JavaScriptParser.ProgramContext? {
        val lexer = JavaScriptLexer(CharStreams.fromString(str))
        val tokens = CommonTokenStream(lexer)
        val parser = JavaScriptParser(tokens).apply { buildParseTree = true }
        val listener = ErrorListener.instance
        val program = parser.program()
        parser.removeErrorListeners()
        parser.addErrorListener(listener)
        return if (parser.numberOfSyntaxErrors == 0) program else null
    }
}

