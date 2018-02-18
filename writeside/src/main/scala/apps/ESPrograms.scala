package apps


import cats.effect.IO
import config.{ApplicationConfig, DatabaseConfig}
import domain.RawEvent
import infrastructure.repository.doobie.EventLogDoobieInterpreter
import io.circe.Json
import io.circe.parser._


//These should be converted to test.
object ESPrograms extends App {

  val dummyJson: Json = parse("""
      {
        "type": "PLANT_CREATED",
        "id": "1",
        "name": "PLANT 1",
        "country": "GREECE"
      }
    """).getOrElse(Json.Null)


  println(dummyJson)

  val addArbitraryEvents: IO[Unit] = for {
    conf <- ApplicationConfig.load[IO]
    xa <- DatabaseConfig.dbTransactor[IO](conf.db)
    _ <- DatabaseConfig.initializeDb(xa)
    eventLog = EventLogDoobieInterpreter(xa)
    _ <- eventLog.append(RawEvent(None, dummyJson))

  } yield ()

  addArbitraryEvents.unsafeRunSync()

}
