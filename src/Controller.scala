import Main.projectPath

import scala.io.StdIn

class Controller {

  def printHelloInfo(): Unit = {
    println("Hello in Version Control System")
    println("To see list of available commands type 'help'")
    println("Write path to directory: ")
    print("scv:> ")
    projectPath = StdIn.readLine()
  }

  def validDirectory(projectDirectory: String): Boolean = {
    new java.io.File(projectDirectory).exists && new java.io.File(projectDirectory).isDirectory
  }

  def getDirectory: Boolean = {
    while (!validDirectory(projectPath)) {
      println("Bad location. Try one more time: ")
      print("scv:> ")
      projectPath = StdIn.readLine()
    }
    println("Init project on: " + projectPath)
    println("Create scv directory")
    new java.io.File(projectPath + "\\scv").mkdir()
  }

  def printCommandInfo(): Unit = {
    println("help - print all available commands")
    println("quit - leave application")
    println("add file/* - add single file or all files to add to new commit")
    println("new commit name - add new commit with choosen files")
    println("list commit - list of existing commits")
    println("load commit name - load files from choosen commit")
    println("status - show files in waiting room")
    println("ls - list files in directory")
    println("del commit name - delete choosen commit")
  }

  def printExistingCommits(): Unit = {
    val commits = Main.commitModel.getExistingCommits
    println("Available commits:")
    for (commit <- commits)
      println(commit)
  }

  def deleteCommit(command: String): AnyVal = {
    val commitToDelete = command.substring(11, command.length)
    Main.commitModel.deleteCommit(commitToDelete)
  }

  def loadCommit(command: String): Unit = {
    val commitToLoad = command.substring(12, command.length)
    Main.commitModel.loadCommit(commitToLoad)
  }

  def addFile(command: String): Unit = {
    val fileToAdd = command.substring(4, command.length)
    Main.commitModel.addFile(fileToAdd)
  }

  def addCommit(command: String): Unit = {
    val commitName = command.substring(11, command.length)
    Main.commitModel.addCommit(commitName)
  }

  def showFilesInWaitingRoom(): Unit = {
    val files = Main.commitModel.showWaitingRoom()
    for (file <- files)
      println(file)
  }

  def showFilesInFolder(): Unit = {
    Main.commitModel.showFilesInFolder(Main.projectPath, "")
  }

  def executeCommand(command: String): Boolean = command match {
    case "list commit" =>
      printExistingCommits()
      true
    case command if command.startsWith("del commit ") =>
      deleteCommit(command)
      true
    case command if command.startsWith("load commit ") =>
      loadCommit(command)
      true
    case command if command.startsWith("add ") =>
      addFile(command)
      true
    case command if command.startsWith("new commit ") =>
      addCommit(command)
      true
    case "help"   =>
      printCommandInfo()
      true
    case "quit"   =>
      println("Leave application")
      false
    case "status" =>
      showFilesInWaitingRoom()
      true
    case "ls"     =>
      showFilesInFolder()
      true
    case any      =>
      println(any + " is a bad command")
      true
  }

  def commandLoop(): Unit = {
    var command = ""
    var exit = true
    while (exit) {
      print("scv:> ")
      command = StdIn.readLine()
      exit = executeCommand(command)
    }
  }

}
