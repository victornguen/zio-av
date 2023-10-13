package com.github.victornguen.av.storage

import zio.macros.{accessible, throwing}
import zio.nio.file.Path
import zio.{IO, Scope, ZIO}

import java.io.IOException

@accessible
trait TempFileStorage {
  @throwing
  def createTempFileScoped(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): ZIO[Scope, IOException, Path]

  def createTempFile(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): IO[IOException, Path]
}
