package com.lbb
import java.text.SimpleDateFormat
import org.junit.runner.RunWith
import org.scalatest.junit.AssertionsForJUnit
import org.scalatest.FunSuite
import com.lbb.entity.Circle
import com.lbb.entity.CircleParticipant
import com.lbb.entity.User
import net.liftweb.common.Box
import net.liftweb.db.DB1.db1ToDb
import net.liftweb.db.DefaultConnectionIdentifier
import net.liftweb.db.StandardDBVendor
import net.liftweb.mapper.DB
import net.liftweb.mapper.Schemifier
import net.liftweb.util.Props
import org.scalatest.junit.JUnitRunner
import scala.xml.Text
import net.liftweb.common.Full
import net.liftweb.common.Empty
import com.lbb.util.LbbLogger
import com.lbb.entity.Friend

@RunWith(classOf[JUnitRunner])
class CircleParticipantTest extends FunSuite with AssertionsForJUnit with LbbLogger {

  def initDb = {
    // this stuff goes in Boot.scala
    val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "com.mysql.jdbc.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:mysql://localhost:3307/bdunklau", //"jdbc:h2:~/test", //"jdbc:mysql://localhost:3306/littlebluebird",
			     Box(Props.get("db.user") openOr "test"), Box(Props.get("db.pass") openOr "test"))

    DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    
    Schemifier.schemify(true, Schemifier.infoF _, User)
    Schemifier.schemify(true, Schemifier.infoF _, Circle)
    Schemifier.schemify(true, Schemifier.infoF _, CircleParticipant)
    Schemifier.schemify(true, Schemifier.infoF _, Friend)
  }

  test("create CircleParticipant with Mapper") {
    initDb
        
    Friend.findAll.foreach(_.delete_!)
    assert(Friend.findAll.size===0)
        
    CircleParticipant.findAll.foreach(_.delete_!)
    assert(CircleParticipant.findAll.size===0)
        
    Circle.findAll.foreach(_.delete_!)
    assert(Circle.findAll.size===0)
        
    User.findAll.foreach(_.delete_!)
    assert(User.findAll.size===0)
    
    
    // this stuff goes in the snippet I guess...
    val brent = UserTest.createBrent
    val tamie = UserTest.createTamie
    val kiera = UserTest.createKiera
    
    assert(brent.save===true)
    assert(tamie.save===true)
    assert(kiera.save===true)
    assert(User.findAll.size===3)
    
    
    // this stuff goes in the snippet I guess...
    val myBday = Circle.create.name("my birthday").date(new SimpleDateFormat("MM/dd/yyyy").parse("12/15/2012"))
    val anniv = Circle.create.name("Anniversary").date(new SimpleDateFormat("MM/dd/yyyy").parse("6/28/2012"))
    val xmas = Circle.create.name("Christmas").date(new SimpleDateFormat("MM/dd/yyyy").parse("12/25/2012"))
    
    assert(myBday.save===true)
    assert(anniv.save===true)
    assert(xmas.save===true)
    
    val me_myBday = CircleParticipant.create.circle(myBday).person(brent).inviter(brent).participationLevel("Receiver")
    val tamie_myBday = CircleParticipant.create.circle(myBday).person(tamie).inviter(brent).participationLevel("Receiver")
    
    
    val kiera_myBday = CircleParticipant.create.circle(myBday).person(kiera).inviter(brent).participationLevel("Receiver")
    
    assert(me_myBday.save===true)
    debug("brent's friend list should be 0");
    assert(brent.friendList.size === 0)
    
    assert(tamie_myBday.save===true)
    
    debug("tamie's friend list should be 1");
    
    tamie.friendList.foreach(bf => debug("tamie's friend: "+bf))
    
    assert(tamie.friendList.size === 1)
    
    debug("brent's friend list should be 1");
//    assert(brent.friendList.size === 1)
    debug("check brent id="+brent.id);
    brent.friendList.foreach(bf => debug("brent's friend: "+bf))
    
    assert(kiera_myBday.save===true)
    assert(brent.friendList.size === 2)
    assert(tamie.friendList.size === 2) 
    assert(kiera.friendList.size === 2)
    
    val me_Anniv = CircleParticipant.create.circle(anniv).person(brent).inviter(brent).participationLevel("Receiver")
    val tamie_Anniv = CircleParticipant.create.circle(anniv).person(tamie).inviter(brent).participationLevel("Receiver")
    
    assert(me_Anniv.save===true)
    assert(tamie_Anniv.save===true)
    
    // friend relationship has already been established in the prev circle, so no change to the number of friends here
    assert(brent.friendList.size === 2)
    assert(tamie.friendList.size === 2)
    assert(kiera.friendList.size === 2)
    
    val me_xmas = CircleParticipant.create.circle(xmas).person(brent).inviter(brent).participationLevel("Receiver")
    val tamie_xmas = CircleParticipant.create.circle(xmas).person(tamie).inviter(brent).participationLevel("Receiver")
    val kiera_xmas = CircleParticipant.create.circle(xmas).person(kiera).inviter(brent).participationLevel("Receiver")
    
    assert(me_xmas.save===true)
    assert(tamie_xmas.save===true)
    assert(kiera_xmas.save===true)
    
    // friend relationship has already been established in the first circle, so no change to the number of friends here
    assert(brent.friendList.size === 2)
    assert(tamie.friendList.size === 2)
    assert(kiera.friendList.size === 2)
    
    // brent.circles is a List.  It's all the instances of brent as a participant
    // This is how you find out all the circles brent is a member of
    brent.circles.foreach(c => debug(brent.first+" belongs to " +c.circleName))
    
    // xmas.participants is a List.  It's all participants in a given circle
    xmas.participants.foreach(p => debug(xmas.name+" has these members: " +p.name(p.person)))
    
  }
  
  test("populate xmas2012") {
    initDb
        
    Friend.findAll.foreach(_.delete_!)
    assert(Friend.findAll.size===0)
        
    CircleParticipant.findAll.foreach(_.delete_!)
    assert(CircleParticipant.findAll.size===0)
    
    Circle.findAll.foreach(_.delete_!)
    assert(Circle.findAll.size===0)
    
    User.findAll.foreach(_.delete_!)
    assert(User.findAll.size===0)
    
    val brent = UserTest.createBrent
    val tamie = UserTest.createTamie
    val kiera = UserTest.createKiera
    val truman = UserTest.createTruman
    val jett = UserTest.createJett
    val status = Map(brent -> "Receiver", tamie -> "Receiver", kiera -> "Receiver", truman -> "Receiver", jett -> "Receiver")
    val expectedPeople = List(brent, tamie, kiera, truman, jett)
    val expectedCircle = CircleTest.nextXmas.add(expectedPeople, brent)
    
    // Now check the stuff...
    // Does the circle have 5 participants
    // Does each user belong to the circle
    assert(expectedPeople.size===expectedCircle.participants.size)
    assert(expectedCircle.name==="Christmas 2012")
    
    // make sure each person belongs to the right circle
    expectedPeople.foreach(u => u.circles.foreach(c => {
                                                              assert(c.circleName.toString===expectedCircle.name.toString())
                                                            }
                                                       ))
                     
    // make sure circle has the right participants
    expectedCircle.participants.foreach(p => matchPerson(p, status))
                                                       
  }
  
  test("populate anniv2012") {
    initDb
        
    Friend.findAll.foreach(_.delete_!)
    assert(Friend.findAll.size===0)
        
    CircleParticipant.findAll.foreach(_.delete_!)
    assert(CircleParticipant.findAll.size===0)
    
    Circle.findAll.foreach(_.delete_!)
    assert(Circle.findAll.size===0)
    
    User.findAll.foreach(_.delete_!)
    assert(User.findAll.size===0)
    
    val brent = UserTest.createBrent
    val tamie = UserTest.createTamie
    val kiera = UserTest.createKiera
    val truman = UserTest.createTruman
    val jett = UserTest.createJett
    val status = Map(brent -> "Receiver", tamie -> "Receiver", kiera -> "Giver", truman -> "Giver", jett -> "Giver")
    val expectedReceivers = List(brent, tamie)
    val expectedGivers = List(kiera, truman, jett)
    val expectedCircle = CircleTest.anniv.add(expectedReceivers, expectedGivers, brent)
    
    // Now check the stuff...
    // Does the circle have 5 participants
    // Does each user belong to the circle
    assert((expectedReceivers.size + expectedGivers.size)===expectedCircle.participants.size)
    assert(expectedCircle.name==="Anniversary 2012")
    
    // make sure each person belongs to the right circle
    expectedReceivers.foreach(u => u.circles.foreach(c => {
                                                              assert(c.circleName.toString===expectedCircle.name.toString())
                                                            }
                                                       ))
    
    // make sure each person belongs to the right circle
    expectedGivers.foreach(u => u.circles.foreach(c => {
                                                              assert(c.circleName.toString===expectedCircle.name.toString())
                                                            }
                                                       ))
                     
    // make sure circle has the right participants
    // TODO how to verify correct giver/receiver status?
    expectedCircle.participants.foreach(p => matchPerson(p, status))
                                                       
  }
  
  test("populate bday2012") {
    initDb
        
    Friend.findAll.foreach(_.delete_!)
    assert(Friend.findAll.size===0)
        
    CircleParticipant.findAll.foreach(_.delete_!)
    assert(CircleParticipant.findAll.size===0)
    
    Circle.findAll.foreach(_.delete_!)
    assert(Circle.findAll.size===0)
    
    User.findAll.foreach(_.delete_!)
    assert(User.findAll.size===0)
    
    val brent = UserTest.createBrent
    val tamie = UserTest.createTamie
    val kiera = UserTest.createKiera
    val truman = UserTest.createTruman
    val jett = UserTest.createJett
    val status = Map(brent -> "Receiver", tamie -> "Giver", kiera -> "Giver", truman -> "Giver", jett -> "Giver")
    val expectedReceivers = List(brent)
    val expectedGivers = List(tamie, kiera, truman, jett)
    val expectedCircle = CircleTest.bday.add(expectedReceivers, expectedGivers, brent)
    
    // Now check the stuff...
    // Does the circle have 5 participants
    // Does each user belong to the circle
    assert((expectedReceivers.size + expectedGivers.size)===expectedCircle.participants.size)
    assert(expectedCircle.name==="BDay 2012")
    
    // make sure each person belongs to the right circle
    expectedReceivers.foreach(u => u.circles.foreach(c => {
                                                              assert(c.circleName.toString===expectedCircle.name.toString())
                                                            }
                                                       ))
    
    // make sure each person belongs to the right circle
    expectedGivers.foreach(u => u.circles.foreach(c => {
                                                              assert(c.circleName.toString===expectedCircle.name.toString())
                                                            }
                                                       ))
                     
    // make sure circle has the right participants
    // TODO how to verify correct giver/receiver status?
    expectedCircle.participants.foreach(p => matchPerson(p, status))
                                                       
  }
  
  def matchPerson(p:CircleParticipant, exp:Map[User, String]) = (p.person.obj openOr Empty, p.participationLevel.is) match {
      case (u:User, r) => assert(exp.get(u).getOrElse(false)===r)
      case _ => fail("fail: "+p.person.obj)
    }
  
}