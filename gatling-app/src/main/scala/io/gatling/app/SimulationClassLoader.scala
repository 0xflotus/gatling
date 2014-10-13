/**
 * Copyright 2011-2014 eBusiness Information, Groupe Excilys (www.ebusinessinformation.fr)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gatling.app

import java.io.File
import java.lang.reflect.Modifier
import java.nio.file.Path

import scala.util.{ Properties, Try, Success }

import io.gatling.core.scenario.Simulation
import io.gatling.core.util.PathHelper._

object SimulationClassLoader {

  private def isInClassPath(binariesDirectory: Path): Boolean = {
    val classpathElements = Properties.javaClassPath split File.pathSeparator
    classpathElements.contains(binariesDirectory.toString)
  }

  def apply(binariesDirectory: Path): SimulationClassLoader = {
    val classloader = {
      if (isInClassPath(binariesDirectory)) getClass.getClassLoader
      else new FileSystemBackedClassLoader(binariesDirectory, getClass.getClassLoader)
    }
    new SimulationClassLoader(classloader, binariesDirectory)
  }
}

class SimulationClassLoader(classLoader: ClassLoader, binaryDir: Path) {

  private def isSimulationClass(clazz: Class[_]): Boolean =
    classOf[Simulation].isAssignableFrom(clazz) && !clazz.isInterface && !Modifier.isAbstract(clazz.getModifiers)

  private def pathToClassName(path: Path, root: Path): String =
    (path.getParent / path.stripExtension)
      .toString
      .stripPrefix(root + File.separator)
      .replace(File.separator, ".")

  def simulationClasses(requestedClassName: Option[String]): List[Class[Simulation]] = {

    requestedClassName.map { requestedClassName =>
      Try(classLoader.loadClass(requestedClassName)) match {
        case Success(clazz) if isSimulationClass(clazz) =>
          List(clazz.asInstanceOf[Class[Simulation]])
        case Success(clazz) =>
          Console.err.println(s"The request class('$requestedClassName') does not extend 'Simulation'.")
          Nil
        case _ =>
          Console.err.println(s"The request class('$requestedClassName') can not be found in the classpath.")
          Nil
      }
    }.getOrElse {
      binaryDir
        .deepFiles
        .collect { case file if file.hasExtension("class") => classLoader.loadClass(pathToClassName(file, binaryDir)) }
        .collect { case clazz if isSimulationClass(clazz) => clazz.asInstanceOf[Class[Simulation]] }
        .toList
    }
  }
}
