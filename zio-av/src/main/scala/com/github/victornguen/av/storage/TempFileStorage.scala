package com.github.victornguen.av.storage

import zio.nio.file.Path
import zio.{IO, Scope, ZIO}

import java.io.IOException

trait TempFileStorage {

  def createTempFileScoped(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): ZIO[Scope, IOException, Path]

  def createTempFile(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): IO[IOException, Path]
}

object TempFileStorage {

  def createTempFileScoped(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): ZIO[Scope with TempFileStorage, IOException, Path] =
    ZIO.serviceWithZIO[TempFileStorage](_.createTempFileScoped(suffix, prefix))

  def createTempFile(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): ZIO[TempFileStorage, IOException, Path] =
    ZIO.serviceWithZIO[TempFileStorage](_.createTempFile(suffix, prefix))
}
