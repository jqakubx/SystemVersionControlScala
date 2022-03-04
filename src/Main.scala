import java.io.File

object Main {
  val controller = new Controller
  val commitModel = new CommitModel
  var projectPath = ""
  var projectDirectory:File = _

  def main(args: Array[String]): Unit = {
    controller.printHelloInfo()
    controller.getDirectory
    projectDirectory = new File(projectPath)
    commitModel.loadStartedCommits(projectPath)
    controller.commandLoop()
  }

}
