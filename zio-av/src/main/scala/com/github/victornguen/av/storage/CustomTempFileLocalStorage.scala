package com.github.victornguen.av.storage

import zio.nio.file.{Files, Path}
import zio.{IO, Scope, ULayer, ZIO, ZLayer}

import java.io.IOException

final case class CustomTempFileLocalStorage(path: Path) extends TempFileStorage {
  def createTempFileScoped(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): ZIO[Scope, IOException, Path] =
    Files.createTempFileInScoped(path, suffix, prefix)

  override def createTempFile(
      suffix: String = ".tmp",
      prefix: Option[String] = None,
  ): IO[IOException, Path] =
    Files.createTempFileIn(path, suffix, prefix, List.empty)
}

object CustomTempFileLocalStorage {
  def makeLayer(path: Path): ULayer[CustomTempFileLocalStorage] = ZLayer.succeed(CustomTempFileLocalStorage(path))
}
