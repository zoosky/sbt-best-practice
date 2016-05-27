package com.thoughtworks.sbtBestPractice

import java.io.File

import com.thoughtworks.sbtBestPractice.TravisGitConfig.autoImport._
import sbt._
import sbt.Keys._
import sbtrelease.{Git, ReleasePlugin, Vcs}
import sbtrelease.ReleasePlugin.autoImport._
import sbtrelease.ReleaseStateTransformations._


object TravisRelease extends AutoPlugin {

  override def trigger = allRequirements

  override def requires = TravisGitConfig && ReleasePlugin

  override def projectSettings = Seq(
    releaseProcess := {
      ReleaseStep(releaseStepTask(travisGitConfig)) +: releaseProcess.value
    },
    releaseVcs := {
      githubCredential.value match {
        case SshKey(privateKeyFile) =>
          Some(new Git(baseDirectory.value) {
            override def cmd(args: Any*) = {
              Process(executableName(commandName) +: args.map(_.toString), baseDir, "GIT_SSH_COMMAND" -> raw"""ssh -i "${privateKeyFile.getAbsolutePath}" """)
            }
          })
        case PersonalAccessToken(key) =>
          releaseVcs.value
      }
    }
  )

}