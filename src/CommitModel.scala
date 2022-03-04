import java.io.File
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.text.SimpleDateFormat
import scala.collection.mutable.ListBuffer

class CommitModel {


  private var commits = List[String]()
  private var files_add = Set[String]()
  var commits_add = new ListBuffer[String]()


  def loadStartedCommits(projectDirectory: String): Unit = {
    val commitNames = new File(projectDirectory + "\\scv").list()
    for (commitName <- commitNames) {
      if (new File(projectDirectory + "\\scv\\" + commitName).isDirectory)
      commits_add += commitName
    }
    commits = commits_add.toList
  }

  def getExistingCommits: List[String] = {
    val res_commits = new ListBuffer[String]()
    for (commit <- commits) {
      val nFile = new File(Main.projectPath + "\\scv\\" + commit)
      val sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
      res_commits += (commit + " - " + sdf.format(nFile.lastModified()))
    }
    res_commits.toList
  }


  def deleteCommit(commitToDelete: String): AnyVal = {
    if (!commits.contains(commitToDelete))
    println("Lack of thah commit")
    else {
      println("Delete commit " + commitToDelete)
      val indexDel = commits_add.indexOf(commitToDelete)
      commits_add.remove(indexDel)
      commits = commits_add.toList
      deleteFileRecursively(Main.projectPath + "\\scv\\" + commitToDelete)
    }
  }

  def copyDirectory(from: String, to: String): Unit = {
    val filesToAdd = new File(from).list()
    for (file <- filesToAdd) {
      if (new File(from + "\\" + file).isDirectory) {
        new File(to + "\\" + file).mkdir()
        copyDirectory(from + "\\" + file, to + "\\" + file)
      }
      else {
        Files.copy(Paths.get(from + "\\" + file),
        Paths.get(to + "\\" + file),
        StandardCopyOption.REPLACE_EXISTING)
      }
    }
  }

  def copySingleFile(from: String, to: String, fileName: String): Unit = {
    val parts = fileName.split("/")
    var new_to = to;
    var new_from = from
    if (parts.length > 0) {
      for(i <- 0 to parts.length - 1) {
        new_to += "\\" + parts(i)
        new_from += "\\" + parts(i)
        val newFile = new File(new_to)
        if (!newFile.exists() && i != parts.length-1)
          newFile.mkdir()
      }
    }
    if (new File(new_from).isDirectory) {
      new File(new_to).mkdir()
      copyDirectory(new_from, new_to)
    }
    else {
      Files.copy(Paths.get(new_from), Paths.get(new_to), StandardCopyOption.REPLACE_EXISTING)
    }
  }

  def deleteFileRecursively(path: String): Unit = {
    val fileToDelete = new File(path)
    if (fileToDelete.isDirectory) {
      for (file <- fileToDelete.list())
      deleteFileRecursively(path + "\\" + file)
    }
    fileToDelete.delete()
  }

  def loadCommit(commitToLoad: String): Unit = {
    if (!commits.contains(commitToLoad))
    println("Lack of thah commit")
    else {
      println("Deleting files in main directory")
      val files = Main.projectDirectory.list()
      for (file <- files) {
        if (file != "scv") {
          deleteFileRecursively(Main.projectPath + "\\" + file)
        }
      }

      println("Copy files from commit " + commitToLoad)
      copyDirectory(Main.projectPath + "\\scv\\" + commitToLoad, Main.projectPath)
      files_add = Set[String]()
    }
  }

  def addFile(fileToAdd: String): Unit = {
    if (fileToAdd == "*") {
      println("Load all files")
      val allFiles = new File(Main.projectPath).list()
      for (singleFile <- allFiles) {
        if (singleFile != "scv" && !files_add.contains(singleFile))
        files_add += singleFile
      }
    }
    else {
      if (!new File(Main.projectPath + "\\" + fileToAdd).exists())
      println("Lack of that file")
      else {
        files_add += fileToAdd
      }
    }
  }

  def addCommit(commitName: String): Unit = {
    val folderCommit = new File(Main.projectPath + "\\scv\\" + commitName)
    if (!folderCommit.exists()) {
      commits_add += commitName
      commits = commits_add.toList
      println("Create new commit")
      folderCommit.mkdir()
    }
    else
    println("Overwrite commit")

    println("Copying files to commit")
    for (newFile <- files_add) {
      copySingleFile(Main.projectPath, Main.projectPath + "\\scv\\" + commitName, newFile)
    }
    files_add = Set[String]()
  }

  def getFolderSize(path: String): Int = {
    var directSize = 0
    val folder = new File(path)
    for (file <- folder.listFiles()) {
      if (file.isFile)
      directSize += file.length().asInstanceOf[Int]
      else
      directSize += getFolderSize(path + "\\" + file.getName)
    }

    directSize
  }

  def showWaitingRoom(): List[String] = {
    println("Files in waiting room")
    val sFiles = files_add
      .filter(x => !new File(Main.projectPath + "\\" + x).isDirectory)
      .map(x => "   " + x + " " +  new File(Main.projectPath + "\\" + x).length() + " bytes")
      .toList
    for (file <- sFiles)
    println(file)
    println("Directories in waiting room")
    val dFiles = files_add
      .filter(x => new File(Main.projectPath + "\\" + x).isDirectory)
      .map(x => "   " + x + " " + getFolderSize(Main.projectPath + "\\" + x) + " bytes")
      .toList
    dFiles
  }

  def showFilesInFolder(path: String, break: String): Unit = {
    val folder = new File(path)
    val files = folder.listFiles()
    val sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
    for (file <- files) {
      if (file.getName != "scv") {
        if (!file.isDirectory)
          println(break + file.getName + " " + file.length() + " bytes " + " - last modification " + sdf.format(file.lastModified()))
        else {
          println(break + file.getName + " " + getFolderSize(path + "\\" + file.getName) + " bytes " + " - last modification " + sdf.format(file.lastModified()))
          showFilesInFolder(path + "\\" + file.getName, break + "   ")
        }
      }
    }
  }
}
