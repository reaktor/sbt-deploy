package sbt.deploy

import sbt._

object SecureConnectivityPlugin extends Plugin {
  val identityFile = SettingKey[File]("identity-filename")
  val user = SettingKey[String]("user")
  val host = SettingKey[String]("host")
  val userHome = System.getProperty("user.home")
  val defaultIdentityFile = new File(".ssh/id_rsa")
  val userIdentityFile = new File(userHome, ".ssh/id_rsa")
  val identityFiles = Seq(defaultIdentityFile, userIdentityFile)
  lazy val secureConnectivitySettings: Seq[Setting[_]] = Seq(
    identityFile := identityFiles.find(_.exists).getOrElse(defaultIdentityFile)
  )
  def ssh(log: Logger, identityFile: File, user: String, host: String, commands: String*) {
    val chain = commands(0) + commands.toList.tail.foldLeft("")(_ + " && " + _)
    log.info("Executing command chain '" + chain + "' on remote server " + host + "...")
    "ssh -i " + identityFile + " " + user + "@" + host + " " + chain ! log
  }
  def scp(log: Logger, identityFile: File, user: String, host: String, src: File, dst: String) {
    scp(log, identityFile, user, host, src, new File(dst))
  }
  def scp(log: Logger, identityFile: File, user: String, host: String, src: File, dst: File) {
    log.info("Copying file " + src + " to " + host + ":" + dst + "...")
    "scp -r -i " + identityFile + " " + src + " " + user + "@" + host + ":" + dst ! log
  }
}
