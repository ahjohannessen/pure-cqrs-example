package apps

import cats.effect.IO
import config.{ApplicationConfig, DatabaseConfig}
import infrastructure.repository.doobie.PlantRepoDoobieInterpreter


// TODO these should be converted to tests
object  RunProgramsDatabase extends App {
  import programs._

  val run = for {
    conf <- ApplicationConfig.load[IO]
    xa <- DatabaseConfig.dbTransactor[IO](conf.db)
    _ <- DatabaseConfig.initializeDb(xa)  // This recreates and initializes the database
    repo = PlantRepoDoobieInterpreter(xa)
    res1 <- fetchPlants(repo)
    res2 <- createDeleteAndFetch(repo)
    res3 <- createAndFetchPlants(repo)

  } yield (res1, res2, res3).productIterator.toList.mkString("\n")

  val res = run.unsafeRunSync()
  println(res)

}