package scalikejdbc.scalatest

import scalikejdbc._
import org.joda.time.DateTime
import unit._
import org.scalatest.fixture.FlatSpec
import org.scalatest._

trait FlatSpecWithCommonTraits extends FlatSpec with Matchers with DBSettings with PreparingTables

class AutoRollbackSpec extends FlatSpecWithCommonTraits with AutoRollback {

  override def fixture(implicit session: DBSession) {
    SQL("insert into ScalaTest_members values (?, ?, ?)").bind(1, "Alice", DateTime.now).update.apply()
    SQL("insert into ScalaTest_members values (?, ?, ?)").bind(2, "Bob", DateTime.now).update.apply()
  }

  behavior of "AutoRollbackFixture"

  it should "be prepared and be able to create a new record" in { implicit session =>
    ScalaTestMember.count() should equal(2)
    ScalaTestMember.create(3, "Chris")
    ScalaTestMember.count() should equal(3)
  }

  it should "be rolled back" in { implicit session =>
    ScalaTestMember.count() should equal(2)
  }

}

class NamedAutoRollbackSpec extends FlatSpecWithCommonTraits with AutoRollback {

  override def db = NamedDB('db2).toDB

  override def fixture(implicit session: DBSession) {
    SQL("insert into scalatest_members2 values (?, ?, ?)").bind(1, "Alice", DateTime.now).update.apply()
    SQL("insert into scalatest_members2 values (?, ?, ?)").bind(2, "Bob", DateTime.now).update.apply()
  }

  behavior of "Named AutoRollbackFixture"

  it should "be prepared and be able to create a new record for NamedDB" in { implicit session =>
    ScalaTestMember2.count() should equal(2)
    ScalaTestMember2.create(3, "Chris")
    ScalaTestMember2.count() should equal(3)
  }

  it should "be rolled back" in { implicit session =>
    ScalaTestMember2.count() should equal(2)
  }

}
